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

        List<Json.Array.UnmodifiableEntry> source = new ArrayList<>();
        source.add(new Json.Array.UnmodifiableEntry("123", SAME));
        source.add(new Json.Array.UnmodifiableEntry("123", WRAP));
        source.add(new Json.Array.UnmodifiableEntry(null, SAME));


        List<Json.Array.UnmodifiableEntry> sourceCopy = new ArrayList<>();
        sourceCopy.add(new Json.Array.UnmodifiableEntry("123", SAME));
        sourceCopy.add(new Json.Array.UnmodifiableEntry("123", WRAP));

        Assert.assertTrue(new Json.Array("[]").isEmpty());
        Assert.assertTrue(new Json.Array("[]".toCharArray()).isEmpty());

        Json.Array list = new Json.Array(source);
        Json.Array other = new Json.Array(sourceCopy);

        sourceCopy.add(new Json.Array.UnmodifiableEntry(null, SAME));
        Json.Array copy = new Json.Array(sourceCopy);

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
