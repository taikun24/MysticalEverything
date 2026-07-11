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
    CropResource cropResource = CropResource.EMPTY;

    @Override
    public void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(tag, provider);
        this.cropResource = TagItemHelper.tagToResourceDirect(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        if (this.cropResource != null && this.cropResource != CropResource.EMPTY) {
            CompoundTag resourceTag = TagItemHelper.resourceToTag(this.cropResource);
            tag.put("resource", resourceTag);
        }
    }

    @Override
    public void saveToItem(@NotNull ItemStack stack,  HolderLookup.@NotNull Provider provider) {
        super.saveToItem(stack, provider);
    }


    @Override
    public @NotNull CompoundTag getUpdateTag( HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        // saveAdditional と同じ内容を書き込む
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
        this.loadAdditional(tag, lookupProvider);
    }


    @Override
    public @NotNull Component getName() {
        return Component.translatable("block.mysticalagriculture.mystical_crop", cropResource.getName());
    }
}
