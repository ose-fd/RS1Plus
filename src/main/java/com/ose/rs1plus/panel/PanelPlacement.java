package com.ose.rs1plus.panel;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PanelPlacement {

    private PanelPlacement() {
    }

    public static Direction facingForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        return (clicked == Direction.UP || clicked == Direction.DOWN)
                ? clicked.getOpposite()
                : clicked;
    }

    public static VoxelShape shapeFor(BlockState state, DirectionProperty directionProperty) {
        Direction direction = state.hasProperty(directionProperty)
                ? state.getValue(directionProperty)
                : Direction.NORTH;

        return PanelShapes.forDirection(direction);
    }
}
