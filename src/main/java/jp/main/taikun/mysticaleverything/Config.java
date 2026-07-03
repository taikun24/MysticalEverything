package jp.main.taikun.mysticaleverything;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue DISABLE_NBT;

    static{
        BUILDER.push("Config");
        DISABLE_NBT = BUILDER.comment("NBTも含めて作物にするか？(true→いいえ)").define("disableNBT", true);
        BUILDER.pop();
        CONFIG_SPEC = BUILDER.build();
    }
}
