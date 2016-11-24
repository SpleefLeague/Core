package com.spleefleague.core.events;

import com.spleefleague.core.utils.fakeentity.FakeCreature;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class FakeCreatureInteractEvent extends Event implements Cancellable {
    
    private final static HandlerList handlerList = new HandlerList();
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    private boolean cancelled = false;
    
    private final Player player;
    private final FakeCreature creature;
    private final ClickType clickType;
    
    public FakeCreatureInteractEvent(Player p, FakeCreature creature, ClickType clickType) {
        this.player = p;
        this.creature = creature;
        this.clickType = clickType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.cancelled = value;
    }

}
