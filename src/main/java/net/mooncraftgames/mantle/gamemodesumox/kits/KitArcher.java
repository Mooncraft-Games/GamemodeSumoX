package net.mooncraftgames.mantle.gamemodesumox.kits;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.utils.TextFormat;
import net.mooncraftgames.mantle.gamemodesumox.SumoXConstants;
import net.mooncraftgames.mantle.gamemodesumox.SumoXKeys;
import net.mooncraftgames.mantle.gamemodesumox.games.GBehaveSumoBase;
import net.mooncraftgames.mantle.newgamesapi.kits.ExtendedKit;
import net.mooncraftgames.mantle.newgamesapi.kits.Kit;

import java.util.Optional;

public class KitArcher extends Kit {

    @Override
    public void onRegister() {
        registerProperty(SumoXKeys.KIT_PROP_GIVEN_KB_MULT, String.valueOf(0.6f));
        registerProperty(SumoXKeys.KIT_PROP_TAKEN_KB_MULT, String.valueOf(1.2f));
        registerProperty(SumoXKeys.KIT_PROP_SPEED_MULT, String.valueOf(1.2f));
        registerProperty(SumoXKeys.KIT_PROP_LEAP_BONUS_WEIGHT, String.valueOf(40)); //Triple
    }

    @Override
    public String getKitID() {
        return "kb_archer";
    }

    @Override
    public String getKitDisplayName() {
        return "Archer";
    }

    @Override
    public int getCost() {
        return 1000;
    }

    @Override
    public String getKitDescription() {
        return "A kit which favours long range over short range. Armed with a bow, the archer can knockback players from a distance.";
    }

    @Override
    public Item[] getHotbarItems() {
        Item arrow =  new ItemArrow().setCustomName(""+TextFormat.GOLD+TextFormat.BOLD+"Gilded Arrow");
        arrow.setCount(64);

        Item slapItem =  new ItemBow().setCustomName(""+TextFormat.AQUA+TextFormat.BOLD+"Olly's Longbow");
        slapItem.addEnchantment(Enchantment.get(Enchantment.ID_BOW_INFINITY).setLevel(1));

        return new Item[]{
                slapItem,
                arrow
        };
    }

    @Override
    public Optional<Item> getKitHelmet() {
        ItemHelmetLeather item = new ItemHelmetLeather();
        item.setColor(54, 133, 27);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitChestplate() {
        ItemChestplateLeather item = new ItemChestplateLeather();
        item.setColor(54, 133, 27);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitLeggings() {
        ItemLeggingsLeather item = new ItemLeggingsLeather();
        item.setColor(240, 213, 146);
        return Optional.of(item);
    }

    @Override
    public Optional<Item> getKitBoots() {
        ItemBootsLeather item = new ItemBootsLeather();
        item.setColor(122, 66, 17);
        return Optional.of(item);
    }

    @Override
    public Optional<Class<? extends ExtendedKit>> getExtendedKitFeatures() {
        return Optional.of(KitArcherExtended.class);
    }

    public static class KitArcherExtended extends ExtendedKit {

        @EventHandler
        public void onDamage(EntityDamageByEntityEvent event){

            if(event.getEntity() instanceof Player){
                Player victim = (Player) event.getEntity();

                if(getGameHandler().getPlayers().contains(victim) && event.getDamager() instanceof EntityArrow){
                    EntityArrow arrow = (EntityArrow) event.getDamager();
                    Entity a = arrow.shootingEntity;

                    if(a instanceof Player){
                        Player attacker = (Player) a;
                        event.setCancelled(true);

                        if(getGameHandler().getGameBehaviors() instanceof GBehaveSumoBase){
                            GBehaveSumoBase base = (GBehaveSumoBase) getGameHandler().getGameBehaviors();
                            base.doKnockback(victim, attacker, SumoXConstants.KNOCKBACK_BASE);
                        } else {
                            GBehaveSumoBase.applyKnockback(victim, attacker, SumoXConstants.KNOCKBACK_BASE);
                        }
                    }
                }
            }
        }

    }
}
