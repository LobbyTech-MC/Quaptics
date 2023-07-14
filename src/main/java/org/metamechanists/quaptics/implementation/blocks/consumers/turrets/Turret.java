package org.metamechanists.quaptics.implementation.blocks.consumers.turrets;

import dev.sefiraat.sefilib.entity.display.DisplayGroup;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.metamechanists.quaptics.connections.ConnectionGroup;
import org.metamechanists.quaptics.connections.ConnectionPoint;
import org.metamechanists.quaptics.connections.ConnectionPointType;
import org.metamechanists.quaptics.connections.Link;
import org.metamechanists.quaptics.implementation.attachments.PowerAnimatedBlock;
import org.metamechanists.quaptics.implementation.base.ConnectedBlock;
import org.metamechanists.quaptics.implementation.blocks.Settings;
import org.metamechanists.quaptics.utils.BlockStorageAPI;
import org.metamechanists.quaptics.utils.Keys;
import org.metamechanists.quaptics.utils.id.complex.ConnectionGroupId;
import org.metamechanists.quaptics.utils.models.ModelBuilder;
import org.metamechanists.quaptics.utils.models.components.ModelCuboid;
import org.metamechanists.quaptics.utils.models.components.ModelLine;
import org.metamechanists.quaptics.utils.transformations.TransformationUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class Turret extends ConnectedBlock implements PowerAnimatedBlock {
    private static final int ARBITRARILY_LARGE_NUMBER = 9999999;
    private final Vector inputLocation = new Vector(0.0F, 0.0F, -settings.getConnectionRadius());

    protected Turret(final ItemGroup itemGroup, final SlimefunItemStack item, final RecipeType recipeType, final ItemStack[] recipe, final Settings settings) {
        super(itemGroup, item, recipeType, recipe, settings);
    }

    @Override
    protected float getConnectionRadius() {
        return settings.getConnectionRadius();
    }
    @Override
    protected DisplayGroup initModel(final @NotNull Location location, final @NotNull Player player) {
        return new ModelBuilder()
                .add("plate", new ModelCuboid()
                        .material(settings.getMainMaterial())
                        .size(0.6F))
                .add("barrel", new ModelCuboid()
                        .material(Material.GRAY_CONCRETE))
                .add("power1", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .size(0.95F, 0.3F, 1.05F)
                        .location(0, -0.25F, 0))
                .add("power2", new ModelCuboid()
                        .material(settings.getTier().concreteMaterial)
                        .size(1.05F, 0.3F, 0.95F)
                        .location(0, -0.25F, 0))
                .buildAtBlockCenter(location);
    }
    @Override
    protected List<ConnectionPoint> initConnectionPoints(final ConnectionGroupId groupId, final Player player, final Location location) {
        return List.of(new ConnectionPoint(ConnectionPointType.INPUT, groupId, "input", formatPointLocation(player, location, inputLocation)));
    }

    @Override
    protected void onSlimefunTick(@NotNull final Block block, final SlimefunItem item, final Config data) {
        if (BlockStorageAPI.getBoolean(block.getLocation(), Keys.BS_POWERED)) {
            retarget(block.getLocation());
            shoot(block.getLocation());
        }
    }
    @Override
    public void onInputLinkUpdated(@NotNull final ConnectionGroup group, @NotNull final Location location) {
        if (doBurnoutCheck(group, "input")) {
            return;
        }

        BlockStorageAPI.set(location, Keys.BS_POWERED, false);

        final Optional<Link> inputLink = getLink(location, "input");
        onPoweredAnimation(location, settings.isOperational(inputLink));
        if (settings.isOperational(inputLink)) {
            BlockStorageAPI.set(location, Keys.BS_POWERED, true);
        }
    }
    @Override
    public void onPoweredAnimation(final Location location, final boolean powered) {
        brightnessAnimation(location, "power1", powered);
        brightnessAnimation(location, "power2", powered);
    }

    private static @NotNull Matrix4f getBarrelMatrix(@NotNull final Location from, final Location to) {
        final Vector3f barrelDirection = TransformationUtils.getDirection(from, to);
        return new ModelLine()
                .to(barrelDirection.mul(0.8F))
                .thickness(0.2F)
                .getMatrix();
    }
    private static void updateBarrelTransformation(final Location location, final LivingEntity target) {
        getDisplay(location, "barrel").ifPresent(value -> value.setTransformationMatrix(getBarrelMatrix(location.toCenterLocation(), target.getEyeLocation())));
    }

    private static void setTarget(@NotNull final Location location, @NotNull final Entity entity) {
        BlockStorageAPI.set(location, Keys.BS_TARGET, entity.getUniqueId());
    }
    private static void clearTarget(final Location location) {
        BlockStorageAPI.set(location, Keys.BS_TARGET, (UUID) null);
    }
    private static Optional<LivingEntity> getTarget(final Location location) {
        final Optional<UUID> targetUuid = BlockStorageAPI.getUuid(location, Keys.BS_TARGET);
        return targetUuid.map(uuid -> (LivingEntity) Bukkit.getEntity(uuid));
    }
    private static Optional<LivingEntity> getClosestEntity(@NotNull final Collection<? extends Entity> entities, final Location location) {
        LivingEntity target = null;
        double targetDistance = ARBITRARILY_LARGE_NUMBER;
        for (final Entity entity : entities) {
            final double distance = entity.getLocation().distance(location.toCenterLocation());
            if (distance < targetDistance) {
                target = (LivingEntity) entity;
                targetDistance = distance;
            }
        }
        return Optional.ofNullable(target);
    }

    private void retarget(@NotNull final Location location) {
        if (BlockStorageAPI.hasData(location, Keys.BS_TARGET)) {
            return;
        }

        final Collection<Entity> entities = location.getWorld()
                .getNearbyEntities(location, settings.getRange(), settings.getRange(), settings.getRange(),
                        (entity -> settings.getTargets().contains(entity.getSpawnCategory())
                                && entity instanceof LivingEntity
                                && entity.getLocation().distance(location) < settings.getRange()));

        if (entities.isEmpty()) {
            return;
        }

        final Optional<LivingEntity> closestEntity = getClosestEntity(entities, location);
        closestEntity.ifPresent(livingEntity -> setTarget(location, livingEntity));
    }
    private void shoot(final Location location) {
        final Optional<LivingEntity> target = getTarget(location);
        if (target.isEmpty() || target.get().isDead() || location.toCenterLocation().distance(target.get().getLocation()) > settings.getRange()) {
            clearTarget(location);
            return;
        }

        updateBarrelTransformation(location, target.get());
        createProjectile(location, target.get().getEyeLocation());
        target.get().damage(settings.getDamage());
    }
    protected abstract void createProjectile(final Location source, final Location target);
}
