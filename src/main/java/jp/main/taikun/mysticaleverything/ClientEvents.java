package jp.main.taikun.mysticaleverything;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = Mysticaleverything.MODID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                Mysticaleverything.EVERYTHING_CROP_BLOCK_ENTITY.get(),
                EverythingCropBlockEntityRenderer::new
        );

    }
    @SubscribeEvent
    public static void onModelBaking(ModelEvent.RegisterAdditional event) {
        event.register(EverythingCropItemRenderer.baseModelLoc);
        event.register(EverythingEssenceItemRenderer.baseModelLoc);
    }
}
