package ru.romeme.json;

import org.junit.Test;

public class ObjectSerializationTest {

    @Test
    public void empty() {
//        Optional<String> rs =
//                new HashMap<>()
//                        .entrySet()
//                        .stream()
//                        .collect(JSObject.collector());
//
//        Assert.assertTrue(rs.isPresent());
//        String json = rs.get();
//
//        Assert.assertEquals("{  }", json);

    }

//    @Test
//    public void singleKey() {
//
//        Assert.assertTrue(Parser.object("{\"key\":1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{ \"key\":1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\"key\" :1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\r\n\t\b\f\"key\":1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\"key\"\r\n\t\b\f:1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\r\n\t\b\f\"key\"\r\n\t\b\f:1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("\r\n\t\b\f{\"key\":1}").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\"key\":1}\r\n\t\b\f").containsKey("key"));
//        Assert.assertTrue(Parser.object("\r\n\t\b\f{\"key\":1}\r\n\t\b\f").containsKey("key"));
//        Assert.assertTrue(Parser.object(" \r \n \t \b \f {\"key\":1} \r \n \t \b \f ").containsKey("key"));
//        Assert.assertTrue(Parser.object("{\"qwertyuiop[]asdfghjkl;'\\\\`zxcvbnm,./\":1}").containsKey("qwertyuiop[]asdfghjkl;'\\`zxcvbnm,./"));
//        Assert.assertTrue(Parser.object("{\"§1234567890-=\":1}").containsKey("§1234567890-="));
//        Assert.assertTrue(Parser.object("{\"±!@#$%^&*()_+\":1}").containsKey("±!@#$%^&*()_+"));
//        Assert.assertTrue(Parser.object("{\"QWERTYUIOP{}ASDFGHJKL:\\\"|~ZXCVBNM<>?\":1}").containsKey("QWERTYUIOP{}ASDFGHJKL:\"|~ZXCVBNM<>?"));
//    }
//
//    @Test
//    public void singleValue() {
//
//        Assert.assertTrue(Parser.object("{\"key\":1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\": 1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\":1 }").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\"\r\n\t\b\f:1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\" \r \n \t \b \f :1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\":\r\n\t\b\f1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\": \r \n \t \b \f 1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\":1\r\n\t\b\f}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"key\":1 \r \n \t \b \f }").containsValue("1"));
//    }
//
//    @Test
//    public void miltyKey() {
//
//        Map<String, String> target = Parser.object("{\"one\":1,\"tow\":2}");
//
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1, \"tow\":1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1\r\n\t\b\f,\"tow\":1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1 \r \n \t \b \f ,\"tow\":1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\r\n\t\b\f\"tow\":1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1, \r \n \t \b \f \"tow\":1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\"\r\n\t\b\f:1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\" \r \n \t \b \f :1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\":\r\n\t\b\f1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\": \r \n \t \b \f 1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\":1\r\n\t\b\f}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\"tow\":1 \r \n \t \b \f}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1,\r \n \t \b \f \"tow\" \r \n \t \b \f : 1}");
//        Assert.assertTrue(target.containsKey("one") && target.containsKey("tow"));
//
//        target = Parser.object("{\"one\":1, \"tow\":\"1\"}");
//        System.out.println(target.toString());
//
//    }
//
//    @Test
//    public void integer() {
//
//        Assert.assertTrue(Parser.object("{\"int\":1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\": 1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\":1 }").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f1}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\":1\r\n\t\b\f}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f1\r\n\t\b\f}").containsValue("1"));
//        Assert.assertTrue(Parser.object("{\"int\":\r \n \t \b \f 1 \r \n \t \b \f }").containsValue("1"));
//
//        Assert.assertTrue(Parser.object("{\"int\":-123}").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\": -123}").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\":-123 }").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f-123}").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\":-123\r\n\t\b\f}").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f-123\r\n\t\b\f}").containsValue("-123"));
//        Assert.assertTrue(Parser.object("{\"int\":\r \n \t \b \f -123 \r \n \t \b \f }").containsValue("-123"));
//
//        Assert.assertTrue(Parser.object("{\"int\":0}").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\": 0}").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\":0 }").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f0}").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\":0\r\n\t\b\f}").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\":\r\n\t\b\f0\r\n\t\b\f}").containsValue("0"));
//        Assert.assertTrue(Parser.object("{\"int\":\r \n \t \b \f 0 \r \n \t \b \f }").containsValue("0"));
//    }
//
//    @Test
//    public void floating() {
//
//        Assert.assertTrue(Parser.object("{\"floating\":1.0}").containsValue("1.0"));
//        Assert.assertTrue(Parser.object("{\"floating\": 1.1}").containsValue("1.1"));
//        Assert.assertTrue(Parser.object("{\"floating\":1.2 }").containsValue("1.2"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f1.3}").containsValue("1.3"));
//        Assert.assertTrue(Parser.object("{\"floating\":1.4\r\n\t\b\f}").containsValue("1.4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f1.5\r\n\t\b\f}").containsValue("1.5"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 1.6 \r \n \t \b \f }").containsValue("1.6"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":-123.0}").containsValue("-123.0"));
//        Assert.assertTrue(Parser.object("{\"floating\": -123.8}").containsValue("-123.8"));
//        Assert.assertTrue(Parser.object("{\"floating\":-123.9 }").containsValue("-123.9"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f-123.01}").containsValue("-123.01"));
//        Assert.assertTrue(Parser.object("{\"floating\":-123.12\r\n\t\b\f}").containsValue("-123.12"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f-123.13\r\n\t\b\f}").containsValue("-123.13"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f -123.14 \r \n \t \b \f }").containsValue("-123.14"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0}").containsValue("0.0"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.1}").containsValue("0.1"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11 }").containsValue("0.11"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01}").containsValue("0.01"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00\r\n\t\b\f}").containsValue("0.00"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12\r\n\t\b\f}").containsValue("0.12"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132 \r \n \t \b \f }").containsValue("0.132"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0E2}").containsValue("0.0E2"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.0E2}").containsValue("0.0E2"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11E3 }").containsValue("0.11E3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01E7}").containsValue("0.01E7"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00E4\r\n\t\b\f}").containsValue("0.00E4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12E3\r\n\t\b\f}").containsValue("0.12E3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132E2 \r \n \t \b \f }").containsValue("0.132E2"));
//        Assert.assertTrue(Parser.object("{\"floating\":-0.132E2}").containsValue("-0.132E2"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0e2}").containsValue("0.0e2"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.0e2}").containsValue("0.0e2"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11e3 }").containsValue("0.11e3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01e7}").containsValue("0.01e7"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00e4\r\n\t\b\f}").containsValue("0.00e4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12e3\r\n\t\b\f}").containsValue("0.12e3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132e2 \r \n \t \b \f }").containsValue("0.132e2"));
//        Assert.assertTrue(Parser.object("{\"floating\":-0.132e2}").containsValue("-0.132e2"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0E+2}").containsValue("0.0E+2"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.0E+2}").containsValue("0.0E+2"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11E+3 }").containsValue("0.11E+3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01E+7}").containsValue("0.01E+7"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00E+4\r\n\t\b\f}").containsValue("0.00E+4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12E+3\r\n\t\b\f}").containsValue("0.12E+3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132E+2 \r \n \t \b \f }").containsValue("0.132E+2"));
//        Assert.assertTrue(Parser.object("{\"floating\":-0.132E+2}").containsValue("-0.132E+2"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0E-2}").containsValue("0.0E-2"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.0E-2}").containsValue("0.0E-2"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11E-3 }").containsValue("0.11E-3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01E-7}").containsValue("0.01E-7"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00E-4\r\n\t\b\f}").containsValue("0.00E-4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12E-3\r\n\t\b\f}").containsValue("0.12E-3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132E-2 \r \n \t \b \f }").containsValue("0.132E-2"));
//        Assert.assertTrue(Parser.object("{\"floating\":-0.132E-2}").containsValue("-0.132E-2"));
//
//        Assert.assertTrue(Parser.object("{\"floating\":0.0e-2}").containsValue("0.0e-2"));
//        Assert.assertTrue(Parser.object("{\"floating\": 0.0e-2}").containsValue("0.0e-2"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.11e-3 }").containsValue("0.11e-3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.01e-7}").containsValue("0.01e-7"));
//        Assert.assertTrue(Parser.object("{\"floating\":0.00e-4\r\n\t\b\f}").containsValue("0.00e-4"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r\n\t\b\f0.12e-3\r\n\t\b\f}").containsValue("0.12e-3"));
//        Assert.assertTrue(Parser.object("{\"floating\":\r \n \t \b \f 0.132e-2 \r \n \t \b \f }").containsValue("0.132e-2"));
//        Assert.assertTrue(Parser.object("{\"floating\":-0.132e-2}").containsValue("-0.132e-2"));
//    }

}
