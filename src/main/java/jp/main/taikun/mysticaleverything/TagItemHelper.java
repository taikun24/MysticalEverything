package jp.main.taikun.mysticaleverything;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * {@link CropResource} と NBT の相互変換。
 * <p>
 * キー名は既存ワールドのアイテムに書き込まれているので変更しないこと。
 * <p>
 * 解決 (NBT → {@link CropResource}) は描画とレシピ判定から毎フレーム / 毎tick呼ばれ、
 * 素直にやると {@link ItemStack#of} が毎回新しいスタックを作ってしまう。
 * ここで結果をキャッシュして、同じ NBT には同じインスタンスを返す。
 */
public final class TagItemHelper {

    /** アイテム側のルートタグに置く、resource 本体のキー。 */
    public static final String KEY_RESOURCE = "resource";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ITEM = "Item";
    private static final String KEY_FLUID = "Fluid";
    private static final String TYPE_ITEM = "item";
    private static final String TYPE_FLUID = "fluid";

    /** NBT → {@link CropResource} の解決結果。 */
    private static final Map<CompoundTag, CropResource> RESOURCE_CACHE = Caches.lru(512);

    private TagItemHelper() {
    }

    @NotNull
    public static CompoundTag itemToTag(@Nullable ItemStack stack) {
        CompoundTag itemTag = new CompoundTag();
        if (stack != null && !stack.isEmpty()) {
            stack.copyWithCount(1).save(itemTag);
        }
        return wrap(TYPE_ITEM, KEY_ITEM, itemTag);
    }

    @NotNull
    public static CompoundTag fluidToTag(@Nullable Fluid fluid) {
        CompoundTag fluidTag = new CompoundTag();
        if (fluid != null) {
            new FluidStack(fluid, 1).writeToNBT(fluidTag);
        }
        return wrap(TYPE_FLUID, KEY_FLUID, fluidTag);
    }

    private static CompoundTag wrap(String type, String payloadKey, CompoundTag payload) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString(KEY_TYPE, type);
        compoundTag.put(payloadKey, payload);
        return compoundTag;
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null || compoundTag.isEmpty() || !compoundTag.contains(KEY_TYPE)) {
            return CropResource.EMPTY;
        }
        CropResource cached = RESOURCE_CACHE.get(compoundTag);
        if (cached != null) {
            return cached;
        }
        CropResource resource = parseResource(compoundTag);
        // キーは呼び出し側が持っているタグなので、後から書き換えられないよう切り離す
        RESOURCE_CACHE.put(compoundTag.copy(), resource);
        return resource;
    }

    private static CropResource parseResource(@NotNull CompoundTag compoundTag) {
        try {
            switch (compoundTag.getString(KEY_TYPE)) {
                case TYPE_ITEM -> {
                    if (compoundTag.contains(KEY_ITEM)) {
                        return CropResource.of(ItemStack.of(compoundTag.getCompound(KEY_ITEM)));
                    }
                }
                case TYPE_FLUID -> {
                    if (compoundTag.contains(KEY_FLUID)) {
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(compoundTag.getCompound(KEY_FLUID));
                        if (fluidStack != null && !fluidStack.isEmpty()) {
                            return CropResource.of(fluidStack.getFluid());
                        }
                    }
                }
                default -> {
                }
            }
        } catch (Exception e) {
            Mysticaleverything.LOGGER.error("Failed to parse CropResource from NBT", e);
        }
        return CropResource.EMPTY;
    }

    /** アイテムのルートタグ (= {@code "resource"} を含む側) から読む。 */
    @NotNull
    public static CropResource tagToResourceDirect(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null || !compoundTag.contains(KEY_RESOURCE, Tag.TAG_COMPOUND)) {
            return CropResource.EMPTY;
        }
        return tagToResource(compoundTag.getCompound(KEY_RESOURCE));
    }

    /**
     * 保存用のタグを作る。中身は {@link CropResource} 側に覚えさせるので、
     * 2 回目からは copy だけで済む (呼び出し側がタグを書き換えても壊れないよう毎回複製する)。
     */
    @NotNull
    public static CompoundTag resourceToTag(@Nullable CropResource cropResource) {
        if (cropResource == null || cropResource == CropResource.EMPTY) {
            return new CompoundTag();
        }
        CompoundTag cached = cropResource.cachedTag();
        if (cached != null) {
            return cached.copy();
        }
        try {
            CompoundTag tag = switch (cropResource.getType()) {
                case ITEM -> itemToTag(cropResource.getItem());
                case FLUID -> fluidToTag(cropResource.getFluid());
            };
            cropResource.cacheTag(tag);
            return tag.copy();
        } catch (Exception e) {
            Mysticaleverything.LOGGER.error("Failed to serialize CropResource to NBT", e);
            return new CompoundTag();
        }
    }

    @NotNull
    public static CropResource getResource(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return CropResource.EMPTY;
        }
        return tagToResourceDirect(stack.getTag());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource) {
        if (stack.isEmpty()) {
            return;
        }
        if (resource == null || resource == CropResource.EMPTY) {
            CompoundTag tag = stack.getTag();
            if (tag != null) {
                tag.remove(KEY_RESOURCE);
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
        } else {
            stack.getOrCreateTag().put(KEY_RESOURCE, resourceToTag(resource));
        }
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource) {
        setResource(stack, CropResource.of(itemResource));
    }

    /** 中身が入っているか。 */
    public static boolean hasResource(@Nullable ItemStack stack) {
        return getResource(stack) != CropResource.EMPTY;
    }
}
