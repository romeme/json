package ru.romeme.json;

import ru.romeme.json.Parser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FnObject extends Parser {

    public static Optional<Map<String, String>> parse(String input) {
        return Optional.ofNullable(Parser.read(input, new MapAccumulator(), OBJECT))
                .flatMap(arr ->
                        Optional.of(arr)
                                .filter(mp -> mp.size() % 2 == 0)
                                .flatMap(mp ->
                                        IntStream.range(0, mp.size() / 2)
                                                .boxed()
                                                .collect(Collectors.toMap(idx -> mp.get(idx * 2), idx -> mp.get(idx * 2 + 1)))
                                                .entrySet()
                                                .stream()
                                                .collect(trimmer())
                                )
                );

    }

    static Collector<Map.Entry<String, String>, Map<String, String>, Optional<Map<String, String>>> trimmer() {
        return new Collector<Map.Entry<String, String>, Map<String, String>, Optional<Map<String, String>>>() {

            @Override
            public Supplier<Map<String, String>> supplier() {
                return HashMap::new;
            }

            @Override
            public BiConsumer<Map<String, String>, Map.Entry<String, String>> accumulator() {
                return (map, en) -> map.put(en.getKey(), en.getValue());
            }

            @Override
            public BinaryOperator<Map<String, String>> combiner() {
                return (mapF, mapS) -> new HashMap<String, String>(mapF) {{ putAll(mapS); }};
            }

            @Override
            public Function<Map<String, String>, Optional<Map<String, String>>> finisher() {
                return acc -> acc.entrySet().stream()
                        .reduce(
                                Optional.of(new HashMap<String, String>()),
                                (mapOP, en) ->
                                        mapOP.flatMap(map ->
                                                Optional.ofNullable(decode(en.getKey()))
                                                        .flatMap(key ->
                                                                en.getValue().matches("^\".+\"$")
                                                                        ?
                                                                        Optional.ofNullable(decode(en.getValue()))
                                                                                .map(vv ->
                                                                                        new HashMap<String, String>(map) {{
                                                                                            put(key, vv);
                                                                                        }}
                                                                                )
                                                                        :
                                                                        Optional.of(
                                                                                new HashMap<String, String>(map) {{
                                                                                    put(key, en.getValue());
                                                                                }}
                                                                        )
                                                        )
                                        ),
                                (mapOF, mapOS) ->
                                        mapOF.flatMap(accF ->
                                                mapOS.map(accS ->
                                                        new HashMap<String, String>(accF) {{
                                                            putAll(accS);
                                                        }}
                                                )
                                        )
                        );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(
                        EnumSet.of(
                                Characteristics.CONCURRENT,
                                Characteristics.UNORDERED));
            }
        };
    }

    public static Collector<Map.Entry<?, ?>, Map<Object, Object>, Optional<String>> collector() {
        return new Collector<Map.Entry<?, ?>, Map<Object, Object>, Optional<String>>() {

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
                                                        Optional.ofNullable(encode(en.getKey()))
                                                                .flatMap(key ->
                                                                        Optional.ofNullable(encode(en.getValue()))
                                                                                .map(vv -> new HashMap<String, String>(map) {{
                                                                                    put(key, vv);
                                                                                }})
                                                                )

                                                        : Optional.empty()
                                        ),
                                (mapOF, mapOS) ->
                                        mapOF.flatMap(accF ->
                                                mapOS.map(accS ->
                                                        new HashMap<String, String>(accF) {{ putAll(accS); }}
                                                )
                                        )
                        )
                        .map(map -> String.format("{ %s }",
                                map.entrySet()
                                        .stream()
                                        .map(en ->
                                                String.format("%s : %s", en.getKey(), en.getValue()))
                                        .collect(Collectors.joining(", "))
                        ));
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.unmodifiableSet(
                        EnumSet.of(
                                Characteristics.CONCURRENT,
                                Characteristics.UNORDERED));
            }
        };
    }

}
