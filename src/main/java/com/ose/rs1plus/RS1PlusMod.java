package com.ose.rs1plus;

import java.util.Map;
import java.util.Collection;
import com.ose.rs1plus.config.RS1PlusClientConfig;
import com.ose.rs1plus.config.RS1PlusCommonConfig;
import com.ose.rs1plus.item.ModItems;
import com.ose.rs1plus.network.ModNetworking;
import com.ose.rs1plus.block.ModBlocks;
import com.ose.rs1plus.mixin.BlockEntityTypeAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import com.refinedmods.refinedstorage.RSBlockEntities;



import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mod(RS1PlusMod.MODID)
public class RS1PlusMod {
    public static final String MODID = "rs1plus";

    private static final ResourceKey<CreativeModeTab> RS_GENERAL_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            new ResourceLocation("refinedstorage", "general")
    );

    public RS1PlusMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModNetworking.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, RS1PlusClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RS1PlusCommonConfig.SPEC);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            patchValidBlocks(RSBlockEntities.CRAFTING_GRID.get(),
                    ModBlocks.CRAFTING_GRID_PANEL, ModBlocks.CRAFTING_GRID_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.GRID.get(),
                    ModBlocks.GRID_PANEL, ModBlocks.GRID_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.FLUID_GRID.get(),
                    ModBlocks.FLUID_GRID_PANEL, ModBlocks.FLUID_GRID_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.PATTERN_GRID.get(),
                    ModBlocks.PATTERN_GRID_PANEL, ModBlocks.PATTERN_GRID_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.CRAFTER_MANAGER.get(),
                    ModBlocks.CRAFTER_MANAGER_PANEL, ModBlocks.CRAFTER_MANAGER_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.CRAFTING_MONITOR.get(),
                    ModBlocks.CRAFTING_MONITOR_PANEL, ModBlocks.CRAFTING_MONITOR_PANEL_COLORED.values());

            patchValidBlocks(RSBlockEntities.STORAGE_MONITOR.get(),
                    ModBlocks.STORAGE_MONITOR_PANEL, Collections.emptySet());
        });
    }

    private static void patchValidBlocks(Object blockEntityType,
                                         RegistryObject<? extends Block> base,
                                         Collection<? extends RegistryObject<? extends Block>> colored) {
        BlockEntityTypeAccessor accessor = (BlockEntityTypeAccessor) blockEntityType;

        Set<Block> updated = new HashSet<>(accessor.rs1plus$getValidBlocks());
        updated.add(base.get());
        for (RegistryObject<? extends Block> panel : colored) {
            updated.add(panel.get());
        }

        accessor.rs1plus$setValidBlocks(Collections.unmodifiableSet(updated));
    }

    private static void acceptWithColored(BuildCreativeModeTabContentsEvent event,
                                          RegistryObject<Item> base,
                                          Map<DyeColor, RegistryObject<Item>> colored) {
        event.accept(base);
        for (RegistryObject<Item> item : colored.values()) {
            event.accept(item);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() != RS_GENERAL_TAB) {
            return;
        }

        event.accept(ModItems.CONFIGURATION_CARD);

        if (!RS1PlusClientConfig.SHOW_PANELS_IN_JEI.get()) {
            return;
        }

        acceptWithColored(event, ModItems.CRAFTING_GRID_PANEL, ModItems.CRAFTING_GRID_PANEL_COLORED);
        acceptWithColored(event, ModItems.GRID_PANEL, ModItems.GRID_PANEL_COLORED);
        acceptWithColored(event, ModItems.FLUID_GRID_PANEL, ModItems.FLUID_GRID_PANEL_COLORED);
        acceptWithColored(event, ModItems.PATTERN_GRID_PANEL, ModItems.PATTERN_GRID_PANEL_COLORED);
        acceptWithColored(event, ModItems.CRAFTER_MANAGER_PANEL, ModItems.CRAFTER_MANAGER_PANEL_COLORED);
        acceptWithColored(event, ModItems.CRAFTING_MONITOR_PANEL, ModItems.CRAFTING_MONITOR_PANEL_COLORED);
        event.accept(ModItems.STORAGE_MONITOR_PANEL);
    }
}