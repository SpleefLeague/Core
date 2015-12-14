/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Jonas
 */
public class FakeArea {
    
    private final Collection<FakeArea> fakeAreas;
    private final Collection<FakeBlock> cache;
    private boolean outdated;
    
    public FakeArea() {
        fakeAreas = new ArrayList<>();
        cache = new ArrayList<>();
        outdated = false;
    }
    
    public void add(FakeArea area) {
        fakeAreas.add(area);
        outdated = true;
    }
    
    public void addBlock(FakeBlock block) {
        fakeAreas.add(block);
        cache.add(block);
    }
    
    public void remove(FakeArea area) {
        fakeAreas.add(area);
        outdated = true;
    }
    
    public void removeBlock(FakeBlock block) {
        fakeAreas.remove(block);
        cache.remove(block);
    }
    
    public Collection<FakeBlock> getBlocks() {
        if(outdated) {
            outdated = false;
            recalcCache();
        }
        return cache;
    }
    
    private void recalcCache() {
        cache.clear();
        for(FakeArea fa : fakeAreas) {
            cache.addAll(fa.getBlocks());
        }
    }

    public void clear() {
        cache.clear();
        fakeAreas.clear();
        outdated = false;
    }
}
