package com.spleefleague.core.cosmetics;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public enum CType {
    
    ARMOR(Material.LEATHER_CHESTPLATE, "Armor", ActiveArea.EVERYWHERE),
    HAT(Material.SKULL_ITEM, "Hats", ActiveArea.EVERYWHERE),
    STATUS_EFFECT(Material.POTION, "Status effects", ActiveArea.OUT_OF_GAME),
    PARTICLE_EFFECT(Material.NETHER_STAR, "Particle effects", ActiveArea.OUT_OF_GAME);
    
    private final Material icon;
    
    private final String sectionName;
    
    private final ActiveArea activeArea;
    
    private final Set<CType> conflicting = new HashSet<>();
    
    private CType(Material icon, String sectionName, ActiveArea activeArea, CType... conflicting) {
        this.icon = icon;
        this.sectionName = sectionName;
        for(CType conflict : conflicting) {
            this.conflicting.add(conflict);
            conflict.conflicting.add(this);
        }
        this.activeArea = activeArea;
        this.conflicting.add(this);
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
