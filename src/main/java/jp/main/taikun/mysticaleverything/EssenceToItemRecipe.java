package jp.main.taikun.mysticaleverything;


import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EssenceToItemRecipe extends CustomRecipe {
    public EssenceToItemRecipe(CraftingBookCategory category) {
        super(category);
    }
    @Override
    public boolean matches(CraftingInput container, Level level) {
        int count = 0;
        CropResource resource = null;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);

            if (!stack.isEmpty()) {
                if (stack.is(Mysticaleverything.EVERYTHING_ESSENCE.get())) {
                    CropResource currentResource = TagItemHelper.getResource(stack, level.registryAccess());
                    if (currentResource == CropResource.EMPTY) {
                        return false;
                    }

                    if (resource == null) {
                        resource = currentResource;
                    } else {
                        if (!resource.equals(currentResource)) {
                            return false;
                        }
                    }
                    count++;
                } else {
                    return false;
                }
            }
        }

        return count == 9 && resource != null;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, @NotNull HolderLookup.Provider registryAccess) {
        ItemStack essenceStack = ItemStack.EMPTY;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && stack.is(Mysticaleverything.EVERYTHING_ESSENCE.get())) {
                essenceStack = stack;
                break;
            }
        }

        if (essenceStack.isEmpty()) return ItemStack.EMPTY;

        CropResource crop = TagItemHelper.getResource(essenceStack, registryAccess);
        if (crop == CropResource.EMPTY || crop.getType() != CropResource.TYPE.ITEM) return ItemStack.EMPTY;

        return crop.getItem().copy();
    }
    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Mysticaleverything.ESSENCE_TO_ITEM_RECIPE_SERIALIZER.get();
    }
}
