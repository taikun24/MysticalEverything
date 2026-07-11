package jp.main.taikun.mysticaleverything;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
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
        itemTag = (CompoundTag) copy.save(Minecraft.getInstance().level.registryAccess(), itemTag);
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
        fluidStack.save(Minecraft.getInstance().level.registryAccess(),fluidTag);
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
                        ItemStack itemStack = ItemStack.parseOptional(Minecraft.getInstance().level.registryAccess(),compoundTag.getCompound("Item"));
                        if (itemStack.isEmpty()) {
                            return CropResource.EMPTY;
                        }
                        return new CropResource(itemStack);
                    }
                }
                case "fluid" -> {
                    if (compoundTag.contains("Fluid")) {
                        FluidStack fluidStack = FluidStack.parse(
                                Minecraft.getInstance().level.registryAccess(),
                                compoundTag.getCompound("Fluid")).get();
                        if (fluidStack.isEmpty()) {
                            return CropResource.EMPTY;
                        }
                        Fluid fluidType = fluidStack.getFluid();
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
        return tagToResourceDirect(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource) {
        if (stack.isEmpty()) {
            return;
        }
        if (resource == null || resource == CropResource.EMPTY) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.remove("resource");
            if (tag.isEmpty()) {
                stack.set(DataComponents.CUSTOM_DATA, null);
            }
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        } else {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.put("resource", resourceToTag(resource));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
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
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains("resource")) {
            return false;
        }
        CropResource resource = getResource(stack);
        return resource != CropResource.EMPTY;
    }
}
