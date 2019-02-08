package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ObjectParsingTest {

    private static final HashMap<String, String> EMPTY_MAP = new HashMap<>();
    private static final HashMap<String, String> NOT_EMPTY_MAP = new HashMap<String, String>() {{
        put("some-key", String.valueOf(UUID.randomUUID()));
    }};

    private static Map<String, String> map(final String key, final String vv) {
        return new HashMap<String, String>() {{ put(key, vv); }};
    }

    private static Map<String, String> map(final String keyA, final String vvA, final String keyB, final String vvB) {
        return new HashMap<String, String>() {{
            put(keyA, vvA);
            put(keyB, vvB);
        }};
    }

    @Test
    public void empty() {
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{ }"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse(" {}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{} "));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{\r\n\t\b\f}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{\r \n \t \b \f}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{}\r\n\t\b\f"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{} \r \n \t \b \f"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r\n\t\b\f{}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r \n \t \b \f {}"));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse(" \r \n \t \b \f {} \r \n \t \b \f "));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r\n\t\b\f{}\r\n\t\b\f"));

    }

    @Test
    public void singleKey() {

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\":1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{ \"key\":1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\" :1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\r\n\t\b\f\"key\":1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\"\r\n\t\b\f:1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\r\n\t\b\f\"key\"\r\n\t\b\f:1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("\r\n\t\b\f{\"key\":1}"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\":1}\r\n\t\b\f"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("\r\n\t\b\f{\"key\":1}\r\n\t\b\f"));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse(" \r \n \t \b \f {\"key\":1} \r \n \t \b \f "));

        Assert.assertEquals(map("qwertyuiop[]asdfghjkl;'\\`zxcvbnm,./", "1"),
                JSObject.parse("{\"qwertyuiop[]asdfghjkl;'\\\\`zxcvbnm,./\":1}"));

        Assert.assertEquals(map("§1234567890-=", "1"),
                JSObject.parse("{\"§1234567890-=\":1}"));

        Assert.assertEquals(map("!@#$%^&*()_+", "1"),
                JSObject.parse("{\"!@#$%^&*()_+\":1}"));

        Assert.assertEquals(map("QWERTYUIOP{}ASDFGHJKL:\"\\|~ZXCVBNM<>?", "1"),
                JSObject.parse("{\"QWERTYUIOP{}ASDFGHJKL:\\\"\\\\|~ZXCVBNM<>?\":1}"));

        Assert.assertEquals(map("key", "null"),
                JSObject.parse("{\"key\": null }"));

        Assert.assertEquals(map("\r\n\t\b\f\\\u0123\u4567\u8989\uAABB\uCCDD\uEEFF", "null"),
                JSObject.parse("{\"\\r\\n\\t\\b\\f\\\\\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\": null }"));

        Assert.assertEquals(map("key", "[ 1, 2, null, 4, 5 ]"),
                JSObject.parse("{\"key\": [ 1, 2, null, 4, 5] }"));
    }

    @Test
    public void miltyKeyCheck() {

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1, \"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1\r\n\t\b\f,\"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1 \r \n \t \b \f ,\"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\r\n\t\b\f\"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1, \r \n \t \b \f \"tow\":2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\"\r\n\t\b\f:2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\" \r \n \t \b \f :2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":\r\n\t\b\f2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\": \r \n \t \b \f 2}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2\r\n\t\b\f}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2 \r \n \t \b \f}"));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\r \n \t \b \f \"tow\" \r \n \t \b \f : 2}"));

    }

    @Test
    public void integerCheck() {

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1}"));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\": 1}"));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1 }"));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r\n\t\b\f1}"));
        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r\n\t\b\f1\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r \n \t \b \f 1 \r \n \t \b \f }"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123}"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\": -123}"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123 }"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r\n\t\b\f-123}"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r\n\t\b\f-123\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r \n \t \b \f -123 \r \n \t \b \f }"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0}"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\": 0}"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0 }"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r\n\t\b\f0}"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r\n\t\b\f0\r\n\t\b\f}"));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r \n \t \b \f 0 \r \n \t \b \f }"));

    }

    @Test
    public void floatingCheck() {

        Assert.assertEquals(map("float", "1.0"),
                JSObject.parse("{\"float\":1.0}"));
        Assert.assertEquals(map("float", "1.1"),
                JSObject.parse("{\"float\": 1.1}"));
        Assert.assertEquals(map("float", "1.2"),
                JSObject.parse("{\"float\":1.2 }"));
        Assert.assertEquals(map("float", "1.3"),
                JSObject.parse("{\"float\":\r\n\t\b\f1.3}"));
        Assert.assertEquals(map("float", "1.4"),
                JSObject.parse("{\"float\":1.4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "1.5"),
                JSObject.parse("{\"float\":\r\n\t\b\f1.5\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "1.6"),
                JSObject.parse("{\"float\":\r \n \t \b \f 1.6 \r \n \t \b \f }"));

        Assert.assertEquals(map("float", "-123.0"),
                JSObject.parse("{\"float\":-123.0}"));
        Assert.assertEquals(map("float", "-123.8"),
                JSObject.parse("{\"float\": -123.8}"));
        Assert.assertEquals(map("float", "-123.9"),
                JSObject.parse("{\"float\":-123.9 }"));
        Assert.assertEquals(map("float", "-123.01"),
                JSObject.parse("{\"float\":\r\n\t\b\f-123.01}"));
        Assert.assertEquals(map("float", "-123.12"),
                JSObject.parse("{\"float\":-123.12\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "-123.13"),
                JSObject.parse("{\"float\":\r\n\t\b\f-123.13\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "-123.14"),
                JSObject.parse("{\"float\":\r \n \t \b \f -123.14 \r \n \t \b \f }"));

        Assert.assertEquals(map("float", "0.0"),
                JSObject.parse("{\"float\":0.0}"));
        Assert.assertEquals(map("float", "0.1"),
                JSObject.parse("{\"float\": 0.1}"));
        Assert.assertEquals(map("float", "0.11"),
                JSObject.parse("{\"float\":0.11 }"));
        Assert.assertEquals(map("float", "0.01"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01}"));
        Assert.assertEquals(map("float", "0.00"),
                JSObject.parse("{\"float\":0.00\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132 \r \n \t \b \f }"));

        Assert.assertEquals(map("float", "0.0E2"),
                JSObject.parse("{\"float\":0.0E2}"));
        Assert.assertEquals(map("float", "0.0E2"),
                JSObject.parse("{\"float\": 0.0E2}"));
        Assert.assertEquals(map("float", "0.11E3"),
                JSObject.parse("{\"float\":0.11E3 }"));
        Assert.assertEquals(map("float", "0.01E7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E7}"));
        Assert.assertEquals(map("float", "0.00E4"),
                JSObject.parse("{\"float\":0.00E4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12E3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E3\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132E2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E2 \r \n \t \b \f }"));
        Assert.assertEquals(map("float", "-0.132E2"),
                JSObject.parse("{\"float\":-0.132E2}"));

        Assert.assertEquals(map("float", "0.0e2"),
                JSObject.parse("{\"float\":0.0e2}"));
        Assert.assertEquals(map("float", "0.0e2"),
                JSObject.parse("{\"float\": 0.0e2}"));
        Assert.assertEquals(map("float", "0.11e3"),
                JSObject.parse("{\"float\":0.11e3 }"));
        Assert.assertEquals(map("float", "0.01e7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01e7}"));
        Assert.assertEquals(map("float", "0.00e4"),
                JSObject.parse("{\"float\":0.00e4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12e3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12e3\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132e2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132e2 \r \n \t \b \f }"));
        Assert.assertEquals(map("float", "-0.132e2"),
                JSObject.parse("{\"float\":-0.132e2}"));

        Assert.assertEquals(map("float", "0.0E+2"),
                JSObject.parse("{\"float\":0.0E+2}"));
        Assert.assertEquals(map("float", "0.0E+2"),
                JSObject.parse("{\"float\": 0.0E+2}"));
        Assert.assertEquals(map("float", "0.11E+3"),
                JSObject.parse("{\"float\":0.11E+3 }"));
        Assert.assertEquals(map("float", "0.01E+7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E+7}"));
        Assert.assertEquals(map("float", "0.00E+4"),
                JSObject.parse("{\"float\":0.00E+4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12E+3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E+3\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132E+2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E+2 \r \n \t \b \f }"));
        Assert.assertEquals(map("float", "-0.132E+2"),
                JSObject.parse("{\"float\":-0.132E+2}"));

        Assert.assertEquals(map("float", "0.0E-2"),
                JSObject.parse("{\"float\":0.0E-2}"));
        Assert.assertEquals(map("float", "0.0E-2"),
                JSObject.parse("{\"float\": 0.0E-2}"));
        Assert.assertEquals(map("float", "0.11E-3"),
                JSObject.parse("{\"float\":0.11E-3 }"));
        Assert.assertEquals(map("float", "0.01E-7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E-7}"));
        Assert.assertEquals(map("float", "0.00E-4"),
                JSObject.parse("{\"float\":0.00E-4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12E-3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E-3\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132E-2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E-2 \r \n \t \b \f }"));
        Assert.assertEquals(map("float", "-0.132E-2"),
                JSObject.parse("{\"float\":-0.132E-2}"));

        Assert.assertEquals(map("float", "0.0e-2"),
                JSObject.parse("{\"float\":0.0e-2}"));
        Assert.assertEquals(map("float", "0.0e-2"),
                JSObject.parse("{\"float\": 0.0e-2}"));
        Assert.assertEquals(map("float", "0.11e-3"),
                JSObject.parse("{\"float\":0.11e-3 }"));
        Assert.assertEquals(map("float", "0.01e-7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01e-7}"));
        Assert.assertEquals(map("float", "0.00e-4"),
                JSObject.parse("{\"float\":0.00e-4\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.12e-3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12e-3\r\n\t\b\f}"));
        Assert.assertEquals(map("float", "0.132e-2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132e-2 \r \n \t \b \f }"));
        Assert.assertEquals(map("float", "-0.132e-2"),
                JSObject.parse("{\"float\":-0.132e-2}"));
    }

    @Test
    public void booleanCheck() {
        Assert.assertEquals(map("bool", "true"),
                JSObject.parse("{\"bool\":true}"));
        Assert.assertEquals(map("bool", "false"),
                JSObject.parse("{\"bool\": false}"));
        Assert.assertEquals(map("bool", "true"),
                JSObject.parse("{\"bool\":true }"));
        Assert.assertEquals(map("bool", "false"),
                JSObject.parse("{\"bool\":\r\n\t\b\ffalse}"));
        Assert.assertEquals(map("bool", "true"),
                JSObject.parse("{\"bool\":true\r\n\t\b\f}"));
        Assert.assertEquals(map("bool", "false"),
                JSObject.parse("{\"bool\":\r\n\t\b\ffalse\r\n\t\b\f}"));
        Assert.assertEquals(map("bool", "true"),
                JSObject.parse("{\"bool\":\r \n \t \b \f true \r \n \t \b \f }"));
        Assert.assertEquals(map("bool", "false"),
                JSObject.parse("{\"bool\":false}"));
    }

    @Test
    public void stringCheck() {
        Assert.assertEquals(map("string", "string-vv"),
                JSObject.parse("{\"string\":\"string-vv\"}"));
        Assert.assertEquals(map("string", "\n\r\t\f\b\"\\"),
                JSObject.parse("{\"string\": \"\\n\\r\\t\\f\\b\\\"\\\\\"}"));
        Assert.assertEquals(map("string", "\u0123\u4567\u8989\uAABB\uCCDD\uEEFF"),
                JSObject.parse("{\"string\":\"\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\" }"));
        Assert.assertEquals(map("string", "string-vv"),
                JSObject.parse("{\"string\":\r\n\t\b\f\"string-vv\"}"));
        Assert.assertEquals(map("string", "string-vv"),
                JSObject.parse("{\"string\":\"string-vv\"\r\n\t\b\f}"));
        Assert.assertEquals(map("string", "string-vv"),
                JSObject.parse("{\"string\":\r\n\t\b\f\"string-vv\"\r\n\t\b\f}"));
        Assert.assertEquals(map("string", "string-vv"),
                JSObject.parse("{\"string\":\r \n \t \b \f \"string-vv\" \r \n \t \b \f }"));
    }

    @Test
    public void nullCheck() {
        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":null}"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\": null}"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":null }"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":\r\n\t\b\fnull}"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":null\r\n\t\b\f}"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":\r\n\t\b\fnull\r\n\t\b\f}"));

        Assert.assertEquals(map("null-key", "null"),
                JSObject.parse("{\"null-key\":\r \n \t \b \f null \r \n \t \b \f }"));
    }

    @Test
    public void objectTest() {

        Assert.assertEquals(map("object", "{  }"),
                JSObject.parse("{\"object\": {}}"));

        Assert.assertEquals(map("object", "{ \"int\" : 1 }"),
                JSObject.parse("{\"object\": { \"int\":1 }}"));

        Assert.assertEquals(map("object", "{ \"string\" : \"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" }"),
                JSObject.parse("{\"object\": { \"string\":\"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" } }"));

        Assert.assertEquals(map("object", "{ \"float-1\" : 1.023045e10, \"float-2\" : -13.23E10, \"float-3\" : 10.23E+12 }"),
                JSObject.parse("{\"object\":\r\n\t\b\f{ \"float-1\":1.023045e10, \"float-2\": -13.23E10, \"float-3\":10.23E+12}}"));

        Assert.assertEquals(map("object", "{ \"sub\" : { \"string\" : \"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" } }"),
                JSObject.parse("{\n\r\t\b\f\"object\"\n\r\t\b\f: {\"sub\":{\"string\":\"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\"}}}"));

    }

    @Test
    public void spacingCheck() {
        Assert.assertEquals(map("object", "{ \"int\" : 1 }"),
                JSObject.parse("\n\r\t\b\f{\n\r\t\b\f\"object\"\r \n\f\t\b: \r\n\t \b \f{\n\r\f \f\f\f \t\b \"int\" \r \n \t \b\f:\r \n  \t\b   \f1 \r  \n \t \b  \f }  \r\n  \t\b\f   }   \r\n\t\b\f  "));
    }

    @Test
    public void arrayCheck() {
        Assert.assertEquals(map("array", "[ 1, 1.1, -1.2, 1.3e12, 1.4E13, -1.5e11, -1.6E10, 123312.3e+2, 123.312E+32, -123.123E+12, -231.32e+12, 123.312E-23, 133.233e-34, -11.23E-3, -31.312E-3, true, false, null, 0, 0.0, 1, -2, [ 0, -1, 2, -3.3E2, true, false, [  ], {  } ], { \"int\" : 123, \"true-key\" : true, \"false-key\" : false, \"null-key\" : null, \"arr\" : [  ], \"sub\" : {  } } ]"),
                JSObject.parse("\n\r\t\b\f{\n\r\t\b\f\"array\" : [   1,   1.1, \r \n \t-1.2,1.3e12,\b\f 1.4E13,  -1.5e11,\t\t\t\f\r -1.6E10,  123312.3e+2,123.312E+32,-123.123E+12,-231.32e+12,123.312E-23,133.233e-34,-11.23E-3,-31.312E-3,true,false, null, 0, 0.0, 1,-2, [ 0, -1, 2, -3.3E2, true, false, [  ], {  } ], { \"int\" : 123, \"true-key\" : true, \"false-key\" : false, \"null-key\" : null, \"arr\" : [  ], \"sub\" : {  } } ] }"));
    }

    @Test
    public  void incorrectTest() {
        Assert.assertNull(JSObject.parse(null));
        Assert.assertNull(JSObject.parse("[]"));

        Assert.assertNull(JSObject.parse(""));

        Assert.assertNull(JSObject.parse("{} ["));
        Assert.assertNull(JSObject.parse("{ "));
        Assert.assertNull(JSObject.parse("{ int }"));
        Assert.assertNull(JSObject.parse("{ int : 1 }"));
//        Assert.assertNull(JSObject.parse("{ \"key\" : 1 }"));
    }

}
