package jp.main.taikun.mysticaleverything.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public abstract class FilterNBT implements IFilter {

    @Override
    public boolean filter(ItemStack itemStack) {
        // アイテム自体にNBT（1.20.4以前のCompoundTag）が存在しない場合は false
        if (!itemStack.hasTag()) {
            return false;
        }
        return matchNBT(itemStack.getTag());
    }

    /**
     * NBTタグに対する個別判定をサブクラスで実装する
     */
    protected abstract boolean matchNBT(CompoundTag tag);

    // --------------------------------------------------
    // サブクラス定義
    // --------------------------------------------------

    /**
     * 1. 指定したキーが存在するか確認するフィルター
     * JSON例: { "type": "nbt_has_key", "key": "CustomModelData" }
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
     * 2. NBT内の文字列（String）の値を比較するフィルター
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
     * 3. NBT内の数値（Int）の値を比較するフィルター
     * JSON例: { "type": "nbt_int", "key": "CustomModelData", "value": 100 }
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
     * 4. エンチャントが付与されているか確認するフィルター
     * JSON例: { "type": "nbt_has_enchantment", "enchantmentId": "minecraft:sharpness" }
     */
    public static class HasEnchantment extends FilterNBT {
        private final String enchantmentId;

        public HasEnchantment(String enchantmentId) {
            this.enchantmentId = enchantmentId;
        }

        @Override
        protected boolean matchNBT(CompoundTag tag) {
            if (!tag.contains("Enchantments", 9)) { // 9 = Tag.TAG_LIST
                return false;
            }
            var list = tag.getList("Enchantments", 10); // 10 = Tag.TAG_COMPOUND
            for (int i = 0; i < list.size(); i++) {
                CompoundTag ench = list.getCompound(i);
                if (ench.getString("id").equals(this.enchantmentId)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 5. 耐久値（Damage）の残量を判定するフィルター
     * JSON例: { "type": "nbt_damage_less_than", "maxDamage": 50 }
     */
    public static class DamageLessThan extends FilterNBT {
        private final int maxDamage;

        public DamageLessThan(int maxDamage) {
            this.maxDamage = maxDamage;
        }

        @Override
        protected boolean matchNBT(CompoundTag tag) {
            return tag.contains("Damage") && tag.getInt("Damage") < this.maxDamage;
        }
    }
}