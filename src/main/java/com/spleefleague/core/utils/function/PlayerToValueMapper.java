package com.spleefleague.core.utils.function;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface PlayerToValueMapper<T> {
	public T toValue(Player p);
}
