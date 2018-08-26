package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class UnmodifiableJsonMapTest {

    @Test
    public void test() {

        Set<Map.Entry<String, String>> set = new HashSet<>();
        set.add(new Json.UnmodifiableMapEntry("key1", "value1", i->i));
        set.add(new Json.UnmodifiableMapEntry("key2", "value2", i->i));

        Set<Map.Entry<String, String>> setCopy = new HashSet<>();
        setCopy.add(new Json.UnmodifiableMapEntry("key1", "value1", i->i));
        setCopy.add(new Json.UnmodifiableMapEntry("key2", "value2", i->i));


        Map<String, String> map = new Json.UnmodifiableJsonMap(set);
        Map<String, String> copy = new Json.UnmodifiableJsonMap(setCopy);


        Assert.assertTrue(new Json.UnmodifiableJsonMap("{}").isEmpty());
        Assert.assertTrue(new Json.UnmodifiableJsonMap("{}".toCharArray()).isEmpty());
        Assert.assertTrue(!map.isEmpty());
        Assert.assertEquals(map.size(), 2);
        Assert.assertTrue(map.containsKey("key1"));
        Assert.assertTrue(map.containsKey("key2"));
        Assert.assertFalse(map.containsKey("key3"));
        Assert.assertFalse(map.containsKey("key"));

        Assert.assertTrue(map.containsValue("value1"));
        Assert.assertTrue(map.containsValue("value2"));
        Assert.assertFalse(map.containsValue("value3"));
        Assert.assertFalse(map.containsValue("value"));

        Assert.assertEquals(map.get("key1"), "value1");
        Assert.assertEquals(map.get("key2"), "value2");
        Assert.assertNull(map.get("key3"));
        Assert.assertNull(map.get("key"));

        try {
            map.put("key", "value");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.remove("key1");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.remove("key1", "value1");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.put("key", "value");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.replace("key1", "value");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.replace("key1", "value1", "value");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            Map<String, String> add = new HashMap<>();
            add.put("key", "value");
            map.putAll(add);
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.putIfAbsent("key", "value");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        try {
            map.clear();
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(map, copy);
        }

        Set<String> keys = new HashSet<>();
        keys.add("key1");
        keys.add("key2");

        Assert.assertEquals(keys, map.keySet());

        List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        Assert.assertEquals(values.size(), map.values().size());
        Assert.assertTrue(values.containsAll(map.values()));

        Assert.assertEquals(map.entrySet(), setCopy);

    }
}
