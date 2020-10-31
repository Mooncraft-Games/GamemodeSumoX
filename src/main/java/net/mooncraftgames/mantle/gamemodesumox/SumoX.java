package net.mooncraftgames.mantle.gamemodesumox;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import net.mooncraftgames.mantle.gamemodesumox.games.GBehaveSumoBase;
import net.mooncraftgames.mantle.gamemodesumox.kits.KitSlapper;
import net.mooncraftgames.mantle.newgamesapi.game.GameHandler;
import net.mooncraftgames.mantle.newgamesapi.game.GameID;
import net.mooncraftgames.mantle.newgamesapi.game.GameProperties;
import net.mooncraftgames.mantle.newgamesapi.kits.KitGroup;
import net.mooncraftgames.mantle.newgamesapi.registry.GameRegistry;
import net.mooncraftgames.mantle.newgamesapi.registry.KitRegistry;

public class SumoX extends PluginBase {

    public static SumoX sumoxinstance;

    @Override
    public void onEnable() {
        sumoxinstance = this;

        KitRegistry.get().registerKitGroup(new KitGroup("sumox", new KitSlapper()));

        GameProperties sumoProperties = new GameProperties(GameHandler.AutomaticWinPolicy.MANUAL_CALLS_ONLY)
                .setCanPlayersMoveDuringCountdown(false)
                .setCanWorldBeManipulated(false)
                .setDefaultCountdownLength(10)
                .setMinimumPlayers(2)
                .setGuidelinePlayers(6)
                .setMaximumPlayers(16);
        GameID sumoID = new GameID("sumox", "sumox", "Sumo X", "The all new Sumo! Slap players off the platform till they run out of lives! The last standing wins each round!", "sumox", new String[]{"sumo", "sumox"}, 2, sumoProperties, GBehaveSumoBase.class);

        GameRegistry.get().registerGame(sumoID);
    }

    public static SumoX get() { return sumoxinstance; }
    public static PluginLogger getPlgLogger(){ return get().getLogger(); }
}
