package com.spleefleague.core.utils.fakeentity;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *
 * @author 0xC0deBabe <iam@kostya.sexy>
 */
public abstract class FakeEquippableCreature extends FakeCreature {
    
    private final FakeEquipment equipment = new FakeEquipment(this);

    public FakeEquippableCreature(int id, Location location) {
        super(id, location);
    }

    public FakeEquippableCreature(Location location) {
        super(location);
    }
    
    public FakeEquipment getEquipment() {
        return equipment;
    }
    
    @Override
    protected void show(Player p) {
        super.show(p);
        equipment.show(p);
    }

}
