package com.github.raverbury.aggroindicator;

import com.github.raverbury.aggroindicator.config.ClientConfig;
import com.github.raverbury.aggroindicator.util.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class AlertRenderer {

    private static final List<LivingEntity> renderedEntities = new ArrayList<>();
    private static final Set<UUID> entityUuidSet = new HashSet<>();
    private static ResourceLocation aggroIcon = getConfiguredAggroIcon();

    public static void addEntity(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        renderedEntities.add(entity);
    }

    public static void addAggroingMob(UUID mobUuid) {
        entityUuidSet.add(mobUuid);
    }

    public static void removeAggroingMob(UUID mobUuid) {
        entityUuidSet.remove(mobUuid);
    }

    public static void clearAggroingMobs() {
        entityUuidSet.clear();
    }

    public static boolean shouldDrawThisUuid(UUID uuid) {
        return entityUuidSet.contains(uuid);
    }

    public static void renderAlertIcon(float partialTick, PoseStack matrix, Camera camera) {
        Minecraft client = Minecraft.getInstance();

        if (camera == null) {
            camera = client.getEntityRenderDispatcher().camera;
        }

        if (camera == null) {
            renderedEntities.clear();
            return;
        }

        if (renderedEntities.isEmpty()) {
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        for (LivingEntity entity : renderedEntities) {

            float scaleToGui = 0.025f;
            boolean sneaking = entity.isCrouching();
            float height = entity.getBbHeight() + 0.6F - (sneaking ? 0.25F : 0.0F);

            double x = Mth.lerp((double) partialTick, entity.xo, entity.getX());
            double y = Mth.lerp((double) partialTick, entity.yo, entity.getY());
            double z = Mth.lerp((double) partialTick, entity.zo, entity.getZ());

            Vec3 camPos = camera.getPosition();
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            matrix.pushPose();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
            matrix.mulPose(MathHelper.rotationDegrees(YP, -camera.getYRot()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
            if (ClientConfig.Cached.SCALE_WITH_MOB_SIZE) {
                float size = (float) entity.getBoundingBox().getSize();
                size *= (size > 2) ? 0.9f : 1.0f;
                matrix.scale(size, size, size);
            }
            _render(matrix, ClientConfig.Cached.X_OFFSET,
                    -(7f + ClientConfig.Cached.Y_OFFSET),
                    (float) ClientConfig.Cached.ALERT_ICON_SIZE);

            matrix.popPose();
        }

        renderedEntities.clear();
    }

    public static void reloadAggroIcon() {
        aggroIcon = getConfiguredAggroIcon();
    }

    private static ResourceLocation getConfiguredAggroIcon() {
        return switch (ClientConfig.Cached.CLIENT_AGGRO_ICON_STYLE) {
            case MGS -> new ResourceLocation(
                    AggroIndicator.MODID + ":textures/alert_icon_mgs.png");
            case BLOCK_BENCH -> new ResourceLocation(
                    AggroIndicator.MODID + ":textures/alert_icon_block_bench.png");
            default -> new ResourceLocation(
                    AggroIndicator.MODID + ":textures/alert_icon_classic.png");
        };
    }

    private static void _render(PoseStack matrix, double x, double y, float size) {
        float[] rgbValues = ClientConfig.Cached.COLORS;
        RenderSystem.setShaderColor(rgbValues[0], rgbValues[1], rgbValues[2],
                1f);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, aggroIcon);
        RenderSystem.enableBlend();

        Matrix4f m4f = matrix.last().pose();
        float halfWidth = size / 2;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) y, 0.25f)
                .uv(0f, 0f).endVertex();
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) (size + y), 0.25f)
                .uv(0f, 1f).endVertex();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) (size + y), 0.25f)
                .uv(1f, 1f).endVertex();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) y, 0.25f).uv(1f, 0f)
                .endVertex();
        tesselator.end();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }

}
