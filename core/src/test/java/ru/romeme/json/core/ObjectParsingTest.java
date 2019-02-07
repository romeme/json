package ru.romeme.json.core;

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

    private static Map<String, String> map(String key, String vv) {
        return new HashMap<String, String>() {{ put(key, vv); }};
    }

    private static Map<String, String> map(String keyA, String vvA, String keyB, String vvB) {
        return new HashMap<String, String>() {{
            put(keyA, vvA);
            put(keyB, vvB);
        }};
    }

    @Test
    public void empty() {
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{ }")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse(" {}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{} ")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{\r\n\t\b\f}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{\r \n \t \b \f}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{}\r\n\t\b\f")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("{} \r \n \t \b \f")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r\n\t\b\f{}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r \n \t \b \f {}")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse(" \r \n \t \b \f {} \r \n \t \b \f ")
                        .orElse(NOT_EMPTY_MAP));
        Assert.assertEquals(EMPTY_MAP,
                JSObject.parse("\r\n\t\b\f{}\r\n\t\b\f")
                        .orElse(NOT_EMPTY_MAP));

    }

    @Test
    public void singleKey() {

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{ \"key\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\" :1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\r\n\t\b\f\"key\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\"\r\n\t\b\f:1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\r\n\t\b\f\"key\"\r\n\t\b\f:1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("\r\n\t\b\f{\"key\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("{\"key\":1}\r\n\t\b\f")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse("\r\n\t\b\f{\"key\":1}\r\n\t\b\f")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "1"),
                JSObject.parse(" \r \n \t \b \f {\"key\":1} \r \n \t \b \f ")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("qwertyuiop[]asdfghjkl;'\\`zxcvbnm,./", "1"),
                JSObject.parse("{\"qwertyuiop[]asdfghjkl;'\\\\`zxcvbnm,./\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("§1234567890-=", "1"),
                JSObject.parse("{\"§1234567890-=\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("!@#$%^&*()_+", "1"),
                JSObject.parse("{\"!@#$%^&*()_+\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("QWERTYUIOP{}ASDFGHJKL:\"\\|~ZXCVBNM<>?", "1"),
                JSObject.parse("{\"QWERTYUIOP{}ASDFGHJKL:\\\"\\\\|~ZXCVBNM<>?\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "null"),
                JSObject.parse("{\"key\": null }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("\r\n\t\b\f\\\u0123\u4567\u8989\uAABB\uCCDD\uEEFF", "null"),
                JSObject.parse("{\"\\r\\n\\t\\b\\f\\\\\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\": null }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("key", "[ 1, 2, null, 4, 5 ]"),
                JSObject.parse("{\"key\": [ 1, 2, null, 4, 5] }")
                        .orElse(EMPTY_MAP));
    }

    @Test
    public void miltyKeyCheck() {

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1, \"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1\r\n\t\b\f,\"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1 \r \n \t \b \f ,\"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\r\n\t\b\f\"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1, \r \n \t \b \f \"tow\":2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\"\r\n\t\b\f:2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\" \r \n \t \b \f :2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":\r\n\t\b\f2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\": \r \n \t \b \f 2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\"tow\":2 \r \n \t \b \f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("one", "1", "tow", "2"),
                JSObject.parse("{\"one\":1,\r \n \t \b \f \"tow\" \r \n \t \b \f : 2}")
                        .orElse(EMPTY_MAP));

    }

    @Test
    public void integerCheck() {

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\": 1}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1 }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r\n\t\b\f1}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":1\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r\n\t\b\f1\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "1"),
                JSObject.parse("{\"int\":\r \n \t \b \f 1 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\": -123}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123 }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r\n\t\b\f-123}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":-123\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r\n\t\b\f-123\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "-123"),
                JSObject.parse("{\"int\":\r \n \t \b \f -123 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\": 0}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0 }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r\n\t\b\f0}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":0\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r\n\t\b\f0\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("int", "0"),
                JSObject.parse("{\"int\":\r \n \t \b \f 0 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

    }

    @Test
    public void floatingCheck() {

        Assert.assertEquals(map("float", "1.0"),
                JSObject.parse("{\"float\":1.0}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "1.1"),
                JSObject.parse("{\"float\": 1.1}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "1.2"),
                JSObject.parse("{\"float\":1.2 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "1.3"),
                JSObject.parse("{\"float\":\r\n\t\b\f1.3}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "1.4"),
                JSObject.parse("{\"float\":1.4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "1.5"),
                JSObject.parse("{\"float\":\r\n\t\b\f1.5\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "1.6"),
                JSObject.parse("{\"float\":\r \n \t \b \f 1.6 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "-123.0"),
                JSObject.parse("{\"float\":-123.0}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.8"),
                JSObject.parse("{\"float\": -123.8}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.9"),
                JSObject.parse("{\"float\":-123.9 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.01"),
                JSObject.parse("{\"float\":\r\n\t\b\f-123.01}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.12"),
                JSObject.parse("{\"float\":-123.12\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.13"),
                JSObject.parse("{\"float\":\r\n\t\b\f-123.13\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-123.14"),
                JSObject.parse("{\"float\":\r \n \t \b \f -123.14 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0"),
                JSObject.parse("{\"float\":0.0}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.1"),
                JSObject.parse("{\"float\": 0.1}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11"),
                JSObject.parse("{\"float\":0.11 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00"),
                JSObject.parse("{\"float\":0.00\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0E2"),
                JSObject.parse("{\"float\":0.0E2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.0E2"),
                JSObject.parse("{\"float\": 0.0E2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11E3"),
                JSObject.parse("{\"float\":0.11E3 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01E7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E7}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00E4"),
                JSObject.parse("{\"float\":0.00E4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12E3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E3\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132E2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E2 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-0.132E2"),
                JSObject.parse("{\"float\":-0.132E2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0e2"),
                JSObject.parse("{\"float\":0.0e2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.0e2"),
                JSObject.parse("{\"float\": 0.0e2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11e3"),
                JSObject.parse("{\"float\":0.11e3 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01e7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01e7}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00e4"),
                JSObject.parse("{\"float\":0.00e4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12e3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12e3\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132e2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132e2 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-0.132e2"),
                JSObject.parse("{\"float\":-0.132e2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0E+2"),
                JSObject.parse("{\"float\":0.0E+2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.0E+2"),
                JSObject.parse("{\"float\": 0.0E+2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11E+3"),
                JSObject.parse("{\"float\":0.11E+3 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01E+7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E+7}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00E+4"),
                JSObject.parse("{\"float\":0.00E+4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12E+3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E+3\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132E+2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E+2 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-0.132E+2"),
                JSObject.parse("{\"float\":-0.132E+2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0E-2"),
                JSObject.parse("{\"float\":0.0E-2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.0E-2"),
                JSObject.parse("{\"float\": 0.0E-2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11E-3"),
                JSObject.parse("{\"float\":0.11E-3 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01E-7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01E-7}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00E-4"),
                JSObject.parse("{\"float\":0.00E-4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12E-3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12E-3\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132E-2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132E-2 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-0.132E-2"),
                JSObject.parse("{\"float\":-0.132E-2}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("float", "0.0e-2"),
                JSObject.parse("{\"float\":0.0e-2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.0e-2"),
                JSObject.parse("{\"float\": 0.0e-2}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.11e-3"),
                JSObject.parse("{\"float\":0.11e-3 }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.01e-7"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.01e-7}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.00e-4"),
                JSObject.parse("{\"float\":0.00e-4\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.12e-3"),
                JSObject.parse("{\"float\":\r\n\t\b\f0.12e-3\r\n\t\b\f}")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "0.132e-2"),
                JSObject.parse("{\"float\":\r \n \t \b \f 0.132e-2 \r \n \t \b \f }")
                        .orElse(EMPTY_MAP));
        Assert.assertEquals(map("float", "-0.132e-2"),
                JSObject.parse("{\"float\":-0.132e-2}")
                        .orElse(EMPTY_MAP));
    }

    @Test
    public void booleanCheck() {
        Assert.assertEquals("true",
                JSObject.parse("{\"bool\":true}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("false",

                JSObject.parse("{\"bool\": false}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("true",
                JSObject.parse("{\"bool\":true }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("false",
                JSObject.parse("{\"bool\":\r\n\t\b\ffalse}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("true",
                JSObject.parse("{\"bool\":true\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("false",
                JSObject.parse("{\"bool\":\r\n\t\b\ffalse\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("true",
                JSObject.parse("{\"bool\":\r \n \t \b \f true \r \n \t \b \f }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("false",
                JSObject.parse("{\"bool\":false}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
    }

    @Test
    public void stringCheck() {
        Assert.assertEquals("string-vv",
                JSObject.parse("{\"string\":\"string-vv\"}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
        Assert.assertEquals("\n\r\t\f\b\"\\",
                JSObject.parse("{\"string\": \"\\n\\r\\t\\f\\b\\\"\\\\\"}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
        Assert.assertEquals("\u0123\u4567\u8989\uAABB\uCCDD\uEEFF",
                JSObject.parse("{\"string\":\"\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\" }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
        Assert.assertEquals("string-vv",
                JSObject.parse("{\"string\":\r\n\t\b\f\"string-vv\"}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
        Assert.assertEquals("string-vv",
                JSObject.parse("{\"bool\":\"string-vv\"\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("bool"))
                        .orElse(""));
        Assert.assertEquals("string-vv",
                JSObject.parse("{\"string\":\r\n\t\b\f\"string-vv\"\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
        Assert.assertEquals("string-vv",
                JSObject.parse("{\"string\":\r \n \t \b \f \"string-vv\" \r \n \t \b \f }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("string"))
                        .orElse(""));
    }

    @Test
    public void nullCheck() {
        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":null}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\": null}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":null }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":\r\n\t\b\fnull}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":null\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":\r\n\t\b\fnull\r\n\t\b\f}")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));

        Assert.assertEquals("null",
                JSObject.parse("{\"null-key\":\r \n \t \b \f null \r \n \t \b \f }")
                        .filter(map -> map.size() == 1)
                        .map(map -> map.get("null-key"))
                        .orElse(""));
    }

    @Test
    public void objectTest() {

        Assert.assertEquals(map("object", "{  }"),
                JSObject.parse("{\"object\": {}}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("object", "{ \"int\" : 1 }"),
                JSObject.parse("{\"object\": { \"int\":1 }}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("object", "{ \"string\" : \"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" }"),
                JSObject.parse("{\"object\": { \"string\":\"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" } }")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("object", "{ \"float-1\" : 1.023045e10, \"float-2\" : -13.23E10, \"float-3\" : 10.23E+12 }"),
                JSObject.parse("{\"object\":\r\n\t\b\f{ \"float-1\":1.023045e10, \"float-2\": -13.23E10, \"float-3\":10.23E+12}}")
                        .orElse(EMPTY_MAP));

        Assert.assertEquals(map("object", "{ \"sub\" : { \"string\" : \"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\" } }"),
                JSObject.parse("{\n\r\t\b\f\"object\"\n\r\t\b\f: {\"sub\":{\"string\":\"\\n\\r\\t\\f\\b\\u0123\\u4567\\u8989\\uAaBb\\uCcDd\\uEeFf\\\"\\\\\"}}}")
                        .orElse(EMPTY_MAP));


    }

    @Test
    public void spacingCheck() {
        Assert.assertEquals(map("object", "{ \"int\" : 1 }"),
                JSObject.parse("\n\r\t\b\f{\n\r\t\b\f\"object\"\r \n\f\t\b: \r\n\t \b \f{\n\r\f \f\f\f \t\b \"int\" \r \n \t \b\f:\r \n  \t\b   \f1 \r  \n \t \b  \f }  \r\n  \t\b\f   }   \r\n\t\b\f  ")
                        .orElse(EMPTY_MAP));
    }

    @Test
    public void arrayCheck() {
        Assert.assertEquals(map("array", "[ 1, 1.1, -1.2, 1.3e12, 1.4E13, -1.5e11, -1.6E10, 123312.3e+2, 123.312E+32, -123.123E+12, -231.32e+12, 123.312E-23, 133.233e-34, -11.23E-3, -31.312E-3, true, false, null, 0, 0.0, 1, -2, [ 0, -1, 2, -3.3E2, true, false, [  ], {  } ], { \"int\" : 123, \"true-key\" : true, \"false-key\" : false, \"null-key\" : null, \"arr\" : [  ], \"sub\" : {  } } ]"),
                JSObject.parse("\n\r\t\b\f{\n\r\t\b\f\"array\" : [   1,   1.1, \r \n \t-1.2,1.3e12,\b\f 1.4E13,  -1.5e11,\t\t\t\f\r -1.6E10,  123312.3e+2,123.312E+32,-123.123E+12,-231.32e+12,123.312E-23,133.233e-34,-11.23E-3,-31.312E-3,true,false, null, 0, 0.0, 1,-2, [ 0, -1, 2, -3.3E2, true, false, [  ], {  } ], { \"int\" : 123, \"true-key\" : true, \"false-key\" : false, \"null-key\" : null, \"arr\" : [  ], \"sub\" : {  } } ] }")
                        .orElse(EMPTY_MAP));
    }

}
