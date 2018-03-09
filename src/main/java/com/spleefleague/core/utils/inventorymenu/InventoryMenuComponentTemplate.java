package com.spleefleague.core.utils.inventorymenu;

import com.sk89q.worldedit.internal.expression.runtime.Function.Dynamic;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponentTemplate<C> {

    //private InventoryMenuTemplate parent;
    private Function<SLPlayer, ItemStack> displayItem;
    private Function<SLPlayer, String> displayName;
    private Function<SLPlayer, Material> displayIcon;
    private Function<SLPlayer, Integer> displayNumber;
    private Function<SLPlayer, List<String>> displayDescription;
    private Function<SLPlayer, Boolean> visibilityController;
    private Function<SLPlayer, Boolean> accessController;
    private boolean overwritePageBehavior;
    
    protected InventoryMenuComponentTemplate() {
        this.overwritePageBehavior = false;
        this.displayItem = s -> null;
        this.displayName = s -> null;
        this.visibilityController = s -> true;
        this.accessController = slp -> slp.getRank().hasPermission(Rank.DEFAULT);
        this.displayIcon = s -> null;
        this.displayNumber = s -> 1;
        this.displayDescription = s -> new ArrayList<>();
    }

    public abstract C construct(SLPlayer slp);

    public String getDisplayName(SLPlayer slp) {
        return displayName.apply(slp);
    }

    protected ItemStackWrapper getDisplayItemStackWrapper() {
        return constructDisplayItem();
    }

    public ItemStack getDisplayItemStack(SLPlayer slp) {
        return getDisplayItemStackWrapper().construct(slp);
    }

    protected ItemStack getDisplayItem(SLPlayer slp) {
        return displayItem.apply(slp);
    }

    public Material getDisplayIcon(SLPlayer slp) {
        return displayIcon.apply(slp);
    }

    public int getDisplayNumber(SLPlayer slp) {
        return displayNumber.apply(slp);
    }

    public List<String> getDisplayDescription(SLPlayer slp) {
        return displayDescription.apply(slp);
    }

    public List<String> getDisplayDescription() {
        return displayDescription.apply(null);
    }

    public boolean isVisible(SLPlayer slp) {
        return visibilityController.apply(slp);
    }

    protected Function<SLPlayer, Boolean> getVisibilityController() {
        return visibilityController;
    }
    
    protected boolean getOverwritePageBehavior() {
        return overwritePageBehavior;
    }

    public boolean hasAccess(SLPlayer slp) {
        return accessController.apply(slp);
    }

    protected Function<SLPlayer, Boolean> getAccessController() {
        return accessController;
    }

    protected ItemStackWrapper constructDisplayItem() {
        ItemStackWrapper wrapper = new ItemStackWrapper(displayItem, displayIcon, displayName, displayNumber, displayDescription);
        return wrapper;
    }
    
    protected void setOverwritePageBehavoir(boolean overwritePageBehavior) {
        this.overwritePageBehavior = overwritePageBehavior;
    }
    
    protected void setDisplayItem(ItemStack displayItem) {
        setDisplayItem(s -> displayItem);
    }

    protected void setDisplayItem(Function<SLPlayer, ItemStack> displayItem) {
        this.displayItem = displayItem;
    }

    protected void setDisplayName(String displayName) {
        this.displayName = s -> displayName;
    }

    protected void setDisplayName(Function<SLPlayer, String> displayName) {
        this.displayName = displayName;
    }

    protected void setDisplayIcon(Material displayIcon) {
        this.displayIcon = s -> displayIcon;
    }

    protected void setDisplayIcon(Function<SLPlayer, Material> displayIcon) {
        this.displayIcon = displayIcon;
    }

    protected void setDisplayNumber(int displayNumber) {
        this.displayNumber = s -> displayNumber;
    }

    protected void setDisplayNumber(Function<SLPlayer, Integer> displayNumber) {
        this.displayNumber = displayNumber;
    }

    protected void setVisibilityController(Function<SLPlayer, Boolean> visibilityController) {
        this.visibilityController = visibilityController;
    }

    protected void setAccessController(Function<SLPlayer, Boolean> accessController) {
        this.accessController = accessController;
    }

    protected void addDescriptionLine(SLPlayer slp, String line) {
        displayDescription = new Function<SLPlayer, List<String>>() {
            private final Map<UUID, List<String>> map = new HashMap<>();
            private final ArrayList<String> oldDefault = (ArrayList) displayDescription.apply(null);

            @Override
            public List<String> apply(SLPlayer slp) {
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
        displayDescription.apply(slp).add(line);
    }

    protected void setDescription(Function<SLPlayer, List<String>> displayDescription) {
        this.displayDescription = displayDescription;
    }
}
