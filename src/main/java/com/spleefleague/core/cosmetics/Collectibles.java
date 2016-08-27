package com.spleefleague.core.cosmetics;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.DBEntity;
import com.spleefleague.core.io.DBLoad;
import com.spleefleague.core.io.DBLoadable;
import com.spleefleague.core.io.DBSave;
import com.spleefleague.core.io.DBSaveable;
import com.spleefleague.core.player.SLPlayer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class Collectibles extends DBEntity implements DBLoadable, DBSaveable {
    
    @Getter
    private final String owner;

    @Getter
    @DBLoad(fieldName = "active")
    @DBSave(fieldName = "active")
    private Set<Integer> active;
    
    @Getter
    @DBLoad(fieldName = "items")
    @DBSave(fieldName = "items")
    private Set<Integer> items;
    
    private Collectibles(SLPlayer slp) {
        this.owner = slp.getName();
    }
    
    public void apply(SLPlayer slp) {
        getActiveItems().forEach(ci -> ci.onSelecting(slp));
    }
    
    public Set<CItem> getActiveItems() {
        return active.stream().map(CosmeticsManager::getItem).collect(Collectors.toSet());
    }
    
    public boolean isActive(CType type) {
        return isActive(Collections.singleton(type));
    }
    
    public boolean isActive(Collection<CType> types) {
        return getActiveItems().stream().map(CItem::getType).anyMatch(types::contains);
    }
    
    public void addActive(CItem item) {
        active.add(item.getId());
    }
    
    public void removeActive(CType type) {
        Optional<CItem> opt = getActiveItems().stream().filter(ci -> ci.getType() == type).findAny();
        if(!opt.isPresent())
            return;
        CItem item = opt.get();
        active.remove(item.getId());
        item.onRemoving(getPlayer());
    }
    
    public void addItem(int id) {
        if(items.contains(id))
            return;
        items.add(id);
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }
    
    public SLPlayer getSLPlayer() {
        return SpleefLeague.getInstance().getPlayerManager().get(owner);
    }

    public static Collectibles getDefault(SLPlayer slp) {
        Collectibles col = new Collectibles(slp);
        col.active = new HashSet<>();
        col.items = new HashSet<>();
        return col;
    }
    
}
