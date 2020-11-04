package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Sound;
import net.mooncraftgames.mantle.gamemodesumox.SumoX;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;

import java.util.HashMap;

public final class PowerUpImmunity extends PowerUp implements Listener {

    private HashMap<Player, Integer> immunityPowerUps;

    public PowerUpImmunity(GameHandler gameHandler) {
        super(gameHandler);
        this.immunityPowerUps = new HashMap<>();

        SumoX.get().getServer().getPluginManager().registerEvents(this, SumoX.get());
    }

    @Override
    public String getName() {
        return "Immunity";
    }

    @Override
    public String getDescription() {
        return "Makes you immune to all damage for 7 seconds.";
    }

    @Override
    public String getUsage() {
        return "You are now immune for 7 seconds.";
    }

    @Override
    public Sound useSound() {
        return Sound.BLOCK_END_PORTAL_SPAWN;
    }

    @Override
    public float useSoundPitch() {
        return 0.9f;
    }

    @Override
    public int getWeight() {
        return 10;
    }

    @Override
    public boolean isConsumedImmediatley() {
        return true;
    }

    @Override
    public boolean use(PowerUpContext context) {
        if(immunityPowerUps.containsKey(context.getPlayer())){
            int oldVal = immunityPowerUps.get(context.getPlayer());
            immunityPowerUps.put(context.getPlayer(), oldVal + 1);
        } else {
            immunityPowerUps.put(context.getPlayer(), 1);
        }

        gameHandler.getGameScheduler().registerGameTask(() -> {
            if(immunityPowerUps.containsKey(context.getPlayer())){
                int oldVal = immunityPowerUps.get(context.getPlayer());
                immunityPowerUps.put(context.getPlayer(), oldVal - 1);
            }
        }, 20*7);

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
    public void getJellyBaboon(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            if(gameHandler.getPlayers().contains(player)){
                if(immunityPowerUps.containsKey(player) && immunityPowerUps.get(player) > 0) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void cleanUp() {
        HandlerList.unregisterAll(this);
    }
}
