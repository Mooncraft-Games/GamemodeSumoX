package net.mooncraftgames.mantle.gamemodesumox.kits;

import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.newgamesapi.kits.Kit;

import java.util.Optional;

public class KitSlapper extends Kit {

    @Override
    public void onRegister() {
        registerProperty(SumoXKeys.KIT_PROP_GIVEN_KB_MULT, String.valueOf(1.2f));
    }

    @Override
    public String getKitID() {
        return "slapper";
    }

    @Override
    public String getKitDisplayName() {
        return "Slapper";
    }

    @Override
    public int getCost() {
        return 0;
    }

    @Override
    public String getKitDescription() {
        return "A basic kit, the slapper does more knockback than the others kits at the cost of no easy way to recover.";
    }

    @Override
    public Item[] getKitItems() {
        Item slapItem =  new ItemFish().setCustomName(""+TextFormat.GREEN+TextFormat.BOLD+"Ol' Slappy");
        slapItem.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL).setLevel(1));
        return new Item[]{
           slapItem
        };
    }

    @Override
    public Optional<Item> getKitChestplate() {
        ItemChestplateLeather item = new ItemChestplateLeather();
        item.setColor(255, 255, 255);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitLeggings() {
        ItemLeggingsLeather item = new ItemLeggingsLeather();
        item.setColor(255, 0, 0);
        return Optional.of(item);
    }

}
