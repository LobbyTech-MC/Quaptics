package org.metamechanists.quaptics.implementation.base;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.scheduler.BukkitRunnable;
import org.metamechanists.quaptics.Quaptics;

public class BurnoutRunnable extends BukkitRunnable {
    private final Location location;

    public BurnoutRunnable(Location location) {
        this.location = location;
    }

    @Override
    public void run() {
        final Block block = location.getBlock();
        if (!(BlockStorage.check(block) instanceof ConnectedBlock connectedBlock)) {
            return;
        }

        for (int delay = 1; delay < 60; delay++) {
            Bukkit.getScheduler().runTaskLater(Quaptics.getInstance(), () -> {
                final DisplayGroup group = connectedBlock.getDisplayGroup(location);
                if (group != null) {
                    group.getDisplays().values().forEach(display -> {
                        final Display.Brightness brightness = display.getBrightness();
                        display.setBrightness(new Display.Brightness(
                                brightness == null ? 14 : Math.min(0, brightness.getBlockLight() - 1),
                                brightness == null ? 14 : Math.min(0, brightness.getSkyLight() - 1)));
                    });
                    location.getWorld().spawnParticle(Particle.LAVA, location.clone().add(ConnectedBlock.RELATIVE_CENTER), 1);
                }
            }, delay);
        }

        Bukkit.getScheduler().runTaskLater(Quaptics.getInstance(), () -> connectedBlock.burnout(block.getLocation()), 60L);
    }
}
