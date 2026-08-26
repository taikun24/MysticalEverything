package jp.main.taikun.mysticaleverything.config;

import net.minecraft.world.item.ItemStack;

/**
 * 1.20.1 の {@code nbt_has_any} 相当。1.21 では NBT がデータコンポーネントに置き換わったので
 * 「デフォルトから変更されているコンポーネントが 1 つでもあるか」で判定する。
 */
public class FilterHasNBT implements IFilter {
    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        return !itemStack.getComponentsPatch().isEmpty();
    }
}
