package com.spleefleague.core.utils.inventorymenu;

import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import org.bukkit.Material;

import java.util.List;
import java.util.Vector;
import java.util.function.Function;
import org.bukkit.inventory.ItemStack;

public abstract class InventoryMenuComponentTemplateBuilder<C extends InventoryMenuComponent, T extends InventoryMenuComponentTemplate<C>, B extends InventoryMenuComponentTemplateBuilder<C, T, B>> extends AbstractInventoryMenuComponentTemplateBuilder<C, T, B> {

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
    
    private Integer LINE_WRAP = 20;
    
    private Vector<String> wrapLine(String line) {
        Vector<String> wrapped = new Vector<>();
        String newline = "";
	int last = 0, next = 0;
	int cLen = 0; // Current length
	int wLen = 0; // Word length
	while((next = line.indexOf("\n", last)) != -1) {
		wLen = next - last;
		if(cLen + wLen > LINE_WRAP) {
                    wrapped.add(newline);
                    newline = "";
                    cLen = 0;
		}
                else {
                    cLen++;
                }
                if(cLen > 1) {
                    newline = newline + " ";
                }
		newline = newline + line.substring(last, next);
		last = next + 1;
		cLen += wLen;
	}
	next = line.length() - 1;
        newline = newline + line.substring(last, next);
        if(newline.length() > 0)
            wrapped.add(newline);
	return wrapped;
    }

    public B description(String line) {
        for(String l : wrapLine(line))
            buildingObj.addDescriptionLine(null, l);
        return actualBuilder;
    }

    public B description(SLPlayer slp, String line) {
        for(String l : wrapLine(line))
            buildingObj.addDescriptionLine(slp, l);
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
    
    public B flags(InventoryMenuComponentFlag... flags) {
        for(InventoryMenuComponentFlag flag : flags) {
            buildingObj.addFlag(flag);
        }
        return (B)this;
    }
    
    public B unsetFlags(InventoryMenuComponentFlag... flags) {
        for(InventoryMenuComponentFlag flag : flags) {
            buildingObj.removeFlag(flag);
        }
        return (B)this;
    }
}
