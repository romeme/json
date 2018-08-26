package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class WrapTest {

    @Test
    public void test() {
        Assert.assertEquals(Json.wrap("\r"), "\"\\r\"");
        Assert.assertEquals(Json.wrap("\n"), "\"\\n\"");
        Assert.assertEquals(Json.wrap("\t"), "\"\\t\"");
        Assert.assertEquals(Json.wrap("\f"), "\"\\f\"");
        Assert.assertEquals(Json.wrap("\b"), "\"\\b\"");
        Assert.assertEquals(Json.wrap("\\"), "\"\\\\\"");
        Assert.assertEquals(Json.wrap("1234567890-="), "\"1234567890-=\"");
        Assert.assertEquals(Json.wrap("!@#$%^&*()_+"), "\"!@#$%^&*()_+\"");
        Assert.assertEquals(Json.wrap("qwertyuiop[]asdfghjkl;'\\`zxcvbnm,./"), "\"qwertyuiop[]asdfghjkl;'\\\\`zxcvbnm,./\"");
        Assert.assertEquals(Json.wrap("Джон Уик 2 - 720-hd.me"), "\"\\u0414\\u0436\\u043e\\u043d \\u0423\\u0438\\u043a 2 - 720-hd.me\"");
    }
}

