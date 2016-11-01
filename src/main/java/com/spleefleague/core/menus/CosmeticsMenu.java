package com.spleefleague.core.menus;

import com.google.common.collect.Lists;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.menu;

import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.utils.UtilChat;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplateBuilder;
import org.bukkit.Material;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsMenu {

    private static InventoryMenuTemplate menu;
    private static InventoryMenuTemplateBuilder builder;
    
    public static InventoryMenuTemplate init() {
        builder = menu()
                .title("Cosmetics")
                .displayName("Cosmetics")
                .displayIcon(Material.GOLD_HELMET)
                .description(slp -> Lists.newArrayList(
                        UtilChat.c("&7Click here to open"),
                        UtilChat.c("&7cosmetics menu.")
//                        UtilChat.c(""),
//                        UtilChat.c("&4&lWork in progress")
                ));
//                .rank(Rank.DEVELOPER)
//                .visibilityController(slp -> slp.getRank().hasPermission(Rank.DEVELOPER));
        for(CType type : CType.values())
            builder.component(new CInventory(type).getMenu());
        menu = builder.build();
        return menu;
    }
    
    public static InventoryMenuTemplate getMenu() {
        return menu;
    }
    
}
