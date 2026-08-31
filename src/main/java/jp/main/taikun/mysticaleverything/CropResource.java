package jp.main.taikun.mysticaleverything;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 作物の「中身」。アイテム 1 種か液体 1 種を指す不変の値。
 * <p>
 * 描画・レシピ判定から毎フレーム / 毎tick触られるので、
 * {@link TagItemHelper} が解決結果をキャッシュして同じインスタンスを配る。
 * そのため<b>中身を書き換えてはいけない</b>。{@link #getItem()} が返す
 * {@link ItemStack} も共有物なので、加工するなら必ず {@code copy()} してから使う。
 */
public final class CropResource {
    public enum TYPE {
        ITEM, FLUID
    }

    /** 中身が無いことを表す番兵。同一性 ({@code == EMPTY}) で判定してよい。 */
    public static final CropResource EMPTY = new CropResource(ItemStack.EMPTY);

    private final TYPE type;
    private final ItemStack item;
    private final Fluid fluid;

    /** 遅延計算のメモ。値は不変なので一度作れば使い回せる。 */
    private int hash;
    private @Nullable Component name;
    private @Nullable CompoundTag tag;

    private CropResource(@NotNull ItemStack item) {
        this.type = TYPE.ITEM;
        this.item = item;
        this.fluid = null;
    }

    private CropResource(@NotNull Fluid fluid) {
        this.type = TYPE.FLUID;
        this.item = ItemStack.EMPTY;
        this.fluid = fluid;
    }

    /**
     * アイテムから作る。空なら {@link #EMPTY} を返すので、
     * {@code == EMPTY} での判定が常に成立する。
     */
    @NotNull
    public static CropResource of(@Nullable ItemStack item) {
        if (item == null || item.isEmpty()) {
            return EMPTY;
        }
        // 呼び出し側のスタックを持ち続けると外から書き換えられるため、必ず切り離す
        return new CropResource(item.copyWithCount(1));
    }

    @NotNull
    public static CropResource of(@Nullable Fluid fluid) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return EMPTY;
        }
        return new CropResource(fluid);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    public TYPE getType() {
        return type;
    }

    /** 共有インスタンス。書き換えないこと。 */
    public ItemStack getItem() {
        if (type != TYPE.ITEM) {
            throw new IllegalArgumentException("type is not ITEM");
        }
        return item;
    }

    public Fluid getFluid() {
        if (type != TYPE.FLUID) {
            throw new IllegalArgumentException("type is not FLUID");
        }
        return fluid;
    }

    public Component getName() {
        Component cached = this.name;
        if (cached == null) {
            cached = switch (type) {
                case ITEM -> item.isEmpty() ? Component.literal("error") : item.getHoverName();
                case FLUID -> fluid == null ? Component.literal("error") : fluid.getFluidType().getDescription();
            };
            this.name = cached;
        }
        return cached;
    }

    /** {@link TagItemHelper} がシリアライズ結果を覚えておくための入口。 */
    @Nullable
    CompoundTag cachedTag() {
        return this.tag;
    }

    void cacheTag(@NotNull CompoundTag tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CropResource that)) return false;
        if (type != that.type) return false;
        return switch (type) {
            case ITEM -> {
                if (this.item.isEmpty() || that.item.isEmpty()) yield this.item.isEmpty() == that.item.isEmpty();
                yield this.item.is(that.item.getItem()) && Objects.equals(this.item.getTag(), that.item.getTag());
            }
            case FLUID -> this.fluid == that.fluid;
        };
    }

    @Override
    public int hashCode() {
        int result = this.hash;
        if (result != 0) {
            return result;
        }
        result = type.hashCode();
        if (type == TYPE.ITEM) {
            if (!item.isEmpty()) {
                result = 31 * result + item.getItem().hashCode();
                CompoundTag itemTag = item.getTag();
                if (itemTag != null) {
                    result = 31 * result + itemTag.hashCode();
                }
            }
        } else if (fluid != null) {
            result = 31 * result + fluid.hashCode();
        }
        // 0 はキャッシュ未計算の印なので避ける
        this.hash = result == 0 ? 1 : result;
        return this.hash;
    }
}
