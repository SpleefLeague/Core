package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Material;

import java.util.List;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponentTemplateBuilder<C, T extends InventoryMenuComponentTemplate<C>, B extends InventoryMenuComponentTemplateBuilder<C, T, B>> {

    //Needed for super fancy Builder inheritance
    protected B actualBuilder;
    protected T buildingObj;

    protected abstract B getThis();

    protected abstract T getObj();

    public InventoryMenuComponentTemplateBuilder() {
        actualBuilder = getThis();
        buildingObj = getObj();
    }

    public B displayName(String displayName) {
        buildingObj.setDisplayName(displayName);
        return actualBuilder;
    }

    public B displayName(Function<SLPlayer, String> displayName) {
        buildingObj.setDisplayName(displayName);
        return actualBuilder;
    }

    public B displayIcon(Material displayIcon) {
        buildingObj.setDisplayIcon(s -> displayIcon);
        return actualBuilder;
    }

    public B displayIcon(Function<SLPlayer, Material> displayIcon) {
        buildingObj.setDisplayIcon(displayIcon);
        return actualBuilder;
    }

    public B displayItem(ItemStack displayItem) {
        buildingObj.setDisplayItem(displayItem);
        return actualBuilder;
    }

    public B displayItem(Function<SLPlayer, ItemStack> displayItem) {
        buildingObj.setDisplayItem(displayItem);
        return actualBuilder;
    }
    
    public B displayNumber(int displayNumber) {
        buildingObj.setDisplayNumber(displayNumber);
        return actualBuilder;
    }

    public B displayNumber(Function<SLPlayer, Integer> displayNumber) {
        buildingObj.setDisplayNumber(displayNumber);
        return actualBuilder;
    }

    public B description(String line) {
        buildingObj.addDescriptionLine(null, line);
        return actualBuilder;
    }

    public B description(SLPlayer slp, String line) {
        buildingObj.addDescriptionLine(slp, line);
        return actualBuilder;
    }

    public B description(Function<SLPlayer, List<String>> description) {
        buildingObj.setDescription(description);
        return actualBuilder;
    }

    public B visibilityController(Function<SLPlayer, Boolean> visibilitsController) {
        buildingObj.setVisibilityController(visibilitsController);
        return actualBuilder;
    }

    public B rank(Rank rank) {
        return accessController((slp) -> slp.getRank().hasPermission(rank));
    }

    public B accessController(Function<SLPlayer, Boolean> accessController) {
        buildingObj.setAccessController(accessController);
        return actualBuilder;
    }
    
    public B overwritePageBehavior(boolean overwritePageBehavior) {
        buildingObj.setOverwritePageBehavoir(overwritePageBehavior);
        return actualBuilder;
    }

    public T build() {
        return buildingObj;
    }

    /*
     public C construct(){
     return buildingObj.construct();
     }
     */
}
