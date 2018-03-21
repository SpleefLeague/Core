/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuFlag;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import com.spleefleague.core.utils.inventorymenu.SelectableInventoryMenuComponent;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 */
public class InventoryMenuDialog<B> extends SelectableInventoryMenuComponent {

    private final BiConsumer<SLPlayer, B> completionListener;
    private final B builder;
    private final SLPlayer slp;
    private final InventoryMenuDialogHolderTemplate<B> start;
    
    public InventoryMenuDialog(
            ItemStackWrapper displayItem, 
            Function<SLPlayer, Boolean> visibilityController, 
            Function<SLPlayer, Boolean> accessController, 
            boolean overwritePageBehavior,
            B builder, 
            SLPlayer slp, 
            BiConsumer<SLPlayer, B> completionListener, 
            InventoryMenuDialogHolderTemplate<B> start) {
        super(displayItem, visibilityController, accessController, overwritePageBehavior);
        this.builder = builder;
        this.slp = slp;
        this.completionListener = completionListener;
        this.start = start;
    }

    @Override
    public void selected(ClickType clickType) {
        if(start != null) {
            if(start.hasAccess(slp)) {
                InventoryMenuDialogHolder<B> holder = start.construct(slp);
                holder.setParent(this.getParent());
                holder.setBuilder(builder);
                holder.setDialogRoot(this);
                holder.open();
            }
            else {
                if(this.getParent().isSet(InventoryMenuFlag.EXIT_ON_NO_PERMISSION)) {
                    slp.closeInventory();
                    slp.sendMessage(ChatColor.RED + "You don't have access to this");
                }
            }
        }
        else {
            completed();
        }
    }
    
    public void completed() {
        completionListener.accept(slp, builder);
    }
}
