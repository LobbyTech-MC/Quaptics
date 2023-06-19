package org.metamechanists.death_lasers;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.inventory.ItemStack;
import org.metamechanists.death_lasers.implementation.blocks.Emitter;
import org.metamechanists.death_lasers.implementation.blocks.Lens;
import org.metamechanists.death_lasers.implementation.tools.TargetingWand;

import static org.metamechanists.death_lasers.ItemStacks.*;

public class Items {
    public static final TargetingWand targetingWand = new TargetingWand(
            Groups.DEATH_LASER_GROUP,
            TARGETING_WAND,
            RecipeType.NULL,
            new ItemStack[] {});
    public static final Emitter emitter = new Emitter(
            Groups.DEATH_LASER_GROUP,
            EMITTER,
            RecipeType.NULL,
            new ItemStack[] {},
            1000,
            100);
    public static final Lens lens = new Lens(
            Groups.DEATH_LASER_GROUP,
            LENS,
            RecipeType.NULL,
            new ItemStack[] {},
            1000,
            100);

    public static void initialize() {
        final SlimefunAddon addon = DEATH_LASERS.getInstance();

        targetingWand.register(addon);
        emitter.register(addon);
        lens.register(addon);
    }
}
