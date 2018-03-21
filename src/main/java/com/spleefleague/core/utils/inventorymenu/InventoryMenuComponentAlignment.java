/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu;

import java.util.Objects;
import java.util.OptionalInt;

/**
 *
 * @author jonas
 */
public class InventoryMenuComponentAlignment implements Comparable<InventoryMenuComponentAlignment> {
    
    private final Direction primary, secondary;
    
    public InventoryMenuComponentAlignment(Direction primary, Direction secondary) {
        this.primary = primary;
        this.secondary = secondary;
    }
    
    public Direction getPrimaryDirection() {
        return primary;
    }

    public Direction getSecondaryDirection() {
        return secondary;
    }
    
    public OptionalInt next(int current, int height) {
        final int rowSize = AbstractInventoryMenu.ROWSIZE;
        final int colSize = AbstractInventoryMenu.COLUMNSIZE;
        height = Math.min(height, colSize);
        int x = current % rowSize;
        int y = current / rowSize;
        int nextX = x;
        int nextY = y;
        switch(primary) {
            case RIGHT: {
                if(x < 8) {
                    nextX = x + 1;
                }
                else {
                    nextX = 0;
                    nextY = (secondary == Direction.DOWN) ? y + 1 : y - 1;
                }
                break;
            }
            case LEFT: {
                if(x > 0) {
                    nextX = x - 1;
                }
                else {
                    nextX = 8;
                    nextY = (secondary == Direction.DOWN) ? y + 1 : y - 1;
                }
                break;
            }
            case UP: {
                if(y > 0) {
                    nextY = y - 1;
                }
                else {
                    nextX = (secondary == Direction.RIGHT) ? x + 1 : x - 1;
                    nextY = height - 1;
                }
                break;
            }
            case DOWN: {
                if(y < height - 1) {
                    nextY = y + 1;
                }
                else {
                    nextX = (secondary == Direction.RIGHT) ? x + 1 : x - 1;
                    nextY = 0;
                }
                break;
            }
        }
        if(nextX < 0 || nextY < 0 || nextX >= rowSize || nextY >= height) {
            return OptionalInt.empty();
        }
        int nextPosition = nextX + nextY * rowSize;
        return OptionalInt.of(nextPosition);
    }
    
    public int getStart(int height) {
        int x = 0;
        int y = 0;
        switch(primary) {
            case RIGHT: {
                x = 0;
                break;
            }
            case LEFT: {
                x = 8;
                break;
            }
            case UP: {
                y = height - 1;
                break;
            }
            case DOWN: {
                y = 0;
                break;
            }
        }
        switch(secondary) {
            case RIGHT: {
                x = 0;
                break;
            }
            case LEFT: {
                x = 8;
                break;
            }
            case UP: {
                y = height - 1;
                break;
            }
            case DOWN: {
                y = 0;
                break;
            }
        }
        return x + y * AbstractInventoryMenu.ROWSIZE;
    }
    
    public static final InventoryMenuComponentAlignment DEFAULT;
    
    static {
        DEFAULT = new InventoryMenuComponentAlignment(Direction.RIGHT, Direction.DOWN);
    }
    
    @Override
    public int compareTo(InventoryMenuComponentAlignment t) {
        if(t == null) {
            return -1;
        }
        int c = this.primary.compareTo(t.primary);
        if(c != 0) return c;
        return this.secondary.compareTo(t.secondary);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.primary);
        hash = 61 * hash + Objects.hashCode(this.secondary);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InventoryMenuComponentAlignment other = (InventoryMenuComponentAlignment) obj;
        if (this.primary != other.primary) {
            return false;
        }
        if (this.secondary != other.secondary) {
            return false;
        }
        return true;
    }
    
    public static enum Direction {
        RIGHT,
        LEFT,
        DOWN,
        UP;
    }
}
