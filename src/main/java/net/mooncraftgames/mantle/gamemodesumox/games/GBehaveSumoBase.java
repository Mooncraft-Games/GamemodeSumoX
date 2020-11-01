package net.mooncraftgames.mantle.gamemodesumox.games;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.gamemodesumox.SumoXStrings;
import net.mooncraftgames.mantle.newgamesapi.game.GameBehavior;
import net.mooncraftgames.mantle.newgamesapi.game.deaths.DeathManager;
import net.mooncraftgames.mantle.newgamesapi.game.events.GamePlayerDeathEvent;
import net.mooncraftgames.mantle.newgamesapi.team.DeadTeam;
import net.mooncraftgames.mantle.newgamesapi.team.Team;
import net.mooncraftgames.mantle.newgamesapi.team.TeamPresets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class GBehaveSumoBase extends GameBehavior {

    // Game Configurables - For game flavours.
    protected boolean isTimerEnabled = true;
    protected boolean isTimerBarDisplayed = true;
    protected boolean isPanicModeAllowed = true;

    // Game Values
    protected int maxTimer = -1;
    protected int roundTimer = -1;

    protected boolean isInPanicMode = false;

    protected int defaultTally;
    protected HashMap<Player, Integer> lifeTally;

    protected HashMap<Player, DummyBossBar> bartimerBossbars;
    protected TextFormat bartimerMainTextColour;
    protected TextFormat bartimerSubTextColour;
    protected BlockColor bartimerColour;

    @Override
    public void onInitialCountdownEnd() {
        this.maxTimer = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_TIMER, SumoXConstants.BASE_TIMER_LEGNTH), 10);
        this.roundTimer = this.maxTimer;

        this.defaultTally = Math.min(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_LIVES, SumoXConstants.DEFAULT_LIVES), 1);
        this.lifeTally = new HashMap<>();

        this.bartimerBossbars = new HashMap<>();
        this.bartimerMainTextColour = TextFormat.BLUE;
        this.bartimerSubTextColour = TextFormat.DARK_AQUA;
        this.bartimerColour = BlockColor.BLUE_BLOCK_COLOR;

        String timebarText = getTimerbarText();
        for(Player player: getSessionHandler().getPlayers()){
            DummyBossBar bar = new DummyBossBar.Builder(player)
                    .color(bartimerColour)
                    .length(100)
                    .text(timebarText)
                    .build();
            player.createBossBar(bar);

            DummyBossBar oldBar = bartimerBossbars.put(player, bar);
            if(oldBar != null) oldBar.destroy();

            lifeTally.put(player, defaultTally);
        }
    }

    @Override
    public void registerGameSchedulerTasks() {
        getSessionHandler().getGameScheduler().registerGameTask(this::handleTimerTick, 20, 20);
    }

    @Override
    public Optional<Team> onMidGameJoinEvent(Player player) {
        DummyBossBar bar = new DummyBossBar.Builder(player)
                .color(bartimerColour)
                .length(100)
                .text(getTimerbarText())
                .build();
        player.createBossBar(bar);
        bartimerBossbars.put(player, bar);

        return Optional.empty();
    }

    @Override
    public void onPlayerLeaveGame(Player player) {
        DummyBossBar b = bartimerBossbars.remove(player);
        if (b != null){
            b.destroy();
        }
        player.clearTitle();
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

            lifeTally.put(player, newVal);
        }
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
                if (roundTimer <= (maxTimer * SumoXConstants.PANIC_KNOCKBACK_MULTIPLIER)) {
                    this.isInPanicMode = true;
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 0);
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 40);
                    getSessionHandler().getGameScheduler().registerGameTask(this::sendPanicWarning, 80);

                    bartimerMainTextColour = TextFormat.RED;
                    bartimerSubTextColour = TextFormat.GOLD;
                    bartimerColour = BlockColor.RED_BLOCK_COLOR;
                }
            }
        }

        if(isTimerBarDisplayed){
            String timebarText = getTimerbarText();
            float timebarValue = ((float) roundTimer) / maxTimer;

            for(DummyBossBar bossBar: bartimerBossbars.values()){
                bossBar.setLength(timebarValue);
                bossBar.setText(timebarText);
                bossBar.setColor(bartimerColour);
            }
        }
    }

    @Override
    public void onRemovePlayerFromTeam(Player player, Team team) {
        // Should account for leaving game + death.
        checkMidGameWinStatus();
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
            player.sendMessage(SumoXStrings.PANIC_MESSAGE);
            player.getLevel().addSound(player.getPosition(), Sound.BEACON_POWER, 0.5f, 1.2f, player);
        }
    }

    protected String getTimerbarText(){
        return String.format("\n\n%s%sTime Remaining: %s%s%s", bartimerMainTextColour, TextFormat.BOLD, bartimerSubTextColour, TextFormat.BOLD, roundTimer);
    }

    public int getTimeElapsed() {
        return maxTimer-roundTimer;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(getSessionHandler().getPlayers().contains(player)){
                event.setCancelled(true);

                //I have no clue what this does. EntityLiving#attack() uses it though sooo...
                double deltaX = player.getX() - event.getDamager().getX();
                double deltaZ = player.getZ() - event.getDamager().getZ();
                // If panic: base * (multiplier ^ time elapsed in panic zone)
                double knockbackValue = isInPanicMode ? SumoXConstants.KNOCKBACK_BASE * (Math.pow(SumoXConstants.PANIC_KNOCKBACK_MULTIPLIER, (getTimeElapsed()-Math.floor(maxTimer*SumoXConstants.BASE_TIMER_PANIC_ZONE)))) : SumoXConstants.KNOCKBACK_BASE;

                player.knockBack(event.getDamager(), 0, deltaX, deltaZ, knockbackValue);
            }
        }
    }

}
