package com.ose.rs1plus.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RS1PlusCommonConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE_WRENCH_DISMANTLE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        ENABLE_WRENCH_DISMANTLE = builder
                .comment(
                        "Whether right-clicking RS blocks with a wrench while sneaking dismantles them",
                        "(preserving upgrades, energy and drops). Set to false to disable this behavior",
                        "entirely, e.g. if it conflicts with another mod's wrench handling."
                )
                .define("enableWrenchDismantle", true);

        SPEC = builder.build();
    }

    private RS1PlusCommonConfig() {
    }
}