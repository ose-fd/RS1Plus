package com.ose.rs1plus.node;

import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class CrafterManagerPanelNetworkNode extends CrafterManagerNetworkNode implements PanelAttachedSide {

    public CrafterManagerPanelNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }
}