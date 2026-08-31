package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.Caches;
import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.darkhax.botanypots.common.impl.data.itemdrops.SimpleDropProvider;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * everything_crop を Botany Pots に植えたときのレシピ。
 * <p>
 * 中身が同じなら完全に同じレシピになるので、インスタンスは使い回す
 * (BotanyPots 側は SimpleDropProvider#apply で収穫物を copy してから配るので、
 * ポット間で共有しても互いに影響しない)。
 */
public class MEBotanyPotsCrop extends BasicCrop {

    /** 中身 → レシピ。中身の種類ぶんしか増えない。 */
    private static final Map<CropResource, MEBotanyPotsCrop> CACHE = Caches.lru(256);

    /** 種の粗いフィルタと見た目は中身によらないので、一度だけ作る。 */
    private static Ingredient seedIngredient;
    private static List<Display> displays;

    private final CropResource resource;

    /** 中身が同じレシピを使い回す。 */
    public static MEBotanyPotsCrop of(ItemStack stack) {
        final CropResource resource = TagItemHelper.getResource(stack);
        MEBotanyPotsCrop cached = CACHE.get(resource);
        if (cached != null) {
            return cached;
        }
        final CompoundTag resourceTag = TagItemHelper.resourceToTag(resource);
        MEBotanyPotsCrop crop = new MEBotanyPotsCrop(resource, resourceTag);
        if (resource == CropResource.EMPTY || !resourceTag.isEmpty()) {
            CACHE.put(resource, crop);
        }
        // レジストリが引けずシリアライズに失敗したものは焼き付けない (次の呼び出しで作り直す)
        return crop;
    }

    private MEBotanyPotsCrop(CropResource resource, CompoundTag resourceTag) {
        super(makeProperties(resourceTag));
        // resourceはmatches/couldMatch/isCacheKeyでの厳密比較用に保持
        this.resource = resource;
    }

    private static Properties makeProperties(final CompoundTag resourceTag) {
        final ItemStack resultStack = new ItemStack(Mysticaleverything.EVERYTHING_ESSENCE.get(), 1);
        CompoundTag tag = new CompoundTag();
        tag.put("resource", resourceTag);
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        final int growTime = 100;

        final List<ItemDropProvider> drops = List.of(
                new SimpleDropProvider(List.of(
                        new SimpleDropProvider.SimpleDrop(resultStack, 1.0F)
                ))
        );

        return new Properties(
                // Ingredientはアイテム種類のみの粗いフィルタ。厳密判定はmatches/couldMatch/isCacheKeyで行う
                seedIngredient(),
                BasicCrop.DIRT,
                growTime,
                displays(),
                0,              // lightLevel
                drops,
                Optional.empty(), // functionId
                Optional.empty(), // potPredicate
                1.0F,           // baseYield
                1.0F            // yieldScale
        );
    }

    private static Ingredient seedIngredient() {
        // レジストリが凍った後にしか呼ばれない (種を見に来た時点で初期化済み)
        if (seedIngredient == null) {
            seedIngredient = Ingredient.of(Mysticaleverything.EVERYTHING_CROP_ITEM.get());
        }
        return seedIngredient;
    }

    private static List<Display> displays() {
        if (displays == null) {
            displays = List.of(new AgingDisplayState(Mysticaleverything.EVERYTHING_CROP.get(), BasicOptions.ofDefault()));
        }
        return displays;
    }

    /**
     * BasicCrop#matches はデフォルトで properties.input.test(seedItem) しか見ないため、
     * NBTで区別される resource の同一性を明示的にチェックするようoverride。
     */
    @Override
    public boolean matches(BotanyPotContext input, Level level) {
        final CropResource other = TagItemHelper.getResource(input.getSeedItem());
        return this.resource.equals(other) && super.matches(input, level);
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        final CropResource other = TagItemHelper.getResource(candidate);
        return this.resource.equals(other);
    }

    /**
     * キャッシュキーとしてもresource単位で区別する。
     * CropResourceにequals/hashCodeがちゃんと実装されている前提。
     */
    @Override
    public boolean isCacheKey(ItemStack stack) {
        return this.resource.equals(TagItemHelper.getResource(stack));
    }
}