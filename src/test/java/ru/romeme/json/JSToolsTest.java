package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class JSToolsTest {

    @Test
    public void wrap() {
        Optional<String> rs = JSTools.encode("\u0123\u4567\u8989\uaAbB\ucCdD\ueEfF");

        Assert.assertTrue(rs.isPresent());

        String json = rs.get();

        Assert.assertEquals("\"\\u0123\\u4567\\u8989\\uAABB\\uCCDD\\uEEFF\"", json);
    }
}