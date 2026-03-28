package io.github.raverbury.aggroindicator.client;

import io.github.raverbury.aggroindicator.config.ClientConfig;

public class CommonClientClass {
    public static void init() {
        ClientConfig.save(ClientConfig.loadOrDefault());
    }
}
