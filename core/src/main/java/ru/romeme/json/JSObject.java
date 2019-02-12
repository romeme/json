package ru.romeme.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class JSObject extends Parser {

    public static Map<String, String> parse(String input) {
        if (input == null)
            return null;

        List<String> rs = read(input, new MapAccumulator(), Parser.OBJECT);

        if (rs == null || rs.size() % 2 != 0)
            return null;

        Map<String, String> map = new HashMap<>();
        for (int index = 0; index < rs.size() / 2; index++) {
            String kk = rs.get(index * 2);
            String vv = rs.get(index * 2 + 1);

            String key = decode(kk);
            String value =
                    vv.matches("^\".+\"$")
                            ? decode(vv)
                            : vv;

            map.put(key, value);
        }

        return map;
    }

    public static String present(Map<String, ?> map) {
        List<String> pairs = new ArrayList<>();

        for (Map.Entry<String, ?> en : map.entrySet()) {
            String key = encode(en.getKey());
            String vv = encode(en.getValue());
            if (key == null || vv == null)
                return null;
            pairs.add(String.format("%s : %s", key, vv));
        }

        return String.format("{ %s }", join(", ", pairs));
    }
}
