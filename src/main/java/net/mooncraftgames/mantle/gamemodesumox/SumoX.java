package net.mooncraftgames.mantle.gamemodesumox;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;
import net.mooncraftgames.mantle.gamemodesumox.games.GBehaveSumoBase;
import net.mooncraftgames.mantle.gamemodesumox.kits.KitArcher;
import net.mooncraftgames.mantle.gamemodesumox.kits.KitRunner;
import net.mooncraftgames.mantle.gamemodesumox.kits.KitSlapper;
import net.mooncraftgames.mantle.gamemodesumox.kits.KitTimelord;
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

        KitRegistry.get().registerKitGroup(new KitGroup("sumox", "Sumo X", true, new KitSlapper(), new KitRunner(), new KitTimelord(), new KitArcher()));

        GameProperties sumoBrawlProperties = new GameProperties(GameHandler.AutomaticWinPolicy.MANUAL_CALLS_ONLY)
                .setCanPlayersMoveDuringCountdown(false)
                .setCanWorldBeManipulated(false)
                .setDefaultCountdownLength(10)
                .setMinimumPlayers(2)
                .setGuidelinePlayers(2)
                .setMaximumPlayers(4);
        GameProperties sumoMegaProperties = new GameProperties(GameHandler.AutomaticWinPolicy.MANUAL_CALLS_ONLY)
                .setCanPlayersMoveDuringCountdown(false)
                .setCanWorldBeManipulated(false)
                .setDefaultCountdownLength(10)
                .setMinimumPlayers(4)
                .setGuidelinePlayers(8)
                .setMaximumPlayers(32);

        GameID sumoBrawlID = new GameID("sumox_brawl", "sumobrawl", "Sumo X Brawl", "The all new Sumo! Slap up to 4 players off the platform till they run out of lives! The last standing wins each round!", "sumox", new String[]{"sumo", "sumox"}, 2, sumoBrawlProperties, GBehaveSumoBase.class);
        GameID sumoMegaID = new GameID("sumox_mega", "sumomega", "Sumo X Mega", "The all new Sumo! Slap up to 32 players off the platform till they run out of lives! The last standing wins each round!", "sumox", new String[]{"sumo", "sumox"}, 2, sumoMegaProperties, GBehaveSumoBase.class);

        GameRegistry.get()
                .registerGame(sumoBrawlID)
                .registerGame(sumoMegaID)
        ;
    }

    public static SumoX get() { return sumoxinstance; }
    public static PluginLogger getPlgLogger(){ return get().getLogger(); }
}
