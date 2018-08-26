package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class UnmodifiableMapEntryTest {

    @Test
    public void test() {
        Json.UnmodifiableMapEntry entry = new Json.UnmodifiableMapEntry(
            "key", "value", i -> i
        );

        Json.UnmodifiableMapEntry copy = new Json.UnmodifiableMapEntry(
                "key", "value", i -> i
        );

        Assert.assertEquals(entry, copy);
        Assert.assertEquals(entry.getKey(), "key");
        Assert.assertEquals(entry.getValue(), "value");

        try {
            entry.setValue("123");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(entry, copy);
        }
    }
}
