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

import java.util.HashMap;

public class GBehaveSumoBase extends GameBehavior {

    protected int maxTimer = -1;
    protected int roundTimer = -1;

    protected boolean isInPanicMode = false;

    // Game Configurables - For game flavours.
    protected boolean isTimerEnabled = true;
    protected boolean isTimerBarDisplayed = true;
    protected boolean isPanicModeAllowed = true;

    protected HashMap<Player, DummyBossBar> bartimerBossbars;
    protected TextFormat bartimerMainTextColour;
    protected TextFormat bartimerSubTextColour;
    protected BlockColor bartimerColour;

    @Override
    public void onInitialCountdownEnd() {
        this.maxTimer = Math.max(getSessionHandler().getPrimaryMapID().getIntegers().getOrDefault(SumoXKeys.SUMO_INTEGER_TIMER, SumoXConstants.BASE_TIMER_LEGNTH), 10);
        this.roundTimer = this.maxTimer;

        bartimerMainTextColour = TextFormat.BLUE;
        bartimerSubTextColour = TextFormat.DARK_AQUA;
        bartimerColour = BlockColor.BLUE_BLOCK_COLOR;

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
        }
    }

    @Override
    public void registerGameSchedulerTasks() {
        getSessionHandler().getGameScheduler().registerGameTask(this::handleTimerTick, 20, 20);
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

    protected void checkMidGameWinStatus(){

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
