package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ArrayParsingTest {

//    @Test
//    public void empty() {
//        Assert.assertTrue(Parser.array("[]").isEmpty());
//    }
//
//    @Test
//    public void space_inside() {
//        Assert.assertTrue(Parser.array("[   ]").isEmpty());
//    }
//
//    @Test
//    public void space_outside() {
//        Assert.assertTrue(Parser.array("   []   ").isEmpty());
//    }
//
//    @Test
//    public void single_true() {
//        Assert.assertTrue(!Parser.array("[true]").isEmpty());
//    }
//
//    @Test
//    public void single_false() {
//        Assert.assertTrue(!Parser.array("[false]").isEmpty());
//    }
//
//    @Test
//    public void single_null() {
//        Assert.assertTrue(!Parser.array("[null]").isEmpty());
//    }
//
//    @Test
//    public void single_string() {
//        Assert.assertTrue(!Parser.array("[\"\123\"]").isEmpty());
//    }
//
//
//    @Test
//    public void numbers() {
//        int size = 200;
//        String array = IntStream.range(0, size)
//                //.mapToObj(i -> round(100 * Math.random(), -6) * Math.pow(10, Math.round(16 - 32 * Math.random())))
//                .mapToObj(i -> "[  null, true, false ]")
//                .map(String::valueOf)
//                .collect(Collectors.toList())
//                .toString();
//
//        Assert.assertEquals(Parser.array(array).size(), size);
//    }
//
//    private static double round(double in, int exponent) {
//        double tail = in % Math.pow(10, exponent);
//        return in - tail;
//    }

}
