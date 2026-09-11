package com.ose.rs1plus.blockentity;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GridPanelBlockEntity extends AbstractPanelBlockEntity {

    public GridPanelBlockEntity(BlockPos pos, BlockState state) {
        super(GridType.NORMAL, pos, state);
    }
}
