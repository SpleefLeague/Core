package com.spleefleague.core.cosmetics;

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
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class Collectibles extends DBEntity implements DBLoadable, DBSaveable {

    @DBLoad(fieldName = "active")
    @DBSave(fieldName = "active")
    private Set<Integer> active;
    
    @DBLoad(fieldName = "items")
    @DBSave(fieldName = "items")
    private Set<Integer> items;
    
    private Collectibles() {}
    
    public Set<Integer> getActive() {
        return active;
    }
    
    public Set<Integer> getItems() {
        return items;
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
    
    public void removeActive(CType type, Player player) {
        Optional<CItem> opt = getActiveItems().stream().filter(ci -> ci.getType() == type).findAny();
        if(!opt.isPresent())
            return;
        CItem item = opt.get();
        active.remove(item.getId());
        item.onRemoving(player);
    }
    
    public void addItem(int id) {
        if(items.contains(id))
            return;
        items.add(id);
    }

    public static Collectibles getDefault(SLPlayer slp) {
        Collectibles col = new Collectibles();
        col.active = new HashSet<>();
        col.items = new HashSet<>();
        return col;
    }
    
}
