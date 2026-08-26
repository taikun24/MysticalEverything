package jp.main.taikun.mysticaleverything.mixin;

import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class MysticalEverythingMixinPlugin implements IMixinConfigPlugin {

    /** 必須依存しか触らない mixin のパッケージ。常に適用する。 */
    private static final String ALWAYS_APPLIED_PACKAGE = "jp.main.taikun.mysticaleverything.mixin.main";

    /** 任意依存 mixin のパッケージ -> 適用に必要な mod id (全て揃っている場合のみ適用)。 */
    private static final Map<String, List<String>> OPTIONAL_MIXIN_PACKAGES = Map.of(
            "jp.main.taikun.mysticaleverything.mixin.botanypots", List.of("botanypots"),
            // グリーンハウスは astral_mekanism の機械だが、レシピの器は botanypots のもの
            "jp.main.taikun.mysticaleverything.mixin.astralmekanism", List.of("botanypots", "astral_mekanism"),
            // Planting Station / Planting Factory は Mekanism:More Machine の機械
            "jp.main.taikun.mysticaleverything.mixin.mekmm", List.of("mekmm")
    );

    private static final Logger LOGGER = LogManager.getLogger("mysticaleverything");

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith(ALWAYS_APPLIED_PACKAGE)) {
            return true;
        }
        for (Map.Entry<String, List<String>> entry : OPTIONAL_MIXIN_PACKAGES.entrySet()) {
            if (!mixinClassName.startsWith(entry.getKey())) {
                continue;
            }
            for (String modId : entry.getValue()) {
                if (!isModLoaded(modId)) {
                    LOGGER.info("Skipping mixin {} (target {}): required mod {} is not loaded.",
                            mixinClassName, targetClassName, modId);
                    return false;
                }
            }
            return true;
        }
        LOGGER.warn("Skipping mixin {}: it belongs to no known mixin package.", mixinClassName);
        return false;
    }

    private static boolean isModLoaded(String modId) {
        return FMLLoader.getLoadingModList().getMods().stream()
                .anyMatch(mod -> mod.getModId().equals(modId));
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
