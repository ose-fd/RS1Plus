package com.ose.rs1plus.mixin;

import com.ose.rs1plus.wrench.WrenchDismantleHelper;
import com.refinedmods.refinedstorage.item.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WrenchItem.class, remap = false)
public abstract class WrenchItemMixin {

    @Inject(method = "onItemUseFirst", at = @At("HEAD"), cancellable = true)
    private void rs1plus$onWrenchUseFirst(ItemStack stack, UseOnContext ctx, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = ctx.getLevel();

        if (level.isClientSide) {
            return;
        }

        Player player = ctx.getPlayer();
        if (player == null || !player.isCrouching()) {
            return;
        }

        BlockPos pos = ctx.getClickedPos();
        BlockState state = level.getBlockState(pos);

        boolean dismantled = WrenchDismantleHelper.tryDismantle(player, level, pos, state);
        if (!dismantled) {
            return;
        }

        player.swing(ctx.getHand(), true);
        cir.setReturnValue(InteractionResult.CONSUME);
    }
}