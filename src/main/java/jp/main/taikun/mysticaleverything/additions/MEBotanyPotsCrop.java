package jp.main.taikun.mysticaleverything.additions;

import jp.main.taikun.mysticaleverything.CropResource;
import jp.main.taikun.mysticaleverything.Mysticaleverything;
import jp.main.taikun.mysticaleverything.TagItemHelper;
import net.darkhax.botanypots.common.api.context.BotanyPotContext;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.api.data.display.types.RenderOptions;
import net.darkhax.botanypots.common.api.data.itemdrops.ItemDropProvider;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.BasicOptions;
import net.darkhax.botanypots.common.impl.data.display.types.SimpleDisplayState;
import net.darkhax.botanypots.common.impl.data.itemdrops.SimpleDropProvider;
import net.darkhax.botanypots.common.impl.data.recipe.crop.BasicCrop;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;

public class MEBotanyPotsCrop extends BasicCrop {

    private final CropResource resource;

    public MEBotanyPotsCrop(ItemStack stack) {
        super(makeProperties(stack));
        // resourceはmatches/couldMatch/isCacheKeyでの厳密比較用に保持
        this.resource = TagItemHelper.getResource(stack);
    }

    private static Properties makeProperties(ItemStack stack) {
        final CropResource resource = TagItemHelper.getResource(stack);

        final ItemStack resultStack = new ItemStack(Mysticaleverything.EVERYTHING_ESSENCE.get(), 1);
        CompoundTag tag = new CompoundTag();
        tag.put("resource", TagItemHelper.resourceToTag(resource));
        resultStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        final int growTime = 100;

        final List<Display> display = List.of(
                new AgingDisplayState(Mysticaleverything.EVERYTHING_CROP.get(), BasicOptions.ofDefault())
        );

        final List<ItemDropProvider> drops = List.of(
                new SimpleDropProvider(List.of(
                        new SimpleDropProvider.SimpleDrop(resultStack, 1.0F)
                ))
        );

        return new Properties(
                // Ingredientはアイテム種類のみの粗いフィルタ。厳密判定はmatches/couldMatch/isCacheKeyで行う
                Ingredient.of(stack.getItem()),
                BasicCrop.DIRT,
                growTime,
                display,
                0,              // lightLevel
                drops,
                Optional.empty(), // functionId
                Optional.empty(), // potPredicate
                1.0F,           // baseYield
                1.0F            // yieldScale
        );
    }

    /**
     * BasicCrop#matches はデフォルトで properties.input.test(seedItem) しか見ないため、
     * NBTで区別される resource の同一性を明示的にチェックするようoverride。
     */
    @Override
    public boolean matches(BotanyPotContext input, Level level) {
        final CropResource other = TagItemHelper.getResource(input.getSeedItem());
        return this.resource.equals(other) && super.matches(input, level);
    }

    @Override
    public boolean couldMatch(ItemStack candidate, BotanyPotContext context, Level level) {
        final CropResource other = TagItemHelper.getResource(candidate);
        return this.resource.equals(other);
    }

    /**
     * キャッシュキーとしてもresource単位で区別する。
     * CropResourceにequals/hashCodeがちゃんと実装されている前提。
     */
    @Override
    public boolean isCacheKey(ItemStack stack) {
        return this.resource.equals(TagItemHelper.getResource(stack));
    }
}