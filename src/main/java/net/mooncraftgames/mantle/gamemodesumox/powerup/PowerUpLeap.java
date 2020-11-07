package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.Player;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.potion.Effect;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;

public class PowerUpLeap extends PowerUp {

    public PowerUpLeap(GameHandler gameHandler) {
        super(gameHandler);
    }

    @Override
    public String getName() {
        return "Leap";
    }

    @Override
    public String getDescription() {
        return "Allows you to leap in a specific direction.";
    }

    @Override
    public String getUsage() {
        return "Face a direction and tap on the ground. You'll go 'weeeee'";
    }

    @Override
    public Sound useSound() {
        return Sound.BLOCK_BEEHIVE_ENTER;
    }

    @Override
    public float useSoundPitch() {
        return 1f;
    }

    @Override
    public int getWeight() {
        return 15;
    }

    @Override
    public Integer getItemID() {
        return ItemID.FEATHER;
    }

    @Override
    public boolean isConsumedImmediatley() {
        return false;
    }

    @Override
    public boolean use(PowerUpContext context) {
        Player p = context.getPlayer();
        Vector3 dir = p.getDirectionVector();
        p.setMotion(new Vector3(dir.x, Math.abs(dir.y), dir.z).multiply(1.7f));
        return true;
    }

    @Override
    public void cleanUp() { }

}
