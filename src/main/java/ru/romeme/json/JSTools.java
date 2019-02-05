package ru.romeme.json;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;

final class JSTools {

    private JSTools() {}

    static <K> Optional<String> encode(K e) {
        if (Objects.isNull(e))
            return Optional.of("null");
        else if (e instanceof Number || e instanceof Boolean)
            return Optional.of(String.valueOf(e));
        else if (e instanceof CharSequence)
            return Optional.of(decode(String.valueOf(e)));
        else if (e instanceof List<?>)
            return ((List<?>) e).stream().collect(new JSArrayCollector<>());
        else if (e instanceof Map<?, ?>)
            return ((Map<?, ?>) e).entrySet().stream().collect(new JSMapCollector<>());
        else
            return Optional.empty();
    }

    private static String decode(String input) {
        StringBuilder builder = new StringBuilder();
        builder.append("\"");

        for (char ch : input.toCharArray()) {
            if (ch <= 127)
                builder.append(ch);
            else {
                builder.append("\\u");
                for (int round = 12; round >= 0; round -= 4)
                    switch ((ch >> round) & 0xF) {
                        case 0x0:
                            builder.append("0");
                            break;
                        case 0x1:
                            builder.append('1');
                            break;
                        case 0x2:
                            builder.append('2');
                            break;
                        case 0x3:
                            builder.append('3');
                            break;
                        case 0x4:
                            builder.append('4');
                            break;
                        case 0x5:
                            builder.append('5');
                            break;
                        case 0x6:
                            builder.append('6');
                            break;
                        case 0x7:
                            builder.append('7');
                            break;
                        case 0x8:
                            builder.append('8');
                            break;
                        case 0x9:
                            builder.append('9');
                            break;
                        case 0xA:
                            builder.append('A');
                            break;
                        case 0xB:
                            builder.append('B');
                            break;
                        case 0xC:
                            builder.append('C');
                            break;
                        case 0xD:
                            builder.append('D');
                            break;
                        case 0xE:
                            builder.append("E");
                            break;
                        case 0xF:
                            builder.append("F");
                            break;
                    }
            }
        }

        builder.append("\"");
        return builder.toString();
    }

    public static Function<Map<String, String>, Optional<String>> key(String key) {
        return map -> Optional.ofNullable(map.get(key));
    }

    public static <T> Function<List<T>, Optional<T>> index(int index) {
        return arr ->
                arr == null || arr.size() < index || index < 0
                        ? Optional.empty()
                        : Optional.ofNullable(arr.get(index));
    }

    public static <T> BinaryOperator<Optional<List<T>>> join() {
        return (accOF, accOS) ->
                accOF.flatMap(accF ->
                        accOS.map(accS -> new ArrayList<>(accS) {{ addAll(accF); }})
                );
    }

    public static Optional<Float> floatOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+(.[0-9]+([Ee][+\\-][0-9]+)?)?$"))
                .flatMap(can -> {
                    try {
                        return Optional.of(Float.valueOf(can));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    public static Optional<Double> doubleOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+(.[0-9]+([Ee][+\\-][0-9]+)?)?$"))
                .flatMap(can -> {
                    try {
                        return Optional.of(Double.valueOf(can));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    public static Optional<Integer> integerOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+$"))
                .flatMap(can -> {
                    try {
                        return Optional.of(Integer.valueOf(can));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    public static Optional<Long> longOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+$"))
                .flatMap(can -> {
                    try {
                        return Optional.of(Long.valueOf(can));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    public static Optional<Short> shortOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+$"))
                .flatMap(can -> {
                    try {
                        return Optional.of(Short.valueOf(can));
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                        return Optional.empty();
                    }
                });
    }

    public static Optional<Boolean> booleanOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^true|false$"))
                .map(Boolean::valueOf);
    }
}
