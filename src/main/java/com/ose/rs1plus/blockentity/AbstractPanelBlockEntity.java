package com.ose.rs1plus.blockentity;

import com.ose.rs1plus.node.PanelNetworkNode;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.blockentity.grid.GridBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Las subclases concretas (una por tipo de grid: Crafting, Normal, Fluid,
// Pattern) solo necesitan fijar su GridType en el constructor -- todo lo
// demas es identico para los 4.
public abstract class AbstractPanelBlockEntity extends GridBlockEntity {

    private final GridType type;

    protected AbstractPanelBlockEntity(GridType type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.type = type;
    }

    @Override
    @Nonnull
    public GridNetworkNode createNode(Level level, BlockPos pos) {
        return new PanelNetworkNode(level, pos, type);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return PanelCapabilitySupport.filter((PanelNetworkNode) getNode(), cap, side, super::getCapability);
    }
}