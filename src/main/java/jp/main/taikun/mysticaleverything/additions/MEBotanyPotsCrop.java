package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.AgingDisplayState;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Set;

public class MEBotanyPotsCrop extends BasicCrop {

    private final CropResource resource;

    public MEBotanyPotsCrop(ResourceLocation id, ItemStack stack) {
        super(
                id,
                Ingredient.of(stack.getItem()), // 種の粗いフィルタ。厳密判定はmatchesLookupで行う
                Set.of("dirt"), // TODO: 旧SoilクラスがgetCategories()で返す文字列と一致するか要確認
                100, // growTime
                buildResults(stack),
                List.of(
                        // TODO: 旧AgingDisplayStateのコンストラクタ引数が新APIと異なる可能性あり要確認
                        new AgingDisplayState(Mysticaleverything.EVERYTHING_CROP.get().defaultBlockState())
                ),
                0 // lightLevel
        );
        this.resource = TagItemHelper.getResource(stack);
    }

    private static List<HarvestEntry> buildResults(ItemStack stack) {
        final CropResource resource = TagItemHelper.getResource(stack);

        final ItemStack resultStack = new ItemStack(Mysticaleverything.EVERYTHING_ESSENCE.get(), 1);
        CompoundTag tag = new CompoundTag();
        tag.put("resource", TagItemHelper.resourceToTag(resource));
        resultStack.setTag(tag);

        // TODO: HarvestEntryのコンストラクタ引数(chance, minRolls, maxRolls)の並びは要確認
        return List.of(new HarvestEntry(1.0F, resultStack, 1, 1));
    }

    /**
     * BasicCrop#matchesLookup はデフォルトで seed.test(placedStack) しか見ないため、
     * NBTで区別される resource の同一性を明示的にチェックするようoverride。
     */
    @Override
    public boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack) {
        final CropResource other = TagItemHelper.getResource(placedStack);
        return this.resource.equals(other) && super.matchesLookup(level, pos, pot, placedStack);
    }

    // couldMatch / isCacheKey は旧API(CacheableRecipeの概念なし)には存在しないため削除
}