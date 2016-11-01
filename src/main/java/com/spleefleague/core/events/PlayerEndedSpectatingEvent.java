package com.spleefleague.core.events;

import com.spleefleague.core.queue.Battle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class PlayerEndedSpectatingEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Battle battle;

    public PlayerEndedSpectatingEvent(Player player, Battle battle) {
        this.player = player;
        this.battle = battle;
    }

    public Player getPlayer() {
        return player;
    }

    public Battle getBattle() {
        return battle;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
