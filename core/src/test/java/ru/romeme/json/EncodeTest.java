package ru.romeme.json;

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
                Parser.encode("\u0123\u4567\u8989\uaAbB\ucCdD\ueEfF\n\r\f\t\b\"\\"));
    }

    @Test
    public void numberCheck() {

        Assert.assertEquals("1", Parser.encode(1));
        Assert.assertEquals("1.0", Parser.encode(Float.valueOf(1)));
        Assert.assertEquals("1.0", Parser.encode(Double.valueOf(1)));

        Assert.assertEquals("1.1", Parser.encode(1.1F));
        Assert.assertEquals("1.000001", Parser.encode(1.000001));
        Assert.assertEquals("15", Parser.encode(0xF));
        Assert.assertEquals("1.1E-12", Parser.encode(1.1E-12));
        Assert.assertEquals("1.0E12", Parser.encode(1.0E+12));
    }

    @Test
    public void nullCheck() {
        Assert.assertEquals("null", Parser.encode(null));
    }

    @Test
    public void boolCheck() {
        Assert.assertEquals("true", Parser.encode(true));
        Assert.assertEquals("false", Parser.encode(false));
        Assert.assertEquals("true", Parser.encode(Boolean.TRUE));
        Assert.assertEquals("false", Parser.encode(Boolean.FALSE));
    }

    @Test
    public void arrayCheck() {
        Assert.assertEquals("[ 1, 2 ]",
                Parser.encode(Arrays.asList(1, 2)));

        Assert.assertEquals("[ 1, null, true, false, true, false ]",
                Parser.encode(Arrays.asList(1, null, true, false, Boolean.TRUE, Boolean.FALSE)));

        Assert.assertEquals("[ \"\" ]",
                Parser.encode(Collections.singletonList("")));

        Assert.assertEquals("[ \"123\" ]",
                Parser.encode(Collections.singletonList("123")));

        Assert.assertEquals("[ { \"1\" : 12 } ]",
                Parser.encode(Collections.singletonList(UnmodifiableMap.builder().put("1", 12).build())));

        Assert.assertEquals("[ [ 1, 2, 3 ] ]",
                Parser.encode(Collections.singletonList(Arrays.asList(1, 2, 3))));
    }

    @Test
    public void objectCheck() {
        Assert.assertEquals("{ \"int\" : 12 }",
                Parser.encode(UnmodifiableMap.builder().put("int", 12).build()));

        Assert.assertEquals("{ \"float\" : 12.0 }",
                Parser.encode(UnmodifiableMap.builder().put("float", 12f).build()));

        Assert.assertEquals("{ \"double\" : 12.0 }",
                Parser.encode(UnmodifiableMap.builder().put("double", 12.0).build()));

        Assert.assertEquals("{ \"object\" : {  } }",
                Parser.encode(UnmodifiableMap.builder().put("object", new HashMap<>()).build()));

        Assert.assertEquals("{ \"object\" : { \"sub-object\" : { \"int\" : 1 } } }",
                Parser.encode(
                        UnmodifiableMap.builder().put("object",
                                UnmodifiableMap.builder()
                                         .put("sub-object",
                                                UnmodifiableMap.builder()
                                                        .put("int", 1)
                                                        .build()
                                        )
                                        .build()
                        )
                                .build()
                ));

        Assert.assertEquals("{ \"array\" : [  ] }",
                Parser.encode(UnmodifiableMap.builder().put("array", Collections.emptyList()).build()));

        Assert.assertEquals("{ \"array\" : [ 1, null, \"\", {  }, [  ] ] }",
                Parser.encode(UnmodifiableMap.builder().put("array", Arrays.asList(1, null, "", Collections.emptyMap(), Collections.emptyList())).build()));
    }

    @Test
    public void incorrectTest() {
        Assert.assertNull(Parser.encode(UUID.randomUUID()));

        Assert.assertNull(Parser.encode(Arrays.asList(1, null, UUID.randomUUID())));

        Assert.assertNull(Parser.encode(Arrays.asList(1, null, Arrays.asList(1, null, UUID.randomUUID()))));

        Assert.assertNull(Parser.encode(
                new HashMap<String, Object>() {{
                    put("int", 123);
                    put("null", null);
                    put("key",
                            new HashMap<String, Object>() {{
                                put("int", 123);
                                put("key", UUID.randomUUID());
                            }});
                }}
        ));

    }

}