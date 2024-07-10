package io.github.raverbury.aggroindicator.client;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue RENDER_ALERT_ICON = BUILDER.comment(
                    "Controls whether the client should render alert icons")
            .translation("config.client.renderAlertIcon")
            .define("renderAlertIcon", true);
    private static final ModConfigSpec.IntValue RENDER_RANGE = BUILDER.comment(
                    "Only render alert icons for mobs within this range")
            .translation("config.client.renderRange")
            .defineInRange("renderRange", 32, 8, 64);
    private static final ModConfigSpec.DoubleValue X_OFFSET = BUILDER.comment(
                    "Adjusts the horizontal placement of alert icons")
            .translation("config.client.xOffset")
            .defineInRange("xOffset", 0, -10, (double) 10);
    private static final ModConfigSpec.DoubleValue Y_OFFSET = BUILDER.comment(
                    "Adjusts the vertical placement of alert icons")
            .translation("config.client.yOffset")
            .defineInRange("yOffset", 10, -10, (double) 50);
    private static final ModConfigSpec.DoubleValue ALERT_ICON_SIZE = BUILDER.comment(
                    "Adjust the size of alert icons")
            .translation("config.client.alertIconSize")
            .defineInRange("alertIconSize", 30, 0, (double) 100);
    private static final ModConfigSpec.BooleanValue SCALE_WITH_MOB_SIZE = BUILDER.comment(
                    "Controls whether alert icons should grow in size with mobs")
            .translation("config.client.scaleWithMobSize")
            .define("scaleWithMobSize", true);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> CLIENT_MOB_BLACKLIST = BUILDER.comment(
                    "Do not render alert icons for these mobs")
            .translation("config.client.clientMobBlacklist")
            .defineList("clientMobBlacklist", new ArrayList<String>(),
                    registry_name -> true);
    private static final ModConfigSpec.EnumValue<AggroIconStyle> CLIENT_AGGRO_ICON_STYLE = BUILDER.comment(
                    "The texture of the aggro icon")
            .translation("config.client.clientAggroIconStyle")
            .defineEnum("clientAggroIconStyle", AggroIconStyle.CLASSIC);
    /**
     * #IMPORTANT: this MUST be after registering config fields, auto format
     * will bring this on top and break stuff
     */
    public static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean renderAlertIcon;
    public static int renderRange;
    public static double xOffset;
    public static double yOffset;
    public static double alertIconSize;
    public static boolean scaleWithMobSize;
    public static List<? extends String> clientMobBlacklist;
    public static AggroIconStyle clientAggroIconStyle;

    public static void reloadCache() {
        renderAlertIcon = RENDER_ALERT_ICON.get();
        renderRange = RENDER_RANGE.get();
        xOffset = X_OFFSET.get();
        yOffset = Y_OFFSET.get();
        alertIconSize = ALERT_ICON_SIZE.get();
        scaleWithMobSize = SCALE_WITH_MOB_SIZE.get();
        clientMobBlacklist = CLIENT_MOB_BLACKLIST.get();
        clientAggroIconStyle = CLIENT_AGGRO_ICON_STYLE.get();
    }

    public enum AggroIconStyle {
        CLASSIC, MGS, BLOCK_BENCH
    }
}