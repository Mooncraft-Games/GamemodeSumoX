package net.mooncraftgames.mantle.gamemodesumox.games.pointentities;

import cn.nukkit.entity.mob.EntityGuardian;
import cn.nukkit.level.Level;
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
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityCallData;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityType;
import net.mooncraftgames.mantle.newgamesapi.map.types.PointEntity;

import java.util.Random;
import java.util.UUID;

public class PETypeSumoXPowerUpSpot extends PointEntityType {

    private boolean triggeredMisuseWarning = false;

    public PETypeSumoXPowerUpSpot(GameHandler gameHandler) {
        super(SumoXKeys.PE_TYPE_POWERUP, gameHandler);
    }

    @Override
    public void onRegister() {
        this.addFunction(SumoXKeys.PE_FUNC_POWERUP_SPAWN, this::spawnPowerUp);
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
                    spawnPowerUpEntity(pe);
                }
            } else {
                Random r = new Random();
                int calcTime = behaviours.getMinimumPowerUpSpawnTime() + r.nextInt(behaviours.getVariationPowerUpSpawnTime());
                behaviours.getPowerUpPointCooldowns().put(pe.getId(), calcTime);
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

        return guardian;
    }

}
