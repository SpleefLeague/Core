package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import com.spleefleague.core.utils.function.Dynamic;
import java.util.List;

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

    public B displayName(Dynamic<String> displayName) {
        buildingObj.setDisplayName(displayName);
        return actualBuilder;
    }

    public B displayIcon(Material displayIcon) {
        return displayIcon(new MaterialData(displayIcon));
    }

    public B displayIcon(MaterialData displayIcon) {
        buildingObj.setDisplayIcon(displayIcon);
        return actualBuilder;
    }

    public B displayIcon(Dynamic<MaterialData> displayIcon) {
        buildingObj.setDisplayIcon(displayIcon);
        return actualBuilder;
    }

    /*public B displayItem(ItemStack displayItem) {
        buildingObj.setDisplayItem(displayItem);
        return actualBuilder;
    }

    public B displayItem(Dynamic<ItemStack> displayItem) {
        buildingObj.setDisplayItem(displayItem);
        return actualBuilder;
    }*/

    public B displayNumber(int displayNumber) {
        buildingObj.setDisplayNumber(displayNumber);
        return actualBuilder;
    }

    public B displayNumber(Dynamic<Integer> displayNumber) {
        buildingObj.setDisplayNumber(displayNumber);
        return actualBuilder;
    }

    public B description(String line) {
        buildingObj.addDescriptionLine(line);
        return actualBuilder;
    }

    public B description(SLPlayer slp, String line) {
        buildingObj.addDescriptionLine(slp, line);
        return actualBuilder;
    }
    
    public B description(Dynamic<List<String>> description) {
        buildingObj.setDescription(description);
        return actualBuilder;
    }

    public B visibilityController(Dynamic<Boolean> visibilitsController) {
    	buildingObj.setVisibilityController(visibilitsController);
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
