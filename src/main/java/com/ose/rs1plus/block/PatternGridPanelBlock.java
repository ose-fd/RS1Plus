package com.ose.rs1plus.block;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.ose.rs1plus.blockentity.PatternGridPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class PatternGridPanelBlock extends AbstractPanelBlock {

    public PatternGridPanelBlock(@Nullable DyeColor color) {
        super(GridType.PATTERN, color);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PatternGridPanelBlockEntity(pos, state);
    }

    @Override
    public RegistryObject<? extends AbstractPanelBlock> panelForColor(DyeColor color) {
        return (color == DyeColor.LIGHT_BLUE)
                ? ModBlocks.PATTERN_GRID_PANEL
                : ModBlocks.PATTERN_GRID_PANEL_COLORED.get(color);
    }
}