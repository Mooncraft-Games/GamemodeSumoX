package net.mooncraftgames.mantle.gamemodesumox;

import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.newgamesapi.Utility;

public class SumoXStrings {

    public static final String PANIC_TITLE = String.format("%s%s/!\\ %s%sPANIC MODE %s%s/!\\", TextFormat.DARK_RED, TextFormat.BOLD, TextFormat.RED, TextFormat.BOLD, TextFormat.DARK_RED, TextFormat.BOLD);
    public static final String PANIC_SUBTITILE = String.format("%s%sWarning! Knockback is increasing!", TextFormat.GOLD, TextFormat.BOLD);
    public static final String PANIC_MESSAGE = Utility.generateServerMessage("!!!", TextFormat.DARK_RED, String.format("Panic Mode! &sKnockback is increasing!", TextFormat.GOLD), TextFormat.RED);

}
