package com.spleefleague.core.utils.rines;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * This class represents RButton which does nothing on click.
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class REmptyButton extends RButton {

    public REmptyButton(Material icon, String name, List<String> description) {
        super(icon, name, description);
    }
    
    public REmptyButton(Material icon, int data, String name, List<String> description) {
        super(icon, data, name, description);
    }
    
    public REmptyButton(ItemStack is) {
        super(is);
    }

    @Override
    public void onClick(Player p, int slot) {
        //Do nothing
    }

}
