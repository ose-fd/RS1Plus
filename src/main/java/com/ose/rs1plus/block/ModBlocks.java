package com.ose.rs1plus.block;

import com.ose.rs1plus.RS1PlusMod;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RS1PlusMod.MODID);

    public static final RegistryObject<CraftingGridPanelBlock> CRAFTING_GRID_PANEL =
            BLOCKS.register("crafting_grid_panel", () -> new CraftingGridPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<CraftingGridPanelBlock>> CRAFTING_GRID_PANEL_COLORED =
            registerColored("crafting_grid_panel", CraftingGridPanelBlock::new);

    public static final RegistryObject<NormalPanelBlock> GRID_PANEL =
            BLOCKS.register("grid_panel", () -> new NormalPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<NormalPanelBlock>> GRID_PANEL_COLORED =
            registerColored("grid_panel", NormalPanelBlock::new);

    public static final RegistryObject<FluidGridPanelBlock> FLUID_GRID_PANEL =
            BLOCKS.register("fluid_grid_panel", () -> new FluidGridPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<FluidGridPanelBlock>> FLUID_GRID_PANEL_COLORED =
            registerColored("fluid_grid_panel", FluidGridPanelBlock::new);

    public static final RegistryObject<PatternGridPanelBlock> PATTERN_GRID_PANEL =
            BLOCKS.register("pattern_grid_panel", () -> new PatternGridPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<PatternGridPanelBlock>> PATTERN_GRID_PANEL_COLORED =
            registerColored("pattern_grid_panel", PatternGridPanelBlock::new);

    public static final RegistryObject<CrafterManagerPanelBlock> CRAFTER_MANAGER_PANEL =
            BLOCKS.register("crafter_manager_panel", () -> new CrafterManagerPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<CrafterManagerPanelBlock>> CRAFTER_MANAGER_PANEL_COLORED =
            registerColored("crafter_manager_panel", CrafterManagerPanelBlock::new);

    public static final RegistryObject<CraftingMonitorPanelBlock> CRAFTING_MONITOR_PANEL =
            BLOCKS.register("crafting_monitor_panel", () -> new CraftingMonitorPanelBlock(null));
    public static final Map<DyeColor, RegistryObject<CraftingMonitorPanelBlock>> CRAFTING_MONITOR_PANEL_COLORED =
            registerColored("crafting_monitor_panel", CraftingMonitorPanelBlock::new);

    public static final RegistryObject<StorageMonitorPanelBlock> STORAGE_MONITOR_PANEL =
            BLOCKS.register("storage_monitor_panel", StorageMonitorPanelBlock::new);

    private static <T extends Block> Map<DyeColor, RegistryObject<T>> registerColored(
            String baseName, Function<DyeColor, T> factory) {
        Map<DyeColor, RegistryObject<T>> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            if (color == DyeColor.LIGHT_BLUE) {
                continue; // cubierto por el bloque base sin colorear
            }
            String name = color.getName() + "_" + baseName;
            map.put(color, BLOCKS.register(name, () -> factory.apply(color)));
        }
        return map;
    }

    private ModBlocks() {
    }
}