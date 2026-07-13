package jp.main.taikun.mysticaleverything;


import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeConfig;

public class Config {
    public static final ModConfigSpec CONFIG_SPEC;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DISABLE_NBT;

    static{
        BUILDER.push("Config");
        DISABLE_NBT = BUILDER.comment("NBTも含めて作物にするか？(true→いいえ)").define("disableNBT", false);
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
