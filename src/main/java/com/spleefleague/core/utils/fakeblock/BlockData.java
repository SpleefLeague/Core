/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

import java.util.Objects;
import org.bukkit.Material;

/**
 *
 * @author Jonas
 */
public class BlockData {
    
    private final Material type;
    private final byte damage;
    
    public BlockData(Material type, byte damage) {
        this.type = type;
        this.damage = damage;
    }
    
    public Material getType() {
        return type;
    }
    
    public byte getDamage() {
        return damage;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof BlockData)) return false;
        BlockData b = (BlockData) o;
        return b.type == type && b.damage == damage;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.type);
        hash = 89 * hash + this.damage;
        return hash;
    }
}
