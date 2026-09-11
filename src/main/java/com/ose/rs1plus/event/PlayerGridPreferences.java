package com.ose.rs1plus.event;

import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.config.GridCloseBehavior;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID)
public final class PlayerGridPreferences {

    private static final Map<UUID, GridCloseBehavior> PREFERENCES = new ConcurrentHashMap<>();

    private PlayerGridPreferences() {
    }

    public static void set(UUID playerId, GridCloseBehavior behavior) {
        PREFERENCES.put(playerId, behavior);
    }

    public static GridCloseBehavior get(UUID playerId) {
        return PREFERENCES.getOrDefault(playerId, GridCloseBehavior.KEEP);
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        PREFERENCES.remove(event.getEntity().getUUID());
    }
}