package jp.main.taikun.mysticaleverything;

import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * {@link CropResource} と NBT の相互変換。
 * <p>
 * キー名は既存ワールドのアイテムに書き込まれているので変更しないこと。
 * <p>
 * 解決 (NBT → {@link CropResource}) は描画とレシピ判定から毎フレーム / 毎tick呼ばれる。
 * 素直に書くと 1 回ごとに {@code CustomData#copyTag} で NBT を丸ごと複製し、
 * さらに {@link ItemStack#parseOptional} で codec を回すことになるので、
 * ここで結果をキャッシュして同じ中身には同じインスタンスを返す。
 */
public class TagItemHelper {

    private static final String KEY_RESOURCE = "resource";

    /**
     * アイテムの CUSTOM_DATA → 中身。{@link CustomData} は中身で equals するので
     * そのまま鍵に使える (= 複製せずに引ける)。
     */
    private static final Map<CustomData, CropResource> COMPONENT_CACHE = Caches.lru(512);
    /** 生の NBT → 中身。ブロックエンティティ側の読み込み用。 */
    private static final Map<CompoundTag, CropResource> TAG_CACHE = Caches.lru(256);

    /**
     * provider を渡してもらえない呼び出し元 (他 Mod の機械の内部処理など) 向けのフォールバック。
     * 専用サーバーでは Minecraft クラスに触れないので、まず動いているサーバーを見る。
     */
    private static @Nullable HolderLookup.Provider getClientRegistryAccess() {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                return server.registryAccess();
            }
        } catch (Throwable ignored) {
            // Not running a server (integrated or dedicated) right now.
        }
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
        if (stack == null || stack.isEmpty() || provider == null) {
            return wrap("item", "Item", new CompoundTag());
        }
        ItemStack copy = stack.copyWithCount(1);
        CompoundTag itemTag = (CompoundTag) copy.save(provider, new CompoundTag());
        return wrap("item", "Item", itemTag);
    }

    public static CompoundTag fluidToTag(@Nullable Fluid fluid) {
        return fluidToTag(fluid, getClientRegistryAccess());
    }

    public static CompoundTag fluidToTag(@Nullable Fluid fluid, @Nullable HolderLookup.Provider provider) {
        if (fluid == null || provider == null) {
            return wrap("fluid", "Fluid", new CompoundTag());
        }
        CompoundTag fluidTag = new CompoundTag();
        new FluidStack(fluid, 1).save(provider, fluidTag);
        return wrap("fluid", "Fluid", fluidTag);
    }

    private static CompoundTag wrap(String type, String payloadKey, CompoundTag payload) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(payloadKey, payload);
        compoundTag.putString("type", type);
        return compoundTag;
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag) {
        return tagToResource(compoundTag, getClientRegistryAccess());
    }

    @NotNull
    public static CropResource tagToResource(@Nullable CompoundTag compoundTag, @Nullable HolderLookup.Provider provider) {
        if (compoundTag == null || compoundTag.isEmpty() || !compoundTag.contains("type")) {
            return CropResource.EMPTY;
        }
        CropResource cached = TAG_CACHE.get(compoundTag);
        if (cached != null) {
            return cached;
        }
        if (provider == null) {
            // レジストリ無しでは解けない。失敗をキャッシュに焼き付けない
            return CropResource.EMPTY;
        }
        CropResource resource = parseResource(compoundTag, provider);
        // キーは呼び出し側が持っているタグなので、後から書き換えられないよう切り離す
        TAG_CACHE.put(compoundTag.copy(), resource);
        return resource;
    }

    private static CropResource parseResource(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        try {
            switch (compoundTag.getString("type")) {
                case "item" -> {
                    if (compoundTag.contains("Item")) {
                        return CropResource.of(ItemStack.parseOptional(provider, compoundTag.getCompound("Item")));
                    }
                }
                case "fluid" -> {
                    if (compoundTag.contains("Fluid")) {
                        FluidStack fluidStack = FluidStack.parse(provider, compoundTag.getCompound("Fluid"))
                                .orElse(FluidStack.EMPTY);
                        if (!fluidStack.isEmpty()) {
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

    @NotNull
    public static CropResource tagToResourceDirect(@Nullable CompoundTag compoundTag) {
        return tagToResourceDirect(compoundTag, getClientRegistryAccess());
    }

    @NotNull
    public static CropResource tagToResourceDirect(@Nullable CompoundTag compoundTag, @Nullable HolderLookup.Provider provider) {
        if (compoundTag == null || !compoundTag.contains(KEY_RESOURCE, Tag.TAG_COMPOUND)) {
            return CropResource.EMPTY;
        }
        return tagToResource(compoundTag.getCompound(KEY_RESOURCE), provider);
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
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null || !data.contains(KEY_RESOURCE)) {
            return CropResource.EMPTY;
        }
        CropResource cached = COMPONENT_CACHE.get(data);
        if (cached != null) {
            return cached;
        }
        if (provider == null) {
            return CropResource.EMPTY;
        }
        // getUnsafe は読むだけ。ここで copyTag すると毎フレーム NBT を丸ごと複製することになる
        CropResource resource = tagToResourceDirect(data.getUnsafe(), provider);
        COMPONENT_CACHE.put(data, resource);
        return resource;
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource) {
        setResource(stack, resource, getClientRegistryAccess());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable CropResource resource, @Nullable HolderLookup.Provider provider) {
        if (stack.isEmpty()) {
            return;
        }
        if (resource == null || resource == CropResource.EMPTY) {
            CustomData data = stack.get(DataComponents.CUSTOM_DATA);
            if (data == null || !data.contains(KEY_RESOURCE)) {
                return;
            }
            CompoundTag tag = data.copyTag();
            tag.remove(KEY_RESOURCE);
            if (tag.isEmpty()) {
                stack.remove(DataComponents.CUSTOM_DATA);
            } else {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
            }
        } else {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.put(KEY_RESOURCE, resourceToTag(resource, provider));
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource) {
        setResource(stack, itemResource, getClientRegistryAccess());
    }

    public static void setResource(@NotNull ItemStack stack, @Nullable ItemStack itemResource, @Nullable HolderLookup.Provider provider) {
        setResource(stack, CropResource.of(itemResource), provider);
    }

    /** 中身が入っているか。 */
    public static boolean hasResource(@Nullable ItemStack stack) {
        return getResource(stack) != CropResource.EMPTY;
    }
}
