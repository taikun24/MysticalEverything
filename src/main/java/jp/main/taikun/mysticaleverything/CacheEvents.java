package jp.main.taikun.mysticaleverything;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * ワールドを離れたらキャッシュを捨てる。
 * <p>
 * 中身の解決結果はレジストリを引いて作るので、ワールドを跨いで持ち越さない。
 */
@EventBusSubscriber(modid = Mysticaleverything.MODID)
public class CacheEvents {

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        Caches.clearAll();
    }
}
