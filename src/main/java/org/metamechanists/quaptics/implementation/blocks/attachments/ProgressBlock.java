package org.metamechanists.quaptics.implementation.blocks.attachments;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.items.Lore;
import org.metamechanists.quaptics.utils.BlockStorageAPI;
import org.metamechanists.quaptics.utils.Keys;

public interface ProgressBlock {
    static void setProgress(final Location location, final double progress) {
        BlockStorageAPI.set(location, Keys.BS_PROGRESS, progress);
    }

    static double getProgress(final Location location) {
        return BlockStorageAPI.getDouble(location, Keys.BS_PROGRESS);
    }

    static String progressBar(final @NotNull ConnectionGroup group) {
        final java.util.Optional<Location> location = group.getLocation();
        if (location.isEmpty()) {
            return "&cERROR";
        }

        final double progress = getProgress(location.get());
        final double maxTime = group.getBlock().getSettings().getTimePerItem();
        return Lore.progressBar(progress, maxTime, "&a", "&7");
    }
}
