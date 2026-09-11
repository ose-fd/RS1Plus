package com.ose.rs1plus.client;

import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.config.RS1PlusClientConfig;
import com.ose.rs1plus.network.GridCloseBehaviorPacket;
import com.ose.rs1plus.network.ModNetworking;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID, value = Dist.CLIENT)
public class ClientConfigSyncListener {

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ModNetworking.CHANNEL.sendToServer(
                new GridCloseBehaviorPacket(RS1PlusClientConfig.GRID_CLOSE_BEHAVIOR.get())
        );
    }
}