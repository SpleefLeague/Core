/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplate;
import java.util.function.Function;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialogItemTemplate<B> extends InventoryMenuComponentTemplate<InventoryMenuDialogItem<B>>{

    private Function<SLPlayer, InventoryMenuDialogTemplate<B>> next;
    private InventoryMenuDialogClickListener<B> listener;
    
    public void setNext(Function<SLPlayer, InventoryMenuDialogTemplate<B>> next) {
        this.next = next;
    }
    
    public void setNext(InventoryMenuDialogTemplate<B> next) {
        setNext((s) -> next);
    }
    
    public void setClickListener(InventoryMenuDialogClickListener<B> listener) {
        this.listener = listener;
    }
    
    @Override
    public InventoryMenuDialogItem<B> construct(SLPlayer slp) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
