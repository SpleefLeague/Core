package com.spleefleague.core.cosmetics;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import org.bukkit.Material;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public enum CType {
    ARMOR(true, Material.LEATHER_CHESTPLATE, "Armor");
    
    @Getter
    private final boolean activeDuringTheGame;
    
    @Getter
    private final Material icon;
    
    @Getter
    private final String sectionName;
    
    @Getter
    private final Set<CType> conflicting = new HashSet<>();
    
    private CType(boolean activeDuringTheGame, Material icon, String sectionName, CType... conflicting) {
        this.activeDuringTheGame = activeDuringTheGame;
        this.icon = icon;
        this.sectionName = sectionName;
        for(CType conflict : conflicting) {
            this.conflicting.add(conflict);
            conflict.conflicting.add(this);
        }
    }
}
