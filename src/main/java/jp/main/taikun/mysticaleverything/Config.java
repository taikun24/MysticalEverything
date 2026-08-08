package jp.main.taikun.mysticaleverything;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jp.main.taikun.mysticaleverything.config.FilterBuilder;
import jp.main.taikun.mysticaleverything.config.IFilter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue DISABLE_NBT;
    public static final ForgeConfigSpec.BooleanValue USE_CUSTOM_FILTER;
    public static final ForgeConfigSpec.BooleanValue FILTER_WHITELIST;
    public static final ForgeConfigSpec.ConfigValue<String> FILTER_JSON;

    private static String lastJsonConfig;
    private static IFilter filter;

    static{
        BUILDER.push("Config");
        DISABLE_NBT = BUILDER.comment("NBTも含めて作物にするか？(true→いいえ)").define("disableNBT", true);
        USE_CUSTOM_FILTER = BUILDER.comment("JSONによるカスタムフィルターを使うか？(true→使う)").define("useCustomFilter", false);
        FILTER_WHITELIST = BUILDER.comment("フィルターをホワイトリストにするか？(true→ホワイトリスト)").define("filterWhitelist", true);
        FILTER_JSON = BUILDER.comment("作物にするアイテムのフィルターJSON。例: {\"type\": \"tag\", \"tag\": \"forge:ores\"}").define("filterJson", "{\"type\": \"tag\", \"tag\": \"forge:ores\"}");
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }

    public static boolean filter(ItemStack itemStack){
        if (!CONFIG_SPEC.isLoaded()) return true;
        if (!USE_CUSTOM_FILTER.get()) return true;

        String jsonConfig = FILTER_JSON.get();
        if (!jsonConfig.equals(lastJsonConfig)) {
            lastJsonConfig = jsonConfig;
            filter = parseFilter(jsonConfig);
        }
        // パースに失敗した場合はフィルター無しとして扱う (毎tick落とさない)
        if (filter == null) return true;
        return filter.filter(itemStack) == FILTER_WHITELIST.get();
    }

    private static IFilter parseFilter(String jsonConfig){
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
}
