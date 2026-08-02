package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.displaystate.AgingDisplayState;
import net.darkhax.botanypots.data.recipes.crop.BasicCrop;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * everything_crop を Botany Pots に植えたときのレシピ。
 * <p>
 * 何が採れるかは種の NBT で決まるためデータパックのレシピとしては登録できない。
 * {@link jp.main.taikun.mysticaleverything.mixin.botanypots.CropOverrideMixin} が
 * 種を見るたびにこのインスタンスを作って返す。
 */
public class MEBotanyPotsCrop extends BasicCrop {

    /** everything_crop が受け付ける土のカテゴリ。 */
    public static final Set<String> SOIL_CATEGORIES = Set.of("dirt");
    public static final int GROWTH_TICKS = 100;
    private static final int LIGHT_LEVEL = 0;

    private final CropResource resource;

    public MEBotanyPotsCrop(ItemStack seedStack) {
        this(TagItemHelper.getResource(seedStack));
    }

    private MEBotanyPotsCrop(CropResource resource) {
        super(
                idFor(resource),
                // 種の粗いフィルタ。NBT を含む厳密な判定は matchesLookup で行う
                Ingredient.of(Mysticaleverything.EVERYTHING_CROP_ITEM.get()),
                SOIL_CATEGORIES,
                GROWTH_TICKS,
                List.of(new HarvestEntry(1.0F, essenceOf(resource), 1, 1)),
                List.of(new AgingDisplayState(Mysticaleverything.EVERYTHING_CROP.get().defaultBlockState())),
                LIGHT_LEVEL
        );
        this.resource = resource;
    }

    private static final ResourceLocation EMPTY_ID =
            Objects.requireNonNull(ResourceLocation.tryBuild(Mysticaleverything.MODID, "crop_empty"));

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
