package jp.main.taikun.mysticaleverything;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemCompressionRecipe extends CustomRecipe {
    public ItemCompressionRecipe(ResourceLocation p_252125_, CraftingBookCategory p_249010_) {
        super(p_252125_, p_249010_);
    }

    @Override
    public boolean matches(CraftingContainer container, @NotNull Level level) {
        if (container.getItem(0).isEmpty()) return false;
        ItemStack catalyst = container.getItem(4);
        if (catalyst.is(Mysticaleverything.COMPRESSION_CATALYST.get())) {
            if (TagItemHelper.hasResource(catalyst)) return false;
            ItemStack first = container.getItem(0);
            for (int i = 1; i < 9; i++){
                if (i == 4) continue;
                ItemStack current = container.getItem(i);
                if (Config.DISABLE_NBT.get()){
                    if (!current.is(first.getItem())) {
                        return false;
                    }
                } else {
                    if (Mysticaleverything.isNotSameItem(first, current)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingContainer container, @NotNull RegistryAccess access) {
        ItemStack first = container.getItem(0);
        if (Config.DISABLE_NBT.get()) {
            first = first.copy();
            first.setTag(null);
        }
        ItemStack catalyst = new ItemStack(Mysticaleverything.COMPRESSION_CATALYST.get());
        TagItemHelper.setResource(catalyst, first);
        return catalyst;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w >= 3 && h >= 3;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Mysticaleverything.ITEM_COMPRESSION_RECIPE_SERIALIZER.get();
    }
}
