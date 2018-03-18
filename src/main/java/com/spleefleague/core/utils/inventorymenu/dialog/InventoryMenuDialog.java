/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponent;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 * @param <B> Builder
 */
public class InventoryMenuDialog<B> extends AbstractInventoryMenu<InventoryMenuDialogComponent> {

    private final B builder;
    private final BiConsumer<SLPlayer, B> completionListener;
    private static final Function<InventoryMenuComponent, InventoryMenuDialogComponent> mapper;
    
    static {
        mapper = null;
    }
    
    protected InventoryMenuDialog(
            ItemStackWrapper displayItem, 
            String title, 
            Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuDialogComponent>> components, 
            Map<Integer, InventoryMenuComponentTemplate<? extends InventoryMenuDialogComponent>> staticComponents, 
            Function<SLPlayer, Boolean> accessController, 
            Function<SLPlayer, Boolean> visibilityController,
            SLPlayer slp, 
            int flags,
            B builder,
            BiConsumer<SLPlayer, B> completionListener) {
        super(displayItem, title, components, staticComponents, mapper, accessController, visibilityController, slp, flags);
        this.builder = builder;
        this.completionListener = completionListener;
    }
    
    @Override
    public void selectItem(int index, ClickType clickType) {
        if (getCurrentComponents().get(getCurrentPage()).containsKey(index)) {
            InventoryMenuDialogComponent component = getCurrentComponents().get(getCurrentPage()).get(index);
            if (component.hasAccess(getSLP())) {
                //Select the item
            } else {
                getSLP().closeInventory();
                getSLP().sendMessage(ChatColor.RED + "You don't have access to this");
            }
        }
    }
    
}
