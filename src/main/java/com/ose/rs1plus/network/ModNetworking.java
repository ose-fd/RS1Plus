package com.ose.rs1plus.network;

import com.ose.rs1plus.RS1PlusMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RS1PlusMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                ConfigurationFeedbackPacket.class,
                ConfigurationFeedbackPacket::encode,
                ConfigurationFeedbackPacket::decode,
                ConfigurationFeedbackPacket::handle
        );

        CHANNEL.registerMessage(
                packetId++,
                GridCloseBehaviorPacket.class,
                GridCloseBehaviorPacket::encode,
                GridCloseBehaviorPacket::decode,
                GridCloseBehaviorPacket::handle
        );
    }
}