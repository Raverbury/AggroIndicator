package io.github.raverbury.aggroindicator.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.raverbury.aggroindicator.AggroIndicator;
import io.github.raverbury.aggroindicator.client.config.ClientConfig;
import io.github.raverbury.aggroindicator.util.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.*;

public final class AlertRenderer {

    private static final Map<UUID, Boolean> entityUuidSet = new HashMap<>();
    private static Identifier aggroIcon = getConfiguredAggroIcon(0);

    private AlertRenderer() {
    }

    /**
     * Adds uuid of mob that is targeting this client player, intended to
     * be called by networking stuff.
     *
     * @param mobUuid
     */
    public static void addAggroingMob(UUID mobUuid, boolean isAboutToAttack) {
        entityUuidSet.put(mobUuid, isAboutToAttack);
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
    public static void renderAlertIcon(float partialTick, MatrixStack matrix,
                                       Camera camera) {
        // early stop
        ClientConfig clientConfig = ClientConfig.cachedOrDefault();
        if (!clientConfig.renderAlertIcon) {
            return;
        }

        ClientPlayerEntity localPlayer = MinecraftClient.getInstance().player;
        ClientWorld clientLevel = MinecraftClient.getInstance().world;

        if (entityUuidSet.isEmpty() || camera == null || clientLevel == null ||
                localPlayer == null || localPlayer.hasStatusEffect(
                StatusEffects.BLINDNESS) || localPlayer.hasStatusEffect(
                StatusEffects.DARKNESS)) {
            return;
        }

        // prep draw
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA,
                GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        aggroIcon = getConfiguredAggroIcon(clientConfig.alertIconStyle);

        // grabs all nearby mobs
        List<MobEntity> nearbyMobs = clientLevel.getEntitiesByClass(MobEntity.class,
                localPlayer.getBoundingBox()
                        .expand(clientConfig.getClampedRenderRange()),
                (mob) -> true);

        // check aggro + blacklist then draw
        HashSet<String> blacklistedMobs =
                clientConfig.getBlacklistLookupTable();
        for (MobEntity mob : nearbyMobs) {
            if (!entityUuidSet.containsKey(mob.getUuid())) {
                continue;
            }
            String entityRegistryName = Registries.ENTITY_TYPE.getKey(
                    mob.getType()).get().getValue().toString();
            if (blacklistedMobs.contains(entityRegistryName)) {
                continue;
            }
            if (mob.hasStatusEffect(
                    StatusEffects.INVISIBILITY) || mob.isInvisible()) {
                continue;
            }
            float scaleToGui = 0.025f;
            boolean sneaking = mob.isSneaking();
            float height = (float) (mob.getBoundingBox().getYLength() + 0.6F - (sneaking ? 0.25F :
                                0.0F));

            double x = net.minecraft.util.math.MathHelper.lerp(partialTick,
                    mob.prevX, mob.getX());
            double y = net.minecraft.util.math.MathHelper.lerp(partialTick,
                    mob.prevY, mob.getY());
            double z = net.minecraft.util.math.MathHelper.lerp(partialTick,
                    mob.prevZ, mob.getZ());

            Vec3d camPos = camera.getPos();
            double camX = camPos.getX();
            double camY = camPos.getY();
            double camZ = camPos.getZ();

            matrix.push();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
            matrix.multiply(MathHelper.rotationDegrees(YP, -camera.getYaw()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
            if (clientConfig.scaleWithMobSize) {
                float size = (float) mob.getBoundingBox().getAverageSideLength();
                size *= (size > 2) ? 0.9f : 1.0f;
                matrix.scale(size, size, size);
            }

            float[] colors = clientConfig.getColors();
            _render(matrix, clientConfig.getClampedXOffset(),
                    -(7f + clientConfig.getClampedYOffset()),
                    clientConfig.getClampedAlertIconSize(),
                    entityUuidSet.get(mob.getUuid()), colors);

            matrix.pop();
        }
        RenderSystem.disableBlend();
    }

    private static Identifier getConfiguredAggroIcon(int style) {
        return switch (style) {
            case 1 -> new Identifier(
                    AggroIndicator.MOD_ID + ":textures/alert_icon_1.png");
            case 2 -> new Identifier(
                    AggroIndicator.MOD_ID + ":textures/alert_icon_2.png");
            default -> new Identifier(
                    AggroIndicator.MOD_ID + ":textures/alert_icon_0.png");
        };
    }

    private static void _render(MatrixStack matrix, double x, double y,
                                float size, boolean isAboutToAttack,
                                float[] colors) {
        RenderSystem.setShaderColor(colors[0], colors[1], colors[2], 1f);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, aggroIcon);
        RenderSystem.enableBlend();

        Matrix4f m4f = matrix.peek().getPositionMatrix();
        float halfWidth = size / 2;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

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
        tessellator.draw();
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
    }
}
