package jp.main.taikun.mysticaleverything.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FilterTag implements IFilter {
    private final TagKey<Item> tagKey;

    /**
     * @param tagLocation タグの文字列 (例: "minecraft:logs", "forge:ores/iron")
     */
    public FilterTag(String tagLocation) {
        // ResourceLocation を生成して TagKey を作成
        ResourceLocation rl = new ResourceLocation(tagLocation);
        this.tagKey = TagKey.create(Registries.ITEM, rl);
    }

    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }
        // アイテムが指定したタグに含まれているか判定
        return itemStack.is(this.tagKey);
    }
}