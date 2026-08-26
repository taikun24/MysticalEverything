package jp.main.taikun.mysticaleverything.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class FilterItemId implements IFilter {
    private final ResourceLocation itemId;

    /**
     * @param itemId アイテムID (例: "minecraft:diamond")
     */
    public FilterItemId(String itemId) {
        this.itemId = ResourceLocation.parse(itemId);
    }

    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        return this.itemId.equals(BuiltInRegistries.ITEM.getKey(itemStack.getItem()));
    }
}
