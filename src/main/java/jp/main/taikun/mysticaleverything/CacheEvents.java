package jp.main.taikun.mysticaleverything;

import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * ワールドを離れたらキャッシュを捨てる。
 * <p>
 * 解決結果自体はワールドに依存しないが、遊んでいるあいだ触ったアイテムを
 * ずっと持ち続ける意味は無いので、区切りのいいところで手放す。
 */
@Mod.EventBusSubscriber(modid = Mysticaleverything.MODID)
public class CacheEvents {

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        Caches.clearAll();
    }
}
