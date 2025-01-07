package io.github.raverbury.aggroindicator.client;

import io.github.raverbury.aggroindicator.ClientConfig;

public class CommonClientClass {
    public static void init() {
        ClientConfig.loadOrDefault();
    }
}
