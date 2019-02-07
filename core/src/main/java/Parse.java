import java.util.Optional;

public final class Parse {

    public static Optional<Float> floatOP(String in) {
        return Optional.ofNullable(in)
                .filter(can -> can.matches("^-?[0-9]+(.[0-9]+([Ee](\\+?|-)[0-9]+)?)?$"))
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
                .filter(can -> can.matches("^-?[0-9]+(.[0-9]+([Ee](\\+?|-)[0-9]+)?)?$"))
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
