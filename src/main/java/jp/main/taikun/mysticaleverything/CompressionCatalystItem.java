package jp.main.taikun.mysticaleverything;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;

public class CompressionCatalystItem extends Item{
    public CompressionCatalystItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        CropResource resource = TagItemHelper.getResource(stack);
        if (resource == CropResource.EMPTY) return super.getName(stack);
        return Component.translatable("item.mysticaleverything.compression_catalyst.compressed", resource.getName());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return TagItemHelper.hasResource(stack);
    }
}
