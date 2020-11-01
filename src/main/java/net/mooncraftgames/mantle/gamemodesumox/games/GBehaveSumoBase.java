package net.mooncraftgames.mantle.gamemodesumox.games;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DummyBossBar;
import cn.nukkit.utils.TextFormat;
import de.lucgameshd.scoreboard.api.ScoreboardAPI;
import de.lucgameshd.scoreboard.network.DisplaySlot;
import de.lucgameshd.scoreboard.network.Scoreboard;
import de.lucgameshd.scoreboard.network.ScoreboardDisplay;
import net.mooncraftgames.mantle.gamemodesumox.SumoX;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.gamemodesumox.SumoXStrings;
import net.mooncraftgames.mantle.newgamesapi.Utility;
import net.mooncraftgames.mantle.newgamesapi.game.GameBehavior;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.game.deaths.DeathManager;
import net.mooncraftgames.mantle.newgamesapi.game.events.GamePlayerDeathEvent;
import net.mooncraftgames.mantle.newgamesapi.team.DeadTeam;
import net.mooncraftgames.mantle.newgamesapi.team.Team;
import net.mooncraftgames.mantle.newgamesapi.team.TeamPresets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    protected HashMap<Player, ScoreboardDisplay> scoreboards;

    @Override
    public void onInitialCountdownEnd() {
        this.maxTimer = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_TIMER, SumoXConstants.BASE_TIMER_LEGNTH), 10);
        this.roundTimer = this.maxTimer;

        this.defaultTally = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.INT_LIVES, SumoXConstants.DEFAULT_LIVES), 1);
        this.lifeTally = new HashMap<>();

        this.bartimerBossbars = new HashMap<>();
        this.bartimerMainTextColour = TextFormat.BLUE;
        this.bartimerSubTextColour = TextFormat.DARK_AQUA;
        this.bartimerColour = BlockColor.BLUE_BLOCK_COLOR;

        Scoreboard mainboard = ScoreboardAPI.createScoreboard();
        this.scoreboards = new HashMap<>();

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

            ScoreboardDisplay display = mainboard.addDisplay(DisplaySlot.SIDEBAR, "sumo-"+ Utility.generateUniqueToken(6, 4), String.format("%s%sSUMO %s%sBRAWL", TextFormat.GOLD, TextFormat.BOLD, TextFormat.RED, TextFormat.BOLD));
            scoreboards.put(player, display);
        }
    }

    @Override
    public void registerGameSchedulerTasks() {
        getSessionHandler().getGameScheduler().registerGameTask(this::handleTimerTick, 20, 20);
    }

    @Override
    public Optional<Team> onMidGameJoinEvent(Player player) {
        getSessionHandler().getGameScheduler().registerGameTask(() -> {
            DummyBossBar bar = new DummyBossBar.Builder(player)
                    .color(bartimerColour)
                    .length(100)
                    .text(getTimerbarText())
                    .build();
            player.createBossBar(bar);
            bartimerBossbars.put(player, bar);
        }, 1, 0);

        lifeTally.put(player, 0);

        Scoreboard playerBoard = ScoreboardAPI.createScoreboard();
        ScoreboardDisplay display = playerBoard.addDisplay(DisplaySlot.SIDEBAR, "sumo-"+ Utility.generateUniqueToken(6, 4), String.format("%s%sSUMO %s%sBRAWL", TextFormat.GOLD, TextFormat.BOLD, TextFormat.RED, TextFormat.BOLD));
        scoreboards.put(player, display);

        return Optional.empty();
    }

    @Override
    public void onPlayerLeaveGame(Player player) {
        DummyBossBar b = bartimerBossbars.remove(player);
        if (b != null){
            b.destroy();
        }
        ScoreboardDisplay display = scoreboards.remove(player);
        if(display != null){
            display.getScoreboard().hideFor(player);
        }
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
                if (roundTimer <= (maxTimer * SumoXConstants.BASE_TIMER_PANIC_ZONE)) {
                    this.isInPanicMode = true;

                    for(Player player: getSessionHandler().getPlayers()) player.sendMessage(SumoXStrings.PANIC_MESSAGE);
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
            float timebarValue = (((float) roundTimer) / maxTimer) * 100;

            for(DummyBossBar bossBar: bartimerBossbars.values()){
                bossBar.setLength(timebarValue);
                bossBar.setText(timebarText);
                bossBar.setColor(bartimerColour);
                bossBar.reshow();
            }
        }
    }

    protected void checkMidGameWinStatus(){
        ArrayList<Player> alivePlayers = new ArrayList<>();

        for(Team team: getSessionHandler().getTeams().values()){
            if(team.isActiveGameTeam()){
                alivePlayers.addAll(team.getPlayers());
            }
        }

        SumoX.getPlgLogger().debug("Alive: "+alivePlayers.size());
        SumoX.getPlgLogger().debug("Pending: "+getSessionHandler().getDeathManager().getPendingRespawns().size());
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

    // Fixed to 5 slots. If updating size, account for this.
    protected void updateScoreboards(Player player){
        ScoreboardDisplay display = scoreboards.get(player);
        display.getLineEntry().clear();

        display.addLine(" ", 15);

        ArrayList<Map.Entry<Player, Integer>> topScores = new ArrayList<>();

        for(Map.Entry<Player, Integer> e: lifeTally.entrySet()){
            if(e.getValue() < 1){
                continue;
            }
            boolean isAdded = false;
            for(int i = 0; i < topScores.size(); i++){
                if(topScores.get(i).getValue() <= e.getValue()){
                    topScores.add(i, e);
                    isAdded = true;
                    break;
                }
            }
            if(!isAdded){
                topScores.add(e);
            }
        }

        int liveListIndex = 0;
        for(int i = 14; i > 8; i--){
            if(topScores.size() > liveListIndex){
                display.addLine(getLivesText(topScores.get(liveListIndex).getKey().getDisplayName(), topScores.get(liveListIndex).getValue().toString()), i);
            } else {
                display.addLine(String.format("%s%s???", TextFormat.DARK_GRAY, TextFormat.BOLD), i);
            }
        }

        display.addLine(String.format("%s%s...", TextFormat.GRAY, TextFormat.BOLD), 8);
        display.addLine(getLivesText("You", "?"), 7);
        display.addLine(" ", 6);
        display.getScoreboard().showFor(player);

    }

    protected String getTimerbarText(){
        return String.format("\n\n%s%sTime Remaining: %s%s%s", bartimerMainTextColour, TextFormat.BOLD, bartimerSubTextColour, TextFormat.BOLD, roundTimer);
    }

    protected String getLivesText(String name, String lives){
        return String.format("%s%s%s: %s%s%s %s%sLives", TextFormat.GOLD, TextFormat.BOLD, name, TextFormat.RED, TextFormat.BOLD, lives, TextFormat.DARK_RED, TextFormat.BOLD);
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
                SumoX.getPlgLogger().debug(String.format("%s * (%s ^ (%s - (%s * %s)))", SumoXConstants.KNOCKBACK_BASE, SumoXConstants.PANIC_KNOCKBACK_MULTIPLIER, getTimeElapsed(), maxTimer, SumoXConstants.BASE_TIMER_PANIC_ZONE));

                player.knockBack(event.getDamager(), 0, deltaX, deltaZ, knockbackValue);
            }
        }
    }

}
