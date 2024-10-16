package org.metamechanists.quaptics.implementation.tools.multiblockwand;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.metamechanists.quaptics.storage.PersistentDataTraverser;
import org.metamechanists.quaptics.utils.Language;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MultiblockWand extends SlimefunItem {
    public static final SlimefunItemStack MULTIBLOCK_WAND = new SlimefunItemStack(
            "QP_MULTBLOCK_WAND",
            Material.AMETHYST_SHARD,
            "&b多方块棒",
            "&7● 向你展示如何建造复杂的多方块结构。",
            "&7● &e右键点击&7多方块结构以查看如何建造。",
            "&7● &e右键点击&7投影方块以了解该方块是什么。");

    public MultiblockWand(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public static void removeProjection(final ItemStack itemStack) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(itemStack);
        final List<UUID> uuids = traverser.getUuidList("uuids");
        if (uuids == null) {
            return;
        }

        uuids.stream()
                .map(Bukkit::getEntity)
                .filter(Objects::nonNull)
                .forEach(Entity::remove);
    }

    public static void tellPlayerBlock(final @NotNull Entity interaction, final Player player) {
        final PersistentDataTraverser traverser = new PersistentDataTraverser(interaction.getUniqueId());
        if (traverser.getString("blockName") == null) {
            return;
        }

        Language.sendLanguageMessage(player, "multiblock.block-name", traverser.getString("blockName"));
    }
}
