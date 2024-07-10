package io.github.raverbury.aggroindicator.common;

import io.github.raverbury.aggroindicator.AggroIndicator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = AggroIndicator.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.ConfigValue<List<? extends String>> SERVER_MOB_BLACKLIST = BUILDER.comment(
                    "Do not render alert icons for these mobs")
            .translation("config.server.serverMobBlacklist")
            .defineList("serverMobBlacklist", new ArrayList<String>(),
                    registry_name -> true);
    /**
     * #IMPORTANT: this MUST be after registering config fields, auto format
     * will bring this on top and break stuff
     */
    public static final ModConfigSpec SPEC = BUILDER.build();

    public static List<? extends String> serverMobBlacklist;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == SPEC) {
            serverMobBlacklist = SERVER_MOB_BLACKLIST.get();
        }
    }
}