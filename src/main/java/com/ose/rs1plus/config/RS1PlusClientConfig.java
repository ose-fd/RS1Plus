package com.ose.rs1plus.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RS1PlusClientConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue SHOW_PANELS_IN_JEI;
    public static final ForgeConfigSpec.EnumValue<GridCloseBehavior> GRID_CLOSE_BEHAVIOR;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        SHOW_PANELS_IN_JEI = builder
                .comment(
                        "Whether the RS1+ panels should be shown/searchable in JEI.",
                        "This is a purely visual client-side preference.",
                        "Toggling this requires a game restart to take effect."
                )
                .define("showPanelsInJei", false);

        GRID_CLOSE_BEHAVIOR = builder
                .comment(
                        "What to do with items inside your Crafting Grid after closing its GUI:",
                        "KEEP - Default RS behavior (keeps items inside its grid)",
                        "CLEAR_TO_NETWORK - Clears items back into the network (if there's any storage available)",
                        "CLEAR_TO_PLAYER - Clears items into your inventory",
                        "This is a personal preference synced to the server you're connected to."
                )
                .defineEnum("gridCloseBehavior", GridCloseBehavior.KEEP);

        SPEC = builder.build();
    }

    private RS1PlusClientConfig() {
    }
}