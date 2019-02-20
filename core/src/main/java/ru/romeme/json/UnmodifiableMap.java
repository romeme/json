package ru.romeme.json;

import java.util.HashMap;
import java.util.Map;

public class UnmodifiableMap {

    public static <KK, VV> Builder<KK, VV> builder() {

        class Shadow<K, V> implements Builder<K, V> {

            private final Map<K, V> map;

            private Shadow(Map<K, V> map) {this.map = map;}

            @Override
            public Builder<K, V> put(final K key, final V vv) {
                return new Shadow<>(new HashMap<K, V>(map) {{ put(key, vv); }});
            }

            @Override
            public Map<K, V> build() {
                return new HashMap<>(map);
            }

        }

        return new Shadow<>(new HashMap<KK, VV>());
    }

    public interface Builder<K, V> {

        Builder<K, V> put(K key, V vv);

        Map<K, V> build();
    }
}
