package jp.main.taikun.mysticaleverything;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EverythingCropItem extends ItemNameBlockItem implements IClientItemExtensions {
    public EverythingCropItem() {
        super(Mysticaleverything.EVERYTHING_CROP.get(), new Item.Properties().rarity(Rarity.EPIC));
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new EverythingCropItemRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        CropResource resource = TagItemHelper.getResource(stack);
        return Component.translatable("item.mysticalagriculture.mystical_seeds", resource.getName());
    }
}
