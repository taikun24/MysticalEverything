package jp.main.taikun.mysticaleverything;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RenderHelper {
    public static void renderResource(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                                      MultiBufferSource buffer, CropResource resource, int light, ModelResourceLocation baseModelLoc){
        BakedModel model = Minecraft.getInstance().getItemRenderer().getModel(
                resource.getItem(),
                null,
                null,
                0
        );
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getInstance().getItemRenderer().render(
                resource.getItem(),
                displayContext,
                true,
                poseStack,
                buffer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY,
                model
        );
        BakedModel baseModel = Minecraft.getInstance().getModelManager().getModel(baseModelLoc);
        Minecraft.getInstance().getItemRenderer().render(
                stack,
                displayContext,
                false,
                poseStack,
                buffer,
                light,
                OverlayTexture.NO_OVERLAY,
                baseModel
        );
    }
}
