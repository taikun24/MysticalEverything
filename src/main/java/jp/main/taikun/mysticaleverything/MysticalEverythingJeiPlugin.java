package jp.main.taikun.mysticaleverything;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class MysticalEverythingJeiPlugin implements IModPlugin {
    private static final ResourceLocation PLUGIN_ID = ResourceLocation.tryBuild(Mysticaleverything.MODID, "jei_plugin");
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        if (PLUGIN_ID == null) throw  new NullPointerException("Plugin ID is null, I'm not sure why...");
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                Mysticaleverything.EVERYTHING_CATALYST.get(),
                Component.translatable("description.mysticaleverything.everything_catalyst.1"),
                Component.translatable("description.mysticaleverything.everything_catalyst.2"),
                Component.translatable("description.mysticaleverything.everything_catalyst.3"),
                Component.translatable("description.mysticaleverything.everything_catalyst.4")
        );
    }

}
