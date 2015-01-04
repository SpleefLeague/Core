/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.spleefleague.core.utils.menu;

import java.util.ArrayList;
import java.util.Collection;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Jonas
 */
public class SubMenu extends Menu {

    private final SubMenu root;
    private final Collection<SubMenu> subMenus;
    private final ItemStack item;
    
    public SubMenu(String name, Player p, SubMenu root, Collection<SubMenu> subMenus, ItemStack item) {
        super(name, p);
        this.root = root;
        if(subMenus == null) {
            subMenus = new ArrayList<>();
        }
        this.subMenus = subMenus;
        this.item = item;
        build();
    }
    
    public SubMenu(String name, Player p, SubMenu root, ItemStack item) {
        this(name, p, root, new ArrayList<SubMenu>(), item);
    }
    
    public SubMenu(String name, Player p, SubMenu root, Collection<SubMenu> subMenus, Material m) {
        super(name, p);
        this.root = root;
        if(subMenus == null) {
            subMenus = new ArrayList<>();
        }
        this.subMenus = subMenus;
        this.item = new ItemStack(m);
        ItemMeta im = this.item.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + name);
        this.item.setItemMeta(im);
        build();
    }
    
    public SubMenu(String name, Player p, SubMenu root, Material m) {
        this(name, p, root, new ArrayList<SubMenu>(), m);
    }
    
    public void addSubMenu(SubMenu subMenu) {
        subMenus.add(subMenu);
        build();
    }
    
    public SubMenu getRootMenu() {
        return root;
    }
    
    public Collection<SubMenu> getSubMenus() {
        return subMenus;
    }
    
    public ItemStack getItem() {
        return item;
    }
    
    public boolean isRoot() {
        return root == null;
    }
    
    public void back() {
        this.close();
        if(this.getRootMenu() != null) {    
            this.getRootMenu().show();
        }
    }
    
    @Override
    public int[] getAvailableSlots(int amount) {
        int pointer = 0;
        int[] available = new int[amount];
        for(int i = 0; pointer < amount; i++) {
            if(super.isSlotFree(i)) {
                available[pointer] = i;
                pointer++;
            }
            if(i % 9 == 6) {
                i++;
            }
        }
        return available;
    }

    private void build() {
        int x = 0, y = 0;
        final SubMenu instance = this;
        for(final SubMenu menu : subMenus) {
            super.addItem(x + y * 9, new MenuItem(ChatColor.GREEN + menu.getName(), menu.getItem()).setLore(new String[]{ChatColor.GRAY + "Click to go open the \"" + menu.getName() + "\" menu"}).setListener(new MenuClickListener() {
                @Override
                public void onClick(InventoryClickEvent event, MenuItem item) {
                    instance.close();
                    menu.show();
                }
            }));
            if(x > 6) {
                x = 0;
                y++;
            }
            else {
                x++;
            }
        }
        super.addItem(8, new MenuItem(ChatColor.GREEN + "Close Menu", Material.MINECART).setLore(new String[]{ChatColor.GRAY + "Click to go back to the main menu."}).setListener(new MenuClickListener() {
            @Override
            public void onClick(InventoryClickEvent event, MenuItem item) {
                back();
            }
        }));
    }
}
