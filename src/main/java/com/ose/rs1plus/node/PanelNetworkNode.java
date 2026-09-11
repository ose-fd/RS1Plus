package com.ose.rs1plus.node;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PanelNetworkNode extends GridNetworkNode implements PanelAttachedSide {

    public PanelNetworkNode(Level level, BlockPos pos, GridType type) {
        super(level, pos, type);
    }
}