package jp.main.taikun.mysticaleverything;


import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Mysticaleverything.MODID)
public class Mysticaleverything {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "mysticaleverything";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // why do we gather registry here? cuz im too lazy to create individual classes
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<Block, Block> EVERYTHING_CROP = BLOCKS.register("everything_crop", EverythingCropBlock::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EverythingCropBlockEntity>> EVERYTHING_CROP_BLOCK_ENTITY = BLOCK_ENTITIES.register("everything_crop", () -> BlockEntityType.Builder.of(EverythingCropBlockEntity::new, EVERYTHING_CROP.get()).build(null));
    public static final DeferredHolder<Item,Item> EVERYTHING_ESSENCE = ITEMS.register("everything_essence", EverythingEssenceItem::new);
    public static final DeferredHolder<Item,Item> EVERYTHING_CROP_ITEM = ITEMS.register("everything_crop", EverythingCropItem::new);
    public static final DeferredHolder<Item,Item> EVERYTHING_CATALYST = ITEMS.register("everything_catalyst",  ()->new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final DeferredHolder<Item,Item> COMPRESSION_CATALYST = ITEMS.register("compression_catalyst", CompressionCatalystItem::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<EssenceToItemRecipe>> ESSENCE_TO_ITEM_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("essence_to_item", () -> new SimpleCraftingRecipeSerializer<>(EssenceToItemRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ItemCompressionRecipe>> ITEM_COMPRESSION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("item_compression", () -> new SimpleCraftingRecipeSerializer<>(ItemCompressionRecipe::new));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MY_CUSTOM_TAB = CREATIVE_MODE_TABS.register("my_custom_tab",
            () -> CreativeModeTab.builder()
                    .icon(() -> Mysticaleverything.EVERYTHING_CATALYST.get().getDefaultInstance())
                    .title(Component.translatable("creativetab.my_custom_tab"))
                    .displayItems((parameters, output) -> {
                        output.accept(Mysticaleverything.EVERYTHING_CATALYST.get());
                        output.accept(Mysticaleverything.COMPRESSION_CATALYST.get());
                    })
                    .build());

    public Mysticaleverything(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC, "mystical-everything.toml");
        IEventBus bus = container.getEventBus();
        if (bus==null) throw new IllegalStateException("は？");
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        CREATIVE_MODE_TABS.register(bus);
    }

    public static boolean isNotSameItem(ItemStack itemStack, ItemStack target) {
        CompoundTag targetTag = target.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        boolean isSameItem = target.is(itemStack.getItem());
        boolean isSameTag = targetTag.equals(itemStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag());
        return (!isSameItem || !isSameTag);
    }

}
