package com.spleefleague.core.cosmetics.items;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.cosmetics.CItem;
import com.spleefleague.core.cosmetics.CType;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class ParticleEffectItem extends CItem {
    
    private final static Map<Player, ParticleEffectItem> AFFECTED_PLAYERS = new HashMap<>();
    private final static long INTERVAL = 60l;
    private final static Random R = new Random();
    
    static {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(SpleefLeague.getInstance(),
                () -> AFFECTED_PLAYERS.forEach((p, i) -> i.sendToPlayer(p)),
                INTERVAL, INTERVAL);
    }
    
    private static void sendToLocation(EnumWrappers.Particle effect, Location loc, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        WrapperPlayServerWorldParticles wrapper = new WrapperPlayServerWorldParticles();
        wrapper.setParticleType(effect);
        wrapper.setX((float) loc.getX());
        wrapper.setY((float) loc.getY());
        wrapper.setZ((float) loc.getZ());
        wrapper.setOffsetX(offsetX);
        wrapper.setOffsetX(offsetY);
        wrapper.setOffsetX(offsetZ);
        wrapper.setNumberOfParticles(count);
        Bukkit.getOnlinePlayers().forEach(wrapper::sendPacket);
    }
    
    //All magic in this function happens cause vanilla minecraft
    //offset doesn't work properly by some reason
    private void sendToPlayer(Player p) {
        final float offset = 1f, mcoffset = 0.2f;
        Location loc = p.getLocation();
        int i = 0;
        double x = loc.getX(), y = loc.getY(), z = loc.getZ();
        do {
            loc.setY(y += (1 + R.nextInt(5)) * 0.15f);
            loc.setX(x + ((100 - R.nextInt(200)) / 100f * offset));
            loc.setZ(z + ((100 - R.nextInt(200)) / 100f * offset));
            sendToLocation(effect, loc, mcoffset, mcoffset, mcoffset, 0.75f, 6);
            ++i;
        }while(i < 4);
    }
    
    private final EnumWrappers.Particle effect;

    public ParticleEffectItem(int id, String name, EnumWrappers.Particle effect, int costInCoins, int costInPremiumCredits) {
        super(id, name, CType.PARTICLE_EFFECT, costInCoins, costInPremiumCredits);
        this.effect = effect;
    }
    
    public EnumWrappers.Particle getEffect() {
        return effect;
    }

    @Override
    public void onSelecting(Player p) {
        AFFECTED_PLAYERS.put(p, this);
    }

    @Override
    public void onRemoving(Player p) {
        AFFECTED_PLAYERS.remove(p);
    }

}