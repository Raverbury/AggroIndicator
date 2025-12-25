package io.github.raverbury.aggroindicator.neoforge.platform;

import io.github.raverbury.aggroindicator.platform.services.IConfigHelper;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class NeoForgeConfigHelper implements IConfigHelper {
    @Override
    public Path getConfigSavePath() {
        return FMLPaths.CONFIGDIR.get();
    }
}
