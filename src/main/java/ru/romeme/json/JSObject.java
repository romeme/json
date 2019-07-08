package ru.romeme.json;

import java.util.*;
import java.util.stream.Collectors;

public final class JSObject extends Parser {

    public static Map<String, String> parse(String input) throws JSParseException {
        if (input == null)
            throw new JSParseException();

        List<String> rs = read(input, new MapAccumulator(), Parser.OBJECT);

        if (rs == null || rs.size() % 2 != 0)
            throw new JSParseException();

        Map<String, String> map = new HashMap<>();
        for (int index = 0; index < rs.size() / 2; index++) {
            String kk = rs.get(index * 2);
            String vv = rs.get(index * 2 + 1);

            if (vv == null)
                continue;

            String key = decode(kk);
            String value =
                    vv.matches("^\".*\"$")
                            ? decode(vv)
                            : vv;

            map.put(key, value);
        }

        return map;
    }

    public static String present(Map<String, ?> map) throws JSPresentException {
        List<String> pairs = new ArrayList<>();

        for (Map.Entry<String, ?> en :
                map.entrySet().stream()
                        .sorted(Comparator.comparing(Map.Entry::getKey))
                        .collect(Collectors.toList())
        ) {
            String key = encode(en.getKey());
            String vv = encode(en.getValue());
            if (key == null || vv == null)
                throw new JSPresentException();
            pairs.add(String.format("%s : %s", key, vv));
        }

        return String.format("{ %s }", join(", ", pairs));
    }
}
