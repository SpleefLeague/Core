/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

/**
 *
 * @author Jonas
 */
public class FakeArea {

    private final Collection<FakeArea> fakeAreas;
    private final Collection<FakeBlock> cache;
    private boolean outdated;
    private UUID uuid;

    public FakeArea() {
        fakeAreas = new HashSet<>();
        cache = new HashSet<>();
        outdated = false;
        uuid = UUID.randomUUID();
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
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
        fakeAreas.remove(area);
        outdated = true;
    }

    public void removeBlock(FakeBlock block) {
        fakeAreas.remove(block);
        cache.remove(block);
    }

    public Collection<FakeBlock> getBlocks() {
        if (outdated) {
            outdated = false;
            recalcCache();
        }
        return cache;
    }

    private void recalcCache() {
        cache.clear();
        for (FakeArea fa : fakeAreas) {
            cache.addAll(fa.getBlocks());
        }
    }

    public void clear() {
        cache.clear();
        fakeAreas.clear();
        outdated = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FakeArea other = (FakeArea) obj;
        return uuid.equals(other.uuid);
    }
}
