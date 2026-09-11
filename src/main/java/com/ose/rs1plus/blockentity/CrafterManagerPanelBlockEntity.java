package com.ose.rs1plus.blockentity;

import com.ose.rs1plus.node.CrafterManagerPanelNetworkNode;
import com.ose.rs1plus.panel.PanelCapabilitySupport;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterManagerNetworkNode;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrafterManagerPanelBlockEntity extends CrafterManagerBlockEntity {

    public CrafterManagerPanelBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    @Nonnull
    public CrafterManagerNetworkNode createNode(Level level, BlockPos pos) {
        return new CrafterManagerPanelNetworkNode(level, pos);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PanelCapabilitySupport.filter(
                (CrafterManagerPanelNetworkNode) getNode(), cap, side, super::getCapability);
    }
}