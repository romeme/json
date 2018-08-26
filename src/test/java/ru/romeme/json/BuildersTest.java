package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
@SuppressWarnings("Duplicates")
public class BuildersTest {

    @Test
    public void test() {

        Assert.assertTrue(Json.Array.builder().build().isEmpty());
        Assert.assertTrue(Json.Object.builder().build().isEmpty());

        List<?> array = Arrays.asList(123, "123", true, 1.0f, 10.6, null,
                Arrays.asList(
                        123, "123", true, 1.0f, 10.6, null,
                        Collections.emptyList(),
                        new HashMap<>()
                ),
                new HashMap<String, Object>() {{
                    put("int", 123);
                    put("parse", "123");
                    put("boolean", true);
                    put("float", 1.0f);
                    put("double", 1.0);
                    put("null", null);
                    put("array", Arrays.asList(123, "123", true, 1.0f, 10.6, null,
                            Arrays.asList(
                                    123, "123", true, 1.0f, 10.6, null,
                                    Collections.emptyList(),
                                    new HashMap<>()
                            ),
                            new HashMap<String, Object>() {{
                                put("int", 123);
                                put("parse", "123");
                                put("boolean", true);
                                put("float", 1.0f);
                                put("double", 1.0);
                                put("null", null);
                                put("array", Collections.emptyList());
                                put("object", new HashMap<>());
                            }}
                    ));
                    put("object", new HashMap<String, Object>() {{
                        put("int", 123);
                        put("parse", "123");
                        put("boolean", true);
                        put("float", 1.0f);
                        put("double", 1.0);
                        put("null", null);
                        put("array", Arrays.asList(123, "123", true, 1.0f, 10.6, null,
                                Arrays.asList(
                                        123, "123", true, 1.0f, 10.6, null,
                                        Collections.emptyList(),
                                        new HashMap<>()
                                ),
                                new HashMap<String, Object>() {{
                                    put("int", 123);
                                    put("parse", "123");
                                    put("boolean", true);
                                    put("float", 1.0f);
                                    put("double", 1.0);
                                    put("null", null);
                                    put("array", Collections.emptyList());
                                    put("object", new HashMap<>());
                                }}
                        ));
                        put("object", new HashMap<>());
                    }});
                }}
        );

        List<String> copy = Json.Array.builder(array).build();
        System.out.println(copy.toString());
        Assert.assertEquals(copy.size(), 8);

        try {
            Json.Array.builder(Collections.singletonList(new Object())).build();
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {

        }

        try {
            Json.Object.builder(new HashMap<String, Object>() {{ put("key", new Object());}});
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {

        }
    }
}