package romeme.json.tests;

import org.junit.Assert;
import org.junit.Test;
import romeme.json.JSON;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Array {

    @Test
    public void empty() {
        Assert.assertTrue(JSON.array("[]").isEmpty());
    }

    @Test
    public void space_inside() {
        Assert.assertTrue(JSON.array("[   ]").isEmpty());
    }

    @Test
    public void space_outside() {
        Assert.assertTrue(JSON.array("   []   ").isEmpty());
    }

    @Test
    public void single_true() {
        Assert.assertTrue(!JSON.array("[true]").isEmpty());
    }

    @Test
    public void single_false() {
        Assert.assertTrue(!JSON.array("[false]").isEmpty());
    }

    @Test
    public void single_null() {
        Assert.assertTrue(!JSON.array("[null]").isEmpty());
    }

    @Test
    public void numbers() {
        int size = 20000;
        String array = IntStream.range(0, size)
                .mapToDouble(i -> round(100 * Math.random(), -6) * Math.pow(10, Math.round(16 - 32 * Math.random())))
                .mapToObj(String::valueOf)
                .collect(Collectors.toList())
                .toString();
        Matcher matcher = Pattern.compile("^[-]?([0]|([1-9][0-9]?))(\\.[0-9]+)").matcher("5841.7211");
        matcher.find();


        Assert.assertEquals(JSON.array(array).size(), size);
    }

    private static double round(double in, int exponent) {
        double tail = in % Math.pow(10, exponent);
        return in - tail;
    }

}
