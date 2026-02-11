package io.github.raverbury.aggroindicator.neoforge.platform;

import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.neoforge.AggroIndicatorNeoForge;
import io.github.raverbury.aggroindicator.platform.services.IPlatformHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {

        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public <V, T extends V> Supplier<T> register(Registry<V> registryType, Identifier identifier, Supplier<T> valueSupplier) {
        DeferredRegister<V> dr = DeferredRegister.create(registryType, Constants.MOD_ID);
        DeferredHolder<V, T> res = dr.register(identifier.getPath(), valueSupplier);
        dr.register(AggroIndicatorNeoForge.getModEventBus());
        return res;
    }
}