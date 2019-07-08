package ru.romeme.json;

import java.util.ArrayList;
import java.util.List;

public final class JSArray extends Parser {

    public static List<String> parse(String input) {
        List<String> rs = read(input, new MapAccumulator(), Parser.ARRAY);
        if (rs == null)
            throw new JSParseException();

        List<String> arr = new ArrayList<>();
        for (String vv : rs) {
            String value =
                    String.valueOf(vv).matches("^\".+\"$")
                            ? decode(vv)
                            : vv;
            arr.add(value);
        }

        return arr;
    }

    public static String present(List<?> arr) {
        List<String> rs = new ArrayList<>();

        for (Object en : arr) {
            String vv = encode(en);
            if (vv == null)
                throw new JSPresentException();
            rs.add(vv);
        }

        return String.format("[ %s ]", join(", ", rs));
    }
}