package io.github.raverbury.aggroindicator.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.raverbury.aggroindicator.ClientConfig;
import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.util.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class AlertRenderer {

    private static final Set<UUID> entityUuidSet = new HashSet<>();
    private static ResourceLocation aggroIcon = getConfiguredAggroIcon(0);

    private AlertRenderer() {
    }

    /**
     * Adds uuid of mob that is targeting this client player, intended to
     * be called by networking stuff.
     *
     * @param mobUuid
     */
    public static void addAggroingMob(UUID mobUuid) {
        entityUuidSet.add(mobUuid);
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
     * Check all nearby mobs if any is targeting this player, and draws an
     * alert icon for each of those.
     *
     * @param partialTick
     * @param matrix
     * @param camera
     */
    public static void renderAlertIcon(float partialTick, PoseStack matrix, Camera camera) {
        // early stop
        ClientConfig clientConfig = ClientConfig.cachedOrDefault();
        if (!clientConfig.renderAlertIcon) {
            return;
        }

        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientLevel clientLevel = Minecraft.getInstance().level;

        if (entityUuidSet.isEmpty() || camera == null || clientLevel == null ||
                localPlayer == null || localPlayer.hasEffect(
                MobEffects.BLINDNESS) || localPlayer.hasEffect(
                MobEffects.DARKNESS)) {
            return;
        }

        // prep draw
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        aggroIcon = getConfiguredAggroIcon(clientConfig.alertIconStyle);

        // grabs all nearby mobs
        List<Mob> nearbyMobs = clientLevel.getEntitiesOfClass(Mob.class,
                localPlayer.getBoundingBox()
                        .inflate(clientConfig.getClampedRenderRange()),
                (mob) -> true);

        // check aggro + blacklist then draw
        HashSet<String> blacklistedMobs =
                clientConfig.getBlacklistLookupTable();
        for (Mob mob : nearbyMobs) {
            if (!entityUuidSet.contains(mob.getUUID())) {
                continue;
            }
            String entityRegistryName = BuiltInRegistries.ENTITY_TYPE.getKey(
                    mob.getType()).toString();
            if (blacklistedMobs.contains(entityRegistryName)) {
                continue;
            }
            if (mob.hasEffect(
                    MobEffects.INVISIBILITY) || mob.isInvisible()) {
                continue;
            }
            float scaleToGui = 0.025f;
            boolean sneaking = mob.isCrouching();
            float height = mob.getBbHeight() + 0.6F - (sneaking ? 0.25F : 0.0F);

            double x = Mth.lerp(partialTick, mob.xo, mob.getX());
            double y = Mth.lerp(partialTick, mob.yo, mob.getY());
            double z = Mth.lerp(partialTick, mob.zo, mob.getZ());

            Vec3 camPos = camera.getPosition();
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            matrix.pushPose();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
            matrix.mulPose(MathHelper.rotationDegrees(YP, -camera.getYRot()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
            if (clientConfig.scaleWithMobSize) {
                float size = (float) mob.getBoundingBox().getSize();
                size *= (size > 2) ? 0.9f : 1.0f;
                matrix.scale(size, size, size);
            }
            _render(matrix, clientConfig.getClampedXOffset(),
                    -(7f + clientConfig.getClampedYOffset()),
                    (float) clientConfig.getClampedAlertIconSize());

            matrix.popPose();
        }
    }

    private static ResourceLocation getConfiguredAggroIcon(int style) {
        return switch (style) {
            case 1 -> ResourceLocation.parse(
                    Constants.MOD_ID + ":textures/alert_icon_1.png");
            case 2 -> ResourceLocation.parse(
                    Constants.MOD_ID + ":textures/alert_icon_2.png");
            default -> ResourceLocation.parse(
                    Constants.MOD_ID + ":textures/alert_icon_0.png");
        };
    }

    private static void _render(PoseStack matrix, double x, double y, float size) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, aggroIcon);
        RenderSystem.enableBlend();

        Matrix4f m4f = matrix.last().pose();
        float halfWidth = size / 2;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_TEX);

        buffer.addVertex(m4f, (float) (-halfWidth + x), (float) y, 0.25f)
                .setUv(0f, 0f);
        buffer.addVertex(m4f, (float) (-halfWidth + x), (float) (size + y),
                0.25f).setUv(0f, 1f);
        buffer.addVertex(m4f, (float) (halfWidth + x), (float) (size + y),
                0.25f).setUv(1f, 1f);
        buffer.addVertex(m4f, (float) (halfWidth + x), (float) y, 0.25f)
                .setUv(1f, 0f);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }
}