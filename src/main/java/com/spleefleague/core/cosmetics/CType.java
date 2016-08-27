package com.spleefleague.core.cosmetics;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public enum CType {
    ARMOR(true);
    
    @Getter
    private final boolean activeDuringTheGame;
    
    @Getter
    private final Set<CType> conflicting = new HashSet<>();
    
    private CType(boolean activeDuringTheGame, CType... conflicting) {
        this.activeDuringTheGame = activeDuringTheGame;
        for(CType conflict : conflicting) {
            this.conflicting.add(conflict);
            conflict.conflicting.add(this);
        }
    }
}
