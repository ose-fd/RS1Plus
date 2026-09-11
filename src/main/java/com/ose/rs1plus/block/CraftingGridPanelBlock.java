package com.ose.rs1plus.block;

import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.ose.rs1plus.blockentity.CraftingGridPanelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class CraftingGridPanelBlock extends AbstractPanelBlock {

    public CraftingGridPanelBlock(@Nullable DyeColor color) {
        super(GridType.CRAFTING, color);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CraftingGridPanelBlockEntity(pos, state);
    }

    @Override
    public RegistryObject<? extends AbstractPanelBlock> panelForColor(DyeColor color) {
        return (color == DyeColor.LIGHT_BLUE)
                ? ModBlocks.CRAFTING_GRID_PANEL
                : ModBlocks.CRAFTING_GRID_PANEL_COLORED.get(color);
    }
}
