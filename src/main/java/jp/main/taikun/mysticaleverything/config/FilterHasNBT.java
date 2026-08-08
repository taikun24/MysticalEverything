package jp.main.taikun.mysticaleverything.config;

import net.minecraft.world.item.ItemStack;

public class FilterHasNBT implements IFilter {
    @Override
    public boolean filter(ItemStack itemStack) {
        return itemStack.hasTag();
    }
}
