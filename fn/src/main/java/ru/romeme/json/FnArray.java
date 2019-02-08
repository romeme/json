package ru.romeme.json;

import ru.romeme.json.Parser;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class FnArray extends Parser {

    private static Collector<String, List<String>, Optional<List<String>>> trimmer() {
        return new Collector<String, List<String>, Optional<List<String>>>() {

            @Override
            public Supplier<List<String>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<String>, String> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<String>> combiner() {
                return (accF, accS) -> new ArrayList<String>(accF) {{ addAll(accS);}};
            }

            @Override
            public Function<List<String>, Optional<List<String>>> finisher() {
                return arr -> arr.stream()
                        .reduce(
                                Optional.of(new ArrayList<String>()),
                                (accOP, e) ->
                                        accOP.flatMap(acc ->
                                                Optional.ofNullable(decode(e))
                                                        .map(vv -> new ArrayList<String>(acc) {{ add(vv); }})
                                        ),
                                (accOF, accOS) ->
                                        accOF.flatMap(accF ->
                                                accOS.map(accS -> new ArrayList<String>(accS) {{ addAll(accF); }})
                                        )
                        );
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(
                        EnumSet.of(
                                Collector.Characteristics.CONCURRENT,
                                Collector.Characteristics.UNORDERED));
            }
        };
    }

    public static <T> Collector<T, List<T>, Optional<String>> collector() {
        return new Collector<T, List<T>, Optional<String>>() {

            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, T> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (accF, accS) -> new ArrayList<T>(accF) {{ addAll(accS);}};
            }

            @Override
            public Function<List<T>, Optional<String>> finisher() {
                //because accumulator is BiConsumer<List<T>, T>, not a BiFunction<...> =(
                return arr -> arr.stream()
                        .reduce(
                                Optional.of(new ArrayList<String>()),
                                (accOP, e) ->
                                        accOP.flatMap(acc ->
                                                Optional.ofNullable(encode(e))
                                                        .map(vv -> new ArrayList<String>(acc) {{ add(vv); }})
                                        ),
                                (accOF, accOS) ->
                                        accOF.flatMap(accF ->
                                                accOS.map(accS -> new ArrayList<String>(accS) {{ addAll(accF); }})
                                        )
                        )
                        .map(acc -> String.format("[ %s ]", String.join(", ", acc)));
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return Collections.unmodifiableSet(
                        EnumSet.of(
                                Collector.Characteristics.CONCURRENT,
                                Collector.Characteristics.UNORDERED));
            }
        };
    }

    public Optional<List<String>> parse(String input) {
        return Optional.ofNullable(read(input, new ArrayAccumulator(), ARRAY))
                .flatMap(map -> map.stream().collect(trimmer()));
    }

}