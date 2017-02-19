package com.spleefleague.core.utils.fakeentity;

import com.comphenix.packetwrapper.WrapperPlayServerEntityEquipment;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.spleefleague.core.utils.fakeentity.FakeEquippableCreature;
import java.lang.invoke.LambdaMetafactory;
import java.util.Collection;
import java.util.EnumMap;
import java.util.function.Consumer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FakeEquipment {
    private final FakeEquippableCreature relatedEntity;
    private final EnumMap<EnumWrappers.ItemSlot, ItemStack> items = new EnumMap(EnumWrappers.ItemSlot.class);

    FakeEquipment(FakeEquippableCreature relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public FakeEquippableCreature getRelatedEntity() {
        return this.relatedEntity;
    }

    public EnumMap<EnumWrappers.ItemSlot, ItemStack> getItems() {
        return this.items;
    }

    public ItemStack getMainhand() {
        return this.items.get((Object)EnumWrappers.ItemSlot.MAINHAND);
    }

    public ItemStack getOffhand() {
        return this.items.get((Object)EnumWrappers.ItemSlot.OFFHAND);
    }

    public ItemStack getHelmet() {
        return this.items.get((Object)EnumWrappers.ItemSlot.HEAD);
    }

    public ItemStack getChestplate() {
        return this.items.get((Object)EnumWrappers.ItemSlot.CHEST);
    }

    public ItemStack getLeggings() {
        return this.items.get((Object)EnumWrappers.ItemSlot.LEGS);
    }

    public ItemStack getBoots() {
        return this.items.get((Object)EnumWrappers.ItemSlot.FEET);
    }

    public void setMainhand(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.MAINHAND, value);
    }

    public void setOffhand(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.OFFHAND, value);
    }

    public void setHelmet(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.HEAD, value);
    }

    public void setChestplate(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.CHEST, value);
    }

    public void setLeggings(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.LEGS, value);
    }

    public void setBoots(ItemStack value) {
        this.update(EnumWrappers.ItemSlot.FEET, value);
    }

    public void clearArmor() {
        for (EnumWrappers.ItemSlot fi : EnumWrappers.ItemSlot.values()) {
            if (fi == EnumWrappers.ItemSlot.MAINHAND || fi == EnumWrappers.ItemSlot.OFFHAND) continue;
            this.update(fi, null);
        }
    }

    public void clearAll() {
        for (EnumWrappers.ItemSlot fi : EnumWrappers.ItemSlot.values()) {
            this.update(fi, null);
        }
    }

    public void show(Player p) {
        for (EnumWrappers.ItemSlot fi : EnumWrappers.ItemSlot.values()) {
            ItemStack is = this.items.get(fi);
            if (is == null || is.getType() == Material.AIR) continue;
            WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
            wrapper.setEntityID(this.relatedEntity.getId());
            wrapper.setItem(this.items.get(fi));
            wrapper.setSlot(fi);
            wrapper.sendPacket(p);
        }
    }

    public void update(EnumWrappers.ItemSlot slot, ItemStack value) {
        this.relatedEntity.validate();
        this.updateUnsafe(slot, value);
    }

    public void updateUnsafe(EnumWrappers.ItemSlot slot, ItemStack value) {
        if (value == null) {
            value = new ItemStack(Material.AIR, 1);
        }
        if (value.getType() == Material.AIR) {
            this.items.remove(slot);
        } else {
            this.items.put(slot, value);
        }
        WrapperPlayServerEntityEquipment wrapper = new WrapperPlayServerEntityEquipment();
        wrapper.setEntityID(this.relatedEntity.getId());
        wrapper.setItem(value);
        wrapper.setSlot(slot);
        this.relatedEntity.getAffectedPlayers().forEach(wrapper::sendPacket);
    }
}