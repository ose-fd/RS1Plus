package com.ose.rs1plus.network;

import com.ose.rs1plus.config.GridCloseBehavior;
import com.ose.rs1plus.event.PlayerGridPreferences;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridCloseBehaviorPacket {

    private final GridCloseBehavior behavior;

    public GridCloseBehaviorPacket(GridCloseBehavior behavior) {
        this.behavior = behavior;
    }

    public static void encode(GridCloseBehaviorPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.behavior);
    }

    public static GridCloseBehaviorPacket decode(FriendlyByteBuf buffer) {
        return new GridCloseBehaviorPacket(buffer.readEnum(GridCloseBehavior.class));
    }

    public static void handle(GridCloseBehaviorPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            var sender = context.getSender();
            if (sender != null) {
                PlayerGridPreferences.set(sender.getUUID(), packet.behavior);
            }
        });

        context.setPacketHandled(true);
    }
}