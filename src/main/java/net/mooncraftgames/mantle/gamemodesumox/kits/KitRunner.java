package net.mooncraftgames.mantle.gamemodesumox.kits;

import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.newgamesapi.kits.Kit;

import java.util.Optional;

public class KitRunner extends Kit {

    @Override
    public void onRegister() {
        registerProperty(SumoXKeys.KIT_PROP_GIVEN_KB_MULT, String.valueOf(0.8f));
        registerProperty(SumoXKeys.KIT_PROP_TAKEN_KB_MULT, String.valueOf(1.6f));
        registerProperty(SumoXKeys.KIT_PROP_SPEED_MULT, String.valueOf(1.5f));
    }

    @Override
    public String getKitID() {
        return "runner";
    }

    @Override
    public String getKitDisplayName() {
        return "Sprinter";
    }

    @Override
    public int getCost() {
        return 500;
    }

    @Override
    public String getKitDescription() {
        return "A low-tier kit that offers a significant speed boost but takes more knockback and has weaker punches";
    }

    @Override
    public Item[] getKitItems() {
        Item slapItem =  new ItemClownfish().setCustomName(""+TextFormat.GOLD+TextFormat.BOLD+"Trendy Terry");
        slapItem.addEnchantment(Enchantment.get(Enchantment.ID_PROTECTION_ALL).setLevel(1));
        return new Item[]{
           slapItem
        };
    }

    @Override
    public Optional<Item> getKitHelmet() {
        ItemHelmetLeather item = new ItemHelmetLeather();
        item.setColor(255, 0, 0);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitChestplate() {
        ItemChestplateLeather item = new ItemChestplateLeather();
        item.setColor(0, 0, 255);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitLeggings() {
        ItemLeggingsLeather item = new ItemLeggingsLeather();
        item.setColor(0, 0, 255);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitBoots() {
        return Optional.of(new ItemBootsGold());
    }
}
