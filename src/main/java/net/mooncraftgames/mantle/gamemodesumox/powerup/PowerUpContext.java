package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.Player;

public class PowerUpContext {

    protected Player player;

    public PowerUpContext(Player player){
        this.player = player;
    }

    public Player getPlayer() { return player; }
}
