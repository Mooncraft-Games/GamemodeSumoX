package net.mooncraftgames.mantle.gamemodesumox.kits;

import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.newgamesapi.kits.Kit;

import java.util.Optional;

public class KitTimelord extends Kit {

    @Override
    public void onRegister() {
        registerProperty(SumoXKeys.KIT_PROP_GIVEN_KB_MULT, String.valueOf(0.9f));
        registerProperty(SumoXKeys.KIT_PROP_TAKEN_KB_MULT, String.valueOf(1.2f));
        registerProperty(SumoXKeys.KIT_PROP_RECALL_TIME, String.valueOf(SumoXConstants.POWERUP_RECALL_MAX_HISTORY + 5));
    }

    @Override
    public String getKitID() {
        return "timelord";
    }

    @Override
    public String getKitDisplayName() {
        return "Timelord";
    }

    @Override
    public int getCost() {
        return 1000;
    }

    @Override
    public String getKitDescription() {
        return "A kit which tweaks the balance of powerups to grant more recalls & time-shifts with longer warp times.";
    }

    @Override
    public Item[] getKitItems() {
        Item slapItem =  new ItemClock().setCustomName(""+TextFormat.LIGHT_PURPLE+TextFormat.BOLD+"The Wibbly Wobbly Timey Wimey");
        slapItem.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL).setLevel(1));
        return new Item[]{
           slapItem
        };
    }

    @Override
    public Optional<Item> getKitChestplate() {
        ItemChestplateLeather item = new ItemChestplateLeather();
        item.setColor(122, 66, 17);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitLeggings() {
        ItemLeggingsLeather item = new ItemLeggingsLeather();
        item.setColor(150, 150, 150);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitBoots() {
        ItemBootsLeather item = new ItemBootsLeather();
        item.setColor(20, 20, 20);
        return Optional.of(item);
    }
}
