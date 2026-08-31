package jp.main.taikun.mysticaleverything.mixin.astralmekanism;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.data.recipes.crop.HarvestEntry;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Astral Mekanism &amp; Energistics のグリーンハウス対応。
 * <p>
 * グリーンハウスは BotanyPots の crop レシピを起動時に総当たりで
 * {@code CropSoilRecipe} に変換して持つ ({@code CropSoilRecipe#getAllRecipes})。
 * everything_crop は「1アイテム = 1レシピ」ではなく NBT で中身が決まるので、
 * レシピ表には {@code data/mysticaleverything/recipes/botanypots/everything_crop.json} の
 * テンプレート 1 件だけを登録し、実際に何が採れるかはここで種の NBT から差し替える。
 * <p>
 * 対象クラスは astral_mekanism 側の実装クラスなので、コンパイル時依存を持たずに
 * {@code @Pseudo} + {@code targets} で名前指定し、{@code remap = false} で SRG 変換を止めている。
 * 適用可否は {@link jp.main.taikun.mysticaleverything.mixin.MysticalEverythingMixinPlugin} が
 * astral_mekanism / botanypots の両方の存在で判定する。
 */
@Pseudo
@Mixin(targets = "astral_mekanism.generalrecipe.cachedrecipe.CropSoilCachedRecipe", remap = false)
public abstract class GreenhouseHarvestMixin {

    /** 種スロットの中身。{@code calculateOperationsThisTick} が毎tick詰め直す。 */
    @Shadow
    private ItemStack cropStack;

    /** そのtickで確定した収穫物。{@code recipe.getOutput()} のコピーではなく参照。 */
    @Shadow
    private List<HarvestEntry> harvestEntries;

    /**
     * 直前に刻んだ結果。同じ収穫物リスト・同じ中身なら作り直さない。
     * 収穫のたびにリストと ItemStack を作り直すと、動いているだけでゴミが出る。
     */
    @Unique
    @Nullable
    private List<HarvestEntry> mysticaleverything$stampedFrom;
    @Unique
    @Nullable
    private List<HarvestEntry> mysticaleverything$stamped;
    @Unique
    @Nullable
    private CropResource mysticaleverything$stampedResource;

    @Inject(method = "finishProcessing(I)V", at = @At("HEAD"))
    private void mysticaleverything$applyCropResource(int operations, CallbackInfo ci) {
        if (cropStack == null || !cropStack.is(Mysticaleverything.EVERYTHING_CROP_ITEM.get())) {
            return;
        }
        CropResource resource = TagItemHelper.getResource(cropStack);
        if (resource == CropResource.EMPTY) {
            // 中身の無い種。空リストにしておけば finishProcessing が何も消費せず抜ける
            harvestEntries = List.of();
            return;
        }
        List<HarvestEntry> source = harvestEntries;
        if (resource == mysticaleverything$stampedResource
                && (source == mysticaleverything$stampedFrom || source == mysticaleverything$stamped)
                && mysticaleverything$stamped != null) {
            harvestEntries = mysticaleverything$stamped;
            return;
        }
        List<HarvestEntry> stamped = new ArrayList<>(harvestEntries.size());
        for (HarvestEntry entry : harvestEntries) {
            ItemStack output = entry.getItem();
            if (!output.is(Mysticaleverything.EVERYTHING_ESSENCE.get())) {
                stamped.add(entry);
                continue;
            }
            ItemStack essence = output.copy();
            TagItemHelper.setResource(essence, resource);
            stamped.add(new HarvestEntry(entry.getChance(), essence, entry.getMinRolls(), entry.getMaxRolls()));
        }
        mysticaleverything$stampedFrom = source;
        mysticaleverything$stampedResource = resource;
        mysticaleverything$stamped = stamped;
        harvestEntries = stamped;
    }
}
