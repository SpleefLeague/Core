package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
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

    private final EnumMap<ItemSlot, ItemStack> items = new EnumMap<>(ItemSlot.class);
    
    FakeEquipment(FakeEquippableCreature relatedEntity) {
        this.relatedEntity = relatedEntity;
    }
    
    public FakeEquippableCreature getRelatedEntity() {
        return relatedEntity;
    }
    
    public EnumMap<ItemSlot, ItemStack> getItems() {
        return items;
    }

    public ItemStack getMainhand() {
        return items.get(ItemSlot.MAINHAND);
    }

    public ItemStack getOffhand() {
        return items.get(ItemSlot.OFFHAND);
    }

    public ItemStack getHelmet() {
        return items.get(ItemSlot.HEAD);
    }

    public ItemStack getChestplate() {
        return items.get(ItemSlot.CHEST);
    }

    public ItemStack getLeggings() {
        return items.get(ItemSlot.LEGS);
    }

    public ItemStack getBoots() {
        return items.get(ItemSlot.FEET);
    }

    public void setMainhand(ItemStack value) {
        update(ItemSlot.MAINHAND, value);
    }

    public void setOffhand(ItemStack value) {
        update(ItemSlot.OFFHAND, value);
    }

    public void setHelmet(ItemStack value) {
        update(ItemSlot.HEAD, value);
    }

    public void setChestplate(ItemStack value) {
        update(ItemSlot.CHEST, value);
    }

    public void setLeggings(ItemStack value) {
        update(ItemSlot.LEGS, value);
    }

    public void setBoots(ItemStack value) {
        update(ItemSlot.FEET, value);
    }

    public void clearArmor() {
        for (ItemSlot fi : ItemSlot.values()) {
            if (fi != ItemSlot.MAINHAND && fi != ItemSlot.OFFHAND) {
                update(fi, null);
            }
        }
    }

    public void clearAll() {
        for (ItemSlot fi : ItemSlot.values()) {
            update(fi, null);
        }
    }

    public void show(Player p) {
        for (ItemSlot fi : ItemSlot.values()) {
            ItemStack is = items.get(fi);
            if (is == null || is.getType() == Material.AIR) {
                continue;
            }
            WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
            wrapper.setEntityID(relatedEntity.getId());
            wrapper.setItem(items.get(fi));
            wrapper.setSlot(fi);
            wrapper.sendPacket(p);
        }
    }

    public void update(ItemSlot slot, ItemStack value) {
        relatedEntity.validate();
        updateUnsafe(slot, value);
    }

    public void updateUnsafe(ItemSlot slot, ItemStack value) {
        if (value == null) {
            value = new ItemStack(Material.AIR, 1);
        }
        if (value.getType() == Material.AIR) {
            items.remove(slot);
        } else {
            items.put(slot, value);
        }
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
        wrapper.setEntityID(relatedEntity.getId());
        wrapper.setItem(value);
        wrapper.setSlot(slot);
        relatedEntity.getAffectedPlayers().forEach(wrapper::sendPacket);
    }
}