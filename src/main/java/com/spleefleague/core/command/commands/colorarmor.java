package com.spleefleague.core.command.commands;

import com.spleefleague.core.command.BasicCommand;
import com.spleefleague.core.player.Rank;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.plugin.CorePlugin;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class colorarmor extends BasicCommand {

    public colorarmor(CorePlugin plugin, String name, String usage) {
        super(plugin, name, usage, Rank.SENIOR_MODERATOR);
    }

    @Override
    protected void run(Player p, SLPlayer slp, Command cmd, String[] args) {
        if (args.length < 3) {
            sendUsage(p);
            return;
        }
        ItemStack is = p.getItemInHand();
        if (is == null || !isLeatherArmor(is)) {
            error(p, "You must be holding a leather armor piece");
            return;
        }
        int r = 0;
        int g = 0;
        int b = 0;
        try {
            r = Integer.parseInt(args[0]);
            g = Integer.parseInt(args[1]);
            b = Integer.parseInt(args[2]);
            r = Math.min(Math.max(0, r), 255);
            g = Math.min(Math.max(0, g), 255);
            b = Math.min(Math.max(0, b), 255);
        } catch (Exception e) {
            error(p, "Invalid color, should be number between 0-255");
            return;
        }
        LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
        meta.setColor(Color.fromRGB(r, g, b));
        is.setItemMeta(meta);
        success(p, "Color applied");
    }

    private Material[] armors = new Material[] {
            Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET, Material.LEATHER_LEGGINGS
    };

    public boolean isLeatherArmor(ItemStack is) {
        for (int i = 0; i < armors.length; i++) {
            if (is.getType() == armors[i]) {
                return true;
            }
        }
        return false;
    }

}
