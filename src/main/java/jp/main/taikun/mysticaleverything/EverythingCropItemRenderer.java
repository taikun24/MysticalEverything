package jp.main.taikun.mysticaleverything;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class EverythingCropItemRenderer extends BlockEntityWithoutLevelRenderer {
    private static final ResourceLocation baseLoc = ResourceLocation.tryBuild("mysticaleverything", "everything_crop_base");
    static final ModelResourceLocation baseModelLoc =  new ModelResourceLocation(baseLoc, "inventory");
    public EverythingCropItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int light, int overlay) {
        CropResource resource = TagItemHelper.getResource(stack);
        if (resource == CropResource.EMPTY) return;
        poseStack.pushPose();
        poseStack.translate(0.5d, 0.5d, 0.5d);

        switch (resource.getType()) {
            case ITEM -> {
                RenderHelper.renderResource(stack, displayContext, poseStack, buffer, resource, light, baseModelLoc);
            }
            case FLUID -> {

            }
        }
        poseStack.popPose();
    }
}
