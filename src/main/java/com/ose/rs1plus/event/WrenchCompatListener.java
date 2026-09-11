package com.ose.rs1plus.event;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.ose.rs1plus.RS1PlusMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID)
public class WrenchCompatListener {

    private static final TagKey<Item> FORGE_WRENCHES =
            ItemTags.create(new ResourceLocation("forge", "wrenches"));
    private static final TagKey<Item> FORGE_TOOLS_WRENCH =
            ItemTags.create(new ResourceLocation("forge", "tools/wrench"));

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof BaseBlock)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (held.isEmpty() || isRefinedStorageItem(held) || !isWrench(held)) {
            return;
        }

        if (!player.isCrouching()) {
            return;
        }

        if (!(player instanceof ServerPlayer)) {
            return;
        }

        boolean dismantled = WrenchDismantleHelper.tryDismantle(player, level, pos, state);
        if (!dismantled) {
            return;
        }

        event.setUseBlock(Event.Result.DENY);
        event.setUseItem(Event.Result.DENY);
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        player.swing(event.getHand(), true);
    }

    private static boolean isRefinedStorageItem(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getNamespace().equals("refinedstorage");
    }

    private static boolean isWrench(ItemStack stack) {
        return stack.is(FORGE_WRENCHES) || stack.is(FORGE_TOOLS_WRENCH);
    }
}