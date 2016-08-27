package com.spleefleague.core.utils.rines;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents RButton which sends specified message to a clicker-player.
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class RMessageButton extends RButton {
    
    private final String msg;

    public RMessageButton(Material icon, String name, List<String> description, String msg) {
        super(icon, name, description);
        this.msg = UtilChat.c(msg);
    }
    
    public RMessageButton(Material icon, int data, String name, List<String> description, String msg) {
        super(icon, data, name, description);
        this.msg = UtilChat.c(msg);
    }
    
    public RMessageButton(ItemStack is, String msg) {
        super(is);
        this.msg = UtilChat.c(msg);
    }

    public RMessageButton(Material icon, String name, List<String> description, String prefix, String msg) {
        super(icon, name, description);
        this.msg = UtilChat.pc(prefix, msg);
    }
    
    public RMessageButton(Material icon, int data, String name, List<String> description, String prefix, String msg) {
        super(icon, data, name, description);
        this.msg = UtilChat.pc(prefix, msg);
    }
    
    public RMessageButton(ItemStack is, String prefix, String msg) {
        super(is);
        this.msg = UtilChat.pc(prefix, msg);
    }

    @Override
    public void onClick(Player p, int slot) {
        p.sendMessage(msg);
    }

}
