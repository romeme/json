package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import static ru.romeme.json.Json.SAME;
import static ru.romeme.json.Json.WRAP;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class UnmodifiableEntryTest {

    @Test
    public void test() {
        Json.Array.UnmodifiableEntry entry = new Json.Array.UnmodifiableEntry(
                "key", SAME
        );

        Json.Array.UnmodifiableEntry copy = new Json.Array.UnmodifiableEntry(
                "key", SAME
        );

        Json.Array.UnmodifiableEntry other = new Json.Array.UnmodifiableEntry(
                "key", WRAP
        );

        Assert.assertEquals(entry, copy);
        Assert.assertEquals(entry.hashCode(), copy.hashCode());
        Assert.assertNotEquals(entry.hashCode(), other.hashCode());
        Assert.assertEquals(entry.toString(), "key -> key");

        Assert.assertNotEquals(entry, other);
        Assert.assertEquals(entry.getKey(), "key");

        try {
            entry.setValue(i -> i);
            throw new RuntimeException();
        } catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(entry, copy);
        }
    }
}
