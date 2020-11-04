package net.mooncraftgames.mantle.gamemodesumox.games.pointentities;

import net.mooncraftgames.mantle.gamemodesumox.SumoX;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.gamemodesumox.games.GBehaveSumoBase;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityCallData;
import net.mooncraftgames.mantle.newgamesapi.map.pointentities.PointEntityType;
import net.mooncraftgames.mantle.newgamesapi.map.types.PointEntity;

import java.util.Random;

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
            if (behaviours.arePowerUpsAllowed()) {
                PointEntity pe = data.getPointEntity();
                if(behaviours.getPowerUpPointCooldowns().containsKey(pe.getId())){

                } else {
                    Random r = new Random();
                    int calcTime = behaviours.getMinimumPowerUpSpawnTime() + r.nextInt(behaviours.getVariationPowerUpSpawnTime());
                }
            }
        } else {
            if(!triggeredMisuseWarning) {
                this.triggeredMisuseWarning = true;
                SumoX.getPlgLogger().error("Misused powerup. Wrong Gamemode! //JellyBaboon was here.");
            }
        }
    }
}
