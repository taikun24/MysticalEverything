package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.CropResource;
import org.jetbrains.annotations.Nullable;

/**
 * Mekanism:More Machine の Planting Station が使う「チャンス出力オブジェクト」に、
 * 種 (everything_crop) の中身を持たせるための後付けインターフェース。
 * <p>
 * 実装は {@code mixin.mekmm.PlantingChanceOutputMixin} が mixin で流し込む。
 * mixin パッケージ内のクラスは通常のクラスロードから外れるので、
 * インターフェース自体はここ (mixin パッケージの外) に置く必要がある。
 */
public interface IPlantingResourceHolder {

    void mysticaleverything$setResource(@Nullable CropResource resource);

    @Nullable
    CropResource mysticaleverything$getResource();
}
