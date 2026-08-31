package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.Caches;
import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.AgingDisplayState;
import net.darkhax.botanypots.data.displaystate.DisplayState;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * everything_crop を Botany Pots に植えたときのレシピ。
 * <p>
 * 何が採れるかは種の NBT で決まるためデータパックのレシピとしては登録できない。
 * {@link jp.main.taikun.mysticaleverything.mixin.botanypots.CropOverrideMixin} が
 * 種を見るたびに {@link #of} を呼ぶ。
 * <p>
 * 中身が同じなら完全に同じレシピになるので、インスタンスは使い回す
 * (BotanyPots 側は {@code generateDrops} で収穫物を copy してから配るので、
 * ポット間で共有しても互いに影響しない)。
 */
public class MEBotanyPotsCrop extends BasicCrop {

    /** everything_crop が受け付ける土のカテゴリ。 */
    public static final Set<String> SOIL_CATEGORIES = Set.of("dirt");
    public static final int GROWTH_TICKS = 100;
    private static final int LIGHT_LEVEL = 0;

    /** 中身 → レシピ。中身の種類ぶんしか増えない。 */
    private static final Map<CropResource, MEBotanyPotsCrop> CACHE = Caches.lru(256);

    private static final ResourceLocation EMPTY_ID =
            Objects.requireNonNull(ResourceLocation.tryBuild(Mysticaleverything.MODID, "crop_empty"));

    /** 種の粗いフィルタと見た目は中身によらないので、一度だけ作る。 */
    private static Ingredient seedIngredient;
    private static List<DisplayState> displayStates;

    private final CropResource resource;

    /** 中身が同じレシピを使い回す。 */
    @NotNull
    public static MEBotanyPotsCrop of(ItemStack seedStack) {
        CropResource resource = TagItemHelper.getResource(seedStack);
        MEBotanyPotsCrop cached = CACHE.get(resource);
        if (cached == null) {
            cached = new MEBotanyPotsCrop(resource);
            CACHE.put(resource, cached);
        }
        return cached;
    }

    private MEBotanyPotsCrop(CropResource resource) {
        super(
                idFor(resource),
                // 種の粗いフィルタ。NBT を含む厳密な判定は matchesLookup で行う
                seedIngredient(),
                SOIL_CATEGORIES,
                GROWTH_TICKS,
                List.of(new HarvestEntry(1.0F, essenceOf(resource), 1, 1)),
                displayStates(),
                LIGHT_LEVEL
        );
        this.resource = resource;
    }

    private static Ingredient seedIngredient() {
        // レジストリが凍った後にしか呼ばれない (種を見に来た時点で初期化済み)
        if (seedIngredient == null) {
            seedIngredient = Ingredient.of(Mysticaleverything.EVERYTHING_CROP_ITEM.get());
        }
        return seedIngredient;
    }

    private static List<DisplayState> displayStates() {
        if (displayStates == null) {
            displayStates = List.of(new AgingDisplayState(Mysticaleverything.EVERYTHING_CROP.get().defaultBlockState()));
        }
        return displayStates;
    }

    /** 中身のアイテムから一意な id を作る。id は表示用で、ポットには永続化されない。 */
    private static ResourceLocation idFor(CropResource resource) {
        if (resource != CropResource.EMPTY && resource.getType() == CropResource.TYPE.ITEM) {
            ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(resource.getItem().getItem());
            if (itemId != null) {
                ResourceLocation cropId = ResourceLocation.tryBuild(Mysticaleverything.MODID,
                        "crop_" + itemId.getNamespace() + "_" + itemId.getPath());
                if (cropId != null) {
                    return cropId;
                }
            }
        }
        return EMPTY_ID;
    }

    private static ItemStack essenceOf(CropResource resource) {
        ItemStack essence = new ItemStack(Mysticaleverything.EVERYTHING_ESSENCE.get());
        TagItemHelper.setResource(essence, resource);
        return essence;
    }

    /**
     * {@link BasicCrop#matchesLookup} は seed の Ingredient しか見ないため、
     * NBT で区別される resource の同一性を明示的に確認する。
     */
    @Override
    public boolean matchesLookup(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack placedStack) {
        return this.resource.equals(TagItemHelper.getResource(placedStack))
                && super.matchesLookup(level, pos, pot, placedStack);
    }
}
