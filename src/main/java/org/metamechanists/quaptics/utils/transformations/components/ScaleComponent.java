package org.metamechanists.quaptics.utils.transformations.components;

import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a scale transformation in X, Y, and Z.
 */
public class ScaleComponent implements TransformationMatrixComponent {
    private final Vector3f scale;

    public ScaleComponent(@NotNull final Vector3f scale) {
        this.scale = scale;
    }

    @Override
    public void apply(@NotNull final Matrix4f matrix) {
        matrix.scale(scale);
    }
}
