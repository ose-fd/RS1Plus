package com.ose.rs1plus.block;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.ose.rs1plus.blockentity.NormalPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class NormalPanelBlock extends AbstractPanelBlock {

    public NormalPanelBlock(@Nullable DyeColor color) {
        super(GridType.NORMAL, color);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NormalPanelBlockEntity(pos, state);
    }

    @Override
    public RegistryObject<? extends AbstractPanelBlock> panelForColor(DyeColor color) {
        return (color == DyeColor.LIGHT_BLUE)
                ? ModBlocks.GRID_PANEL
                : ModBlocks.GRID_PANEL_COLORED.get(color);
    }
}
