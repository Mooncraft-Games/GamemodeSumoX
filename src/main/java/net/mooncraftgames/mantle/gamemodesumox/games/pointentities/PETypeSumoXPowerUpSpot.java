package net.mooncraftgames.mantle.gamemodesumox.games.pointentities;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityGuardian;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.level.Level;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import net.mooncraftgames.mantle.gamemodesumox.SumoX;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.gamemodesumox.SumoXStrings;
import net.mooncraftgames.mantle.gamemodesumox.games.GBehaveSumoBase;
import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUp;
import net.mooncraftgames.mantle.gamemodesumox.powerup.PowerUpContext;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityCallData;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityType;
import net.mooncraftgames.mantle.newgamesapi.map.types.PointEntity;

import java.util.ArrayList;
import java.util.Random;

public class PETypeSumoXPowerUpSpot extends PointEntityType implements Listener {

    private boolean triggeredMisuseWarning = false;

    private int maxWeight;
    private PowerUp[] powerUpPool;

    private ArrayList<Entity> powerUpEntities;

    public PETypeSumoXPowerUpSpot(GameHandler gameHandler) {
        super(SumoXKeys.PE_TYPE_POWERUP, gameHandler);

        this.maxWeight = 0;
        this.powerUpPool = new PowerUp[0];

        this.powerUpEntities = new ArrayList<>();
    }

    @Override
    public void onRegister() {
        this.addFunction(SumoXKeys.PE_FUNC_POWERUP_SPAWN, this::spawnPowerUp);

        this.powerUpPool = new PowerUp[SumoXConstants.AVAILABLE_POWER_UPS.size()];
        for(int i = 0; i < SumoXConstants.AVAILABLE_POWER_UPS.size(); i++){
            try {
                Class<? extends PowerUp> c = SumoXConstants.AVAILABLE_POWER_UPS.get(i);
                PowerUp powerUp = c.getConstructor(GameHandler.class).newInstance(gameHandler);
                powerUpPool[i] = powerUp;
                maxWeight += Math.max(powerUp.getWeight(), 1);
            } catch (Exception err){
                this.maxWeight = 0;
                this.powerUpPool = new PowerUp[0];
                SumoX.getPlgLogger().error("Very Broken PowerUp: Constructor Fault.");
                return;
            }
        }

        gameHandler.getGameScheduler().registerGameTask(this::animatePowerUp, SumoXConstants.POWERUP_ANIMATE_TICK_INTERVAL, SumoXConstants.POWERUP_ANIMATE_TICK_INTERVAL);
        SumoX.get().getServer().getPluginManager().registerEvents(this, SumoX.get());
    }

    @Override
    public void onUnregister() {
        for(Entity entity: powerUpEntities){
            entity.close();
        }
        HandlerList.unregisterAll(this);
    }

    protected void animatePowerUp(){
        for(Entity entity: new ArrayList<>(powerUpEntities)){
            if(entity.isClosed()){
                powerUpEntities.remove(entity);
            } else {
                entity.setRotation(entity.getYaw() + SumoXConstants.POWERUP_ANIMATE_TICK_SPEED, entity.getPitch());
            }
        }
    }

    protected void spawnPowerUp(PointEntityCallData data){
        if(getGameHandler().getGameBehaviors() instanceof GBehaveSumoBase) {

            GBehaveSumoBase behaviours = (GBehaveSumoBase) getGameHandler().getGameBehaviors();
            PointEntity pe = data.getPointEntity();

            if(behaviours.getPowerUpPointCooldowns().containsKey(pe.getId())){
                int time = behaviours.getPowerUpPointCooldowns().get(pe.getId());
                if(time < 0){
                    // Power up still spawned. Keep there.
                    return;
                }

                // Decrement Timer
                time--;
                behaviours.getPowerUpPointCooldowns().put(pe.getId(), time);
                if (time == 0){
                    // Spawn Power up
                    spawnPowerUpEntity(pe).spawnToAll();
                }
            } else {
                behaviours.getPowerUpPointCooldowns().put(pe.getId(), generateNewTime(behaviours));
            }
        } else {
            if(!triggeredMisuseWarning) {
                this.triggeredMisuseWarning = true;
                SumoX.getPlgLogger().error("Misused powerup. Wrong Gamemode! //JellyBaboon was here.");
            }
        }
    }

    protected EntityGuardian spawnPowerUpEntity(PointEntity pe){
        Level level = getParentManager().getLevelLookup().get(pe);

        Vector3 position = pe.positionToVector3().add(!pe.isAccuratePosition() ? 0.5d : 0, 1, !pe.isAccuratePosition() ? 0.5d : 0);
        Vector2 rotation = pe.rotationToVector2();

        FullChunk chunk = level.getChunk((int)Math.floor(position.getX() / 16.0D), (int)Math.floor(position.getZ() / 16.0D), true);
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<>("Pos")
                        .add(new DoubleTag("", position.getX()))
                        .add(new DoubleTag("", position.getY()))
                        .add(new DoubleTag("", position.getZ())))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) rotation.getY()))
                        .add(new FloatTag("", (float) rotation.getX())))
                .putBoolean("npc", true)
                .putString(SumoXKeys.NBT_POWERUP_PE_TIE, pe.getId())
                .putFloat("scale", 2);

        EntityGuardian guardian = new EntityGuardian(chunk, nbt);
        guardian.setImmobile(true);
        guardian.setPositionAndRotation(position, rotation.getY(), rotation.getX());
        guardian.setNameTag(SumoXStrings.POWERUP_ENTITY_NAME);
        guardian.setNameTagVisible(true);
        guardian.setNameTagAlwaysVisible(true);
        guardian.setScale(2);

        level.addSound(position, Sound.RANDOM_EXPLODE, 0.7f, 1f, gameHandler.getPlayers());
        level.addParticleEffect(position, ParticleEffect.HUGE_EXPLOSION_LEVEL);

        powerUpEntities.add(guardian);

        return guardian;
    }

    public int generateNewTime(GBehaveSumoBase behaviours){
        Random r = new Random();
        return behaviours.getMinimumPowerUpSpawnTime() + r.nextInt(behaviours.getVariationPowerUpSpawnTime());
    }

    protected boolean runPowerUp(PowerUp powerUp, PowerUpContext context){
        if(powerUp.isConsumedImmediatley()){
            boolean result = powerUp.use(context);
            if(result){
                //TODO: Message
                context.getPlayer().getLevel().addSound(
                        context.getPlayer().getPosition(),
                        powerUp.useSound(),
                        powerUp.useSoundVolume(),
                        powerUp.useSoundPitch(),
                        gameHandler.getPlayers());
                context.getPlayer().getLevel().addParticleEffect(context.getPlayer().getPosition().add(0, 2.2, 0), ParticleEffect.BASIC_CRIT);
                return true;
            } else {
                return false;
            }
        } else {
            //TODO: Item Usage.
        }
        return false;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityHit(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Player){
            Player attacker = (Player) event.getDamager();

            if(getGameHandler().getPlayers().contains(attacker)){

                if(event.getEntity().namedTag.contains(SumoXKeys.NBT_POWERUP_PE_TIE)){

                    String s = event.getEntity().namedTag.getString(SumoXKeys.NBT_POWERUP_PE_TIE);
                    if(s != null){
                        if(getGameHandler().getGameBehaviors() instanceof GBehaveSumoBase) {
                            event.setCancelled(true);
                            int selection = new Random().nextInt(maxWeight);
                            int cumulativeWeightChecked = 1;
                            for(PowerUp entry: powerUpPool){
                                if(selection <= (cumulativeWeightChecked + entry.getWeight())){
                                    if(runPowerUp(entry, new PowerUpContext(attacker))){
                                        event.getEntity().getLevel().addSound(event.getEntity().getPosition(), Sound.MOB_WITHER_BREAK_BLOCK, 0.5f, 0.9f, gameHandler.getPlayers());
                                        event.getEntity().close();
                                        powerUpEntities.remove(event.getEntity());
                                        GBehaveSumoBase behaviours = (GBehaveSumoBase) getGameHandler().getGameBehaviors();
                                        behaviours.getPowerUpPointCooldowns().put(s, generateNewTime(behaviours));
                                    }
                                }
                                cumulativeWeightChecked += entry.getWeight();
                            }
                        }
                    }
                }
            }
        }
    }

}
