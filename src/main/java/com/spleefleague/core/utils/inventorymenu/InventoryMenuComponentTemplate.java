package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.function.Dynamic;
import com.spleefleague.core.utils.function.DynamicDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponentTemplate<C> {

    //private InventoryMenuTemplate parent;
    private Dynamic<ItemStack> displayItem;
    private Dynamic<String> displayName;
    private Dynamic<Material> displayIcon;
    private Dynamic<Integer> displayNumber;
    private Dynamic<List<String>> displayDescription;
    private Dynamic<Boolean> visibilityController;
    private Dynamic<Boolean> accessController;

    protected InventoryMenuComponentTemplate() {
        this.displayItem = Dynamic.getConstant(new ItemStack(Material.STONE));
        this.displayName = Dynamic.getConstant("");
        this.visibilityController = Dynamic.getConstant(true);
        this.accessController = (SLPlayer slp) -> slp.getRank().hasPermission(Rank.DEFAULT);
        this.displayIcon = Dynamic.getConstant(Material.STONE);
        this.displayNumber = Dynamic.getConstant(1);
        this.displayDescription = Dynamic.getConstant(new ArrayList<>()); //Always returns the same(!) object
    }

    public abstract C construct(SLPlayer slp);

    public String getDisplayName(SLPlayer slp) {
        return displayName.get(slp);
    }

    protected ItemStackWrapper getDisplayItemStackWrapper() {
        return constructDisplayItem();
    }

    public ItemStack getDisplayItemStack(SLPlayer slp) {
        return getDisplayItemStackWrapper().construct(slp);
    }

    protected ItemStack getDisplayItem(SLPlayer slp) {
        return displayItem.get(slp);
    }

    public Material getDisplayIcon(SLPlayer slp) {
        return displayIcon.get(slp);
    }

    public int getDisplayNumber(SLPlayer slp) {
        return displayNumber.get(slp);
    }

    public List<String> getDisplayDescription(SLPlayer slp) {
        return displayDescription.get(slp);
    }

    public List<String> getDisplayDescription() {
        return displayDescription.get(null);
    }

    public boolean isVisible(SLPlayer slp) {
        return visibilityController.get(slp);
    }

    protected Dynamic<Boolean> getVisibilityController() {
        return visibilityController;
    }

    public boolean hasAccess(SLPlayer slp) {
        return accessController.get(slp);
    }

    protected Dynamic<Boolean> getAccessController() {
        return accessController;
    }

    protected ItemStackWrapper constructDisplayItem() {
        ItemStackWrapper wrapper = new ItemStackWrapper(displayItem, displayIcon, displayName, displayNumber, displayDescription);
        return wrapper;
    }

    protected void setDisplayItem(ItemStack displayItem) {
        this.displayItem = Dynamic.getConstant(displayItem);
    }

    protected void setDisplayItem(Dynamic<ItemStack> displayItem) {
        this.displayItem = displayItem;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = Dynamic.getConstant(displayName);
    }

    protected void setDisplayName(Dynamic<String> displayName) {
        this.displayName = displayName;
    }

    protected void setDisplayIcon(Material displayIcon) {
        this.displayIcon = Dynamic.getConstant(displayIcon);
    }

    protected void setDisplayIcon(Dynamic<Material> displayIcon) {
        this.displayIcon = displayIcon;
    }

    protected void setDisplayNumber(int displayNumber) {
        this.displayNumber = Dynamic.getConstant(displayNumber);
    }

    protected void setDisplayNumber(Dynamic<Integer> displayNumber) {
        this.displayNumber = displayNumber;
    }

    protected void setVisibilityController(Dynamic<Boolean> visibilityController) {
        this.visibilityController = visibilityController;
    }

    protected void setAccessController(Dynamic<Boolean> accessController) {
        this.accessController = accessController;
    }

    protected void addDescriptionLine(String line) {
        this.getDisplayDescription().add(line);
    }

    protected void addDescriptionLine(SLPlayer slp, String line) {
        if (displayDescription instanceof DynamicDefault) {
            displayDescription = new Dynamic<List<String>>() {
                private final Map<UUID, List<String>> map = new HashMap<>();
                private final ArrayList<String> oldDefault = (ArrayList) displayDescription.get(null);

                @Override
                public List<String> get(SLPlayer slp) {
                    List<String> result;
                    if (slp == null) {
                        result = oldDefault;
                    } else if (map.containsKey(slp.getUniqueId())) {
                        result = map.get(slp.getUniqueId());
                    } else {
                        result = (List<String>) oldDefault.clone();
                        map.put(slp.getUniqueId(), result);
                    }
                    return result;
                }
            };
        }
        this.getDisplayDescription(slp).add(line);
    }

    protected void setDescription(Dynamic<List<String>> displayDescription) {
        this.displayDescription = displayDescription;
    }
}
