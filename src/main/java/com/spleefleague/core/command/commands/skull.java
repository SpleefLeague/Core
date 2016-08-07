package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Created by Josh on 06/08/2016.
 */
public class skull extends BasicCommand {

    public skull(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.SENIOR_MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if(args.length != 1) {
            sendUsage(p);
            return;
        }
        ItemStack give = new ItemStack(Material.SKULL_ITEM);
        give.setDurability((short) 3);

        SkullMeta skullMeta = (SkullMeta) give.getItemMeta();
        skullMeta.setOwner(args[0]);
        give.setItemMeta(skullMeta);

        slp.getInventory().addItem(give);
        success(slp, "You have been given " + args[0] + "'s head!");
    }

}
