package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.level.Sound;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;

public abstract class PowerUp {

    protected GameHandler gameHandler;

    public PowerUp (GameHandler gameHandler){
        this.gameHandler = gameHandler;
    }

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();

    public abstract Sound useSound();
    public float useSoundPitch() { return 1f; }
    public float useSoundVolume() { return 0.5f; }

    public abstract int getWeight();

    public abstract Integer getItemID();

    public abstract boolean isConsumedImmediatley();

    public abstract boolean use(PowerUpContext context);
    public abstract void cleanUp();

}
