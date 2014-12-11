/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.queue;

/**
 *
 * @author Jonas
 */
public interface Queue {
    public boolean isOccupied();
    public int getSize();
}
