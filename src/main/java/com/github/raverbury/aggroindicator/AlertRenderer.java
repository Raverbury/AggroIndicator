package com.github.raverbury.aggroindicator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class AlertRenderer {

    private static final List<LivingEntity> renderedEntities = new ArrayList<>();
    private static final Set<UUID> entityUuidSet = new HashSet<>();
    private static final float FULL_SIZE = 30f;
    private static final float Y_OFFSET = -7f;
    private static final ResourceLocation ALERT_ICON = new ResourceLocation(AggroIndicator.MODID + ":textures/mgs_alert_icon.png");

    public static void addEntity(LivingEntity entity) {
        if (entity == null) {
            return;
        }
        renderedEntities.add(entity);
    }

    public static void setTarget(UUID mobUuid, UUID targetUuid) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) {
            return;
        }
        // AggroIndicator.LOGGER.debug(mobUuid.toString() + (targetUuid != null? targetUuid.toString() : "no target") + player.getUUID().toString());
        if ((targetUuid == null) || !player.getUUID().equals(targetUuid)) {
            entityUuidSet.remove(mobUuid);
            // AggroIndicator.LOGGER.debug(entityUuidSet.toString());
            return;
        }
        entityUuidSet.add(mobUuid);
        // AggroIndicator.LOGGER.debug(entityUuidSet.toString());
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
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                GL11.GL_ZERO);

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
            matrix.mulPose(Vector3f.YP.rotationDegrees(-camera.getYRot()));
            // matrix.mulPose(Vector3f.XP.rotationDegrees(camera.getXRot()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);

            _render(matrix, entity, 0, Y_OFFSET, FULL_SIZE);

            matrix.popPose();
        }

        renderedEntities.clear();
    }

    private static void _render(PoseStack matrix, LivingEntity entity, double x, double y, float width) {

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ALERT_ICON);
        RenderSystem.enableBlend();

        Matrix4f m4f = matrix.last().pose();
        float halfWidth = width / 2;
        float height = FULL_SIZE;

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) y, -0.1f).uv(0f, 0f).endVertex();
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) (height + y), -0.1f).uv(0f, 1f).endVertex();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) (height + y), -0.1f).uv(1f, 1f).endVertex();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) y, 0f).uv(1f, -0.1f).endVertex();
        tesselator.end();
    }

}
