/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.fakeblock;

import java.util.HashSet;

public class FakeChunk {

    private final int x, z;
    private final HashSet<FakeBlock> fakeblocks;

    protected FakeChunk(int x, int z) {
        this.x = x;
        this.z = z;
        fakeblocks = new HashSet<>();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public void addBlocks(FakeBlock... blocks) {
        for (FakeBlock block : blocks) {
            fakeblocks.add(block);
        }
    }

    public void removeBlocks(FakeBlock... blocks) {
        for (FakeBlock block : blocks) {
            fakeblocks.add(block);
        }
    }

    public HashSet<FakeBlock> getBlocks() {
        return fakeblocks;
    }
}
