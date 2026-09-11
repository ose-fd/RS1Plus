package com.ose.rs1plus.node;

import net.minecraft.core.Direction;

public interface PanelAttachedSide {

    Direction getDirection();

    default Direction getAttachedSide() {
        return attachedSideFor(getDirection());
    }

    default boolean canConduct(Direction direction) {
        return direction == getAttachedSide();
    }

    static Direction attachedSideFor(Direction storedDirection) {
        return storedDirection.getAxis() == Direction.Axis.Y
                ? storedDirection
                : storedDirection.getOpposite();
    }
}