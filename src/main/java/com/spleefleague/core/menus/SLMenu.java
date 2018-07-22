package com.spleefleague.core.menus;

import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.item;
import static com.spleefleague.core.utils.inventorymenu.InventoryMenuAPI.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.ChatChannel;
import com.spleefleague.core.chat.ChatManager;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplate;
import com.spleefleague.core.utils.inventorymenu.InventoryMenuTemplateBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;

public class SLMenu {

    private static InventoryMenuTemplateBuilder slMenuBuilder;
    public static InventoryMenuTemplate slMenu;
    private static Integer currentGamemodeMenu = Integer.MIN_VALUE;

    public static void init() {
        slMenuBuilder = menu()
                .title("SpleefLeague Menu")
                .displayIcon(Material.COMPASS)
                .displayName(ChatColor.GREEN + "" + ChatColor.BOLD + "SpleefLeague Menu")
                .description("A menu for doing")
                .description("various things")
                //Gamemode submenus added by game plugins
                .component(createOptionsMenu())
                .component(createStaffMenu());
        Bukkit.getScheduler().runTask(SpleefLeague.getInstance(), () -> {
            slMenu = slMenuBuilder.build();
            InventoryMenuTemplateRepository.addMenu(slMenu);
        }); //Gets called after all plugins were loaded
    }

    private static InventoryMenuTemplateBuilder createOptionsMenu() {
        InventoryMenuTemplateBuilder chatOptions = menu()
                .title("Chat options")
                .displayName("Chat options")
                .displayIcon(Material.BOOK_AND_QUILL);
        for (ChatChannel channel : ChatManager.getVisibleChatChannels()) {
            chatOptions.component(item()
                    .displayName(channel.getDisplayName())
                    .displayIcon(Material.BOOK)
                    .visibilityController((slp) -> slp.getRank().hasPermission(channel.getMinRank()))
                    .description((slp) -> {
                        List<String> description = new ArrayList<>();
                        description.add(ChatColor.GRAY + "Click here to");
                        description.add(slp.isInChatChannel(channel) ? ChatColor.RED + "disable" : ChatColor.GREEN + "enable");
                        description.add(ChatColor.GRAY + "this channel!");
                        return description;
                    })
                    .onClick((event) -> {
                        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(event.getPlayer());
                        if (slp.isInChatChannel(channel)) {
                            slp.removeChatChannel(channel);
                            slp.getOptions().disableChatChannel(channel);
                        } else {
                            slp.addChatChannel(channel);
                            slp.getOptions().enableChatChannel(channel);
                        }
                        event.getItem().getParent().update();
                    }));
        }
        InventoryMenuTemplateBuilder builder = menu()
                .title("Options")
                .displayName("Options")
                .displayIcon(Material.SIGN)
                .description("Various options")
                .component(chatOptions);
        return builder;
    }

    private static InventoryMenuTemplateBuilder createStaffMenu() {
        InventoryMenuTemplateBuilder builder = menu()
                .displayName("Moderative")
                .displayIcon(Material.REDSTONE)
                .description("Moderative tools")
                .rank(Rank.DEVELOPER)
                .visibilityController((slp) -> slp.getRank().hasPermission(Rank.DEVELOPER))
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
                            //GamePlugin.cancelAllMatches();
                            //event.getPlayer().sendMessage(Theme.SUCCESS + "All games have been cancelled.");
                        })
                );
        return builder;
    }

    public static InventoryMenuTemplateBuilder getNewGamemodeMenu() {
        InventoryMenuTemplateBuilder builder = menu();
        slMenuBuilder.component(currentGamemodeMenu++, builder);
        return builder;
    }

    public static InventoryMenuTemplate getInstance() {
        return slMenu;
    }
}
