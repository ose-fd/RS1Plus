package com.ose.rs1plus.client;

import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.item.ModItems;
import com.refinedmods.refinedstorage.RSBlockEntities;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    ModItems.CONFIGURATION_CARD.get(),
                    new ResourceLocation("rs1plus", "active"),
                    (stack, level, entity, seed) -> stack.hasTag() && stack.getTag().contains("NodeId") ? 1.0F : 0.0F
            );

            BlockEntityRenderers.register(
                    RSBlockEntities.STORAGE_MONITOR.get(),
                    ctx -> new StorageMonitorPanelBlockEntityRenderer()
            );
        });
    }
}