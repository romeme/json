package ru.romeme.json.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("UnnecessaryBoxing")
public class EncodeTest {

    @Test
    public void stringCheck() {

        Assert.assertEquals("\"\\u0123\\u4567\\u8989\\uAABB\\uCCDD\\uEEFF\\n\\r\\f\\t\\b\\\"\\\\\"",
                Json.encode("\u0123\u4567\u8989\uaAbB\ucCdD\ueEfF\n\r\f\t\b\"\\").orElse(""));
    }

    @Test
    public void numberCheck() {

        Assert.assertEquals("1", Json.encode(1).orElse(""));
        Assert.assertEquals("1.0", Json.encode(Float.valueOf(1)).orElse(""));
        Assert.assertEquals("1.0", Json.encode(Double.valueOf(1)).orElse(""));

        Assert.assertEquals("1.1", Json.encode(1.1F).orElse(""));
        Assert.assertEquals("1.000001", Json.encode(1.000001).orElse(""));
        Assert.assertEquals("15", Json.encode(0xF).orElse(""));
        Assert.assertEquals("1.1E-12", Json.encode(1.1E-12).orElse(""));
        Assert.assertEquals("1.0E12", Json.encode(1.0E+12).orElse(""));
    }

    @Test
    public void nullCheck() {
        Assert.assertEquals("null", Json.encode(null).orElse(""));
    }

    @Test
    public void boolCheck() {
        Assert.assertEquals("true", Json.encode(true).orElse(""));
        Assert.assertEquals("false", Json.encode(false).orElse(""));
        Assert.assertEquals("true", Json.encode(Boolean.TRUE).orElse(""));
        Assert.assertEquals("false", Json.encode(Boolean.FALSE).orElse(""));
    }

    @Test
    public void arrayCheck() {
        Assert.assertEquals("[ 1, 2 ]",
                Json.encode(Arrays.asList(1, 2)).orElse(""));

        Assert.assertEquals("[ 1, null, true, false, true, false ]",
                Json.encode(Arrays.asList(1, null, true, false, Boolean.TRUE, Boolean.FALSE)).orElse(""));

        Assert.assertEquals("[ \"\" ]",
                Json.encode(Collections.singletonList("")).orElse(""));

        Assert.assertEquals("[ \"123\" ]",
                Json.encode(Collections.singletonList("123")).orElse(""));

        Assert.assertEquals("[ { \"1\" : 12 } ]",
                Json.encode(Collections.singletonList(UnmodifiableMap.builder().put("1", 12).get())).orElse(""));

        Assert.assertEquals("[ [ 1, 2, 3 ] ]",
                Json.encode(Collections.singletonList(Arrays.asList(1, 2, 3))).orElse(""));
    }

    @Test
    public void objectCheck() {
        Assert.assertEquals("{ \"int\" : 12 }",
                Json.encode(UnmodifiableMap.builder().put("int", 12).get()).orElse(""));

        Assert.assertEquals("{ \"float\" : 12.0 }",
                Json.encode(UnmodifiableMap.builder().put("float", 12f).get()).orElse(""));

        Assert.assertEquals("{ \"double\" : 12.0 }",
                Json.encode(UnmodifiableMap.builder().put("double", 12.0).get()).orElse(""));

        Assert.assertEquals("{ \"object\" : {  } }",
                Json.encode(UnmodifiableMap.builder().put("object", new HashMap<>()).get()).orElse(""));

        Assert.assertEquals("{ \"object\" : { \"double\" : 1.0, \"float\" : 1.0, \"string\" : \"sub-vv\", \"sub-object\" : { \"null\" : null, \"double\" : 1.0, \"float\" : 1.0, \"string\" : \"sub-vv\", \"int\" : 1, \"object\" : {  }, \"array\" : [  ] }, \"int\" : 1, \"array\" : [ 1, null, 1.0, 1.0, [  ], {  } ] } }",
                Json.encode(
                        UnmodifiableMap.builder().put("object",
                                UnmodifiableMap.builder()
                                        .put("int", 1)
                                        .put("float", 1.0f)
                                        .put("double", 1.0)
                                        .put("string", "sub-vv")
                                        .put("array", Arrays.asList(1, null, 1.0f, 1.0, Collections.emptyList(), Collections.emptyMap()))
                                        .put("sub-object",
                                                UnmodifiableMap.builder()
                                                        .put("int", 1)
                                                        .put("float", 1.0f)
                                                        .put("double", 1.0)
                                                        .put("null", null)
                                                        .put("string", "sub-vv")
                                                        .put("array", Collections.emptyList())
                                                        .put("object", Collections.emptyMap())
                                                        .get()
                                        )
                                        .get()
                        )
                                .get()
                ).orElse(""));

        Assert.assertEquals("{ \"array\" : [  ] }",
                Json.encode(UnmodifiableMap.builder().put("array", Collections.emptyList()).get()).orElse(""));

        Assert.assertEquals("{ \"array\" : [ 1, null, \"\", {  }, [  ] ] }",
                Json.encode(UnmodifiableMap.builder().put("array", Arrays.asList(1, null, "", Collections.emptyMap(), Collections.emptyList())).get()).orElse(""));
    }

    @Test
    public void incorrectTest() {
        Assert.assertTrue(Json.encode(UUID.randomUUID()).isEmpty());

        Assert.assertTrue(Json.encode(Arrays.asList(1, null, UUID.randomUUID())).isEmpty());

        Assert.assertTrue(Json.encode(Arrays.asList(1, null, Arrays.asList(1, null, UUID.randomUUID()))).isEmpty());

        Assert.assertTrue(Json.encode(
                new HashMap<String, Object>() {{
                    put("int", 123);
                    put("null", null);
                    put("key",
                            new HashMap<String, Object>() {{
                                put("int", 123);
                                put("key", UUID.randomUUID());
                            }});
                }}
        ).isEmpty());

    }

}