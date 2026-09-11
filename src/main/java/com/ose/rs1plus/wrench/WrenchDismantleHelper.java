package com.ose.rs1plus.wrench;

import com.ose.rs1plus.config.RS1PlusCommonConfig;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import com.refinedmods.refinedstorage.blockentity.grid.portable.PortableGridBlockEntity;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public final class WrenchDismantleHelper {

    private WrenchDismantleHelper() {
    }

    public static boolean tryDismantle(Player player, Level level, BlockPos pos, BlockState state) {
        if (!RS1PlusCommonConfig.ENABLE_WRENCH_DISMANTLE.get()) {
            return false;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        INetworkNode node = NetworkUtils.getNodeFromBlockEntity(blockEntity);
        INetwork network = NetworkUtils.getNetworkFromNode(node);

        if (network != null && !network.getSecurityManager().hasPermission(Permission.BUILD, player)) {
            LevelUtils.sendNoPermissionMessage(player);
            return false;
        }

        if (node instanceof NetworkNode networkNode) {
            IItemHandler drops = networkNode.getDrops();
            if (drops != null) {
                for (int i = 0; i < drops.getSlots(); i++) {
                    ItemStack extracted = drops instanceof IItemHandlerModifiable
                            ? drops.extractItem(i, drops.getStackInSlot(i).getCount(), false)
                            : drops.getStackInSlot(i).copy();

                    if (!extracted.isEmpty()) {
                        giveOrDrop(player, level, pos, extracted);
                    }
                }
            }
        }

        ItemStack pickedStack = new ItemStack(state.getBlock());

        // RS no usa BlockEntityTag para estos bloques, asi que hay que replicar
        // a mano lo que hacen sus loot functions oficiales: la Portable Grid
        // usa el mismo metodo que su loot function, y la Controller copia la
        // energia de la red a la capability ENERGY del item igual que ControllerLootFunction.
        if (blockEntity instanceof PortableGridBlockEntity portableGrid) {
            portableGrid.applyDataFromBlockEntityToItem(pickedStack);
        } else if (blockEntity instanceof ControllerBlockEntity controller && network != null) {
            int energy = network.getEnergyStorage().getEnergyStored();
            pickedStack.getCapability(ForgeCapabilities.ENERGY)
                    .ifPresent(itemEnergy -> itemEnergy.receiveEnergy(energy, false));
        }

        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        giveOrDrop(player, level, pos, pickedStack);
        level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);

        return true;
    }

    private static void giveOrDrop(Player player, Level level, BlockPos pos, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
        }
    }
}