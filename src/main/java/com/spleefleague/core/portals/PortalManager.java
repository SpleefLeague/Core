package com.spleefleague.core.portals;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.io.*;
import com.spleefleague.core.listeners.PortalListener;
import com.spleefleague.core.utils.DatabaseConnection;
import org.bson.Document;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Josh on 15/02/2016.
 */
public class PortalManager {

    private List<Portal> portals;

    public PortalManager() {
        this.portals = new ArrayList<>();
        DatabaseConnection.find(SpleefLeague.getInstance().getPluginDB().getCollection("Portals"), new Document("locationOne", new Document("$exists", true)).append("locationTwo", new Document("$exists", true)).append("teleport", new Document("$exists", true)), (result -> {
            result.forEach((Consumer<Document>) (Document document) -> portals.add(EntityBuilder.load(document, Portal.class)));
        }));
        PortalListener.init();
    }

    /**
     * Get a portal by location.
     *
     * @param check location to get portal by.
     * @return Portal object if exists, or null if not.
     */
    public Portal getByLocation(Location check) {
        for (Portal portal : portals) {
            if (portal.within(check)) {
                return portal;
            }
        }
        return null;
    }

    public static class Portal extends DBEntity implements DBLoadable {

        @DBLoad(fieldName = "locationOne", typeConverter = TypeConverter.LocationConverter.class)
        private Location locationOne;
        @DBLoad(fieldName = "locationTwo", typeConverter = TypeConverter.LocationConverter.class)
        private Location locationTwo;
        @DBLoad(fieldName = "teleport", typeConverter = TypeConverter.LocationConverter.class)
        private Location teleport;

        public Portal(Location teleport) {
            this.locationOne = null;
            this.locationTwo = null;
            this.teleport = teleport;
        }

        public Location getTeleportTo() {
            return teleport;
        }

        public Location getLocationOne() {
            return locationOne;
        }

        public Location getLocationTwo() {
            return locationTwo;
        }

        public boolean within(Location location) {
            return locationOne != null && locationTwo != null
                    && location.getWorld().getName().equals(locationOne.getWorld().getName())
                    && ((location.getBlockX() >= locationOne.getBlockX() && location.getBlockX() <= locationTwo.getBlockX()) || (location.getBlockX() <= locationOne.getBlockX() && location.getBlockX() >= locationTwo.getBlockX()))
                    && ((location.getBlockZ() >= locationOne.getBlockZ() && location.getBlockZ() <= locationTwo.getBlockZ()) || (location.getBlockZ() <= locationOne.getBlockZ() && location.getBlockZ() >= locationTwo.getBlockZ()))
                    && ((location.getBlockY() >= locationOne.getBlockY() && location.getBlockY() <= locationTwo.getBlockY()) || (location.getBlockY() <= locationOne.getBlockY() && location.getBlockY() >= locationTwo.getBlockY()));
        }

    }

}
