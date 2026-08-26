package jp.main.taikun.mysticaleverything.config;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FilterTag implements IFilter {
    private final TagKey<Item> tagKey;

    /**
     * @param tagLocation タグの文字列 (例: "minecraft:logs", "c:ores")
     */
    public FilterTag(String tagLocation) {
        ResourceLocation rl = ResourceLocation.parse(tagLocation);
        this.tagKey = TagKey.create(Registries.ITEM, rl);
    }

    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        return itemStack.is(this.tagKey);
    }
}
