package jp.main.taikun.mysticaleverything;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Mysticaleverything.MODID)
public class Mysticaleverything {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "mysticaleverything";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // why do we gather registry here? cuz im too lazy to create individual classes
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<Block> EVERYTHING_CROP = BLOCKS.register("everything_crop", EverythingCropBlock::new);
    public static final RegistryObject<BlockEntityType<EverythingCropBlockEntity>> EVERYTHING_CROP_BLOCK_ENTITY = BLOCK_ENTITIES.register("everything_crop", () -> BlockEntityType.Builder.of(EverythingCropBlockEntity::new, EVERYTHING_CROP.get()).build(null));
    public static final RegistryObject<Item> EVERYTHING_ESSENCE = ITEMS.register("everything_essence", EverythingEssenceItem::new);
    public static final RegistryObject<Item> EVERYTHING_CROP_ITEM = ITEMS.register("everything_crop", EverythingCropItem::new);
    public static final RegistryObject<Item> EVERYTHING_CATALYST = ITEMS.register("everything_catalyst",  ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> COMPRESSION_CATALYST = ITEMS.register("compression_catalyst", CompressionCatalystItem::new);
    public static final RegistryObject<RecipeSerializer<EssenceToItemRecipe>> ESSENCE_TO_ITEM_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("essence_to_item", () -> new SimpleCraftingRecipeSerializer<>(EssenceToItemRecipe::new));
    public static final RegistryObject<RecipeSerializer<ItemCompressionRecipe>> ITEM_COMPRESSION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("item_compression", () -> new SimpleCraftingRecipeSerializer<>(ItemCompressionRecipe::new));
    public static final RegistryObject<CreativeModeTab> MY_CUSTOM_TAB = CREATIVE_MODE_TABS.register("my_custom_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> Mysticaleverything.EVERYTHING_CATALYST.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.my_custom_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(Mysticaleverything.EVERYTHING_CATALYST.get());
                        output.accept(Mysticaleverything.COMPRESSION_CATALYST.get());
                    })
                    .build());

    public Mysticaleverything(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC, "mystical-everything.toml");
        IEventBus bus = context.getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
    }

    public static boolean isNotSameItem(ItemStack itemStack, ItemStack target) {
        CompoundTag targetTag = target.getTag();
        boolean isSameItem = target.is(itemStack.getItem());
        boolean isSameTag = targetTag==null?itemStack.getTag()==null:targetTag.equals(itemStack.getTag());
        return (!isSameItem || !isSameTag);
    }
}
