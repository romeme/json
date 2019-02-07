package ru.romeme.json;

import ru.romeme.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
public class Generator {

    private static char[] codes = "qwertyuiop[]asdfghjkl;'\\`zxcvbnm,./1234567890-=!@#$%^&*()_+QWERTYUIOP{}ASDFGHJKL:\"|~ZXCVBNM<>?ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЁЯЧСМИТЬБЮ?йцукенгшщзхъфывапролджэё]ячсмитьбю/\r\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\n\t\b\f\r\r\r\r\r\r".toCharArray();

    static String rnd(char[] cases, int count) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < count; index++)
            builder.append(cases[rnd(0, cases.length - 1)]);
        return builder.toString();
    }

    static int rnd(int min, int max) {
        return Math.max(min, (int) Math.round(max * Math.random()));
    }

    static String string(int size) {
        return Json.encode(rnd(codes, size)).get();
    }

    static String spacing() {
        char[] chars = "\n\t\f\r     ".toCharArray();
        return rnd(chars, rnd(0, 10));
    }

    static String array(int count) {
        StringBuilder builder = new StringBuilder();
        builder.append(spacing());
        builder.append('[');
        List<String> fields = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            switch (rnd(0, 25)) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                    fields.add(String.format("%snull%s", spacing(), spacing()));
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    fields.add(String.format("%s%b%s", spacing(), Math.random() >= 0.5, spacing()));
                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    fields.add(String.format("%s%d%s", spacing(), rnd(0, Integer.MAX_VALUE), spacing()));
                    break;
                case 15:
                case 16:
                case 17:
                case 18:
                    fields.add(
                            Math.random() > 0.5
                                    ? String.format("%s%f%s",
                                    spacing(), 10 * Math.random() - 10 * Math.random(), spacing())
                                    : String.format("%s%16.8E%s",
                                    spacing(), Double.MAX_VALUE * Math.random() - Double.MAX_VALUE * Math.random(), spacing())

                    );
                    break;
                case 19:
                case 20:
                    fields.add(
                            String.format("%s%s%s",

                                    spacing(),
                                    object(5 - 10),
                                    spacing())
                    );
                    break;
                case 21:
                case 22:
                    fields.add(
                            String.format("%s%s%s",
                                    spacing(),
                                    array(rnd(5, 10)),
                                    spacing())
                    );
                    break;
                case 23:
                case 24:
                case 25:
                    fields.add(
                            String.format("%s%s%s",
                                    spacing(),
                                    string(500),
                                    spacing())
                    );
                    break;
                default:
            }
        }

        builder.append(
                String.join(spacing() + "," + spacing(), fields)
        );
        builder.append(']');
        builder.append(spacing());
        return builder.toString();
    }

    static String object(int size) {

        StringBuilder builder = new StringBuilder();
        builder.append(spacing());
        builder.append('{');

        List<String> fields = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            switch (rnd(0, 25)) {
                case 0:
                case 1:
                case 2:
                case 3:
                    fields.add(
                            String.format("%s%s%s:%snull%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    spacing())
                    );
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    fields.add(
                            String.format("%s%s%s:%s%b%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    Math.random() >= 0.5,
                                    spacing())
                    );

                    break;
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                    fields.add(
                            String.format("%s%s%s:%s%d%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    rnd(0, Integer.MAX_VALUE),
                                    spacing())
                    );
                    break;
                case 15:
                case 16:
                case 17:
                case 18:
                    fields.add(
                            String.format("%s%s%s:%s",
                                    spacing(),
                                    string(20),
                                    spacing(),
                                    Math.random() > 0.5
                                            ? String.format("%s%f%s",
                                            spacing(), 10 * Math.random() - 10 * Math.random(), spacing())
                                            : String.format("%s%16.8E%s",
                                            spacing(), Double.MAX_VALUE * Math.random() - Double.MAX_VALUE * Math.random(), spacing())
                            )

                    );

                    break;

                case 19:
                case 20:
                    fields.add(
                            String.format("%s%s%s:%s%s%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    object(rnd(5, 10)),
                                    spacing())
                    );
                    break;
                case 21:
                case 22:
                    fields.add(
                            String.format("%s%s%s:%s%s%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    array(rnd(5, 10)),
                                    spacing())
                    );
                    break;

                case 23:
                case 24:
                case 25:
                    fields.add(
                            String.format("%s%s%s:%s%s%s",
                                    spacing(),
                                    string(20),
                                    spacing(),

                                    spacing(),
                                    string(500),
                                    spacing())
                    );
                    break;
                default:
            }
        }

        builder.append(
                String.join(spacing() + "," + spacing(), fields)
        );
        builder.append('}');
        builder.append(spacing());
        return builder.toString();

    }
}
