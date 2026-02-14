package io.github.raverbury.aggroindicator.fabric.platform;

import io.github.raverbury.aggroindicator.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <V, T extends V> Supplier<T> register(Registry<V> registryType,
                                                 ResourceLocation identifier,
                                                 Supplier<T> valueSupplier) {
        T res = Registry.register(registryType, identifier, valueSupplier.get());
        return () -> res;
    }
}