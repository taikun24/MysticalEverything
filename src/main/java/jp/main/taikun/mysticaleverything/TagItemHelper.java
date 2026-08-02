package jp.main.taikun.mysticaleverything;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link CropResource} と NBT の相互変換。
 * <p>
 * キー名は既存ワールドのアイテムに書き込まれているので変更しないこと。
 */
public final class TagItemHelper {

    /** アイテム側のルートタグに置く、resource 本体のキー。 */
    public static final String KEY_RESOURCE = "resource";
    private static final String KEY_TYPE = "type";
    private static final String KEY_ITEM = "Item";
    private static final String KEY_FLUID = "Fluid";
    private static final String TYPE_ITEM = "item";
    private static final String TYPE_FLUID = "fluid";

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
        if (compoundTag == null || !compoundTag.contains(KEY_TYPE)) {
            return CropResource.EMPTY;
        }
        try {
            switch (compoundTag.getString(KEY_TYPE)) {
                case TYPE_ITEM -> {
                    if (compoundTag.contains(KEY_ITEM)) {
                        ItemStack itemStack = ItemStack.of(compoundTag.getCompound(KEY_ITEM));
                        if (!itemStack.isEmpty()) {
                            return new CropResource(itemStack);
                        }
                    }
                }
                case TYPE_FLUID -> {
                    if (compoundTag.contains(KEY_FLUID)) {
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(compoundTag.getCompound(KEY_FLUID));
                        if (fluidStack != null && !fluidStack.isEmpty() && fluidStack.getFluid() != null) {
                            return new CropResource(fluidStack.getFluid());
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
        if (compoundTag == null || !compoundTag.contains(KEY_RESOURCE)) {
            return CropResource.EMPTY;
        }
        return tagToResource(compoundTag.getCompound(KEY_RESOURCE));
    }

    @NotNull
    public static CompoundTag resourceToTag(@Nullable CropResource cropResource) {
        if (cropResource == null || cropResource == CropResource.EMPTY) {
            return new CompoundTag();
        }
        try {
            return switch (cropResource.getType()) {
                case ITEM -> itemToTag(cropResource.getItem());
                case FLUID -> fluidToTag(cropResource.getFluid());
            };
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
        setResource(stack, itemResource == null || itemResource.isEmpty()
                ? CropResource.EMPTY
                : new CropResource(itemResource));
    }

    public static boolean hasResource(@Nullable ItemStack stack) {
        return getResource(stack) != CropResource.EMPTY;
    }
}
