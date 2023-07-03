package com.github.raverbury.aggroindicator.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    private static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec INSTANCE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> SERVER_MOB_BLACKLIST;

    static {
        SERVER_BUILDER.push("Targeting");

        SERVER_MOB_BLACKLIST = SERVER_BUILDER
                .comment("Do not check for target acquisition for these mobs")
                .translation("config.server.serverMobBlacklist").defineList("serverMobBlacklist", new ArrayList<String>(), registry_name -> true);

        SERVER_BUILDER.pop();

        INSTANCE = SERVER_BUILDER.build();
    }
}
