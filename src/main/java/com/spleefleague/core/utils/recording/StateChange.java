package com.spleefleague.core.utils.recording;

import com.spleefleague.entitybuilder.DBSaveable;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface StateChange extends DBSaveable {

    int getTick();
    void applyTo(int entityId, boolean forward, boolean ghost, Player... audience);
}
