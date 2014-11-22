/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils;

/**
 *
 * @author Jonas
 */
public interface DBStorable {
    public void load(Object data);
    public Object save();
}
