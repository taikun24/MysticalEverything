package jp.main.taikun.mysticaleverything;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.joml.Quaternionf;

import java.util.List;

public class EverythingCropBlockEntityRenderer implements BlockEntityRenderer<EverythingCropBlockEntity>{
    private final BlockEntityRendererProvider.Context context;
    public EverythingCropBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        this.context = context;
    }
    @Override
    public void render(EverythingCropBlockEntity entity, float p_112308_, PoseStack poseStack, MultiBufferSource multiBufferSource, int v1, int v2) {
        // check age
        if (entity.getBlockState().getValue(CropBlock.AGE) == 7) {
            CropResource resource = entity.cropResource;
            if (resource == null || resource.getType() != CropResource.TYPE.ITEM) {
                return;
            }
            ItemStack itemStack = resource.getItem();
            if (itemStack.isEmpty()) return;
            poseStack.pushPose();
            poseStack.translate(0.5,0.7,0.5);
            poseStack.scale(0.7f,0.7f,0.7f);
            BakedModel model = context.getItemRenderer().getModel(
                    itemStack,
                    entity.getLevel(),
                    null,
                    1
            );
            context.getItemRenderer().render(
                    itemStack,
                    ItemDisplayContext.NONE,
                    false,
                    poseStack,
                    multiBufferSource,
                    v1,
                    v2,
                    model
            );

            poseStack.mulPose(new Quaternionf().rotateXYZ(0,(float)Math.toRadians(90),0));
            context.getItemRenderer().render(
                    itemStack,
                    ItemDisplayContext.NONE,
                    false,
                    poseStack,
                    multiBufferSource,
                    v1,
                    v2,
                    model
            );
            poseStack.popPose();
        }


    }

}
