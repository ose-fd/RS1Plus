package com.ose.rs1plus.network;

import com.ose.rs1plus.client.ConfigurationCardOutlineRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ConfigurationFeedbackPacket {

    private final BlockPos pos;

    public ConfigurationFeedbackPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(ConfigurationFeedbackPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.pos);
    }

    public static ConfigurationFeedbackPacket decode(FriendlyByteBuf buffer) {
        return new ConfigurationFeedbackPacket(buffer.readBlockPos());
    }

    public static void handle(ConfigurationFeedbackPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ConfigurationCardOutlineRenderer.showConfigurationFeedback(packet.pos);
        });

        context.setPacketHandled(true);
    }
}