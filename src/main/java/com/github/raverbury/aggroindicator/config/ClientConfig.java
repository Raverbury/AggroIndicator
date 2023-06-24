package com.github.raverbury.aggroindicator.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec INSTANCE;

    public static ForgeConfigSpec.BooleanValue RENDER_ALERT_ICON;
    public static ForgeConfigSpec.IntValue RENDER_RANGE;

    static {
        CLIENT_BUILDER.push("Rendering");

        RENDER_ALERT_ICON = CLIENT_BUILDER
                .comment("Controls whether the client should render alert icons")
                .translation("config.renderAlertIcon")
                .define("renderAlertIcon", true);

        RENDER_RANGE = CLIENT_BUILDER
                .comment("Only render alert icons for mobs within this range")
                .translation("config.renderRange")
                .defineInRange("renderRange", 64, 8, 64);

        CLIENT_BUILDER.pop();

        INSTANCE = CLIENT_BUILDER.build();
    }
}
