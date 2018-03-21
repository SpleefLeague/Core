/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spleefleague.core.utils.inventorymenu.dialog;

import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.Tuple;
import com.spleefleague.core.utils.inventorymenu.AbstractInventoryMenu;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentAlignment;
import com.spleefleague.core.utils.inventorymenu.SelectableInventoryMenuComponent;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuComponentTemplate;
import com.spleefleague.core.utils.inventorymenu.ItemStackWrapper;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author jonas
 * @param <B> Builder
 */
public class InventoryMenuDialogHolder<B> extends AbstractInventoryMenu<InventoryMenuDialogComponent<B>> {
    
    private B builder;
    private InventoryMenuDialog<B> dialogRoot;
    private final Supplier<InventoryMenuDialogHolderTemplate<B>> next;
    
    private static <B> Function<SelectableInventoryMenuComponent, InventoryMenuDialogComponent<B>> generateMapper(){
        return cimc -> {
            return new InventoryMenuDialogComponent<B>(cimc) {
                @Override
                protected void selected(ClickType clickType, B builder) {
                    cimc.selected(clickType);
                }
            };
        };
    }
    
    protected void setDialogRoot(InventoryMenuDialog<B> dialogRoot) {
        this.dialogRoot = dialogRoot;
    }
    
    protected InventoryMenuDialog<B> getDialogRoot() {
        return dialogRoot;
    }
    
    protected InventoryMenuDialogHolder(
            ItemStackWrapper displayItem, 
            String title, 
            Map<Integer, Tuple<Supplier<InventoryMenuComponentTemplate<? extends InventoryMenuDialogComponent<B>>>, InventoryMenuComponentAlignment>> components, 
            Map<Integer, Supplier<InventoryMenuComponentTemplate<? extends InventoryMenuDialogComponent<B>>>> staticComponents, 
            Function<SLPlayer, Boolean> accessController, 
            Function<SLPlayer, Boolean> visibilityController,
            SLPlayer slp, 
            int flags,
            Supplier<InventoryMenuDialogHolderTemplate<B>> next) {
        super(displayItem, title, components, staticComponents, generateMapper(), accessController, visibilityController, slp, flags);
        this.next = next;
    }

    public Supplier<InventoryMenuDialogHolderTemplate<B>> getNext() {
        return next;
    }

    public void setBuilder(B builder) {
        this.builder = builder;
    }
    
    protected InventoryMenuDialogHolder<B> construct(InventoryMenuDialogHolderTemplate<B> template) {
        InventoryMenuDialogHolder<B> holder = template.construct(this.getParent().getOwner());
        holder.setParent(this.getParent());
        holder.setBuilder(builder);
        holder.setDialogRoot(dialogRoot);
        return holder;
    }
    
    @Override
    public void selectItem(int index, ClickType clickType) {
        if (getCurrentComponents().get(getCurrentPage()).containsKey(index)) {
            InventoryMenuDialogComponent<B> component = getCurrentComponents().get(getCurrentPage()).get(index);
            if (component.hasAccess(getSLP())) {
                if(component instanceof InventoryMenuDialogItem) {
                    ((InventoryMenuDialogItem<B>) component).setHolderInstance(this);
                }
                component.selected(clickType, builder);
            } else {
                getSLP().closeInventory();
                getSLP().sendMessage(ChatColor.RED + "You don't have access to this");
            }
        }
    }
    
    public B getBuilderState() {
        return builder;
    }
}
