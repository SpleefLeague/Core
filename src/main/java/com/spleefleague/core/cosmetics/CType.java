package com.spleefleague.core.cosmetics;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public enum CType {
    
    ARMOR(true, Material.LEATHER_CHESTPLATE, "Armor", ActiveArea.EVERYWHERE);
    
    private final boolean activeDuringTheGame;
    
    private final Material icon;
    
    private final String sectionName;
    
    private final ActiveArea activeArea;
    
    private final Set<CType> conflicting = new HashSet<>();
    
    private CType(boolean activeDuringTheGame, Material icon, String sectionName, ActiveArea activeArea, CType... conflicting) {
        this.activeDuringTheGame = activeDuringTheGame;
        this.icon = icon;
        this.sectionName = sectionName;
        for(CType conflict : conflicting) {
            this.conflicting.add(conflict);
            conflict.conflicting.add(this);
        }
        this.activeArea = activeArea;
        this.conflicting.add(this);
    }
    
    public boolean isActiveDureingTheGame() {
        return activeDuringTheGame;
    }
    
    public Material getIcon() {
        return icon;
    }
    
    public String getSectionName() {
        return sectionName;
    }
    
    public ActiveArea getActiveArea() {
        return activeArea;
    }
    
    public Set<CType> getConflicting() {
        return conflicting;
    }
}
