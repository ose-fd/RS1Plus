package com.ose.rs1plus.mixin;

import com.ose.rs1plus.config.GridCloseBehavior;
import com.ose.rs1plus.event.PlayerGridPreferences;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.INetworkAwareGrid;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.container.GridContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GridContainerMenu.class, remap = false)
public abstract class GridContainerMenuMixin {

    @Shadow
    @Final
    private IGrid grid;

    @Inject(method = "m_6877_", at = @At("HEAD"))
    private void rs1plus$onGridMenuClosed(Player player, CallbackInfo ci) {
        if (player.getCommandSenderWorld().isClientSide || grid.getGridType() != GridType.CRAFTING) {
            return;
        }

        GridCloseBehavior behavior = PlayerGridPreferences.get(player.getUUID());
        if (behavior == GridCloseBehavior.KEEP) {
            return;
        }

        CraftingContainer matrix = grid.getCraftingMatrix();
        if (matrix == null) {
            return;
        }

        INetwork network = grid instanceof INetworkAwareGrid networkAwareGrid ? networkAwareGrid.getNetwork() : null;
        boolean networkAvailable = network != null && network.canRun()
                && network.getSecurityManager().hasPermission(Permission.INSERT, player);

        boolean changed = false;
        for (int i = 0; i < matrix.getContainerSize(); i++) {
            ItemStack slot = matrix.getItem(i);
            if (slot.isEmpty()) {
                continue;
            }

            if (behavior == GridCloseBehavior.CLEAR_TO_NETWORK && networkAvailable) {
                ItemStack remainder = network.insertItem(slot, slot.getCount(), Action.PERFORM);
                matrix.setItem(i, remainder);
                network.getItemStorageTracker().changed(player, slot.copy());
                changed = true;
            } else if (player.getInventory().add(slot.copy())) {
                matrix.setItem(i, ItemStack.EMPTY);
                changed = true;
            }
        }

        if (changed) {
            grid.onCraftingMatrixChanged();
        }
    }
}