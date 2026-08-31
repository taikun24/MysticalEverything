package jp.main.taikun.mysticaleverything.mixin.mekmm;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import jp.main.taikun.mysticaleverything.additions.IPlantingResourceHolder;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * {@link PlantingRecipeOutputMixin} が刻んだ種の中身を、実際に出てくる
 * everything_essence へ載せる。main / secondary のどの取得経路でも同じように扱う。
 */
@Pseudo
@Mixin(targets = "com.jerry.mekmm.api.recipes.basic.BasicPlantingRecipe$BasicChanceOutput", remap = false)
public abstract class PlantingChanceOutputMixin implements IPlantingResourceHolder {

    @Unique
    @Nullable
    private CropResource mysticaleverything$resource;

    /**
     * 中身を刻んだ結果。{@code getMainOutput} などは出力スロットの空き判定のために
     * 毎tick呼ばれるので、同じ中身のあいだは作り直さない。
     * <p>
     * 素の出力 ({@code mekmm} 側のフィールド) をそのまま鍵にすることで、
     * main / secondary を取り違えずに済む。
     */
    @Unique
    @Nullable
    private ItemStack mysticaleverything$stampedFrom;
    @Unique
    @Nullable
    private ItemStack mysticaleverything$stamped;

    @Override
    public void mysticaleverything$setResource(@Nullable CropResource resource) {
        if (resource != this.mysticaleverything$resource) {
            // 中身が変わったら刻み直す
            this.mysticaleverything$stampedFrom = null;
            this.mysticaleverything$stamped = null;
        }
        this.mysticaleverything$resource = resource;
    }

    @Override
    @Nullable
    public CropResource mysticaleverything$getResource() {
        return this.mysticaleverything$resource;
    }

    @Inject(method = {"getMainOutput", "getMaxSecondaryOutput", "getSecondaryOutput", "nextSecondaryOutput"},
            at = @At("RETURN"), cancellable = true, remap = false)
    private void mysticaleverything$applyResource(CallbackInfoReturnable<ItemStack> cir) {
        CropResource resource = this.mysticaleverything$resource;
        if (resource == null) {
            // everything_crop 以外の普通のレシピ。触らない
            return;
        }
        ItemStack output = cir.getReturnValue();
        if (output == null || output.isEmpty() || !output.is(Mysticaleverything.EVERYTHING_ESSENCE.get())) {
            return;
        }
        if (resource == CropResource.EMPTY) {
            // 中身の無い種。素の実を吐かせない
            cir.setReturnValue(ItemStack.EMPTY);
            return;
        }
        if (output == this.mysticaleverything$stampedFrom && this.mysticaleverything$stamped != null) {
            cir.setReturnValue(this.mysticaleverything$stamped);
            return;
        }
        ItemStack essence = output.copy();
        TagItemHelper.setResource(essence, resource);
        this.mysticaleverything$stampedFrom = output;
        this.mysticaleverything$stamped = essence;
        cir.setReturnValue(essence);
    }
}
