package io.github.raverbury.aggroindicator;

import com.google.gson.GsonBuilder;
import io.github.raverbury.aggroindicator.platform.Services;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

public class ClientConfig {

    private static final File CONFIG_FILE =
            new File(Services.CONFIG_HELPER.getConfigSavePath().toFile(),
                    Constants.MOD_ID + "-client.json");
    private static final float[] cachedColors = new float[]{1f, 0f, 0f};
    private static ClientConfig CACHED_CONFIG = null;
    private static HashSet<String> CACHED_BLACKLIST_TABLE = null;
    public boolean renderAlertIcon = true;
    public boolean scaleWithMobSize = true;
    public List<String> mobBlacklist = List.of("minecraft:bat");
    public boolean treatBlacklistAsWhitelist = false;
    public int alertIconStyle = 0;
    private float renderRange = 24;
    private float xOffset = 0;
    private float yOffset = 10;
    private float alertIconSize = 30;
    private String alertColorHex = "0xFF6666";

    public static ClientConfig cachedOrDefault() {
        if (CACHED_CONFIG != null) {
            return CACHED_CONFIG;
        }

        return new ClientConfig();
    }

    public static ClientConfig loadOrDefault() {
        if (!CONFIG_FILE.exists()) {
            save(new ClientConfig());
        }

        ClientConfig config;

        try (Reader reader = Files.newBufferedReader(CONFIG_FILE.toPath())) {
            config =
                    new GsonBuilder().setPrettyPrinting().create()
                            .fromJson(reader, ClientConfig.class);
        } catch (IOException e) {
            Constants.LOG.error("[Aggro Indicator] Loading config failed: {}," +
                            " using default value.",
                    e.getMessage(), e);
            config = new ClientConfig();
        }

        CACHED_BLACKLIST_TABLE = null;
        Color alertColor = Color.decode(config.alertColorHex);
        cachedColors[0] = alertColor.getRed() / 255f;
        cachedColors[1] = alertColor.getGreen() / 255f;
        cachedColors[2] = alertColor.getBlue() / 255f;
        CACHED_CONFIG = config;
        return config;
    }

    public static void save(ClientConfig clientConfig) {
        try (Writer writer = Files.newBufferedWriter(CONFIG_FILE.toPath())) {
            new GsonBuilder().setPrettyPrinting().create().toJson(
                    clientConfig, writer);
        } catch (IOException e) {
            Constants.LOG.error("[Aggro Indicator] Saving config failed: {}",
                    e.getMessage(), e);
        }
    }

    public float getClampedRenderRange() {
        return Math.min(32f, Math.max(renderRange, 0f));
    }

    public float getClampedXOffset() {
        return Math.min(30f, Math.max(xOffset, -30f));
    }

    public float getClampedYOffset() {
        return Math.min(50f, Math.max(yOffset, -50f));
    }

    public float getClampedAlertIconSize() {
        return Math.min(100f, Math.max(alertIconSize, 10f));
    }

    public HashSet<String> getBlacklistLookupTable() {
        if (CACHED_BLACKLIST_TABLE != null) {
            return CACHED_BLACKLIST_TABLE;
        }
        CACHED_BLACKLIST_TABLE = new HashSet<>(mobBlacklist);
        return CACHED_BLACKLIST_TABLE;
    }

    public float[] getColors() {
        return cachedColors;
    }
}
