package jp.main.taikun.mysticaleverything.mixin.botanypots;


import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.additions.MEBotanyPotsCrop;
import net.darkhax.botanypots.common.api.data.components.CropOverride;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CropOverride.class)
public class CropOverrideMixin {
    @Inject(method = "get", at=@At("HEAD"), cancellable=true)
    private static void getMixin(ItemStack stack, CallbackInfoReturnable<Optional<CropOverride>> cir) {
        Mysticaleverything.LOGGER.info("CropOverrideMixin.get called with stack: " + stack);
        if (stack.getItem() == Mysticaleverything.EVERYTHING_CROP_ITEM.get()) {
            Mysticaleverything.LOGGER.info("Returning MEBotanyPotsCrop for " + stack);
            cir.setReturnValue(Optional.of(
                    new CropOverride(
                            new MEBotanyPotsCrop(stack)
                    ))
            );
            cir.cancel();
        }

    }

}
