package com.ose.rs1plus.block;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

final class PanelShapes {

    private static final Map<Direction, VoxelShape> BY_DIRECTION = build();

    static VoxelShape forDirection(Direction direction) {
        return BY_DIRECTION.getOrDefault(direction, Shapes.block());
    }

    private static Map<Direction, VoxelShape> build() {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

        shapes.put(Direction.NORTH, or(
                voxel(2, 2, 13.3, 14, 14, 15),
                or(
                        or(
                                voxel(5, 5, 15, 11, 11, 15.9),
                                voxel(6, 6, 15.9, 10, 10, 16)
                        ),
                        cornerBrackets(Direction.NORTH)
                )
        ));

        shapes.put(Direction.EAST, or(
                voxel(1, 2, 2, 2.7, 14, 14),
                or(
                        or(
                                voxel(0.1, 5, 5, 1, 11, 11),
                                voxel(0, 6, 6, 0.1, 10, 10)
                        ),
                        cornerBrackets(Direction.EAST)
                )
        ));

        shapes.put(Direction.SOUTH, or(
                voxel(2, 2, 1, 14, 14, 2.7),
                or(
                        or(
                                voxel(5, 5, 0.1, 11, 11, 1),
                                voxel(6, 6, 0, 10, 10, 0.1)
                        ),
                        cornerBrackets(Direction.SOUTH)
                )
        ));

        shapes.put(Direction.WEST, or(
                voxel(13.3, 2, 2, 15, 14, 14),
                or(
                        or(
                                voxel(15, 5, 5, 15.9, 11, 11),
                                voxel(15.9, 6, 6, 16, 10, 10)
                        ),
                        cornerBrackets(Direction.WEST)
                )
        ));

        shapes.put(Direction.UP, or(
                voxel(2, 13.3, 2, 14, 15, 14),
                or(
                        or(
                                voxel(5, 15, 5, 11, 15.9, 11),
                                voxel(6, 15.9, 6, 10, 16, 10)
                        ),
                        cornerBrackets(Direction.UP)
                )
        ));

        shapes.put(Direction.DOWN, or(
                voxel(2, 1, 2, 14, 2.7, 14),
                or(
                        or(
                                voxel(5, 0.1, 5, 11, 1, 11),
                                voxel(6, 0, 6, 10, 0.1, 10)
                        ),
                        cornerBrackets(Direction.DOWN)
                )
        ));

        return shapes;
    }

    private static VoxelShape cornerBrackets(Direction dir) {
        double[] depth = cornerDepth(dir);
        double d1 = depth[0];
        double d2 = depth[1];

        return orAll(
                cornerVoxel(dir, 1.95, 1.95, 4.05, 3.05, d1, d2),
                cornerVoxel(dir, 1.95, 1.95, 3.05, 4.05, d1, d2),
                cornerVoxel(dir, 1.95, 13, 4.05, 14.1, d1, d2),
                cornerVoxel(dir, 1.95, 12, 3.05, 14.1, d1, d2),
                cornerVoxel(dir, 12, 1.95, 14.1, 3.05, d1, d2),
                cornerVoxel(dir, 12.9, 1.95, 14.1, 4.05, d1, d2),
                cornerVoxel(dir, 12, 13, 14.1, 14.1, d1, d2),
                cornerVoxel(dir, 13, 12, 14.1, 14.1, d1, d2)
        );
    }

    private static double[] cornerDepth(Direction dir) {
        return switch (dir) {
            case NORTH, WEST, UP -> new double[]{13.1, 15.2};
            case EAST, SOUTH, DOWN -> new double[]{0.8, 2.9};
            default -> throw new IllegalArgumentException("Unsupported direction: " + dir);
        };
    }

    private static VoxelShape cornerVoxel(Direction dir, double u1, double v1, double u2, double v2, double d1, double d2) {
        return switch (dir) {
            case NORTH, SOUTH -> voxel(u1, v1, d1, u2, v2, d2);
            case EAST, WEST -> voxel(d1, u1, v1, d2, u2, v2);
            case UP, DOWN -> voxel(u1, d1, v1, u2, d2, v2);
            default -> throw new IllegalArgumentException("Unsupported direction: " + dir);
        };
    }

    private static VoxelShape voxel(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Shapes.box(x1 / 16.0, y1 / 16.0, z1 / 16.0, x2 / 16.0, y2 / 16.0, z2 / 16.0);
    }

    private static VoxelShape orAll(VoxelShape... shapes) {
        VoxelShape result = Shapes.empty();
        for (VoxelShape shape : shapes) {
            result = or(result, shape);
        }
        return result;
    }

    private static VoxelShape or(VoxelShape a, VoxelShape b) {
        return Shapes.joinUnoptimized(a, b, BooleanOp.OR).optimize();
    }

    private PanelShapes() {
    }
}