package jp.main.taikun.mysticaleverything;

import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.api.crop.CropTier;
import com.blakebr0.mysticalagriculture.api.crop.CropType;
import com.blakebr0.mysticalagriculture.api.lib.LazyIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CustomCrop extends Crop {
    public static ResourceLocation itemToResourceLocation(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
    public static ResourceLocation itemToCropResourceLocation(ResourceLocation itemLocation) {
        return ResourceLocation.tryBuild("mysticaleverything", "crop_"+itemLocation.getNamespace() + "_"+itemLocation.getPath());
    }
    public CustomCrop(Item item) {
        super(itemToCropResourceLocation(itemToResourceLocation(item)), CropTier.FIVE, CropType.RESOURCE, LazyIngredient.item(itemToResourceLocation(item).toString()));
    }

}
