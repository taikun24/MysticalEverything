package jp.main.taikun.mysticaleverything.mixin.main;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InfusionAltarTileEntity.class)
public class InfusionAlterTileEntityMixin {
    @Unique
    private static final ItemStack REQUIRED_ESSENCE = Mysticaleverything.EVERYTHING_CATALYST.get().getDefaultInstance();

    /**
     * 直前に組み立てたレシピ。{@code getActiveRecipe} は毎tick呼ばれるが、
     * 触媒の中身が変わらない限り同じレシピにしかならないので使い回す
     * (中身ごとに Ingredient 9 個と ItemStack を作り直すと、置いてあるだけで
     * 毎tickゴミが出る)。
     */
    @Unique
    @Nullable
    private CropResource mysticaleverything$cachedResource;
    @Unique
    private boolean mysticaleverything$cachedIgnoreNBT;
    @Unique
    @Nullable
    private InfusionRecipe mysticaleverything$cachedRecipe;

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
        if (recipe != null) {
            return recipe;
        }
        CropResource resource = mysticaleverything$findPatternResource(inventory, level);
        if (resource == null) {
            return null;
        }
        return mysticaleverything$recipeFor(resource, level);
    }

    /** 中身ごとの種インフュージョンレシピ。同じ中身なら作り直さない。 */
    @Unique
    private InfusionRecipe mysticaleverything$recipeFor(CropResource resource, Level level) {
        ItemStack ingredient = resource.getItem();
        boolean ignoreNBT = Config.disableNBT(ingredient);
        InfusionRecipe cached = this.mysticaleverything$cachedRecipe;
        if (cached != null
                && this.mysticaleverything$cachedIgnoreNBT == ignoreNBT
                && resource.equals(this.mysticaleverything$cachedResource)) {
            return cached;
        }

        NonNullList<Ingredient> stacks = NonNullList.create();
        Ingredient essence = Ingredient.of(REQUIRED_ESSENCE);
        // Ingredient は渡したスタックを保持するので、共有インスタンスは切り離して渡す
        Ingredient crop = Ingredient.of(ingredient.copy());
        for (int i = 0; i < 4; i++) {
            stacks.add(essence);
            stacks.add(crop);
        }

        ItemStack outputItem = Mysticaleverything.EVERYTHING_CROP.get().asItem().getDefaultInstance();
        outputItem.setCount(1);
        TagItemHelper.setResource(outputItem, ignoreNBT
                        ? CropResource.of(new ItemStack(ingredient.getItem()))
                        : resource,
                level.registryAccess());

        InfusionRecipe built = new InfusionRecipe(
                Ingredient.of(ModItems.PROSPERITY_SEED_BASE.get().getDefaultInstance()),
                stacks,
                outputItem,
                false
        );
        this.mysticaleverything$cachedResource = resource;
        this.mysticaleverything$cachedIgnoreNBT = ignoreNBT;
        this.mysticaleverything$cachedRecipe = built;
        return built;
    }

    @Unique
    public boolean mysticaleverything$doesntMatchItem(RecipeInput inventory, int slot, ItemStack itemStack){
        return !ItemStack.isSameItemSameComponents(inventory.getItem(slot), itemStack);
    }

    /**
     * 祭壇に「種の素 + 触媒 4 + everything_catalyst 4」が正しく並んでいるか調べ、
     * 並んでいれば触媒の中身を返す。並んでいなければ {@code null}。
     */
    @Unique
    @Nullable
    public CropResource mysticaleverything$findPatternResource(RecipeInput inventory, Level level) {
        if (inventory.size() != 9) return null;
        if (mysticaleverything$doesntMatchItem(inventory, 0, ModItems.PROSPERITY_SEED_BASE.get().getDefaultInstance())) {
            return null;
        }

        ItemStack firstCatalyst = inventory.getItem(5);
        if (!firstCatalyst.is(Mysticaleverything.COMPRESSION_CATALYST.get())) {
            return null;
        }
        CropResource firstResource = TagItemHelper.getResource(firstCatalyst, level.registryAccess());
        if (firstResource == CropResource.EMPTY || firstResource.getType() != CropResource.TYPE.ITEM) {
            return null;
        }
        if (!Config.filter(firstResource.getItem())) {
            return null;
        }

        for (int i = 0; i < 4; i++) {
            if (!inventory.getItem(1 + i).is(REQUIRED_ESSENCE.getItem())) {
                return null;
            }
            ItemStack currentCatalyst = inventory.getItem(5 + i);
            if (!currentCatalyst.is(Mysticaleverything.COMPRESSION_CATALYST.get())) {
                return null;
            }
            if (!firstResource.equals(TagItemHelper.getResource(currentCatalyst, level.registryAccess()))) {
                return null;
            }
        }
        return firstResource;
    }
}
