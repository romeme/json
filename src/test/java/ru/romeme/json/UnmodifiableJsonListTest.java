package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static ru.romeme.json.Json.SAME;
import static ru.romeme.json.Json.WRAP;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class UnmodifiableJsonListTest {

    @Test
    public void test() {

        List<Map.Entry<String, Json.Function<String, String>>> source = new ArrayList<>();
        source.add(new Json.UnmodifiableListEntry("123", SAME));
        source.add(new Json.UnmodifiableListEntry("123", WRAP));
        source.add(new Json.UnmodifiableListEntry(null, SAME));


        List<Map.Entry<String, Json.Function<String, String>>> sourceCopy = new ArrayList<>();
        sourceCopy.add(new Json.UnmodifiableListEntry("123", SAME));
        sourceCopy.add(new Json.UnmodifiableListEntry("123", WRAP));

        Assert.assertTrue(new Json.UnmodifiableJsonList("[]").isEmpty());
        Assert.assertTrue(new Json.UnmodifiableJsonList("[]".toCharArray()).isEmpty());

        Json.UnmodifiableJsonList list = new Json.UnmodifiableJsonList(source);
        Json.UnmodifiableJsonList other = new Json.UnmodifiableJsonList(sourceCopy);

        sourceCopy.add(new Json.UnmodifiableListEntry(null, SAME));
        Json.UnmodifiableJsonList copy = new Json.UnmodifiableJsonList(sourceCopy);

        Assert.assertEquals(list.toString(), "[123,\"123\",null]");
        Assert.assertEquals(list, copy);
        Assert.assertNotEquals(list, other);

        Iterator<String> iterator = list.iterator();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), "123");
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(iterator.next(), "123");
        Assert.assertTrue(iterator.hasNext());
        Assert.assertNull(iterator.next());
        Assert.assertFalse(iterator.hasNext());

        iterator = list.iterator();
        try {
            iterator.remove();
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {

        }

        try {
            list.add("123");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.remove("123");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.addAll(Collections.singletonList("222"));
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.addAll(0, Collections.singletonList("222"));
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.add(0, "222");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.removeRange(0, 1);
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.removeAll(Collections.singletonList("222"));
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.remove("123");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.remove(0);
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        ListIterator<String> listIterator = list.listIterator();
        Assert.assertTrue(listIterator.hasNext());
        Assert.assertEquals(listIterator.nextIndex(), 1);
        Assert.assertEquals(listIterator.next(), "123");
        Assert.assertTrue(listIterator.hasNext());
        Assert.assertEquals(listIterator.next(), "123");
        Assert.assertTrue(listIterator.hasNext());
        Assert.assertNull(listIterator.next());
        Assert.assertFalse(listIterator.hasNext());
        Assert.assertEquals(listIterator.nextIndex(), -1);

        Assert.assertTrue(listIterator.hasPrevious());
        Assert.assertNull(listIterator.previous());
        Assert.assertTrue(listIterator.hasPrevious());
        Assert.assertEquals(listIterator.previous(), "123");
        Assert.assertTrue(listIterator.hasPrevious());

        Assert.assertEquals(listIterator.previousIndex(), 0);
        Assert.assertEquals(listIterator.previous(), "123");
        Assert.assertFalse(listIterator.hasPrevious());
        Assert.assertEquals(listIterator.previousIndex(), -1);

        try {
            ListIterator<String> ltr = list.listIterator();
            ltr.remove();
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            ListIterator<String> ltr = list.listIterator();
            ltr.set("222");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            ListIterator<String> ltr = list.listIterator();
            ltr.add("222");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.retainAll(Collections.singletonList("123"));
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.clear();
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }

        try {
            list.set(0, "222");
            throw new RuntimeException();
        }
        catch (UnsupportedOperationException ignored) {
            Assert.assertEquals(list, copy);
        }
    }
}
