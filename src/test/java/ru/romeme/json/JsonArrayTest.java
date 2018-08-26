package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonArrayTest {

    @Test
    public void empty() {
        Assert.assertTrue(Json.Array.parse("[]").isEmpty());
    }

    @Test
    public void space_inside() {
        Assert.assertTrue(Json.Array.parse("[   ]").isEmpty());
    }

    @Test
    public void space_outside() {
        Assert.assertTrue(Json.Array.parse("   []   ").isEmpty());
    }

    @Test
    public void single_true() {
        Assert.assertTrue(!Json.Array.parse("[true]").isEmpty());
    }

    @Test
    public void single_false() {
        Assert.assertTrue(!Json.Array.parse("[false]").isEmpty());
    }

    @Test
    public void single_null() {
        Assert.assertTrue(!Json.Array.parse("[null]").isEmpty());
    }

    @Test
    public void single_string() {
        Assert.assertTrue(!Json.Array.parse("[\"\123\"]").isEmpty());
    }


    @Test
    public void numbers() {
        int size = 200;
        String array = IntStream.range(0, size)
                //.mapToObj(i -> round(100 * Math.random(), -6) * Math.pow(10, Math.round(16 - 32 * Math.random())))
                .mapToObj(i -> "[  null, true, false ]")
                .map(String::valueOf)
                .collect(Collectors.toList())
                .toString();

        Assert.assertEquals(Json.Array.parse(array).size(), size);
    }

    private static double round(double in, int exponent) {
        double tail = in % Math.pow(10, exponent);
        return in - tail;
    }

}
