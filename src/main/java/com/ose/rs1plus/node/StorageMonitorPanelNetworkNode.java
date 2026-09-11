package com.ose.rs1plus.node;

import com.ose.rs1plus.panel.PanelAttachedSide;
import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class StorageMonitorPanelNetworkNode extends StorageMonitorNetworkNode implements PanelAttachedSide {

    public StorageMonitorPanelNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }
}