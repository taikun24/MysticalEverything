package jp.main.taikun.mysticaleverything;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EverythingEssenceItem extends Item{
    public EverythingEssenceItem() {
        super(new Item.Properties().rarity(Rarity.EPIC));
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new EverythingEssenceItemRenderer();
                }
                return renderer;
            }
        });
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        CropResource resource = TagItemHelper.getResource(stack);
        return Component.translatable("item.mysticalagriculture.mystical_essence", resource.getName());
    }
}
