package jp.main.taikun.mysticaleverything.config;

import net.minecraft.world.item.ItemStack;

public interface IFilter {
    public boolean filter(ItemStack itemStack);
}
