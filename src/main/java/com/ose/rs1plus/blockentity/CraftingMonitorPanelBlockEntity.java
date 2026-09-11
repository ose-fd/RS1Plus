package com.ose.rs1plus.blockentity;

import com.ose.rs1plus.node.CraftingMonitorPanelNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.CraftingMonitorNetworkNode;
import com.refinedmods.refinedstorage.blockentity.craftingmonitor.CraftingMonitorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingMonitorPanelBlockEntity extends CraftingMonitorBlockEntity {

    public CraftingMonitorPanelBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    @Nonnull
    public CraftingMonitorNetworkNode createNode(Level level, BlockPos pos) {
        return new CraftingMonitorPanelNetworkNode(level, pos);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PanelCapabilitySupport.filter(
                (CraftingMonitorPanelNetworkNode) getNode(), cap, side, super::getCapability);
    }
}