package net.mooncraftgames.mantle.gamemodesumox;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.PluginLogger;

public class SumoX extends PluginBase {

    public static SumoX sumoxinstance;

    @Override
    public void onEnable() {
        sumoxinstance = this;
    }

    public static SumoX get() { return sumoxinstance; }
    public static PluginLogger getPlgLogger(){ return get().getLogger(); }
}
