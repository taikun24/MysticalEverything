package jp.main.taikun.mysticaleverything;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EverythingCropBlockEntity extends BlockEntity implements Nameable {

    CropResource cropResource = CropResource.EMPTY;

    public EverythingCropBlockEntity(BlockPos pos, BlockState state) {
        super(Mysticaleverything.EVERYTHING_CROP_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.cropResource = TagItemHelper.tagToResourceDirect(tag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        if (this.cropResource != CropResource.EMPTY) {
            tag.put(TagItemHelper.KEY_RESOURCE, TagItemHelper.resourceToTag(this.cropResource));
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        // saveAdditional と同じ内容をクライアントへ送る
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.load(tag);
        }
    }

    @Override
    public @NotNull Component getName() {
        return Component.translatable("block.mysticalagriculture.mystical_crop", cropResource.getName());
    }
}
