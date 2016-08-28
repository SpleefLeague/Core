package com.spleefleague.core.menus;

import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.item;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.menu;

import com.google.common.collect.Lists;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.cosmetics.Collectibles;
import com.spleefleague.core.cosmetics.CosmeticsManager;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.UtilChat;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplateBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CInventory {
    
    @Getter
    private final CType type;
    
    protected InventoryMenuTemplateBuilder builder;
    private int lastSlot = 0;
    
    @Getter
    private InventoryMenuTemplate menu;

    CInventory(CType type) {
        String iname = "Cosmetics: " + type.getSectionName();
        builder = menu()
                .title(iname)
                .displayName(iname)
                .displayIcon(type.getIcon());
        this.type = type;
        
        builder.component(3, 5, item()
                .displayName("Back to Cosmetics Menu")
                .displayIcon(Material.ARROW)
                .onClick(e -> {
                    SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(e.getPlayer());
                    CosmeticsMenu.getMenu().construct(slp).open();
                })
        );
        builder.component(4, 5, item()
                .displayName(UtilChat.c("&6Currency"))
                .description(slp -> Lists.newArrayList(
                        UtilChat.c("&7You have &6%d coins", slp.getCoins()),
                        UtilChat.c("&7You have &b%d premium credits", slp.getPremiumCredits())
                ))
                .displayIcon(Material.EMERALD)
                .onClick(e -> {
                    UtilChat.s(Theme.INFO, e.getPlayer(), "Earn coins by playing games & buy more premium credits on our website!");
                })
        );
        builder.component(5, 5, item()
                .displayName(UtilChat.c("&cTurn off"))
                .displayIcon(Material.BARRIER)
                .onClick(e -> {
                    SpleefLeague.getInstance().getPlayerManager().get(e.getPlayer()).getCollectibles().removeActive(type);
                })
        );
        CosmeticsManager.getItems(type).forEach(this::addItem);
        menu = builder.build();
    }
    
    private void addItem(CItem item) {
        addItem(item, lastSlot++);
    }
    
    private void addItem(CItem item, int slot) {
        builder.component(slot, item()
                .displayName(item.getName())
                .displayItem(slp -> slp.getCollectibles().getItems().contains(item.getId()) ? item.getIcon().clone() : item.getEmptyIcon().clone())
                .onClick(e -> {
                    ClickType clickType = e.getClickType();
                    Player p = e.getPlayer();
                    SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
                    Collectibles col = slp.getCollectibles();
                    if(!col.getItems().contains(item.getId())) {
                        //todo: insert confirmation page
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
                })
        );
    }

}
