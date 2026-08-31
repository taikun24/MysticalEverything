package jp.main.taikun.mysticaleverything;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * NBT から組み立て直すと重いものを覚えておくための、上限付きキャッシュ。
 * <p>
 * 中身の種類はワールド内で実際に使われるぶんしか増えないので上限は小さくてよい。
 * 描画スレッドとサーバースレッドの両方から触るため同期する
 * (アクセス順 LRU は {@code get} でも構造を書き換えるので、読みにも排他が要る)。
 */
public final class Caches {

    /**
     * 作った LRU の一覧。ワールドを離れたらまとめて捨てる
     * ({@link #clearAll} の呼び出し側が、任意 Mod 向けのクラスを読まずに済むように
     * ここで集約している)。
     */
    private static final List<Map<?, ?>> ALL = new CopyOnWriteArrayList<>();

    private Caches() {
    }

    public static <K, V> Map<K, V> lru(int capacity) {
        Map<K, V> map = Collections.synchronizedMap(new LinkedHashMap<>(Math.min(capacity, 64), 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        });
        ALL.add(map);
        return map;
    }

    public static void clearAll() {
        for (Map<?, ?> map : ALL) {
            map.clear();
        }
    }
}
