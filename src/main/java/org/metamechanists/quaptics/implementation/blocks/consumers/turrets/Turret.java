package org.metamechanists.quaptics.implementation.blocks.consumers.turrets;

import com.google.common.base.Objects;
import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.points.ConnectionPoint;
import org.metamechanists.quaptics.connections.points.ConnectionPointInput;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.base.Settings;
import org.metamechanists.quaptics.utils.Keys;
import org.metamechanists.quaptics.utils.Transformations;
import org.metamechanists.quaptics.utils.builders.BlockDisplayBuilder;
import org.metamechanists.quaptics.utils.id.ConnectionGroupId;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public abstract class Turret extends ConnectedBlock {
    private final Vector3f mainDisplaySize = new Vector3f(0.6F, 0.6F, 0.6F);
    private final Vector3f barrelSize = new Vector3f(0.18F, 0.18F, settings.getDisplayRadius()*1.3F);
    private final Vector3f barrelTranslation = new Vector3f(0, 0, settings.getDisplayRadius()*0.8F);
    protected final Vector barrelLocation = new Vector(0.5, 0.7, 0.5);
    private final Vector inputLocation = new Vector(0.0F, 0.0F, -settings.getConnectionRadius());

    protected Turret(ItemGroup group, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe, Settings settings) {
        super(group, item, recipeType, recipe, settings);
    }

    protected Matrix4f getBarrelMatrix(@NotNull Location from, Location to) {
        return Transformations.lookAlong(barrelSize, Transformations.getDirection(from.clone().add(barrelLocation), to)).translate(barrelTranslation);
    }

    private BlockDisplay generateBarrel(@NotNull Location from, Location to) {
        return new BlockDisplayBuilder(from.clone().add(barrelLocation))
                .setMaterial(Material.GRAY_CONCRETE)
                .setTransformation(getBarrelMatrix(from, to))
                .build();
    }

    @Override
    protected void addDisplays(@NotNull DisplayGroup displayGroup, @NotNull Location location, Player player) {
        displayGroup.addDisplay("main", new BlockDisplayBuilder(location.toCenterLocation())
                        .setMaterial(settings.getMainMaterial())
                        .setTransformation(Transformations.adjustedScale(mainDisplaySize))
                        .build());
        displayGroup.addDisplay("barrel", generateBarrel(location, location.clone().add(barrelLocation).add(new Vector(0, -1, 0))));
    }

    @Override
    protected List<ConnectionPoint> generateConnectionPoints(ConnectionGroupId groupId, Player player, Location location) {
        return List.of(new ConnectionPointInput(groupId, "input", formatPointLocation(player, location, inputLocation)));
    }

    @Override
    public void onInputLinkUpdated(@NotNull ConnectionGroup group) {
        final ConnectionPoint input = group.getPoint("input");
        if (input == null || doBurnoutCheck(group, input)) {
            return;
        }

        final Location location = group.getLocation();
        if (location != null) {
            BlockStorage.addBlockInfo(location, Keys.BS_POWERED, "false");
        }

        if (!input.hasLink()) {
            return;
        }

        if (settings.checkPower(input.getLink().getPower()) && settings.checkFrequency(input.getLink().getFrequency())) {
            BlockStorage.addBlockInfo(group.getLocation(), Keys.BS_POWERED, "true");
        }
    }

    private void setTarget(@NotNull Location location, @NotNull Damageable entity) {
        BlockStorage.addBlockInfo(location, Keys.BS_TARGET, entity.getUniqueId().toString());
    }

    protected void clearTarget(Location location) {
        BlockStorage.addBlockInfo(location, Keys.BS_TARGET, null);
    }

    protected @Nullable LivingEntity getTarget(Location location) {
        final String targetString = BlockStorage.getLocationInfo(location, Keys.BS_TARGET);
        if (targetString == null) {
            return null;
        }

        return (LivingEntity) Bukkit.getEntity(UUID.fromString(targetString));
    }

    private LivingEntity getClosestEntity(@NotNull Collection<Entity> entities, Location location) {
        LivingEntity target = null;
        double targetDistance = 9999999;
        for (Entity entity : entities) {
            final double distance = entity.getLocation().distance(location.toCenterLocation());
            if (distance < targetDistance) {
                target = (LivingEntity) entity;
                targetDistance = distance;
            }
        }
        return target;
    }

    protected void retarget(@NotNull Location location) {
        if (BlockStorage.getLocationInfo(location, Keys.BS_TARGET) != null) {
            return;
        }

        final Collection<Entity> entities = location.getWorld()
                .getNearbyEntities(location, settings.getRange(), settings.getRange(), settings.getRange(),
                        (entity -> settings.getTargets().contains(entity.getSpawnCategory())
                        && entity instanceof Damageable
                        && entity.getLocation().distance(location) < settings.getRange()));

        if (entities.isEmpty()) {
            return;
        }

        final LivingEntity closestEntity = getClosestEntity(entities, location);
        if (closestEntity != null) {
            setTarget(location, closestEntity);
        }
    }

    protected abstract void shoot(Location location);

    @Override
    protected void onSlimefunTick(@NotNull Block block, SlimefunItem item, Config data) {
        if (Objects.equal(BlockStorage.getLocationInfo(block.getLocation(), Keys.BS_POWERED), "true")) {
            retarget(block.getLocation());
            shoot(block.getLocation());
        }
    }
}
