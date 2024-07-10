package io.github.raverbury.aggroindicator;

import com.mojang.logging.LogUtils;
import io.github.raverbury.aggroindicator.client.ClientConfig;
import io.github.raverbury.aggroindicator.client.ClientEventHandler;
import io.github.raverbury.aggroindicator.client.ClientPayloadHandler;
import io.github.raverbury.aggroindicator.common.CommonConfig;
import io.github.raverbury.aggroindicator.common.CommonEventHandler;
import io.github.raverbury.aggroindicator.common.network.packets.S2CMobChangeTargetPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AggroIndicator.MODID)
public class AggroIndicator {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "aggroindicator";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public AggroIndicator(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::registerNetworkPayload);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        CommonEventHandler.register();

        // Client only registration
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ClientEventHandler.register(modEventBus);
            modContainer.registerConfig(ModConfig.Type.CLIENT,
                    ClientConfig.SPEC);
        }
    }

    // private void commonSetup(final FMLCommonSetupEvent event) {
    //     // Some common setup code
    //     // LOGGER.info("HELLO FROM COMMON SETUP");
    // }

    public void registerNetworkPayload(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(S2CMobChangeTargetPacket.TYPE,
                S2CMobChangeTargetPacket.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleData,
                        ((s2CMobChangeTargetPacket, iPayloadContext) -> {
                        })));
    }
}


