package com.ose.rs1plus.item;

import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RS1PlusMod.MODID);

    public static final RegistryObject<Item> CONFIGURATION_CARD = ITEMS.register(
            "configuration_card",
            () -> new ConfigurationCardItem(new Item.Properties().stacksTo(1))
    );

    public static final RegistryObject<Item> CRAFTING_GRID_PANEL =
            registerBlockItem("crafting_grid_panel", ModBlocks.CRAFTING_GRID_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> CRAFTING_GRID_PANEL_COLORED =
            registerColoredBlockItems("crafting_grid_panel", ModBlocks.CRAFTING_GRID_PANEL_COLORED);

    public static final RegistryObject<Item> GRID_PANEL =
            registerBlockItem("grid_panel", ModBlocks.GRID_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> GRID_PANEL_COLORED =
            registerColoredBlockItems("grid_panel", ModBlocks.GRID_PANEL_COLORED);

    public static final RegistryObject<Item> FLUID_GRID_PANEL =
            registerBlockItem("fluid_grid_panel", ModBlocks.FLUID_GRID_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> FLUID_GRID_PANEL_COLORED =
            registerColoredBlockItems("fluid_grid_panel", ModBlocks.FLUID_GRID_PANEL_COLORED);

    public static final RegistryObject<Item> PATTERN_GRID_PANEL =
            registerBlockItem("pattern_grid_panel", ModBlocks.PATTERN_GRID_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> PATTERN_GRID_PANEL_COLORED =
            registerColoredBlockItems("pattern_grid_panel", ModBlocks.PATTERN_GRID_PANEL_COLORED);

    public static final RegistryObject<Item> CRAFTER_MANAGER_PANEL =
            registerBlockItem("crafter_manager_panel", ModBlocks.CRAFTER_MANAGER_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> CRAFTER_MANAGER_PANEL_COLORED =
            registerColoredBlockItems("crafter_manager_panel", ModBlocks.CRAFTER_MANAGER_PANEL_COLORED);

    public static final RegistryObject<Item> CRAFTING_MONITOR_PANEL =
            registerBlockItem("crafting_monitor_panel", ModBlocks.CRAFTING_MONITOR_PANEL);
    public static final Map<DyeColor, RegistryObject<Item>> CRAFTING_MONITOR_PANEL_COLORED =
            registerColoredBlockItems("crafting_monitor_panel", ModBlocks.CRAFTING_MONITOR_PANEL_COLORED);

    public static final RegistryObject<Item> STORAGE_MONITOR_PANEL =
            registerBlockItem("storage_monitor_panel", ModBlocks.STORAGE_MONITOR_PANEL);

    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static Map<DyeColor, RegistryObject<Item>> registerColoredBlockItems(
            String suffix, Map<DyeColor, ? extends RegistryObject<? extends Block>> coloredBlocks) {
        Map<DyeColor, RegistryObject<Item>> map = new EnumMap<>(DyeColor.class);
        for (Map.Entry<DyeColor, ? extends RegistryObject<? extends Block>> entry : coloredBlocks.entrySet()) {
            DyeColor color = entry.getKey();
            String name = color.getName() + "_" + suffix;
            map.put(color, ITEMS.register(name, () -> new BlockItem(entry.getValue().get(), new Item.Properties())));
        }
        return map;
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}