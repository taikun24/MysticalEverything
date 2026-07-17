package jp.main.taikun.mysticaleverything.mixin;

import net.neoforged.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public class MysticalEverythingMixinPlugin implements IMixinConfigPlugin {
    private static final Map<String, String> mixinPackageMap = Map.of(
            "jp.main.taikun.mysticaleverything.mixin.botanypots", "botanypots"
    );
    private static final Logger logger = Logger.getLogger(MysticalEverythingMixinPlugin.class.getName());
    @Override

    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }
    private static boolean checkMod(String modId) {
        return LoadingModList.get().getMods().stream()
                .anyMatch(mod -> mod.getModId().equals(modId));

    }
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("jp.main.taikun.mysticaleverything.mixin.main")) return true;
        AtomicBoolean result = new AtomicBoolean(false);
        mixinPackageMap.forEach((mixinPackage, modId) -> {
            if (mixinClassName.startsWith(mixinPackage)) {
                if (!MysticalEverythingMixinPlugin.checkMod(modId)) {
                    logger.info("Skipping mixin " + mixinClassName + " for target class " + targetClassName + " because the required mod " + modId + " is not loaded.");
                } else {
                    result.set(true);
                }
            }
        });
        if (!result.get()) { logger.info("Skipped. ( " + mixinClassName + ")");  return false; }
        else return true;
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
