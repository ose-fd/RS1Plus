package com.ose.rs1plus.node;

import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CraftingMonitorPanelNetworkNode extends CraftingMonitorNetworkNode implements PanelAttachedSide {

    public CraftingMonitorPanelNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }
}