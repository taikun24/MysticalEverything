package jp.main.taikun.mysticaleverything;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TagItemHelper {
    private static @Nullable HolderLookup.Provider getClientRegistryAccess() {
        try {
            if (Minecraft.getInstance().level != null) {
                return Minecraft.getInstance().level.registryAccess();
            }
        } catch (Throwable ignored) {
            // Dedicated server or client not initialized yet.
        }
        return null;
    }

    public static CompoundTag itemToTag(@Nullable ItemStack stack) {
        return itemToTag(stack, getClientRegistryAccess());
    }

    public static CompoundTag itemToTag(@Nullable ItemStack stack, @Nullable HolderLookup.Provider provider) {
        if (stack == null || stack.isEmpty()) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("type", "item");
            compoundTag.put("Item", new CompoundTag());
            return compoundTag;
        }
        if (provider == null) {
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
        itemTag = (CompoundTag) copy.save(provider, itemTag);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Item", itemTag);
        compoundTag.putString("type", "item");
        return compoundTag;
    }

    public static CompoundTag fluidToTag(@Nullable Fluid fluid) {
        return fluidToTag(fluid, getClientRegistryAccess());
    }

    public static CompoundTag fluidToTag(@Nullable Fluid fluid, @Nullable HolderLookup.Provider provider) {
        if (fluid == null) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("type", "fluid");
            compoundTag.put("Fluid", new CompoundTag());
            return compoundTag;
        }
        if (provider == null) {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("type", "fluid");
            compoundTag.put("Fluid", new CompoundTag());
            return compoundTag;
        }
        FluidStack fluidStack = new FluidStack(fluid, 1);
        CompoundTag fluidTag = new CompoundTag();
        fluidStack.save(provider, fluidTag);
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("Fluid", fluidTag);
        compoundTag.putString("type", "fluid");
        return compoundTag;
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag) {
        return tagToResource(compoundTag, getClientRegistryAccess());
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag, @Nullable HolderLookup.Provider provider) {
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
                    if (compoundTag.contains("Item") && provider != null) {
                        ItemStack itemStack = ItemStack.parseOptional(provider, compoundTag.getCompound("Item"));
                        if (itemStack.isEmpty()) {
                            return CropResource.EMPTY;
                        }
                        return new CropResource(itemStack);
                    }
                }
                case "fluid" -> {
                    if (compoundTag.contains("Fluid") && provider != null) {
                        FluidStack fluidStack = FluidStack.parse(
                                provider,
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
        return tagToResourceDirect(compoundTag, getClientRegistryAccess());
    }

    @NotNull
    public static CropResource tagToResourceDirect(@Nullable CompoundTag compoundTag, @Nullable HolderLookup.Provider provider) {
        if (compoundTag == null || !compoundTag.contains("resource")) {
            return CropResource.EMPTY;
        }
        return tagToResource(compoundTag.getCompound("resource"), provider);
    }

    @NotNull
    public static CompoundTag resourceToTag(@Nullable CropResource cropResource) {
        return resourceToTag(cropResource, getClientRegistryAccess());
    }

    @NotNull
    public static CompoundTag resourceToTag(@Nullable CropResource cropResource, @Nullable HolderLookup.Provider provider) {
        if (cropResource == null || cropResource == CropResource.EMPTY) {
            return new CompoundTag();
        }
        try {
            return switch (cropResource.getType()) {
                case ITEM -> itemToTag(cropResource.getItem(), provider);
                case FLUID -> fluidToTag(cropResource.getFluid(), provider);
            };
        } catch (Exception e) {
            Mysticaleverything.LOGGER.error("Failed to serialize CropResource to NBT", e);
            return new CompoundTag();
        }
    }

    @NotNull
    public static CropResource getResource(@Nullable ItemStack stack) {
        return getResource(stack, getClientRegistryAccess());
    }

    @NotNull
    public static CropResource getResource(@Nullable ItemStack stack, @Nullable HolderLookup.Provider provider) {
        if (stack == null || stack.isEmpty()) {
            return CropResource.EMPTY;
        }
        return tagToResourceDirect(stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(), provider);
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource) {
        setResource(stack, resource, getClientRegistryAccess());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource, @Nullable HolderLookup.Provider provider) {
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
            tag.put("resource", resourceToTag(resource, provider));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource) {
        setResource(stack, itemResource, getClientRegistryAccess());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource, @Nullable HolderLookup.Provider provider) {
        if (itemResource == null || itemResource.isEmpty()) {
            setResource(stack, CropResource.EMPTY, provider);
        } else {
            setResource(stack, new CropResource(itemResource), provider);
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
