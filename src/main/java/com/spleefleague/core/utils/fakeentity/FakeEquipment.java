package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import java.util.EnumMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeEquipment {
    
    private final FakeEquippableCreature relatedEntity;

    private final EnumMap<FakeItem, ItemStack> items = new EnumMap<>(FakeItem.class);
    
    FakeEquipment(FakeEquippableCreature relatedEntity) {
        this.relatedEntity = relatedEntity;
    }
    
    public FakeEquippableCreature getRelatedEntity() {
        return relatedEntity;
    }
    
    public EnumMap<FakeItem, ItemStack> getItems() {
        return items;
    }

    public ItemStack getHand() {
        return items.get(FakeItem.HAND);
    }

    public ItemStack getHelmet() {
        return items.get(FakeItem.HELMET);
    }

    public ItemStack getChestplate() {
        return items.get(FakeItem.CHESTPLATE);
    }

    public ItemStack getLeggings() {
        return items.get(FakeItem.LEGGINGS);
    }

    public ItemStack getBoots() {
        return items.get(FakeItem.BOOTS);
    }

    public void setHand(ItemStack value) {
        update(FakeItem.HAND, value);
    }

    public void setHelmet(ItemStack value) {
        update(FakeItem.HELMET, value);
    }

    public void setChestplate(ItemStack value) {
        update(FakeItem.CHESTPLATE, value);
    }

    public void setLeggings(ItemStack value) {
        update(FakeItem.LEGGINGS, value);
    }

    public void setBoots(ItemStack value) {
        update(FakeItem.BOOTS, value);
    }

    public void clearArmor() {
        for (FakeItem fi : FakeItem.values()) {
            if (fi != FakeItem.HAND) {
                update(fi, null);
            }
        }
    }

    public void clearAll() {
        for (FakeItem fi : FakeItem.values()) {
            update(fi, null);
        }
    }

    public void show(Player p) {
        for (FakeItem fi : FakeItem.values()) {
            ItemStack is = items.get(fi);
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
            wrapper.setEntityID(relatedEntity.getId());
            wrapper.setItem(items.get(fi));
            wrapper.setSlot(fi.getSlot());
            wrapper.sendPacket(p);
        }
    }

    public void update(FakeItem type, ItemStack value) {
        relatedEntity.validate();
        updateUnsafe(type, value);
    }

    public void updateUnsafe(FakeItem type, ItemStack value) {
        if (value == null) {
            value = new ItemStack(Material.AIR, 1);
        }
        if (value.getType() == Material.AIR) {
            items.remove(type);
        } else {
            items.put(type, value);
        }
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
        wrapper.setEntityID(relatedEntity.getId());
        wrapper.setItem(value);
        wrapper.setSlot(type.getSlot());
        relatedEntity.getAffectedPlayers().forEach(wrapper::sendPacket);
    }
    
    public enum FakeItem {
        
        HAND(0),
        HELMET(4),
        CHESTPLATE(3),
        LEGGINGS(2),
        BOOTS(1);

        private final int slot;
        
        public int getSlot() {
            return slot;
        }
        
        private FakeItem(int slot) {
            this.slot = slot;
        }
        
    }
    
}