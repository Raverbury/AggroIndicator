package com.github.raverbury.aggroindicator.config;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec INSTANCE;

    public static ForgeConfigSpec.BooleanValue RENDER_ALERT_ICON;
    public static ForgeConfigSpec.IntValue RENDER_RANGE;
    public static ForgeConfigSpec.DoubleValue X_OFFSET;
    public static ForgeConfigSpec.DoubleValue Y_OFFSET;
    public static ForgeConfigSpec.DoubleValue ALERT_ICON_SIZE;
    public static ForgeConfigSpec.BooleanValue SCALE_WITH_MOB_SIZE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> CLIENT_MOB_BLACKLIST;
    public static ForgeConfigSpec.EnumValue<AggroIconStyle> CLIENT_AGGRO_ICON_STYLE;
    public static ForgeConfigSpec.ConfigValue<String> ALERT_COLOR_HEX;

    static {
        CLIENT_BUILDER.push("Rendering");

        RENDER_ALERT_ICON = CLIENT_BUILDER.comment(
                        "Controls whether the client should render alert icons")
                .translation("config.client.renderAlertIcon")
                .define("renderAlertIcon", true);

        RENDER_RANGE = CLIENT_BUILDER.comment(
                        "Only render alert icons for mobs within this range")
                .translation("config.client.renderRange")
                .defineInRange("renderRange", 32, 8, 64);

        X_OFFSET = CLIENT_BUILDER.comment(
                        "Adjusts the horizontal placement of alert icons")
                .translation("config.client.xOffset")
                .defineInRange("xOffset", 0, -10, (double) 10);

        Y_OFFSET = CLIENT_BUILDER.comment(
                        "Adjusts the vertical placement of alert icons")
                .translation("config.client.yOffset")
                .defineInRange("yOffset", 0, -10, (double) 50);

        ALERT_ICON_SIZE = CLIENT_BUILDER.comment(
                        "Adjust the size of alert icons")
                .translation("config.client.alertIconSize")
                .defineInRange("alertIconSize", 30, 0, (double) 100);

        SCALE_WITH_MOB_SIZE = CLIENT_BUILDER.comment(
                        "Controls whether alert icons should grow in size with mobs")
                .translation("config.client.scaleWithMobSize")
                .define("scaleWithMobSize", false);

        CLIENT_MOB_BLACKLIST = CLIENT_BUILDER.comment(
                        "Do not render alert icons for these mobs")
                .translation("config.client.clientMobBlacklist")
                .defineList("clientMobBlacklist", new ArrayList<String>(),
                        registry_name -> true);

        CLIENT_AGGRO_ICON_STYLE = CLIENT_BUILDER.comment(
                        "The texture of the aggro icon")
                .translation("config.client.clientAggroIconStyle")
                .defineEnum("clientAggroIconStyle", AggroIconStyle.CLASSIC);

        ALERT_COLOR_HEX = CLIENT_BUILDER.comment(
                "The color of the alert icon")
                .translation("config.client.clientAlertColorHex")
                .define("alertColorHex", "0xFF6666"
        );

        CLIENT_BUILDER.pop();

        INSTANCE = CLIENT_BUILDER.build();
    }

    public enum AggroIconStyle {
        CLASSIC, MGS, BLOCK_BENCH
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT)
    public static class Cached {
        public static boolean RENDER_ALERT_ICON = true;
        public static int RENDER_RANGE = 32;
        public static double X_OFFSET = 0;
        public static double Y_OFFSET = 5;
        public static double ALERT_ICON_SIZE = 30;
        public static boolean SCALE_WITH_MOB_SIZE = false;
        public static List<? extends String> CLIENT_MOB_BLACKLIST = new ArrayList<>();
        public static AggroIconStyle CLIENT_AGGRO_ICON_STYLE = AggroIconStyle.CLASSIC;
        public static float[] COLORS = {1f, 1f, 1f};

        public static void reload() {
            RENDER_ALERT_ICON = ClientConfig.RENDER_ALERT_ICON.get();
            RENDER_RANGE = ClientConfig.RENDER_RANGE.get();
            X_OFFSET = ClientConfig.X_OFFSET.get();
            Y_OFFSET = ClientConfig.Y_OFFSET.get();
            ALERT_ICON_SIZE = ClientConfig.ALERT_ICON_SIZE.get();
            SCALE_WITH_MOB_SIZE = ClientConfig.SCALE_WITH_MOB_SIZE.get();
            CLIENT_MOB_BLACKLIST = ClientConfig.CLIENT_MOB_BLACKLIST.get();
            CLIENT_AGGRO_ICON_STYLE = ClientConfig.CLIENT_AGGRO_ICON_STYLE.get();
            Color color = Color.decode(ClientConfig.ALERT_COLOR_HEX.get());
            COLORS[0] = color.getRed() / 255f;
            COLORS[1] = color.getGreen() / 255f;
            COLORS[2] = color.getBlue() / 255f;
        }
    }
}
