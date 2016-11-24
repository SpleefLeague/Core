package com.spleefleague.core.utils.fakeentity.exact;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.menus.CosmeticsMenu;
import com.spleefleague.core.player.SLPlayer;
import com.spleefleague.core.utils.fakeentity.FakeNpc;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public class CosmeticsMaster extends FakeNpc {

    public CosmeticsMaster(UUID uuid, String name, Location location) {
        super(uuid, "Cosmetics Master", location);
    }
    
    @Override
    public void onRightClick(Player p) {
        lookAt(p);
        SLPlayer slp = SpleefLeague.getInstance().getPlayerManager().get(p);
        CosmeticsMenu.getMenu().construct(slp).open();
    }

}
