package com.ose.rs1plus.panel;

import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;

// Generaliza el getCapability() de AbstractPanelBlockEntity para que lo
// puedan usar tambien los paneles que no extienden esa clase.
public final class PanelCapabilitySupport {

    private PanelCapabilitySupport() {
    }

    public static <T> LazyOptional<T> filter(PanelAttachedSide node, @Nonnull Capability<T> cap,
                                             @Nullable Direction side,
                                             BiFunction<Capability<T>, Direction, LazyOptional<T>> superCall) {
        if (cap == NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY
                && side != null
                && side != node.getAttachedSide()) {
            return LazyOptional.empty();
        }
        return superCall.apply(cap, side);
    }
}