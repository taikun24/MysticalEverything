package jp.main.taikun.mysticaleverything;


import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = Mysticaleverything.MODID, value = net.neoforged.api.distmarker.Dist.CLIENT)
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
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new EverythingEssenceItemRenderer();
                }
                return renderer;
            }
        }, Mysticaleverything.EVERYTHING_ESSENCE.get());
        event.registerItem(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new EverythingCropItemRenderer();
                }
                return renderer;
            }
        }, Mysticaleverything.EVERYTHING_CROP_ITEM.get()); // 対象のItemインスタンスを渡す
    }

}

