package com.spleefleague.core.menus.cosmetics;

import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.cosmetics.CosmeticsManager;
import java.util.List;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class ArmorInventory extends CInventory {

    public ArmorInventory(Player p, int page, int max_page) {
        super(p, "Armor", CType.ARMOR, page, max_page);
        List<CItem> items = CosmeticsManager.getItems(CType.ARMOR);
        for(int i = (page - 1) * getItemsPerPage(); i < Math.min(page * getItemsPerPage(), items.size()); ++i)
            addItem(items.get(i));
    }

    @Override
    public CInventory getPage(Player p, int page) {
        return new ArmorInventory(p, page, getMaxPage());
    }

}
