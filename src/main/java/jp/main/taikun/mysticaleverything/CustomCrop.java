package jp.main.taikun.mysticaleverything;

import com.blakebr0.mysticalagriculture.api.crop.Crop;
import com.blakebr0.mysticalagriculture.api.crop.CropTier;
import com.blakebr0.mysticalagriculture.api.crop.CropType;
import com.blakebr0.mysticalagriculture.api.lib.LazyIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

public class CustomCrop extends Crop {
    public static ResourceLocation itemToResourceLocation(Item item) {
        ResourceLocation itemLocation = ForgeRegistries.ITEMS.getKey(item);
        if (itemLocation == null) {
            throw new IllegalArgumentException("Item is not registered: " + item);
        }
        return itemLocation;
    }
    public static ResourceLocation itemToCropResourceLocation(ResourceLocation itemLocation) {
        return ResourceLocation.tryBuild("mysticaleverything", "crop_"+itemLocation.getNamespace() + "_"+itemLocation.getPath());
    }
    public CustomCrop(Item item) {
        super(itemToCropResourceLocation(itemToResourceLocation(item)), CropTier.FIVE, CropType.RESOURCE, LazyIngredient.item(itemToResourceLocation(item).toString()));
    }

}
