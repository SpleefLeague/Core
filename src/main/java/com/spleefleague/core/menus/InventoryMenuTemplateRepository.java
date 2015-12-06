package com.spleefleague.core.menus;

import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.item;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.GamePlugin;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplateBuilder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.bukkit.inventory.ItemStack;

public class InventoryMenuTemplateRepository {

    private static InventoryMenuTemplateBuilder slMenuBuilder;
    public static InventoryMenuTemplate slMenu;

    public static void initTemplates() {
        slMenuBuilder = menu()
                .title("SLMenu")
                .displayIcon(Material.GHAST_TEAR)
                .displayName("SL Menu")
                .description("A menu for doing")
                .description("various things")
                //Gamemode submenus added by game plugins
                .component(menu()
                        .displayName("Options")
                        .displayIcon(Material.SIGN)
                        .description("Various options")
                )
                .component(menu()
                        .displayName("Moderative")
                        .displayIcon(Material.REDSTONE)
                        .description("Moderative tools")
                        .rank(Rank.MODERATOR)
                        .visibilityController((slp) -> slp.getRank().hasPermission(Rank.MODERATOR))
                        .component(menu()
                                .title("Reload Menu")
                                .displayName("Reload Menu")
                                .displayIcon(Material.WATCH)
                                .description("Reloading various things")
                                .component(item()
                                        .displayName("Server")
                                        .displayIcon(Material.REDSTONE_TORCH_ON)
                                        .description("Reloads the server")
                                        .onClick(event -> {
                                            event.getPlayer().closeInventory();
                                            Bukkit.reload();
                                        })
                                ).component(item()
                                        .displayName("Ranks")
                                        .displayIcon(Material.BOOK_AND_QUILL)
                                        .description("Reloads all ranks")
                                        .onClick(event -> {
                                            event.getPlayer().closeInventory();
                                            Rank.init();
                                            SpleefLeague.getInstance().getPlayerManager().getAll().stream().forEach((slp) -> {
                                                slp.setRank(Rank.valueOf(slp.getRank().getName()));
                                            });
                                            event.getPlayer().sendMessage(Theme.SUCCESS + "Reloaded " + Rank.values().length + " ranks!");
                                        })
                                )
                        )
                        .component(item()
                                .displayName("Cancel all")
                                .displayIcon(Material.DIAMOND_SPADE)
                                .description("Cancels all currently")
                                .description("running matches")
                                .onClick(event -> {
                                    event.getPlayer().closeInventory();
                                    GamePlugin.cancelAllMatches();
                                    event.getPlayer().sendMessage(Theme.SUCCESS + "All games have been cancelled.");
                                })
                        )
                );
        Bukkit.getScheduler().runTaskLater(SpleefLeague.getInstance(), () -> {
            slMenu = slMenuBuilder.build();
            menus.add(slMenu);
        }, 0); //Gets called after all plugins were loaded
    }

    private static Integer currentGamemodeMenu = Integer.MIN_VALUE;

    public static InventoryMenuTemplateBuilder getNewGamemodeMenu() {
        InventoryMenuTemplateBuilder builder = menu();
        slMenuBuilder.component(currentGamemodeMenu++, builder);
        return builder;
    }

    public static final Set<InventoryMenuTemplate> menus = new HashSet<>();

    public static boolean isMenuItem(ItemStack is, SLPlayer slp) {
        return menus.stream().anyMatch((template) -> (template.getDisplayItemStack(slp).equals(is)));
    }

    public static void openMenu(ItemStack is, SLPlayer slp) {
        Optional<InventoryMenuTemplate> oimt = menus.stream().filter((template) -> (template.getDisplayItemStack(slp).equals(is))).findFirst();
        if (oimt.isPresent()) {
            oimt.get().construct(slp).open();
        }
    }
}
