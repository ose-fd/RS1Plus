package com.ose.rs1plus.client;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ose.rs1plus.block.StorageMonitorPanelBlock;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.block.StorageMonitorBlock;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.util.TransformationHelper;
import net.minecraftforge.fluids.FluidStack;
import org.joml.Vector3f;

public class StorageMonitorPanelBlockEntityRenderer implements BlockEntityRenderer<StorageMonitorBlockEntity> {

    private static final float PANEL_ITEM_OFFSET = -0.340F;
    private static final float PANEL_ITEM_SCALE_X = 0.4F;
    private static final float PANEL_ITEM_SCALE_Y = -0.4F;
    private static final float PANEL_ITEM_Y = 0.0453F;

    private static final float PANEL_TEXT_OFFSET = -0.340F;
    private static final float PANEL_TEXT_Y = -0.229F;
    private static final float PANEL_TEXT_SCALE = 0.008F;

    private static final float PANEL_FLUID_OFFSET = -0.339F;
    private static final float PANEL_FLUID_Y = 0.45F;
    private static final float PANEL_FLUID_SCALE = 0.4F;

    private static final float VANILLA_ITEM_OFFSET = 0.501F;
    private static final float VANILLA_ITEM_SCALE_X = 0.5F;
    private static final float VANILLA_ITEM_SCALE_Y = -0.5F;

    private static final float VANILLA_TEXT_OFFSET = 0.501F;
    private static final float VANILLA_TEXT_Y = -0.275F;
    private static final float VANILLA_TEXT_SCALE = 0.01F;

    private static final float VANILLA_FLUID_OFFSET = 0.51F;
    private static final float VANILLA_FLUID_SCALE = 0.5F;

    @Override
    public void render(StorageMonitorBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int light1, int overlay1) {
        Direction direction = Direction.NORTH;

        BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() instanceof StorageMonitorBlock storageMonitorBlock) {
            direction = state.getValue(storageMonitorBlock.getDirection().getProperty());
        }

        boolean isPanel = state.getBlock() instanceof StorageMonitorPanelBlock;

        final int light = isPanel
                ? LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos())
                : LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().offset(direction.getNormal()));
        final float yaw = (float) (Math.PI * (360 - direction.getOpposite().get2DDataValue() * 90) / 180d);

        final int type = blockEntity.getStackType();
        final ItemStack itemStack = blockEntity.getItemStack();
        final FluidStack fluidStack = blockEntity.getFluidStack();

        if (type == IType.ITEMS && itemStack != null && !itemStack.isEmpty()) {
            renderItem(poseStack, buffer, direction, yaw, light, itemStack, isPanel);
            String amount = API.instance().getQuantityFormatter().formatWithUnits(blockEntity.getAmount());
            renderText(poseStack, buffer, direction, yaw, light, amount, isPanel);
        } else if (type == IType.FLUIDS && fluidStack != null && !fluidStack.isEmpty()) {
            renderFluid(poseStack, buffer, direction, yaw, light, fluidStack, isPanel);
            String amount = API.instance().getQuantityFormatter().formatInBucketFormWithOnlyTrailingDigitsIfZero(blockEntity.getAmount());
            renderText(poseStack, buffer, direction, yaw, light, amount, isPanel);
        }
    }

    private void renderItem(PoseStack poseStack, MultiBufferSource buffer, Direction direction,
                            float yaw, int light, ItemStack itemStack, boolean isPanel) {
        poseStack.pushPose();

        float offset = isPanel ? PANEL_ITEM_OFFSET : VANILLA_ITEM_OFFSET;
        float scaleX = isPanel ? PANEL_ITEM_SCALE_X : VANILLA_ITEM_SCALE_X;
        float scaleY = isPanel ? PANEL_ITEM_SCALE_Y : VANILLA_ITEM_SCALE_Y;
        float yOffset = isPanel ? PANEL_ITEM_Y : 0.0F;

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate(
                (float) direction.getStepX() * offset,
                yOffset,
                (float) direction.getStepZ() * offset
        );

        poseStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(0, yaw, 0), false));

        poseStack.scale(scaleX, scaleY, -0.00005f);
        poseStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(0, 0, 180), true));

        BakedModel itemModel = Minecraft.getInstance().getItemRenderer().getModel(itemStack, null, null, 0);
        boolean render3D = itemModel.isGui3d();

        if (render3D) {
            Lighting.setupFor3DItems();
        } else {
            Lighting.setupForFlatItems();
        }

        Minecraft.getInstance().getItemRenderer().render(
                itemStack, ItemDisplayContext.GUI, false, poseStack, buffer,
                light, OverlayTexture.NO_OVERLAY, itemModel
        );

        poseStack.popPose();
    }

    private void renderText(PoseStack poseStack, MultiBufferSource buffer, Direction direction,
                            float yaw, int light, String amount, boolean isPanel) {
        poseStack.pushPose();

        float offset = isPanel ? PANEL_TEXT_OFFSET : VANILLA_TEXT_OFFSET;
        float yOffset = isPanel ? PANEL_TEXT_Y : VANILLA_TEXT_Y;
        float textScale = isPanel ? PANEL_TEXT_SCALE : VANILLA_TEXT_SCALE;
        float stringOffset = -(Minecraft.getInstance().font.width(amount) * textScale) / 2F;

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate(
                ((float) direction.getStepX() * offset) + (direction.getStepZ() * stringOffset),
                yOffset,
                ((float) direction.getStepZ() * offset) - (direction.getStepX() * stringOffset)
        );

        poseStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(direction.getStepX() * 180, 0, direction.getStepZ() * 180), true));
        poseStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(0, yaw, 0), false));

        poseStack.scale(textScale, textScale, textScale);

        Minecraft.getInstance().font.drawInBatch(
                amount, 0, 0, -1, false, poseStack.last().pose(), buffer,
                Font.DisplayMode.NORMAL, 0, light
        );

        poseStack.popPose();
    }

    private void renderFluid(PoseStack poseStack, MultiBufferSource buffer, Direction direction,
                             float yaw, int light, FluidStack fluidStack, boolean isPanel) {
        poseStack.pushPose();

        float offset = isPanel ? PANEL_FLUID_OFFSET : VANILLA_FLUID_OFFSET;
        float scale = isPanel ? PANEL_FLUID_SCALE : VANILLA_FLUID_SCALE;
        float yOffset = isPanel ? PANEL_FLUID_Y : 0.5F;

        poseStack.translate(0.5D, 0.5D, 0.5D);
        poseStack.translate((float) direction.getStepX() * offset, yOffset, (float) direction.getStepZ() * offset);
        poseStack.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(0, yaw, 0), false));

        poseStack.scale(scale, scale, scale);

        final Fluid fluid = fluidStack.getFluid();
        final IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluid);
        final ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        final TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
        final int fluidColor = attributes.getTintColor(fluidStack);

        final VertexConsumer consumer = buffer.getBuffer(RenderType.text(sprite.atlasLocation()));

        final int colorRed = fluidColor >> 16 & 0xFF;
        final int colorGreen = fluidColor >> 8 & 0xFF;
        final int colorBlue = fluidColor & 0xFF;
        final int colorAlpha = fluidColor >> 24 & 0xFF;

        consumer.vertex(poseStack.last().pose(), -0.5F, -0.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .uv(sprite.getU0(), sprite.getV0())
                .uv2(light)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), 0.5F, -0.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .uv(sprite.getU1(), sprite.getV0())
                .uv2(light)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), 0.5F, -1.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .uv(sprite.getU1(), sprite.getV1())
                .uv2(light)
                .endVertex();
        consumer.vertex(poseStack.last().pose(), -0.5F, -1.5F, 0F)
                .color(colorRed, colorGreen, colorBlue, colorAlpha)
                .uv(sprite.getU0(), sprite.getV1())
                .uv2(light)
                .endVertex();

        poseStack.popPose();
    }
}