package com.github.raverbury.aggroindicator;

import com.github.raverbury.aggroindicator.config.ClientConfig;
import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.event.ClientEventHandler;
import com.github.raverbury.aggroindicator.event.ServerEventHandler;
import com.github.raverbury.aggroindicator.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AggroIndicator.MODID)
public class AggroIndicator {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "aggroindicator";

    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public AggroIndicator() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        // BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        // ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(ClientEventHandler::handleConfigEvent);
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.SERVER, ServerConfig.INSTANCE);
        ModLoadingContext.get()
                .registerConfig(ModConfig.Type.CLIENT, ClientConfig.INSTANCE);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> ClientEventHandler::register);
        ServerEventHandler.register();
        NetworkHandler.register();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }


}
