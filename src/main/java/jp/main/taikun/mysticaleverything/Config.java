package jp.main.taikun.mysticaleverything;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jp.main.taikun.mysticaleverything.config.FilterBuilder;
import jp.main.taikun.mysticaleverything.config.IFilter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.Nullable;

public class Config {
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue DISABLE_NBT;
    public static final ForgeConfigSpec.BooleanValue USE_CUSTOM_FILTER;
    public static final ForgeConfigSpec.BooleanValue FILTER_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<String> FILTER_JSON;
    public static final ForgeConfigSpec.BooleanValue USE_NBT_FILTER;
    public static final ForgeConfigSpec.BooleanValue NBT_FILTER_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<String> NBT_FILTER_JSON;

    /** 作物にできるアイテムを絞るフィルター。 */
    private static final CachedFilter CROP_FILTER = new CachedFilter();
    /** disableNBT の挙動を反転させるアイテムを選ぶフィルター。 */
    private static final CachedFilter NBT_FILTER = new CachedFilter();

    static{
        BUILDER.push("Config");
        DISABLE_NBT = BUILDER.comment("NBTも含めて作物にするか？(true→いいえ)").define("disableNBT", true);
        USE_CUSTOM_FILTER = BUILDER.comment("JSONによるカスタムフィルターを使うか？(true→使う)").define("useCustomFilter", false);
        FILTER_WHITELIST = BUILDER.comment("フィルターをホワイトリストにするか？(true→ホワイトリスト)").define("filterWhitelist", true);
        FILTER_JSON = BUILDER.comment("作物にするアイテムのフィルターJSON。例: {\"type\": \"tag\", \"tag\": \"forge:ores\"}").define("filterJson", "{\"type\": \"tag\", \"tag\": \"forge:ores\"}");
        USE_NBT_FILTER = BUILDER.comment("disableNBT をアイテムごとに切り替えるフィルターを使うか？(true→使う)").define("useNBTFilter", false);
        NBT_FILTER_WHITELIST = BUILDER.comment("NBTフィルターをホワイトリストにするか？(true→ホワイトリスト)").define("nbtFilterWhitelist", true);
        NBT_FILTER_JSON = BUILDER.comment("このフィルターに当たったアイテムだけ disableNBT が逆になる。例: {\"type\": \"tag\", \"tag\": \"forge:tools\"}").define("nbtFilterJson", "{\"type\": \"nbt_has_any\"}");
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }

    public static boolean filter(ItemStack itemStack){
        if (!CONFIG_SPEC.isLoaded()) return true;
        if (!USE_CUSTOM_FILTER.get()) return true;

        IFilter filter = CROP_FILTER.get(FILTER_JSON.get());
        // パースに失敗した場合はフィルター無しとして扱う (毎tick落とさない)
        if (filter == null) return true;
        return filter.filter(itemStack) == FILTER_WHITELIST.get();
    }

    /**
     * そのアイテムを NBT 抜きで扱うか。
     * <p>
     * 既定は {@code disableNBT} で、{@code useNBTFilter} が有効なときは
     * NBTフィルターに当たったアイテムだけ挙動が反転する。
     * (disableNBT=false + ホワイトリストなら「当たったものだけ NBT を捨てる」、
     * disableNBT=true + ホワイトリストなら「当たったものだけ NBT を残す」)
     */
    public static boolean disableNBT(ItemStack itemStack){
        boolean base = DISABLE_NBT.get();
        if (!CONFIG_SPEC.isLoaded()) return base;
        if (!USE_NBT_FILTER.get()) return base;

        IFilter filter = NBT_FILTER.get(NBT_FILTER_JSON.get());
        if (filter == null) return base;
        boolean matched = filter.filter(itemStack) == NBT_FILTER_WHITELIST.get();
        return matched != base;
    }

    private static @Nullable IFilter parseFilter(String jsonConfig){
        try {
            JsonObject object = JsonParser.parseString(jsonConfig).getAsJsonObject();
            IFilter parsed = FilterBuilder.getInstance().parse(object);
            Mysticaleverything.LOGGER.info("Parsed filterJson: {}", jsonConfig);
            return parsed;
        } catch (Exception e) {
            Mysticaleverything.LOGGER.error("Failed to parse filterJson, the filter is ignored: {}", jsonConfig, e);
            return null;
        }
    }

    /** JSON が変わったときだけパースし直す。パース失敗は null のまま覚えておく。 */
    private static final class CachedFilter {
        private String lastJsonConfig;
        private IFilter filter;

        private @Nullable IFilter get(String jsonConfig) {
            if (!jsonConfig.equals(lastJsonConfig)) {
                lastJsonConfig = jsonConfig;
                filter = parseFilter(jsonConfig);
            }
            return filter;
        }
    }
}
