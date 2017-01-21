package com.spleefleague.core.cosmetics.items;

import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import com.spleefleague.core.utils.SafePlayerTask;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class StatusEffectItem extends CItem {
    
    private final PotionEffect effect;

    public StatusEffectItem(int id, String name, PotionEffectType effectType, int effectLevel, int costInCoins, int costInPremiumCredits) {
        super(id, name, CType.STATUS_EFFECT, costInCoins, costInPremiumCredits);
        this.effect = new PotionEffect(effectType, Integer.MAX_VALUE, effectLevel - 1);
    }
    
    public PotionEffect getEffect() {
        return effect;
    }

    @Override
    public void onSelecting(Player p) {
        SafePlayerTask.call(p, player -> player.addPotionEffect(effect));
    }

    @Override
    public void onRemoving(Player p) {
        SafePlayerTask.call(p, player -> player.removePotionEffect(effect.getType()));
    }

}
