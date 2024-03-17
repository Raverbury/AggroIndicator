package com.github.raverbury.aggroindicator;

import com.github.raverbury.aggroindicator.config.ClientConfig;
import com.github.raverbury.aggroindicator.config.ServerConfig;
import com.github.raverbury.aggroindicator.event.ClientEventHandler;
import com.github.raverbury.aggroindicator.event.ServerEventHandler;
import com.github.raverbury.aggroindicator.network.packets.S2CMobChangeTargetPacket;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AggroIndicator.MODID)
public class AggroIndicator {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "aggroindicator";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AggroIndicator(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        modEventBus.register(this);
        IEventBus neoForgeEventBus = NeoForge.EVENT_BUS;

        // Register the item to a creative tab
        // modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.INSTANCE);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.INSTANCE);
        ServerEventHandler.register(neoForgeEventBus);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEventHandler.register(neoForgeEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onRegisterPayloadEventHandler(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(MODID);
        registrar.play(S2CMobChangeTargetPacket.ID, S2CMobChangeTargetPacket::new,
                handler -> handler.client(S2CMobChangeTargetPacket::handle));
    }
}
