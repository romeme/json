package ru.romeme.json;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class RandomDataTest {

    @Test
    public void test() throws Exception {

        for (String name : IntStream.range(0, 7)
                .mapToObj(i -> String.format("array-%d.json", i))
                .collect(Collectors.toList())) {

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                int size;
                byte[] buffer = new byte[2048];
                try (InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(name)) {
                    while ((size = stream.read(buffer)) != -1)
                        out.write(buffer, 0, size);
                }

                String test = new String(out.toByteArray());
                Assert.assertTrue(!Json.array(test).isEmpty());
            }
        }

        for (String name : IntStream.range(0, 10)
                .mapToObj(i -> String.format("object-%d.json", i))
                .collect(Collectors.toList())) {

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

                int size;
                byte[] buffer = new byte[2048];
                try (InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(name)) {
                    while ((size = stream.read(buffer)) != -1)
                        out.write(buffer, 0, size);
                }

                String test = new String(out.toByteArray());
                Assert.assertTrue(!Json.map(test).isEmpty());
            }
        }
    }
}
