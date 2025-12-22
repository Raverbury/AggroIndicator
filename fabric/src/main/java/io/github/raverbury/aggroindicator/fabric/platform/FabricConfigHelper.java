package io.github.raverbury.aggroindicator.fabric.platform;

import io.github.raverbury.aggroindicator.platform.services.IConfigHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class FabricConfigHelper implements IConfigHelper {
    @Override
    public Path getConfigSavePath() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
