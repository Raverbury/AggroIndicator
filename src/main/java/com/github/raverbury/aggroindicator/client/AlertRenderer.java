package com.github.raverbury.aggroindicator.client;

import com.github.raverbury.aggroindicator.common.AggroIndicator;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import net.minecraft.util.math.MathHelper;


import java.util.*;

public class AlertRenderer {

    private static final List<LivingEntity> renderedEntities = new ArrayList<>();
    private static final Set<UUID> entityUuidSet = new HashSet<>();
    private static final Identifier ALERT_ICON = new Identifier(AggroIndicator.MODID + ":textures/alert_icon.png");

    public static void addEntity(LivingEntity entity) {
        if (entity == null) {
            return;
        }

        renderedEntities.add(entity);
    }

    public static void setTarget(UUID mobUuid, UUID targetUuid) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) {
            return;
        }
        // AggroIndicator.LOGGER.debug(mobUuid.toString() + (targetUuid != null? targetUuid.toString() : "no target") + player.getUUID().toString());
        if ((targetUuid == null) || !player.getUuid().equals(targetUuid)) {
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

    public static void renderAlertIcon(float partialTick, MatrixStack matrix, Camera camera) {
        MinecraftClient client = MinecraftClient.getInstance();

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

        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE,
                GL11.GL_ZERO);

        for (LivingEntity entity : renderedEntities) {

            float scaleToGui = 0.025f;
            boolean sneaking = entity.isSneaking();
            float height = (float) (entity.getBoundingBox().getLengthY()) + 0.6F - (sneaking ? 0.25F : 0.0F);

            double x = MathHelper.lerp((double) partialTick, entity.prevX, entity.getX());
            double y = MathHelper.lerp((double) partialTick, entity.prevY, entity.getY());
            double z = MathHelper.lerp((double) partialTick, entity.prevZ, entity.getZ());

            Vec3d camPos = camera.getPos();
            double camX = camPos.getX();
            double camY = camPos.getY();
            double camZ = camPos.getZ();

            matrix.push();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            Vec3d YP = new Vec3d(0.0f, 1.0f, 0.0f);
            matrix.multiply(
                    MathHelper.rotateAround(new Vector3f(0f, 1f, 0f),
                            new Quaternionf().rotationY((float) (-camera.getYaw() * Math.PI / 180)),
                            new Quaternionf()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
            // if (ClientConfig.SCALE_WITH_MOB_SIZE.get()) {
            //     float size = (float) entity.getBoundingBox().getSize();
            //     size *= (size > 2) ? 0.9f : 1.0f;
            //     matrix.scale(size, size, size);
            // }
            // _render(matrix, ClientConfig.X_OFFSET.get(), -(7f + ClientConfig.Y_OFFSET.get()),
            //         ClientConfig.ALERT_ICON_SIZE.get().floatValue());
            _render(matrix, 0, -(7f + 20), 30f);

            matrix.pop();
        }

        renderedEntities.clear();
    }

    private static void _render(MatrixStack matrix, double x, double y, float size) {

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, ALERT_ICON);
        RenderSystem.enableBlend();

        Matrix4f m4f = matrix.peek().getPositionMatrix();
        float halfWidth = size / 2;

        Tessellator tesselator = Tessellator.getInstance();
        BufferBuilder buffer = tesselator.getBuffer();

        VertexFormat format = new VertexFormat(
                ImmutableMap.<String, VertexFormatElement>builder().put("Position",
                                new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT,
                                        VertexFormatElement.Type.POSITION, 3))
                        .put("UV0", new VertexFormatElement(0, VertexFormatElement.ComponentType.FLOAT,
                                VertexFormatElement.Type.UV, 2)).build());

        buffer.begin(VertexFormat.DrawMode.QUADS, format);
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) y, 0.25f).texture(0f, 0f).next();
        buffer.vertex(m4f, (float) (-halfWidth + x), (float) (size + y), 0.25f).texture(0f, 1f).next();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) (size + y), 0.25f).texture(1f, 1f).next();
        buffer.vertex(m4f, (float) (halfWidth + x), (float) y, 0.25f).texture(1f, 0f).next();
        tesselator.draw();
    }

}