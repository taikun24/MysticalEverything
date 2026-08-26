package jp.main.taikun.mysticaleverything.mixin.mekmm;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import jp.main.taikun.mysticaleverything.additions.IPlantingResourceHolder;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mekanism:More Machine の Planting Station / Planting Factory 対応。
 * <p>
 * everything_crop は「1アイテム = 1レシピ」ではなく NBT (custom_data) で中身が決まるので、
 * レシピ表には {@code data/mysticaleverything/recipe/mekmm/everything_crop.json} の
 * テンプレート 1 件だけを登録し、実際に何が採れるかは種の中身から差し替える。
 * <p>
 * {@code getOutput} は毎tick、収穫物の決定と「出力スロットに入るか」の判定の
 * 両方に使われる。ここで中身を刻んでおかないと、既に別の実の入った出力スロットに
 * 素の everything_essence を入れようとして機械が停止する。
 * <p>
 * 対象クラスは mekmm 側の実装クラスなので、コンパイル時依存を持たずに
 * {@code @Pseudo} + {@code targets} で名前指定している (メソッドの引数型は
 * Mekanism API なので compileOnly で解決する)。
 */
@Pseudo
@Mixin(targets = "com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe", remap = false)
public abstract class PlantingRecipeOutputMixin {

    @Inject(method = "getOutput", at = @At("RETURN"), remap = false)
    private void mysticaleverything$stampResource(ItemStack solid, ChemicalStack chemical, CallbackInfoReturnable<Object> cir) {
        if (!(cir.getReturnValue() instanceof IPlantingResourceHolder holder)) {
            return;
        }
        if (solid == null || !solid.is(Mysticaleverything.EVERYTHING_CROP_ITEM.get())) {
            holder.mysticaleverything$setResource(null);
            return;
        }
        CropResource resource = TagItemHelper.getResource(solid);
        holder.mysticaleverything$setResource(resource);
    }
}
