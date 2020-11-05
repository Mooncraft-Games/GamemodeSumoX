package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;

public class PowerUpBlindness extends PowerUp{

    public PowerUpBlindness(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public String getName() {
        return "AAHHHH MY EYES";
    }

    @Override
    public String getDescription() {
        return "Makes you blind <3 :)";
    }

    @Override
    public String getUsage() {
        return "You are now blind for 10 seconds. :)";
    }

    @Override
    public Sound useSound() {
        return Sound.MOB_WITHER_SPAWN;
    }

    @Override
    public float useSoundPitch() {
        return 0.8f;
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
        context.getPlayer().addEffect(Effect.getEffect(Effect.BLINDNESS).setDuration(20*10));
        return true;
    }

    @Override
    public void cleanUp() { }

}
