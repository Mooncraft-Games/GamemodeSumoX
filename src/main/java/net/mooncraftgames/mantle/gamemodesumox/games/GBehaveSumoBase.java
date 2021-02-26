package net.mooncraftgames.mantle.gamemodesumox.games;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerToggleSprintEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import de.lucgameshd.scoreboard.api.ScoreboardAPI;
import de.lucgameshd.scoreboard.network.*;
import net.mooncraftgames.mantle.gamemodesumox.*;
import net.mooncraftgames.mantle.gamemodesumox.games.pointentities.PETypeSumoXPowerUpSpot;
import net.mooncraftgames.mantle.newgamesapi.NewGamesAPI1;
import net.mooncraftgames.mantle.newgamesapi.Utility;
import net.mooncraftgames.mantle.newgamesapi.game.GameBehavior;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.game.events.GamePlayerDeathEvent;
import net.mooncraftgames.mantle.newgamesapi.kits.Kit;
import net.mooncraftgames.mantle.newgamesapi.rewards.PlayerRewardsProfile;
import net.mooncraftgames.mantle.newgamesapi.rewards.RewardsManager;
import net.mooncraftgames.mantle.newgamesapi.team.Team;
import net.mooncraftgames.mantle.newgamesapi.team.TeamPresets;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GBehaveSumoBase extends GameBehavior {

    // Game Configurables - For game flavours.
    protected boolean isTimerEnabled = true;
    protected boolean isPanicModeAllowed = true;
    protected boolean arePowerUpsAllowed = true;

    // Game Values
    protected int maxTimer = -1;
    protected int roundTimer = -1;

    protected boolean isInPanicMode = false;

    protected int minimumPowerUpSpawnTime;
    protected int variationPowerUpSpawnTime;
    protected HashMap<String, Integer> powerUpPointCooldowns;

    protected int defaultTally;
    protected HashMap<Player, Integer> lifeTally;

    protected float gameBaseSpeedMultiplier;
    protected float gameSpeedMultiplier;

    protected int initialPlayerCount;



    @Override
    public Team.GenericTeamBuilder[] getTeams() {
        return TeamPresets.FREE_FOR_ALL;
    }

    @Override
    public int onGameBegin() {
        getSessionHandler().getPointEntityTypeManager().registerPointEntityType(new PETypeSumoXPowerUpSpot(getSessionHandler()));
        return 5;
    }

    @Override
    public void onInitialCountdownEnd() {
        this.maxTimer = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_TIMER, SumoXConstants.BASE_TIMER_LEGNTH), 10);
        this.roundTimer = this.maxTimer;

        this.minimumPowerUpSpawnTime = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_MIN_POWERUP_SPAWN_TIME, SumoXConstants.DEFAULT_MIN_POWERUP_SPAWN_TIME), 5);
        this.variationPowerUpSpawnTime = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_VARY_POWERUP_SPAWN_TIME, SumoXConstants.DEFAULT_VARIATION_POWERUP_SPAWN_TIME), 0) + 1;
        this.powerUpPointCooldowns = new HashMap<>();

        this.defaultTally = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_LIVES, SumoXConstants.DEFAULT_LIVES), 1);
        this.lifeTally = new HashMap<>();

        this.gameBaseSpeedMultiplier = Math.max(getSessionHandler().getPrimaryMapID().getFloats().getOrDefault(SumoXKeys.FLOAT_BASE_GAME_SPEED, SumoXConstants.DEFAULT_BASE_GAME_SPEED), 0f);
        this.gameSpeedMultiplier = gameBaseSpeedMultiplier;

        for(Player player: getSessionHandler().getPlayers()){
            lifeTally.put(player, defaultTally);
        }

        this.initialPlayerCount = getSessionHandler().getPlayers().size();
    }

    @Override
    public void registerGameSchedulerTasks() {
        getSessionHandler().getGameScheduler().registerGameTask(this::scoreboardUpdateTick, 15, 15);
        getSessionHandler().getGameScheduler().registerGameTask(this::handleTimerTick, 20, 20);
    }

    @Override
    public Optional<Team> onMidGameJoinEvent(Player player) {
        lifeTally.put(player, 0);
        return Optional.empty();
    }

    @Override
    public void onPlayerLeaveGame(Player player) {
        player.clearTitle();
    }

    @Override
    public void onAddPlayerToTeam(Player player, Team team) {
        if(getSessionHandler().getGameState() == GameHandler.GameState.MAIN_LOOP){
            checkMidGameWinStatus();
        }
    }

    @Override public void onGameDeathByBlock(GamePlayerDeathEvent event) { handleDeath(event); }
    @Override public void onGameDeathByEntity(GamePlayerDeathEvent event) { handleDeath(event); }
    @Override public void onGameDeathByPlayer(GamePlayerDeathEvent event) { handleDeath(event); }
    @Override public void onGameMiscDeathEvent(GamePlayerDeathEvent event) { handleDeath(event); }

    public void handleDeath(GamePlayerDeathEvent event){
        Player player = event.getDeathCause().getVictim();
        int newVal = lifeTally.getOrDefault(player, 1) - 1;

        if(newVal <= 0){
            player.sendTitle(SumoXStrings.DEAD_TITLE, SumoXStrings.DEAD_SUBTITLE, 5, 50, 5);
            event.setDeathState(GamePlayerDeathEvent.DeathState.MOVE_TO_DEAD_SPECTATORS);

        } else {
            int respawnTime = getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_RESPAWN_SECS, SumoXConstants.DEFAULT_RESPAWN_SECONDS);

            if(respawnTime < 1){
                event.setDeathState(GamePlayerDeathEvent.DeathState.INSTANT_RESPAWN);

            } else {
                event.setDeathState(GamePlayerDeathEvent.DeathState.TIMED_RESPAWN);
                event.setRespawnSeconds(respawnTime);
            }
        }
        lifeTally.put(player, newVal);
    }

    protected void handleTimerTick(){
        checkMidGameWinStatus();
        if(isTimerEnabled){
            roundTimer--;
            if(roundTimer < 0){
                declareWinByTimerEnd();
            }
        }

        if(isPanicModeAllowed){
            if(!isInPanicMode){
                if (roundTimer <= (maxTimer * SumoXConstants.BASE_TIMER_PANIC_ZONE)) {
                    this.isInPanicMode = true;

                    for(Player player: getSessionHandler().getPlayers()) player.sendMessage(SumoXStrings.PANIC_MESSAGE);
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 0);
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 40);
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 80);
                }
            }
        }

        if(arePowerUpsAllowed){
            getSessionHandler().getPointEntityTypeManager().getRegisteredTypes().get(SumoXKeys.PE_TYPE_POWERUP).executeFunctionForAll(SumoXKeys.PE_FUNC_POWERUP_SPAWN, new HashMap<>());
        }
    }

    protected void checkMidGameWinStatus(){
        ArrayList<Player> alivePlayers = new ArrayList<>();

        for(Team team: getSessionHandler().getTeams().values()){
            if(team.isActiveGameTeam()){
                alivePlayers.addAll(team.getPlayers());
            }
        }

        alivePlayers.addAll(getSessionHandler().getDeathManager().getPendingRespawns());

        if(alivePlayers.size() == 1) getSessionHandler().declareVictoryForPlayer(alivePlayers.get(0));
        if(alivePlayers.size() < 1) getSessionHandler().declareLoss();
    }

    protected void declareWinByTimerEnd(){
        //TODO: Add a way to declare winners with a player[]
        getSessionHandler().declareVictoryForEveryone();
    }

    protected void sendPanicWarning(){
        for(Player player: getSessionHandler().getPlayers()){
            player.sendTitle(SumoXStrings.PANIC_TITLE, SumoXStrings.PANIC_SUBTITILE, 15, 5, 15);
            player.getLevel().addSound(player.getPosition(), Sound.BEACON_POWER, 0.5f, 1.2f, player);
        }
    }



    public void scoreboardUpdateTick(){
        for(Player player: getSessionHandler().getPlayers()) updateScoreboards(player);
    }

    protected void updateScoreboards(Player player){
        int aliveCount = 0;

        for(Integer v: lifeTally.values()){

            if((v != null) && (v > 0)) {
                aliveCount += 1;
            }
        }

        getSessionHandler().getScoreboardManager().setLine(player, 3, String.format("%s %s%s", Utility.ResourcePackCharacters.HEART_FULL, TextFormat.WHITE, lifeTally.getOrDefault(player, 0)));
        getSessionHandler().getScoreboardManager().setLine(player, 5, String.format("%s %s%s/%s", Utility.ResourcePackCharacters.MORE_PEOPLE, TextFormat.WHITE, aliveCount, initialPlayerCount));
        getSessionHandler().getScoreboardManager().setLine(player, 9, String.format("%s %s%s", Utility.ResourcePackCharacters.TIME, isInPanicMode ? TextFormat.RED : TextFormat.WHITE, roundTimer));
    }



    public int getTimeElapsed() { return maxTimer-roundTimer; }
    public boolean isTimerEnabled() { return isTimerEnabled; }
    public boolean isPanicModeAllowed() { return isPanicModeAllowed; }
    public boolean arePowerUpsAllowed() { return arePowerUpsAllowed; }

    public float getGameBaseSpeedMultiplier() { return gameBaseSpeedMultiplier; }
    public float getGameSpeedMultiplier() { return gameSpeedMultiplier; }

    public int getMinimumPowerUpSpawnTime() { return minimumPowerUpSpawnTime; }
    public int getVariationPowerUpSpawnTime() { return variationPowerUpSpawnTime; }
    public HashMap<String, Integer> getPowerUpPointCooldowns() {
        return powerUpPointCooldowns;
    }


    public void setGameSpeedMultiplier(float gameSpeedMultiplier) { this.gameSpeedMultiplier = gameSpeedMultiplier; }



    @EventHandler
    public void onSprintChange(PlayerToggleSprintEvent event){
        if(getSessionHandler().getPlayers().contains(event.getPlayer()) && getSessionHandler().getGameState() == GameHandler.GameState.MAIN_LOOP) sprintChangeEvent(event.getPlayer(), event.isSprinting());
    }

    protected void sprintChangeEvent(Player player, boolean sprint){
        float kitModifier = 1f;
        Kit kit = getSessionHandler().getAppliedSessionKits().get(player);

        if(kit != null){
            Optional<String> kitSpeedStr = kit.getProperty(SumoXKeys.KIT_PROP_SPEED_MULT);

            if(kitSpeedStr.isPresent()){
                Optional<Float> f = SumoUtil.StringToFloat(kitSpeedStr.get());
                if(f.isPresent()) kitModifier = f.get();
            }
        }

        if(sprint){
            player.setMovementSpeed((SumoXConstants.VANILLA_BASE_SPEED * kitModifier * SumoXConstants.VANILLA_SPRINT_SPEED_MULTIPLIER) * gameSpeedMultiplier); //Vanilla is a 30% increase
        } else {
            player.setMovementSpeed(SumoXConstants.VANILLA_BASE_SPEED * kitModifier * gameSpeedMultiplier);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player && event.getDamager() instanceof Player && event.getCause() != EntityDamageEvent.DamageCause.CONTACT){
            Player player = (Player) event.getEntity();
            if(getSessionHandler().getPlayers().contains(player)){
                event.setCancelled(true);

                double attackModifier = 1.0f;

                if(event.getDamager() instanceof Player) {
                    Player p = (Player) event.getDamager();
                    Kit attackerkit = getSessionHandler().getAppliedSessionKits().get(p);
                    if (attackerkit != null) {
                        attackModifier = SumoUtil.StringToFloat(attackerkit.getProperty(SumoXKeys.KIT_PROP_GIVEN_KB_MULT).orElse(null)).orElse(1.0f);
                    }
                }

                doKnockback(player, event.getDamager(), SumoXConstants.KNOCKBACK_BASE, attackModifier);

            }
        }
    }

    public void doKnockback(Player victim, Entity attacker, double baseKB, double kitDamageModifier) {
        // If panic: base * (multiplier ^ time elapsed in panic zone)

        float victimModifier = 1.0f;

        Kit victimkit = getSessionHandler().getAppliedSessionKits().get(victim);
        if(victimkit != null){
            victimModifier = SumoUtil.StringToFloat(victimkit.getProperty(SumoXKeys.KIT_PROP_TAKEN_KB_MULT).orElse(null)).orElse(1.0f);
        }

        double baseKBValue = baseKB * victimModifier * kitDamageModifier;
        double knockbackValue = isInPanicMode ? Math.min(baseKBValue * (Math.pow(SumoXConstants.PANIC_KNOCKBACK_MULTIPLIER, (getTimeElapsed()-Math.floor(maxTimer*(1-SumoXConstants.BASE_TIMER_PANIC_ZONE))))), baseKBValue*5) : baseKBValue;
        applyKnockback(victim, attacker, knockbackValue);
    }



    public static void applyKnockback(Player victim, Entity attacker, double fullKB) {
        double deltaX = victim.getX() - attacker.getX();
        double deltaZ = victim.getZ() - attacker.getZ();
        victim.knockBack(attacker, 0, deltaX, deltaZ, fullKB);
    }

}
