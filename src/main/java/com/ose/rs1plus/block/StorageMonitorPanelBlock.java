package com.ose.rs1plus.block;

import com.ose.rs1plus.blockentity.StorageMonitorPanelBlockEntity;
import com.refinedmods.refinedstorage.apiimpl.network.node.StorageMonitorNetworkNode;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.StorageMonitorBlock;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.container.StorageMonitorContainerMenu;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class StorageMonitorPanelBlock extends StorageMonitorBlock {

    private static final DirectionProperty DIRECTION_PROPERTY = BlockDirection.HORIZONTAL.getProperty();

    public StorageMonitorPanelBlock() {
    }

    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clicked = context.getClickedFace();
        Direction facing = clicked.getAxis() == Direction.Axis.Y
                ? context.getHorizontalDirection().getOpposite()
                : clicked;

        return this.defaultBlockState().setValue(DIRECTION_PROPERTY, facing);
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageMonitorPanelBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (player.isCrouching()) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    new BlockEntityMenuProvider<StorageMonitorBlockEntity>(
                            Component.translatable("gui.refinedstorage.storage_monitor"),
                            (blockEntity, windowId, inventory, p) -> new StorageMonitorContainerMenu(blockEntity, player, windowId),
                            pos
                    ),
                    pos
            ));
        }

        StorageMonitorNetworkNode storageMonitor = ((StorageMonitorBlockEntity) level.getBlockEntity(pos)).getNode();

        return heldItem.isEmpty() ? storageMonitor.depositAll(player) : storageMonitor.deposit(player, heldItem);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        super.attack(state, level, pos, player);

        if (level.isClientSide) {
            return;
        }

        HitResult result = LevelUtils.rayTracePlayer(level, player);
        if (!(result instanceof BlockHitResult blockHit)) {
            return;
        }

        ((StorageMonitorBlockEntity) level.getBlockEntity(pos)).getNode().extract(player, blockHit.getDirection());
    }
}