package io.github.raverbury.aggroindicator.client;

import com.mojang.blaze3d.vertex.*;
import io.github.raverbury.aggroindicator.ClientConfig;
import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.modules.AggroSoundPlayer;
import io.github.raverbury.aggroindicator.util.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public final class AlertRenderer {

    private static final Map<UUID, Tuple<Integer, Long>> entityUuidSet = new HashMap<>();
    private static Identifier aggroIconIdentifier = getConfiguredAggroIcon(0);

    private AlertRenderer() {
    }

    /**
     * Adds uuid of mob that is targeting this client player, intended to
     * be called by networking stuff.
     *
     * @param mobUuid
     */
    public static void addAggroingMob(UUID mobUuid, boolean isAboutToAttack) {
        Tuple<Integer, Long> a = entityUuidSet.putIfAbsent(mobUuid, new Tuple<>(0, 0L));

        if (a == null)
        {
            AggroSoundPlayer.playClientSoundForPlayer(Minecraft.getInstance().player);
        }
    }

    /**
     * Removes uuid of mob that is targeting this client player, intended to
     * be called by networking stuff.
     *
     * @param mobUuid
     */
    public static void removeAggroingMob(UUID mobUuid) {
        entityUuidSet.remove(mobUuid);
    }

    /**
     * Clears all mobs targeting this client player, called on world leave or
     * such.
     */
    public static void clearAggroingMobs() {
        entityUuidSet.clear();
    }

    /**
     * Increases "seen" frame count by 1, used for "hide after some time" feature
     */
    public static void increaseSeenFrameCountForEntity(UUID mobUuid, long currentTick) {
        if (!entityUuidSet.containsKey(mobUuid)) {
            return;
        }
        Tuple<Integer, Long> oldTuple = entityUuidSet.get((mobUuid));
        if (oldTuple == null) {
            oldTuple = new Tuple<>(0, 0L);
        }
        if (oldTuple.getB() >= currentTick) {
            return;
        }
        oldTuple.setA(oldTuple.getA() + 1);
        oldTuple.setB(currentTick);
        entityUuidSet.replace(mobUuid, oldTuple);
    }

    public static void setAggroIcon(int aggroIconIndex) {
        aggroIconIdentifier = getConfiguredAggroIcon(aggroIconIndex);
    }

    /**
     * Draws aggro icon for this entity, if applicable.
     */
    public static void renderAlertIconForEntity(Entity entity, float partialTick, PoseStack matrix, MultiBufferSource multiBufferSource, Camera camera) {
        // early stop
        ClientConfig clientConfig = ClientConfig.cachedOrDefault();
        if (!clientConfig.renderAlertIcon) {
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientLevel clientLevel = Minecraft.getInstance().level;

        boolean SHOULD_RETURN_EARLY_1 =
                entityUuidSet.isEmpty() ||
                        !(entity instanceof Mob) ||
                        camera == null ||
                        clientLevel == null ||
                        localPlayer == null ||
                        localPlayer.hasEffect(MobEffects.BLINDNESS) ||
                        localPlayer.hasEffect(MobEffects.DARKNESS);

        if (SHOULD_RETURN_EARLY_1) {
            return;
        }

        Mob mob = (Mob) entity;

        // check timer, stop if seen tick exceed configured hideTimer
        int hideTimer = clientConfig.getHideTimer();
        if (!entityUuidSet.containsKey(entity.getUUID())) {
            return;
        }
        Tuple<Integer, Long> tuple = entityUuidSet.get(entity.getUUID());
        if (tuple == null)
        {
//            tuple = new Tuple<>(0, 0L);
        }
        if (hideTimer > 0 && tuple.getA() > hideTimer) {
            return;
        }

        // check if mob has invisibility
        if (mob.hasEffect(
                MobEffects.INVISIBILITY) || entity.isInvisible()) {
            return;
        }

        // check aggro list, stop if not present
        if (!entityUuidSet.containsKey(entity.getUUID())) {
            return;
        }

        // check blacklist, stop if present, or for whitelist, stop if not present
        HashSet<String> blacklistedMobs =
                clientConfig.getBlacklistLookupTable();
        String entityRegistryName = BuiltInRegistries.ENTITY_TYPE.getKey(
                entity.getType()).toString();
        boolean treatAsWhitelist = clientConfig.treatBlacklistAsWhitelist;
        boolean inList = blacklistedMobs.contains(entityRegistryName);
        if ((treatAsWhitelist && !inList) || (!treatAsWhitelist && inList)) {
            return;
        }

        // math time
        float scaleToGui = 0.025f;
        boolean sneaking = entity.isCrouching();
        float height = entity.getBbHeight() + 0.6F - (sneaking ? 0.25F : 0.0F);

        double x = Mth.lerp(partialTick, entity.xo, entity.getX());
        double y = Mth.lerp(partialTick, entity.yo, entity.getY());
        double z = Mth.lerp(partialTick, entity.zo, entity.getZ());

        Vec3 camPos = camera.position();
        double camX = camPos.x();
        double camY = camPos.y();
        double camZ = camPos.z();

        matrix.pushPose();
        matrix.translate(x - camX, (y + height) - camY, z - camZ);
        Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
        matrix.mulPose(MathHelper.rotationDegrees(YP, -camera.yRot()));
        matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
        if (clientConfig.scaleWithMobSize) {
            float size = (float) entity.getBoundingBox().getSize();
            size *= (size > 2) ? 0.9f : 1.0f;
            matrix.scale(size, size, size);
        }
        float[] rgb = clientConfig.getAlertColorRGB();

        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderTypes.text(aggroIconIdentifier));

        _render(vertexConsumer, matrix, clientConfig.getClampedXOffset(),
                -(7f + clientConfig.getClampedYOffset()),
                clientConfig.getClampedAlertIconSize(),
                rgb);

        matrix.popPose();
    }

    private static Identifier getConfiguredAggroIcon(int style) {
        return switch (style) {
            case 1 -> Identifier.parse(
                    Constants.MOD_ID + ":textures/alert_icon_1.png");
            case 2 -> Identifier.parse(
                    Constants.MOD_ID + ":textures/alert_icon_2.png");
            default -> Identifier.parse(
                    Constants.MOD_ID + ":textures/alert_icon_0.png");
        };
    }

    private static void _render(VertexConsumer vertexConsumer, PoseStack poseStack, double x, double y,
                                float size,
                                float[] rgb) {
        Matrix4f m4f = poseStack.last().pose();
        PoseStack.Pose last = poseStack.last();
        final int LIGHT = 0xF000F0;
        float halfWidth = size / 2;
        float r = rgb[0];
        float g = rgb[1];
        float b = rgb[2];
        final float a = 1f;

        vertexConsumer.addVertex(m4f, (float) (-halfWidth + x), (float) y, 0.25f)
                .setUv(0f, 0f).setColor(r, g, b, a).setLight(LIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, 0.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(m4f, (float) (-halfWidth + x), (float) (size + y),
                0.25f).setUv(0f, 1f).setColor(r, g, b, a).setLight(LIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, 0.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(m4f, (float) (halfWidth + x), (float) (size + y),
                0.25f).setUv(1f, 1f).setColor(r, g, b, a).setLight(LIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, 0.0F, 0.0F, 0.0F);
        vertexConsumer.addVertex(m4f, (float) (halfWidth + x), (float) y, 0.25f)
                .setUv(1f, 0f).setColor(r, g, b, a).setLight(LIGHT).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(last, 0.0F, 0.0F, 0.0F);
    }
}