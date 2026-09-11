package com.ose.rs1plus.block;

import com.ose.rs1plus.blockentity.CrafterManagerPanelBlockEntity;
import com.ose.rs1plus.panel.PanelColorSwap;
import com.ose.rs1plus.panel.PanelPlacement;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.CrafterManagerBlock;
import com.refinedmods.refinedstorage.blockentity.CrafterManagerBlockEntity;
import com.refinedmods.refinedstorage.container.factory.CrafterManagerMenuProvider;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;

public class CrafterManagerPanelBlock extends CrafterManagerBlock implements PanelColorSwap.Host {

    private static final DirectionProperty DIRECTION_PROPERTY =
            com.refinedmods.refinedstorage.block.BlockDirection.ANY.getProperty();

    @Nullable
    private final DyeColor color;

    public CrafterManagerPanelBlock(@Nullable DyeColor color) {
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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrafterManagerPanelBlockEntity(pos, state);
    }

    @Override
    public RegistryObject<? extends Block> panelForColor(DyeColor color) {
        return (color == DyeColor.LIGHT_BLUE)
                ? ModBlocks.CRAFTER_MANAGER_PANEL
                : ModBlocks.CRAFTER_MANAGER_PANEL_COLORED.get(color);
    }

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
            return NetworkUtils.attempt(level, pos, player, () -> NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    new CrafterManagerMenuProvider((CrafterManagerBlockEntity) level.getBlockEntity(pos)),
                    buf -> CrafterManagerMenuProvider.writeToBuffer(buf, level, pos)
            ), Permission.MODIFY, Permission.AUTOCRAFTING);
        }

        return InteractionResult.SUCCESS;
    }
}