package com.spleefleague.core.menus;

import com.google.common.collect.Lists;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.SimpleItemStack;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class TransactionMenu {
    
    public static void constructAndOpen(Player p, Transaction transaction, InventoryMenuTemplate cancellationTarget, List<String> info) {
        new TransactionMenu(transaction, cancellationTarget, info).open(p);
    }
    
    private final InventoryMenuTemplate menu;
    
    public TransactionMenu(Transaction transaction, InventoryMenuTemplate cancellationTarget, List<String> info) {
        this.menu = InventoryMenuAPI.menu()
                .title("Transaction")
                .displayName("Transaction")
                .component(2, 1, InventoryMenuAPI.item()
                        .displayItem(new SimpleItemStack(Material.STAINED_GLASS, "&aConfirm", info, (short) 5))
                        .onClick(e -> transaction.process(e.getPlayer()))
                ).component(6, 1, InventoryMenuAPI.item()
                        .displayItem(new SimpleItemStack(Material.STAINED_GLASS, "&cCancel", Lists.newArrayList(
                                "&7Click to return to",
                                "&7the previous menu."
                        ), (short) 6))
                        .onClick(e -> cancellationTarget.construct(SpleefLeague.getInstance().getPlayerManager().get(e.getPlayer())).open())
                ).component(4, 2, InventoryMenuAPI.item()
                        .displayItem(new SimpleItemStack(Material.PAPER, "&eTransaction page"))
                )
                .build();
    }
    
    public void open(Player p) {
        open(SpleefLeague.getInstance().getPlayerManager().get(p));
    }
    
    public void open(SLPlayer slp) {
        menu.construct(slp).open();
    }

    public static interface Transaction {
        
        void process(Player p);
        
    }
    
}
