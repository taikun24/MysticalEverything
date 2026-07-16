package jp.main.taikun.mysticaleverything;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EverythingCropBlockEntity extends BlockEntity implements Nameable {

    public EverythingCropBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Mysticaleverything.EVERYTHING_CROP_BLOCK_ENTITY.get(), p_155229_, p_155230_);
        cropResource = CropResource.EMPTY;
    }
    CropResource cropResource;

    private static @Nullable CompoundTag getResourceTag(@NotNull CompoundTag tag) {
        if (tag.contains("components")) {
            CompoundTag componentsTag = tag.getCompound("components");
            if (componentsTag.contains("custom_data")) {
                CompoundTag customDataTag = componentsTag.getCompound("custom_data");
                if (customDataTag.contains("resource")) {
                    return customDataTag.getCompound("resource");
                }
            }
        }
        if (tag.contains("resource")) {
            return tag.getCompound("resource");
        }
        return null;
    }

    private static void putResourceTag(@NotNull CompoundTag tag, @NotNull CompoundTag resourceTag) {
        CompoundTag componentsTag = tag.contains("components") ? tag.getCompound("components") : new CompoundTag();
        CompoundTag customDataTag = componentsTag.contains("custom_data") ? componentsTag.getCompound("custom_data") : new CompoundTag();
        customDataTag.put("resource", resourceTag);
        componentsTag.put("custom_data", customDataTag);
        tag.put("components", componentsTag);
        tag.remove("resource");
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        CompoundTag resourceTag = getResourceTag(tag);
        this.cropResource = resourceTag == null ? CropResource.EMPTY : TagItemHelper.tagToResource(resourceTag, provider);
    }
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (this.cropResource != null && this.cropResource != CropResource.EMPTY) {
            CompoundTag resourceTag = TagItemHelper.resourceToTag(this.cropResource, provider);
            putResourceTag(tag, resourceTag);
        } else {
            tag.remove("resource");
        }
    }


    @Override
    public void saveToItem(@NotNull ItemStack stack,  HolderLookup.@NotNull Provider provider) {
        super.saveToItem(stack, provider);
    }


    @Override
    public @NotNull CompoundTag getUpdateTag( HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        CompoundTag resourceTag = getResourceTag(tag);
        this.cropResource = resourceTag == null ? CropResource.EMPTY : TagItemHelper.tagToResource(resourceTag, lookupProvider);
    }
    @Override
    public @NotNull Component getName() {
        return Component.translatable("block.mysticalagriculture.mystical_crop", cropResource.getName());
    }
}
