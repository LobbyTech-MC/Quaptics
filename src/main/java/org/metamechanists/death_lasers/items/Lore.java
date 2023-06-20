package org.metamechanists.death_lasers.items;

import org.metamechanists.death_lasers.utils.Colors;

import java.util.Objects;

public class Lore {
    public static String emissionPower(double emissionPower) {
        return "&8⇨ " + Colors.EMISSION_POWER.getString() + "⏻ &7Emission Power &e" + Objects.toString(emissionPower) + " &8W";
    }

    public static String maxPower(double maxPower) {
        return "&8⇨ " + Colors.MAX_POWER.getString() + "⏻ &7Max Power &e" + Objects.toString(maxPower) + " &8W";
    }

    public static String powerLoss(int powerLossPercentage) {
        return "&8⇨ " + Colors.POWER_LOSS.getString() + "⏻ &7Max Power Loss &e" + Objects.toString(powerLossPercentage) + " &8%";
    }
}
