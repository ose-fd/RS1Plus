package com.ose.rs1plus.client;

import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.config.RS1PlusClientConfig;
import com.ose.rs1plus.network.GridCloseBehaviorPacket;
import com.ose.rs1plus.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientConfigReloadListener {

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != RS1PlusClientConfig.SPEC) {
            return;
        }
        if (Minecraft.getInstance().player == null) {
            return; // aun no conectado; el login handler ya cubre ese caso
        }

        ModNetworking.CHANNEL.sendToServer(
                new GridCloseBehaviorPacket(RS1PlusClientConfig.GRID_CLOSE_BEHAVIOR.get())
        );
    }
}