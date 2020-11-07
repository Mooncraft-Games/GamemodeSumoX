package net.mooncraftgames.mantle.gamemodesumox.powerup;

import cn.nukkit.Player;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Sound;
import cn.nukkit.math.Vector3;
import net.mooncraftgames.mantle.gamemodesumox.SumoX;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class PowerUpRecall extends PowerUp {

    protected HashMap<Player, ArrayList<Vector3>> recallLists = new HashMap<>();

    public PowerUpRecall(GameHandler gameHandler) {
        super(gameHandler);
        gameHandler.getGameScheduler().registerGameTask(this::onSecondTick, 0 , SumoXConstants.POWERUP_RECALL_TICK_FRAMES);
    }

    @Override
    public String getName() {
        return "Recall";
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
        return 500;
    }

    @Override
    public Integer getItemID() {
        return ItemID.ENDER_PEARL;
    }

    @Override
    public boolean isConsumedImmediatley() {
        return false;
    }

    @Override
    public boolean use(PowerUpContext context) {
        if(recallLists.containsKey(context.getPlayer())){
            ArrayList<Vector3> posHistory = recallLists.get(context.getPlayer());
            context.getPlayer().teleport(posHistory.get(Math.min(SumoXConstants.POWERUP_RECALL_MAX_HISTORY, posHistory.size()) - 1));
        }
        return true;
    }

    @Override
    public void cleanUp() { }

    public void onSecondTick(){
        for(Player player: gameHandler.getPlayers()){
            if(!recallLists.containsKey(player)){
                recallLists.put(player, new ArrayList<>());
            }
            ArrayList<Vector3> positions = recallLists.get(player);
            positions.add(0, player.getPosition());

            int originalSize = positions.size();
            for(int i = SumoXConstants.POWERUP_RECALL_MAX_HISTORY; i < originalSize; i++){
                positions.remove(SumoXConstants.POWERUP_RECALL_MAX_HISTORY);
            }
        }
    }

}
