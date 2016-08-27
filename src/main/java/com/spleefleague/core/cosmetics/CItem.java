package com.spleefleague.core.cosmetics;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.chat.Theme;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.rines.SimpleItemStack;
import com.spleefleague.core.utils.rines.UtilChat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
@Data
public abstract class CItem {

    private final int id;
    
    private final String name;
    
    private final CType type;
    
    private final List<String> description;
    
    private final int costInCoins, costInPremiumCredits;
    
    private ItemStack icon, emptyIcon;
    
    public CItem(int id, String name, CType type, int costInCoins, int costInPremiumCredits) {
        this(id, name, type, new ArrayList<>(), costInCoins, costInPremiumCredits);
    }
    
    public CItem(int id, String name, CType type, List<String> description, int costInCoins, int costInPremiumCredits) {
        this.id = id;
        this.name = UtilChat.c("&f%s", name);
        this.type = type;
        this.description = description.stream().map(UtilChat::c).collect(Collectors.toList());
        this.costInCoins = costInCoins;
        this.costInPremiumCredits = costInPremiumCredits;
        
        List<String> lore = new ArrayList<>();
        lore.addAll(description);
        lore.add("");
        lore.add(UtilChat.c("&7Left click to buy"));
        lore.add(UtilChat.c("&7it for &6%d coins", costInCoins));
        lore.add("");
        lore.add(UtilChat.c("&7Right click to buy"));
        lore.add(UtilChat.c("&7it for &b%d premium credits", costInPremiumCredits));
        this.emptyIcon = new SimpleItemStack(Material.INK_SACK, getName(), lore, (short) 8);
    }
    
    public boolean select(Player p) {
        SpleefLeague.getInstance().getPlayerManager().get(p).getCollectibles().addActive(this);
        onSelecting(p);
        return true;
    }
    
    public void buy(Player p, boolean usingCoins) {
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
        //check for already having
        if(usingCoins)
            if(slp.getCoins() >= costInCoins) {
                slp.changeCoins(-costInCoins);
                SpleefLeague.getInstance().getPlayerManager().get(p).getCollectibles().addItem(id);
                UtilChat.s(Theme.SUCCESS, p, "You have just bought %s for %d coins.", name, costInCoins);
            }else {
                UtilChat.s(Theme.ERROR, p, "You don't have coins enough to buy this.");
            }
        else
            if(slp.getPremiumCredits() >= costInPremiumCredits) {
                slp.changeCoins(-costInPremiumCredits);
                SpleefLeague.getInstance().getPlayerManager().get(p).getCollectibles().addItem(id);
                UtilChat.s(Theme.SUCCESS, p, "You have just bought %s for %d premium credits.", name, costInPremiumCredits);
            }else {
                UtilChat.s(Theme.ERROR, p, "You don't have premium credits enough to buy this.");
            }
    }
    
    public abstract void onSelecting(Player p);
    
    public abstract void onRemoving(Player p);
    
    protected void setIcon(ItemStack prototype) {
        this.icon = prototype.clone();
        ItemMeta im = icon.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.addAll(description);
        lore.add("");
        lore.add(UtilChat.c("&aClick to select!"));
        im.setLore(lore);
        this.icon.setItemMeta(im);
    }
    
}
