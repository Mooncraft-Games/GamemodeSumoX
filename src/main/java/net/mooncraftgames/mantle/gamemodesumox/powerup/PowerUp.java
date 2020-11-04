package net.mooncraftgames.mantle.gamemodesumox.powerup;

public abstract class PowerUp {

    public abstract String getName();
    public abstract String getDescription();
    public abstract String getUsage();

    public abstract boolean isConsumedImmediatley();

    public abstract boolean use(PowerUpContext context);

}
