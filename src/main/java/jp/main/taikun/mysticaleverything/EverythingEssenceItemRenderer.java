package jp.main.taikun.mysticaleverything;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class EverythingEssenceItemRenderer extends BlockEntityWithoutLevelRenderer {

    private static final ResourceLocation baseLoc = ResourceLocation.tryBuild("mysticaleverything", "everything_essence_base");
    static final ModelResourceLocation baseModelLoc =  new ModelResourceLocation(baseLoc, "inventory");
    public EverythingEssenceItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack,
                             MultiBufferSource buffer, int light, int overlay) {
        CropResource resource = TagItemHelper.getResource(stack);
        if (resource != CropResource.EMPTY && resource.getType() == CropResource.TYPE.ITEM) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.5D, 0.5D);
            RenderHelper.renderResource(stack, displayContext, poseStack, buffer, resource, light, baseModelLoc);
            poseStack.popPose();
        }
    }
}
