package ru.romeme.json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JSMapCollector<T> implements Collector<Map.Entry<?, ?>, Map<Object, Object>, Optional<String>> {

    @Override
    public Supplier<Map<Object, Object>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<Map<Object, Object>, Map.Entry<?, ?>> accumulator() {
        return (map, en) -> map.put(en.getKey(), en.getValue());
    }

    @Override
    public BinaryOperator<Map<Object, Object>> combiner() {
        return (mapF, mapS) -> new HashMap<Object, Object>(mapF) {{ putAll(mapS); }};
    }

    @Override
    public Function<Map<Object, Object>, Optional<String>> finisher() {
        return acc -> acc.entrySet().stream()
                .reduce(
                        Optional.of(new HashMap<String, String>()),
                        (mapOP, en) ->
                                mapOP.flatMap(map ->
                                        en.getKey() instanceof CharSequence
                                                ?
                                                JSTools.encode(en.getKey())
                                                        .flatMap(key ->
                                                                JSTools.encode(en.getValue())
                                                                        .map(vv -> new HashMap<String, String>(map) {{
                                                                            put(key, vv);
                                                                        }})
                                                        )

                                                : Optional.empty()
                                ),
                        (mapOF, mapOS) ->
                                mapOF.flatMap(accF ->
                                        mapOS.map(accS -> new HashMap<String, String>(accF) {{ putAll(accS); }}
                                        )
                                )
                )
                .map(map -> String.format("{ %s }",
                        acc.entrySet()
                                .stream()
                                .map(en -> String.format(" %s : %s", en.getKey(), en.getValue()))
                                .collect(Collectors.joining(" , "))
                ));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(
                EnumSet.of(
                        Characteristics.CONCURRENT,
                        Characteristics.UNORDERED ));
    }
}
