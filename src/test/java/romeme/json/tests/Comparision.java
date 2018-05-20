package romeme.json.tests;

import com.jsoniter.JsonIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import romeme.json.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("Duplicates")
public class Comparision {

    private int tries = 50000;
    private int size = 1;

    public static void main(String[] args) {
//        new Comparision().compare_bool();
//        new Comparision().compare_nums();
//        new Comparision().compare_strings();
        new Comparision().compare_object();
    }

    @Test
    public void compare_bool() {
        System.out.println("Boolean");

        List<String> list = IntStream.range(0, size)
                .mapToObj(i -> i % 2 == 1)
                .map(String::valueOf)
                .collect(Collectors.toList());
        String data = list.toString();
        System.gc();
        System.gc();
        System.gc();

        long org_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            new JSONArray(data);
            org_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long iter_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JsonIterator.deserialize(data, boolean[].class);
            iter_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long my_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JSON.array(data);
            my_total += System.nanoTime() - start;
        }

        System.out.println();
        System.out.println(String.format("ORG: %10s\t%10s", org_total / tries, String.format("%.2f", org_total / (float) my_total)));
        System.out.println(String.format("ITR: %10s\t%10s", iter_total / tries, String.format("%.2f", iter_total / (float) my_total)));
        System.out.println(String.format("MY:  %10s", my_total / tries));
    }

    @Test
    public void compare_nums() {

        System.out.println("Numbers");

        String data = IntStream.range(0, size)
                .mapToDouble(i -> round(100 * Math.random(), -6) * Math.pow(10, Math.round(16 - 32 * Math.random())))
                .mapToObj(String::valueOf)
                .collect(Collectors.toList())
                .toString();


        System.gc();
        System.gc();
        System.gc();

        long org_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            new JSONArray(data);
            org_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        Map<String, String> map = JSON.object("")
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));

        long iter_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JsonIterator.deserialize(data, float[].class);
            iter_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long my_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JSON.array(data)
                    .stream()
                    .map(Object::toString)
                    .map(Float::parseFloat)
                    .collect(Collectors.toList())
            ;
            my_total += System.nanoTime() - start;
        }
        System.out.println();
        System.out.println(String.format("ORG: %10s\t%10s", org_total / tries, String.format("%.2f", org_total / (float) my_total)));
        System.out.println(String.format("ITR: %10s\t%10s", iter_total / tries, String.format("%.2f", iter_total / (float) my_total)));
        System.out.println(String.format("MY:  %10s", my_total / tries));
    }

    @Test
    public void compare_strings() {

        System.out.println("Strings");

        @SuppressWarnings("SpellCheckingInspection")
        String data = IntStream.range(0, size)
                .mapToObj(i -> String.format("\"\\u%04d\\r\\t\\n132 daswfwg\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231\\u1231 234  234 42 fssd fg g tg sfdwef wfg wtwt w w wer we wer wer gbrt\"", i))
                .collect(Collectors.toList())
                .toString();

        System.gc();
        System.gc();
        System.gc();

        long org_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            new JSONArray(data);

            org_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long iter_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JsonIterator.deserialize(data.getBytes(), String[].class);
            iter_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long my_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JSON.array(data).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            my_total += System.nanoTime() - start;
        }
        System.out.println();
        System.out.println(String.format("ORG: %10s\t%10s", org_total / tries, String.format("%.2f", org_total / (float) my_total)));
        System.out.println(String.format("ITR: %10s\t%10s", iter_total / tries, String.format("%.2f", iter_total / (float) my_total)));
        System.out.println(String.format("MY:  %10s", my_total / tries));
    }

    @Test
    public void compare_object() {

        System.out.println("Strings");

        @SuppressWarnings("SpellCheckingInspection")
        String data = "{\"integer\": 1, \"boolean\": true, \"null\": null, \"float\": 7.55345E12, \"internal_array\": [1, true, [1, 1.1234E123, true, null, \"test string \\\\u1234 \\\\u1233 \\\\t\\\\n\\\\r\", {}, []], {\"int\": 1, \"float\": 1.1234E123, \"bool\": true, \"null\": null, \"string\": \"test string \\\\u1234 \\\\u1233 \\\\t\\\\n\\\\r\", \"empty_object\": {}, \"empty_array\": []}, {}, []], \"internal_object\": {\"int\": 1, \"float\": 1.1234E123, \"bool\": true, \"null\": null, \"string\": \"test string \\\\u1234 \\\\u1233 \\\\t\\\\n\\\\r\", \"empty_object\": {}, \"empty_array\": []}}";

        System.gc();
        System.gc();
        System.gc();

        long org_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            new JSONObject(data);

            org_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long iter_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JsonIterator.deserialize(data, HashMap.class);
            iter_total += System.nanoTime() - start;
        }

        System.gc();
        System.gc();
        System.gc();

        long my_total = 0;
        for (int index = 0; index < tries; index++) {
            System.out.print(String.format("\rDo Test %d", index));
            long start = System.nanoTime();
            JSON.object(data)
            .entrySet()
            .stream()
            .collect(Collectors.toMap(e -> String.valueOf(e.getKey()), e -> String.valueOf(e.getValue())));
            
            my_total += System.nanoTime() - start;
        }
        System.out.println();
        System.out.println(String.format("ORG: %10s\t%10s", org_total / tries, String.format("%.2f", org_total / (float) my_total)));
        System.out.println(String.format("ITR: %10s\t%10s", iter_total / tries, String.format("%.2f", iter_total / (float) my_total)));
        System.out.println(String.format("MY:  %10s", my_total / tries));
    }

    private static double round(double in, int exponent) {
        double tail = in % Math.pow(10, exponent);
        return in - tail;
    }
}
