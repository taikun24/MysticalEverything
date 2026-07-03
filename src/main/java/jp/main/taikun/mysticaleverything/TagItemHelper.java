package jp.main.taikun.mysticaleverything;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagItemHelper {
    public static CompoundTag itemToTag(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("type", "item");
            compoundTag.put("Item", new CompoundTag());
            return compoundTag;
        }
        ItemStack copy = stack.copy();
        if (copy.getCount() != 1) {
            copy.setCount(1);
        }
        CompoundTag itemTag = new CompoundTag();
        copy.save(itemTag);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Item", itemTag);
        compoundTag.putString("type", "item");
        return compoundTag;
    }

    public static CompoundTag fluidToTag(@Nullable Fluid fluid) {
        if (fluid == null) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("type", "fluid");
            compoundTag.put("Fluid", new CompoundTag());
            return compoundTag;
        }
        FluidStack fluidStack = new FluidStack(fluid, 1);
        CompoundTag fluidTag = new CompoundTag();
        fluidStack.writeToNBT(fluidTag);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Fluid", fluidTag);
        compoundTag.putString("type", "fluid");
        return compoundTag;
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null || compoundTag.isEmpty()) {
            return CropResource.EMPTY;
        }
        if (!compoundTag.contains("type")) {
            return CropResource.EMPTY;
        }
        String type = compoundTag.getString("type");
        try {
            switch (type) {
                case "item" -> {
                    if (compoundTag.contains("Item")) {
                        ItemStack itemStack = ItemStack.of(compoundTag.getCompound("Item"));
                        if (itemStack.isEmpty()) {
                            return CropResource.EMPTY;
                        }
                        return new CropResource(itemStack);
                    }
                }
                case "fluid" -> {
                    if (compoundTag.contains("Fluid")) {
                        FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(compoundTag.getCompound("Fluid"));
                        if (fluidStack == null || fluidStack.isEmpty()) {
                            return CropResource.EMPTY;
                        }
                        Fluid fluidType = fluidStack.getFluid();
                        if (fluidType == null) {
                            return CropResource.EMPTY;
                        }
                        return new CropResource(fluidType);
                    }
                }
            }
        } catch (Exception e) {
            Mysticaleverything.LOGGER.error("Failed to parse CropResource from NBT", e);
        }
        return CropResource.EMPTY;
    }

    @NotNull
    public static CropResource tagToResourceDirect(@Nullable CompoundTag compoundTag) {
        if (compoundTag == null || !compoundTag.contains("resource")) {
            return CropResource.EMPTY;
        }
        return tagToResource(compoundTag.getCompound("resource"));
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
                tag.remove("resource");
                if (tag.isEmpty()) {
                    stack.setTag(null);
                }
            }
        } else {
            CompoundTag tag = stack.getOrCreateTag();
            tag.put("resource", resourceToTag(resource));
        }
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource) {
        if (itemResource == null || itemResource.isEmpty()) {
            setResource(stack, CropResource.EMPTY);
        } else {
            setResource(stack, new CropResource(itemResource));
        }
    }

    public static boolean hasResource(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("resource")) {
            return false;
        }
        CropResource resource = getResource(stack);
        return resource != CropResource.EMPTY;
    }
}
