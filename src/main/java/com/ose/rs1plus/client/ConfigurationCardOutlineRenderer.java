package com.ose.rs1plus.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.ose.rs1plus.RS1PlusMod;
import com.ose.rs1plus.item.ConfigurationCardItem;
import com.ose.rs1plus.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.OptionalDouble;

@Mod.EventBusSubscriber(modid = RS1PlusMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ConfigurationCardOutlineRenderer {

    private static final float R = 0.0F;
    private static final float G = 1.0F;
    private static final float B = 1.0F;
    private static final float LINE_ALPHA = 0.90F;

    private static final long FEEDBACK_DURATION = 750L;

    private static BlockPos feedbackPos = null;
    private static long feedbackStartTime = 0L;

    private static final RenderType LINES_NO_DEPTH = ModRenderTypes.LINES_NO_DEPTH;

    // Rendering technique adapted from supermartijn642corelib by SuperMartijn642.
    // Used with explicit permission from the author (Discord).
    // Original library: https://github.com/SuperMartijn642/SuperMartijn642sCoreLib
    private static abstract class ModRenderTypes extends RenderType {

        private ModRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
        }

        private static final RenderType LINES_NO_DEPTH = create(
                "rs1plus_lines_no_depth",
                DefaultVertexFormat.POSITION_COLOR_NORMAL,
                VertexFormat.Mode.LINES,
                256,
                false,
                true,
                CompositeState.builder()
                        .setShaderState(new ShaderStateShard(GameRenderer::getRendertypeLinesShader))
                        .setLineState(new LineStateShard(OptionalDouble.empty()))
                        .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setOutputState(MAIN_TARGET)
                        .setWriteMaskState(COLOR_WRITE)
                        .setCullState(NO_CULL)
                        .setDepthTestState(NO_DEPTH_TEST)
                        .createCompositeState(false)
        );
    }

    // Antes esto iba en un mixin al HEAD de LevelRenderer#renderLevel, pero con
    // shaders (Oculus/Embedium) el outline a veces no se dibujaba: dependia del
    // orden en que se aplicaban los mixins, no de un conflicto real entre mods.
    // AFTER_TRANSLUCENT_BLOCKS evita eso porque corre ya con los gbuffers del
    // shader bindeados, que es el punto pensado para overlays como este.
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        Level level = mc.level;

        if (player == null || level == null) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 camPos = mc.gameRenderer.getMainCamera().getPosition();
        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();

        renderCardOutline(player, level, poseStack, camPos, bufferSource);
        renderFeedbackOutline(level, poseStack, camPos, bufferSource);

        bufferSource.endBatch(LINES_NO_DEPTH);
    }

    private static void renderCardOutline(Player player, Level level, PoseStack poseStack, Vec3 camPos,
                                          MultiBufferSource.BufferSource bufferSource) {
        ItemStack stack = getHeldConfigurationCard(player);
        if (stack.isEmpty()) {
            return;
        }

        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(ConfigurationCardItem.TAG_POS) || !tag.contains(ConfigurationCardItem.TAG_DIMENSION)) {
            return;
        }

        ResourceLocation cardDim = ResourceLocation.tryParse(tag.getString(ConfigurationCardItem.TAG_DIMENSION));
        if (cardDim == null || !cardDim.equals(level.dimension().location())) {
            return;
        }

        BlockPos pos = BlockPos.of(tag.getLong(ConfigurationCardItem.TAG_POS));
        if (!level.isLoaded(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }

        VoxelShape shape = state.getOcclusionShape(level, pos);
        if (shape.isEmpty()) {
            shape = Shapes.block();
        }

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        renderShapeLines(poseStack, shape, bufferSource, R, G, B, LINE_ALPHA);
        poseStack.popPose();
    }

    private static void renderFeedbackOutline(Level level, PoseStack poseStack, Vec3 camPos,
                                              MultiBufferSource.BufferSource bufferSource) {
        if (feedbackPos == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - feedbackStartTime;
        if (elapsed >= FEEDBACK_DURATION) {
            feedbackPos = null;
            return;
        }

        if (!level.isLoaded(feedbackPos)) {
            return;
        }

        BlockState feedbackState = level.getBlockState(feedbackPos);
        if (feedbackState.isAir()) {
            return;
        }

        VoxelShape feedbackShape = feedbackState.getOcclusionShape(level, feedbackPos);
        if (feedbackShape.isEmpty()) {
            feedbackShape = Shapes.block();
        }

        float alpha = 1.0F - (float) elapsed / FEEDBACK_DURATION;

        poseStack.pushPose();
        poseStack.translate(-camPos.x, -camPos.y, -camPos.z);
        poseStack.translate(feedbackPos.getX(), feedbackPos.getY(), feedbackPos.getZ());
        renderShapeLines(poseStack, feedbackShape, bufferSource, 0.0F, 1.0F, 0.0F, alpha);
        poseStack.popPose();
    }

    public static void showConfigurationFeedback(BlockPos pos) {
        feedbackPos = pos;
        feedbackStartTime = System.currentTimeMillis();
    }

    private static void renderShapeLines(PoseStack poseStack, VoxelShape shape, MultiBufferSource.BufferSource bufferSource,
                                         float r, float g, float b, float a) {
        VertexConsumer consumer = bufferSource.getBuffer(LINES_NO_DEPTH);
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f normalMatrix = poseStack.last().normal();

        shape.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
            float dx = (float) (x2 - x1);
            float dy = (float) (y2 - y1);
            float dz = (float) (z2 - z1);

            float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (len > 1.0E-6F) {
                dx /= len;
                dy /= len;
                dz /= len;
            }

            consumer.vertex(matrix, (float) x1, (float) y1, (float) z1)
                    .color(r, g, b, a)
                    .normal(normalMatrix, dx, dy, dz)
                    .endVertex();

            consumer.vertex(matrix, (float) x2, (float) y2, (float) z2)
                    .color(r, g, b, a)
                    .normal(normalMatrix, dx, dy, dz)
                    .endVertex();
        });
    }

    private static ItemStack getHeldConfigurationCard(Player player) {
        ItemStack main = player.getMainHandItem();
        if (main.is(ModItems.CONFIGURATION_CARD.get())) {
            return main;
        }

        ItemStack off = player.getOffhandItem();
        if (off.is(ModItems.CONFIGURATION_CARD.get())) {
            return off;
        }

        return ItemStack.EMPTY;
    }
}