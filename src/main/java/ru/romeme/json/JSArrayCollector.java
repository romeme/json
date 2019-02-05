package ru.romeme.json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class JSArrayCollector<T> implements Collector<T, List<T>, Optional<String>> {

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
                                        JSTools.encode(e).map(vv -> new ArrayList<String>(acc) {{ add(vv); }}
                                        )
                                ),
                        (accOF, accOS) ->
                                accOF.flatMap(accF ->
                                        accOS.map(accS -> new ArrayList<String>(accF) {{ addAll(accS); }}
                                        )
                                )
                )
                .map(acc -> String.format("[ %s ]", String.join(",", acc)));
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(
                EnumSet.of(
                        Collector.Characteristics.CONCURRENT,
                        Collector.Characteristics.UNORDERED ));
    }
}
