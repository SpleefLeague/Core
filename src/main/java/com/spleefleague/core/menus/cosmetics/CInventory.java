package com.spleefleague.core.menus.cosmetics;

import com.google.common.collect.Lists;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.cosmetics.Collectibles;
import com.spleefleague.core.menus.CosmeticsMenu;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.rines.RButton;
import com.spleefleague.core.utils.rines.REmptyButton;
import com.spleefleague.core.utils.rines.RInventory;
import com.spleefleague.core.utils.rines.RInventoryManager;
import com.spleefleague.core.utils.rines.RItem;
import com.spleefleague.core.utils.rines.UtilChat;
import java.util.ArrayList;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public abstract class CInventory extends RInventory {
    
    @Getter
    private final static int columnsPerPage = 7;
    
    @Getter
    private final static int linesPerPage = 4;
    
    @Getter
    private final static int itemsPerPage = columnsPerPage * linesPerPage;
    
    @Getter
    protected final SLPlayer slp;
    
    @Getter
    private final String section;
    
    @Getter
    private final CType resetType;
    
    @Getter
    private final int page;
    
    @Getter
    private final int maxPage;
    
    private int lastLine = 1;
    private int lastSlot = 1;

    public CInventory(Player p, String section, CType resetType, int page, int max_page) {
        super("Cosmetics: " + section + " (Page " + page + "/" + max_page + ")", 6);
        this.slp = SpleefLeague.getInstance().getPlayerManager().get(p);
        this.section = section;
        this.resetType = resetType;
        this.page = page;
        this.maxPage = max_page;
        if(page == 1) {
            addItem(new RButton(Material.ARROW, "Back to Cosmetics Menu", new ArrayList<>()) {
                
                @Override
                public void onClick(Player p, int slot) {
                    RInventoryManager.openInventory(p, CosmeticsMenu.getInstance());
                }
            }, 6, 4);
        }else {
            addItem(new RButton(Material.ARROW, "Previous (" + (page - 1) + ") page", new ArrayList<>()) {
                
                @Override
                public void onClick(Player p, int slot) {
                    RInventoryManager.openInventory(p, getPage(p, page - 1));
                }
            }, 6, 4);
        }
        if(page != max_page) {
            addItem(new RButton(Material.ARROW, "Next (" + (page + 1) + ") page", new ArrayList<>()) {
                
                @Override
                public void onClick(Player p, int slot) {
                    RInventoryManager.openInventory(p, getPage(p, page + 1));
                }
            }, 6, 6);
        }
        addItem(new REmptyButton(Material.EMERALD, "&6Currency", Lists.newArrayList(
        
            "&7You have &6" + slp.getCoins() + " coins",
            "&7You have &b" + slp.getPremiumCredits() + " premium credits"
            
        )), 6, 5);
        addItem(new RButton(Material.BARRIER, 14, "&cTurn off", new ArrayList<>()) {

            @Override
            public void onClick(Player p, int slot) {
                SpleefLeague.getInstance().getPlayerManager().get(p).getCollectibles().removeActive(resetType);
            }
            
        }, 5, 5);
    }
    
    public void addItem(CItem item) {
        if(++lastSlot == 9) {
            ++lastLine;
            lastSlot = 2;
        }
        addItem(item, lastLine, lastSlot);
    }
    
    public void addItem(CItem item, int line, int slot) {
        Collectibles col = slp.getCollectibles();
        boolean has = col.getItems().contains(item.getId());
        RItem ritem = new RItem() {

            @Override
            public void onClick(Player p, ClickType clickType, int slot) {
                if(!has) {
                    if(clickType == ClickType.LEFT)
                        item.buy(p, true);
                    else if(clickType == ClickType.RIGHT)
                        item.buy(p, false);
                    return;
                }
                if(col.isActive(item.getType().getConflicting())) {
                    UtilChat.s(Theme.ERROR, p, "You can not use this item, because it's conflicting with one of already used items.");
                    return;
                }
                item.select(p);
                UtilChat.s(Theme.SUCCESS, p, "Item has been selected.");
            }

            @Override
            public void onClick(Player p, int slot) {}
            
        };
        ritem.setItem(has ? item.getIcon().clone() : item.getEmptyIcon().clone());
        super.addItem(ritem, line, slot);
    }

    public abstract CInventory getPage(Player p, int page);

}
