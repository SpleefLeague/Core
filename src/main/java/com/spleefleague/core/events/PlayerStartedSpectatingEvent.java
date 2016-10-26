package com.spleefleague.core.events;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.plugin.GamePlugin;
import com.spleefleague.core.queue.Battle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class PlayerStartedSpectatingEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final Player target;
    private final Battle battle;

    public PlayerStartedSpectatingEvent(Player player, Player target, GamePlugin game) {
        this.player = player;
        this.target = target;
        this.battle = game.getBattleManager().getBattle(SpleefLeague.getInstance().getPlayerManager().get(target));
    }

    public Player getPlayer() {
        return player;
    }

    public Player getTarget() {
        return target;
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
