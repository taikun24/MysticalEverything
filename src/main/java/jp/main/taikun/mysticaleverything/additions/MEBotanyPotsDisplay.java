package jp.main.taikun.mysticaleverything.additions;

import net.darkhax.bookshelf.common.api.function.CachedSupplier;
import net.darkhax.botanypots.common.api.data.display.types.Display;
import net.darkhax.botanypots.common.api.data.display.types.DisplayType;
import net.darkhax.botanypots.common.impl.BotanyPotsMod;
import net.darkhax.botanypots.common.impl.data.display.types.AgingDisplayState;
import net.darkhax.botanypots.common.impl.data.display.types.PhasedDisplayState;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MEBotanyPotsDisplay extends PhasedDisplayState {
    public static final ResourceLocation TYPE_ID = BotanyPotsMod.id("me_custom");
    public static final CachedSupplier<DisplayType<MEBotanyPotsDisplay>> TYPE = CachedSupplier.cache(() -> DisplayType.get(TYPE_ID));
    @Override
    public List<Display> getDisplayPhases() {
        return List.of();
    }

    @Override
    public DisplayType<?> getType() {
        return TYPE.get();
    }
}
