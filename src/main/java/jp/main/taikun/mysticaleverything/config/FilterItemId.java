package jp.main.taikun.mysticaleverything.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class FilterItemId implements IFilter {
    private final ResourceLocation itemId;

    /**
     * @param itemId アイテムID (例: "minecraft:diamond")
     */
    public FilterItemId(String itemId) {
        this.itemId = new ResourceLocation(itemId);
    }

    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        return this.itemId.equals(ForgeRegistries.ITEMS.getKey(itemStack.getItem()));
    }
}
