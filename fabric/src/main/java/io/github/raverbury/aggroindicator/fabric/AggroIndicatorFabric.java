package io.github.raverbury.aggroindicator.fabric;

import io.github.raverbury.aggroindicator.CommonClass;
import io.github.raverbury.aggroindicator.Constants;
import net.fabricmc.api.ModInitializer;

public class AggroIndicatorFabric implements ModInitializer {

    @Override
    public void onInitialize() {

        // This method is invoked by the Fabric mod loader when it is ready
        // to load your mod. You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common mod.
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();
    }
}
