package jp.main.taikun.mysticaleverything.config;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * 1.21 では任意 NBT は {@code minecraft:custom_data} コンポーネントに集約されたので、
 * キー指定系のフィルターはそこを見る。
 */
public abstract class FilterNBT implements IFilter {

    @Override
    public boolean filter(ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return false;
        }
        CompoundTag tag = customData.copyTag();
        if (tag.isEmpty()) {
            return false;
        }
        return matchNBT(tag);
    }

    /**
     * custom_data の中身に対する個別判定をサブクラスで実装する
     */
    protected abstract boolean matchNBT(CompoundTag tag);

    // --------------------------------------------------
    // サブクラス定義
    // --------------------------------------------------

    /**
     * 1. 指定したキーが存在するか確認するフィルター
     * JSON例: { "type": "nbt_has_key", "key": "myKey" }
     */
    public static class HasKey extends FilterNBT {
        private final String key;

        public HasKey(String key) {
            this.key = key;
        }

        @Override
        protected boolean matchNBT(CompoundTag tag) {
            return tag.contains(this.key);
        }
    }

    /**
     * 2. 文字列（String）の値を比較するフィルター
     * JSON例: { "type": "nbt_string", "key": "Owner", "value": "Steve" }
     */
    public static class StringEquals extends FilterNBT {
        private final String key;
        private final String value;

        public StringEquals(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        protected boolean matchNBT(CompoundTag tag) {
            return tag.contains(this.key) && tag.getString(this.key).equals(this.value);
        }
    }

    /**
     * 3. 数値（Int）の値を比較するフィルター
     * JSON例: { "type": "nbt_int", "key": "myCounter", "value": 100 }
     */
    public static class IntEquals extends FilterNBT {
        private final String key;
        private final int value;

        public IntEquals(String key, int value) {
            this.key = key;
            this.value = value;
        }

        @Override
        protected boolean matchNBT(CompoundTag tag) {
            return tag.contains(this.key) && tag.getInt(this.key) == this.value;
        }
    }

    /**
     * 4. エンチャントが付与されているか確認するフィルター。
     * 1.21 では enchantments / stored_enchantments コンポーネントを見る。
     * JSON例: { "type": "nbt_has_enchantment", "enchantmentId": "minecraft:sharpness" }
     */
    public static class HasEnchantment implements IFilter {
        private final String enchantmentId;

        public HasEnchantment(String enchantmentId) {
            this.enchantmentId = enchantmentId;
        }

        @Override
        public boolean filter(ItemStack itemStack) {
            if (itemStack.isEmpty()) return false;
            return contains(itemStack.get(DataComponents.ENCHANTMENTS))
                    || contains(itemStack.get(DataComponents.STORED_ENCHANTMENTS));
        }

        private boolean contains(ItemEnchantments enchantments) {
            if (enchantments == null || enchantments.isEmpty()) {
                return false;
            }
            return enchantments.keySet().stream()
                    .anyMatch(holder -> holder.unwrapKey()
                            .map(key -> key.location().toString().equals(this.enchantmentId))
                            .orElse(false));
        }
    }

    /**
     * 5. 耐久値（Damage）の残量を判定するフィルター
     * JSON例: { "type": "nbt_damage_less_than", "maxDamage": 50 }
     */
    public static class DamageLessThan implements IFilter {
        private final int maxDamage;

        public DamageLessThan(int maxDamage) {
            this.maxDamage = maxDamage;
        }

        @Override
        public boolean filter(ItemStack itemStack) {
            if (itemStack.isEmpty()) return false;
            return itemStack.getDamageValue() < this.maxDamage;
        }
    }
}
