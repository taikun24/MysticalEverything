package jp.main.taikun.mysticaleverything.mixin.botanypots;


import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.additions.MEBotanyPotsCrop;
import net.darkhax.botanypots.BotanyPotHelper;
import net.darkhax.botanypots.block.BlockEntityBotanyPot;
import net.darkhax.botanypots.data.recipes.crop.Crop;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.UUID;

@Mixin(value = BotanyPotHelper.class, remap = false)
public class CropOverrideMixin {
    @Inject(method = "findCrop", at=@At("HEAD"), cancellable=true)
    private static void getMixin(Level level, BlockPos pos, BlockEntityBotanyPot pot, ItemStack stack, CallbackInfoReturnable<Crop> cir) {
        if (stack.getItem() == Mysticaleverything.EVERYTHING_CROP_ITEM.get()) {
            cir.setReturnValue(
                            new MEBotanyPotsCrop(
                                    ResourceLocation.tryBuild(Mysticaleverything.MODID, "crop_" + (stack.getOrCreateTag().getString("id"))),
                                    stack)

            );
            cir.cancel();
        }

    }

}
