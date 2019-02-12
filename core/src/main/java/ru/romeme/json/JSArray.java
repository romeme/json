package ru.romeme.json;

import java.util.ArrayList;
import java.util.List;

public final class JSArray extends Parser {

    public static List<String> parse(String input) {
        List<String> rs = read(input, new MapAccumulator(), Parser.OBJECT);
        if (rs == null)
            return null;

        List<String> arr = new ArrayList<>();
        for (String vv : rs) {

            if (vv == null)
                return null;
            String value =
                    vv.matches("^\".+\"$")
                            ? decode(vv)
                            : vv;

            if (value == null)
                return null;

            arr.add(value);
        }

        return arr;
    }

    public static String present(List<?> arr) {
        List<String> rs = new ArrayList<>();

        for (Object en : arr) {
            String vv = encode(en);
            if (vv == null)
                return null;
            rs.add(vv);
        }

        return String.format("[ %s ]", join(", ", rs));
    }
}