package com.ose.rs1plus.block;

import com.ose.rs1plus.panel.PanelColorSwap;
import com.ose.rs1plus.panel.PanelPlacement;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.GridBlockGridFactory;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.GridBlock;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public abstract class AbstractPanelBlock extends GridBlock implements PanelColorSwap.Host {

    protected static final DirectionProperty DIRECTION_PROPERTY = BlockDirection.ANY.getProperty();

    @Nullable
    private final DyeColor color;

    protected AbstractPanelBlock(GridType type, @Nullable DyeColor color) {
        super(type);
        this.color = color;
    }

    @Override
    @Nullable
    public DyeColor getColor() {
        return color;
    }

    @Override
    public DirectionProperty getDirectionProperty() {
        return DIRECTION_PROPERTY;
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.ANY;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(DIRECTION_PROPERTY, PanelPlacement.facingForPlacement(context));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PanelPlacement.shapeFor(state, DIRECTION_PROPERTY);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PanelPlacement.shapeFor(state, DIRECTION_PROPERTY);
    }

    @Override
    public abstract RegistryObject<? extends Block> panelForColor(DyeColor color);

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        InteractionResult swap = PanelColorSwap.trySwapColor(this, state, level, pos, player, heldItem);
        if (swap != null) {
            return swap;
        }

        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () ->
                    API.instance().getGridManager().openGrid(GridBlockGridFactory.ID, (ServerPlayer) player, pos));
        }
        return InteractionResult.SUCCESS;
    }
}