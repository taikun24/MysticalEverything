package jp.main.taikun.mysticaleverything;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.network.chat.Component;
import java.util.Objects;

public class CropResource {
    public enum TYPE {
        ITEM, FLUID
    }

    public static final CropResource EMPTY = new CropResource(ItemStack.EMPTY);
    private final TYPE type;
    private ItemStack item;
    private Fluid fluid;

    public CropResource(ItemStack item) {
        this.type = TYPE.ITEM;
        this.item = item == null ? ItemStack.EMPTY : item;
    }

    public CropResource(Fluid fluid) {
        this.type = TYPE.FLUID;
        this.fluid = fluid;
    }

    public TYPE getType() {
        return type;
    }

    public ItemStack getItem() {
        if (type != TYPE.ITEM) {
            throw new IllegalArgumentException("type is not ITEM");
        }
        return item == null ? ItemStack.EMPTY : item;
    }

    public Fluid getFluid() {
        if (type != TYPE.FLUID) {
            throw new IllegalArgumentException("type is not FLUID");
        }
        return fluid;
    }

    public Component getName() {
        return switch (type) {
            case ITEM -> (item == null || item.isEmpty()) ? Component.literal("error") : item.getHoverName();
            case FLUID -> fluid == null ? Component.literal("error") : fluid.getFluidType().getDescription();
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropResource that = (CropResource) o;
        if (type != that.type) return false;
        return switch (type) {
            case ITEM -> {
                ItemStack item1 = this.getItem();
                ItemStack item2 = that.getItem();
                if (item1.isEmpty() && item2.isEmpty()) yield true;
                if (item1.isEmpty() || item2.isEmpty()) yield false;
                yield item1.is(item2.getItem()) && Objects.equals(item1.getTag(), item2.getTag());
            }
            case FLUID -> this.getFluid() == that.getFluid();
        };
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        if (type == TYPE.ITEM) {
            ItemStack itemStack = getItem();
            if (!itemStack.isEmpty()) {
                result = 31 * result + itemStack.getItem().hashCode();
                if (itemStack.hasTag()) {
                    result = 31 * result + Objects.hashCode(itemStack.getTag());
                }
            }
        } else if (type == TYPE.FLUID) {
            if (fluid != null) {
                result = 31 * result + fluid.hashCode();
            }
        }
        return result;
    }
}
