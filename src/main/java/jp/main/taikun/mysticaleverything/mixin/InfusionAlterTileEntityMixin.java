package jp.main.taikun.mysticaleverything.mixin;

import com.blakebr0.cucumber.inventory.BaseItemStackHandler;
import com.blakebr0.cucumber.inventory.CachedRecipe;
import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalagriculture.crafting.recipe.InfusionRecipe;
import com.blakebr0.mysticalagriculture.init.ModItems;
import com.blakebr0.mysticalagriculture.tileentity.InfusionAltarTileEntity;
import jp.main.taikun.mysticaleverything.Config;
import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InfusionAltarTileEntity.class)
public class InfusionAlterTileEntityMixin {
    @Unique
    private static final ItemStack REQUIRED_ESSENCE = Mysticaleverything.EVERYTHING_CATALYST.get().getDefaultInstance();
    @Redirect(
            method = "getActiveRecipe"
            ,
            at = @At(
                    value = "INVOKE"
                    , target = "Lcom/blakebr0/cucumber/inventory/CachedRecipe;checkAndGet(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/item/crafting/Recipe;"
            ),
            remap = false
    )
    public Recipe<?> getActiveRecipeRedirect(CachedRecipe<CraftingInput, IInfusionRecipe> instance, RecipeInput inventory, Level level) {
        if (inventory.isEmpty()) return null;
        Recipe<?> recipe = instance.checkAndGet((CraftingInput) inventory, level);

        if (recipe == null && mysticaleverything$isPatternValid(inventory)) {
            NonNullList<Ingredient> stacks = NonNullList.create();
            Ingredient stack = (Ingredient.of(ModItems.PROSPERITY_SEED_BASE.get().getDefaultInstance()));
            ItemStack catalyst = inventory.getItem(5);
            CropResource resource = TagItemHelper.getResource(catalyst);
            if (resource == CropResource.EMPTY) return recipe;
            ItemStack ing = resource.getItem();
            for (int i = 0; i < 4; i++) {
                stacks.add(Ingredient.of(REQUIRED_ESSENCE));
                stacks.add(Ingredient.of(ing));
            }
            ItemStack outputItem = Mysticaleverything.EVERYTHING_CROP.get().asItem().getDefaultInstance();
            outputItem.setCount(1);
            if (Config.DISABLE_NBT.get()) {
                ing = new ItemStack(ing.getItem(), 1);
                TagItemHelper.setResource(outputItem, ing);
            } else {
                TagItemHelper.setResource(outputItem, resource);
            }
            return new InfusionRecipe(
                    stack,
                    stacks,
                    outputItem,
                    false
            );
        }
        return recipe;
    }
    @Unique
    public boolean mysticaleverything$doesntMatchItem(RecipeInput inventory, int slot, ItemStack itemStack){
        return Mysticaleverything.isNotSameItem(inventory.getItem(slot), itemStack);
    }
    @Unique
    public boolean mysticaleverything$isPatternValid(RecipeInput inventory) {
        if (mysticaleverything$doesntMatchItem(inventory, 0, ModItems.PROSPERITY_SEED_BASE.get().getDefaultInstance())) {
            return false;
        }
        int[] essenceIndex = new int[]{1, 2, 3, 4};
        int[] ingredientIndex = new int[]{5, 6, 7, 8};
        
        ItemStack firstCatalyst = inventory.getItem(ingredientIndex[0]);
        if (firstCatalyst.isEmpty()) return false;
        
        CropResource firstResource = TagItemHelper.getResource(firstCatalyst);
        if (firstResource == CropResource.EMPTY) return false;

        for (int i = 0; i < 4; i++) {
            if (!inventory.getItem(essenceIndex[i]).is(REQUIRED_ESSENCE.getItem())) {
                return false;
            }
            ItemStack currentCatalyst = inventory.getItem(ingredientIndex[i]);
            if (!currentCatalyst.is(Mysticaleverything.COMPRESSION_CATALYST.get())) {
                return false;
            }
            CropResource currentResource = TagItemHelper.getResource(currentCatalyst);
            if (!firstResource.equals(currentResource)) {
                return false;
            }
        }
        return true;
    }
}
