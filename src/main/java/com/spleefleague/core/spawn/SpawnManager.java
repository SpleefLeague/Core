package com.spleefleague.core.spawn;

import com.spleefleague.core.SpleefLeague;
import com.spleefleague.core.utils.ModifiableFinal;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Josh on 05/02/2016.
 */
public class SpawnManager {

    public static final Integer RADIUS = 20;

    private final List<SpawnLocation> spawnLocations;
    private long lastCached;

    public SpawnManager(List<SpawnLocation> spawnLocations) {
        this.spawnLocations = spawnLocations;
        Bukkit.getScheduler().runTaskTimer(SpleefLeague.getInstance(), () -> {
            this.spawnLocations.forEach(SpawnLocation::calculatePlayerRadius);
            this.lastCached = System.currentTimeMillis();
        }, 0, 600);
    }

    /**
     * Get the next spawn location (with the least players).
     *
     * @return SpawnLocation instance - null if none defined.
     */
    public SpawnLocation getNext() {
        List<SpawnLocation> spawnLocations = this.spawnLocations.stream().sorted((e1, e2) -> Integer.compare(e1.getPlayersInRadius(), e2.getPlayersInRadius())).collect(Collectors.toList());
        return spawnLocations.get(0);
    }

    /**
     * Get the time when each spawn location was last cached.
     *
     * @return long millis.
     */
    public long getLastCached() {
        return lastCached;
    }

    /**
     * Get a list of all loaded SpawnLocations.
     *
     * @return list, shouldn't be null.
     */
    public List<SpawnLocation> getAll() {
        return spawnLocations;
    }

    public static class SpawnLocation {

        private final Location location;
        private int playersInRadius;

        public SpawnLocation(Location location) {
            this.location = location;
            this.playersInRadius = 0;
        }

        public Location getLocation() {
            return location;
        }

        public int getPlayersInRadius() {
            return playersInRadius;
        }

        public void incrementPlayersInRadius() {
            playersInRadius++;
        }

        /**
         * Calculates the amount of players in a circle radius from the spawnpoint.
         */
        public void calculatePlayerRadius() {
            double radiusSquared = RADIUS * RADIUS;
            ModifiableFinal<Integer> result = new ModifiableFinal<>(0);
            location.getWorld().getPlayers().forEach((Player player) -> {
                if(player.getLocation().distanceSquared(location) <= radiusSquared) {
                    result.setValue(result.getValue() + 1);
                }
            });
            this.playersInRadius = result.getValue();
        }

    }

}
