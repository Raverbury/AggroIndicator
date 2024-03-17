package com.github.raverbury.aggroindicator.config;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    private static final ModConfigSpec.Builder SERVER_BUILDER = new ModConfigSpec.Builder();
    public static ModConfigSpec INSTANCE;
    public static ModConfigSpec.ConfigValue<List<? extends String>> SERVER_MOB_BLACKLIST;

    static {
        SERVER_BUILDER.push("Targeting");

        SERVER_MOB_BLACKLIST = SERVER_BUILDER
                .comment("Do not check for target acquisition for these mobs")
                .translation("config.server.serverMobBlacklist").defineList("serverMobBlacklist", new ArrayList<String>(), registry_name -> true);

        SERVER_BUILDER.pop();

        INSTANCE = SERVER_BUILDER.build();
    }
}