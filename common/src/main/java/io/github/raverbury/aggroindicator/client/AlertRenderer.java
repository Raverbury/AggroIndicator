package io.github.raverbury.aggroindicator.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.raverbury.aggroindicator.Constants;
import io.github.raverbury.aggroindicator.util.MathHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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

    private static final Set<LivingEntity> renderedEntities = new HashSet<>();
    private static final Set<UUID> entityUuidSet = new HashSet<>();
    private static ResourceLocation aggroIcon = getConfiguredAggroIcon();

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
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        ClientLevel clientLevel = Minecraft.getInstance().level;

        // early stop
        if (camera == null || clientLevel == null ||
                localPlayer == null || localPlayer.hasEffect(
                MobEffects.BLINDNESS) || localPlayer.hasEffect(
                MobEffects.DARKNESS)) {
            return;
        }
        // if (entityUuidSet.isEmpty() || camera == null || clientLevel == null ||
        //         localPlayer == null || localPlayer.hasEffect(
        //         MobEffects.BLINDNESS) || localPlayer.hasEffect(
        //         MobEffects.DARKNESS)) {
        //     return;
        // }

        // grabs all nearby mobs
        List<Mob> mobs = clientLevel.getEntitiesOfClass(Mob.class,
                localPlayer.getBoundingBox().inflate(Constants.DEFAULT_RANGE),
                (mob) -> true);

        for (Mob mob : mobs) {
            Constants.LOG.info(mob.getName().getString());
            if (mob.hasEffect(
                    MobEffects.INVISIBILITY) || mob.isInvisible()) {
                continue;
            }
            if (true || entityUuidSet.contains(mob.getUUID())) {
                renderedEntities.add(mob);
            }
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

            double x = Mth.lerp(partialTick, entity.xo, entity.getX());
            double y = Mth.lerp(partialTick, entity.yo, entity.getY());
            double z = Mth.lerp(partialTick, entity.zo, entity.getZ());

            Vec3 camPos = camera.getPosition();
            double camX = camPos.x();
            double camY = camPos.y();
            double camZ = camPos.z();

            matrix.pushPose();
            matrix.translate(x - camX, (y + height) - camY, z - camZ);
            Vector3f YP = new Vector3f(0.0f, 1.0f, 0.0f);
            matrix.mulPose(MathHelper.rotationDegrees(YP, -camera.getYRot()));
            matrix.scale(-scaleToGui, -scaleToGui, scaleToGui);
            // if (ClientConfig.scaleWithMobSize) {
            //     float size = (float) entity.getBoundingBox().getSize();
            //     size *= (size > 2) ? 0.9f : 1.0f;
            //     matrix.scale(size, size, size);
            // }
            // _render(matrix, ClientConfig.xOffset, -(7f + ClientConfig.yOffset),
            //         (float) ClientConfig.alertIconSize);
            _render(matrix, 0, -(7f + 10), (float) 30);

            matrix.popPose();
        }

        renderedEntities.clear();
    }

    public static void reloadAggroIcon() {
        aggroIcon = getConfiguredAggroIcon();
    }

    private static ResourceLocation getConfiguredAggroIcon() {
        return ResourceLocation.parse(
                Constants.MOD_ID + ":textures" + "/alert_icon_classic.png");
        // return switch (ClientConfig.clientAggroIconStyle) {
        //     case MGS -> ResourceLocation.parse(
        //             AggroIndicator.MODID + ":textures/alert_icon_mgs.png");
        //     case BLOCK_BENCH -> ResourceLocation.parse(
        //             AggroIndicator.MODID + ":textures/alert_icon_block_bench.png");
        //     default -> ResourceLocation.parse(
        //             AggroIndicator.MODID + ":textures/alert_icon_classic.png");
        // };
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