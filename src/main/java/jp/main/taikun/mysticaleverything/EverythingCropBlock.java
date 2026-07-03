package jp.main.taikun.mysticaleverything;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// blockEntity crops?????? wtf?????
public class EverythingCropBlock extends CropBlock implements EntityBlock {

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);
    public EverythingCropBlock() {
        super(Properties.copy(Blocks.WHEAT));
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource source) {
        if (level.getBlockEntity(pos) instanceof EverythingCropBlockEntity) {
            super.randomTick(state, level, pos, source);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState p_52297_, @NotNull BlockGetter p_52298_, @NotNull BlockPos p_52299_, @NotNull CollisionContext p_52300_) {
        return SHAPE;
    }

    @Override
    protected @NotNull ItemLike getBaseSeedId() {
        return Mysticaleverything.EVERYTHING_CROP_ITEM.get();
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter getter, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (getter.getBlockEntity(pos) instanceof EverythingCropBlockEntity be) {
            CompoundTag tag = be.getUpdateTag();
            ItemStack stack = new ItemStack(Mysticaleverything.EVERYTHING_CROP_ITEM.get());
            stack.setTag(tag);
            return stack;
        }
        return new ItemStack(Mysticaleverything.EVERYTHING_CROP_ITEM.get());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new EverythingCropBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState bs, LootParams.@NotNull Builder builder) {
        BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        EverythingCropBlockEntity myBE = null;
        if (blockEntity instanceof EverythingCropBlockEntity) {
            myBE = (EverythingCropBlockEntity) blockEntity;
        } else{
            Vec3 pos = builder.getOptionalParameter(LootContextParams.ORIGIN);
            if (pos != null) {
                BlockPos blockPos = BlockPos.containing(pos);
                BlockEntity be = builder.getLevel().getBlockEntity(blockPos);
                if (be instanceof EverythingCropBlockEntity) {
                    myBE = (EverythingCropBlockEntity) be;
                }
            }
        }
        if (myBE == null) {
            Mysticaleverything.LOGGER.warn("BlockEntity is not EverythingCropBlockEntity ");
            return Lists.newArrayList();
        }
        CropResource resource = myBE.cropResource;
        if (resource == null || resource == CropResource.EMPTY || resource.getType() != CropResource.TYPE.ITEM) {
            return Lists.newArrayList();
        }
        ItemStack cropStack = new ItemStack(Mysticaleverything.EVERYTHING_CROP_ITEM.get());
        TagItemHelper.setResource(cropStack, resource);

        if (bs.getValue(CropBlock.AGE) == 7) {
            ItemStack essenceStack = new ItemStack(Mysticaleverything.EVERYTHING_ESSENCE.get());
            TagItemHelper.setResource(essenceStack, resource);
            return Lists.newArrayList(cropStack, essenceStack);
        }
        return Lists.newArrayList(cropStack);
    }

    @Override
    public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity p_49850_, @NotNull ItemStack stack) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof EverythingCropBlockEntity myTile) {
                if (stack.hasTag() && stack.getTag() != null) {
                    CompoundTag itemNbt = stack.getTag();
                    myTile.load(itemNbt);
                    myTile.setChanged();
                }
            }
        }
    }

}
