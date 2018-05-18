package romeme.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JSON {

    private JSON() {}

    public static List<String> array(String input) {
        return array(input.toCharArray());
    }

    public static List<String> array(char[] input) {

        List<String> result = new ArrayList<>();
        Pattern PATTERN = Pattern.compile("^[-]?([0]|[1-9]\\d?)([.]\\d+)?([eE]([+-])?\\d+)?$");

        final int INIT = 0;
        final int FINISH = 1;
        final int SPACE_OR_END = 2;

        final int VALUE_HEAD = 31;
        final int VALUE_READ = 32;
        final int VALUE_TAIL = 33;

        int index = -1;
        int length = input.length;
        int state = INIT;

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                                continue init;
                            case '[':
                                state = SPACE_OR_END;
                                continue main;
                        }
                    throw new RuntimeException();

                case SPACE_OR_END:

                    space_end:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                                continue space_end;
                            case ']':
                                state = FINISH;
                                continue main;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }
                    throw new RuntimeException();

                case FINISH:
                    finish:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                                continue finish;
                            default:
                                throw new RuntimeException();
                        }
                    return result;

                case VALUE_HEAD:
                    value_head:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                                continue value_head;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }
                    throw new RuntimeException();

                case VALUE_TAIL:
                    value_tail:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                                continue value_tail;
                            case ']':
                                state = FINISH;
                                continue main;
                            case ',':
                                state = VALUE_HEAD;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                    throw new RuntimeException();

                case VALUE_READ:
                    while (++index < length)
                        switch (input[index]) {
                            case '-':
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                int start = index;

                                num:
                                while (true)
                                    switch (input[index + 1]) {
                                        case ' ':
                                        case ',':
                                        case ']':
                                            break num;
                                        default:
                                            index++;
                                    }

                                String number = new String(Arrays.copyOfRange(input, start, index + 1));
                                Matcher matcher = PATTERN.matcher(number);
                                boolean find = matcher.find();

                                if (!find)
                                    throw new RuntimeException();
                                result.add(number);

                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                String vnull = new String(
                                        new char[]{input[index],
                                                input[index + 1],
                                                input[index + 2],
                                                input[index + 3]}
                                );

                                if (!vnull.equals("null"))
                                    throw new RuntimeException();

                                result.add(null);
                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                String vtrue = new String(
                                        new char[]{input[index],
                                                input[index + 1],
                                                input[index + 2],
                                                input[index + 3]}
                                );

                                if (!vtrue.equals("true"))
                                    throw new RuntimeException();

                                result.add(vtrue);
                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                String vfalse = new String(
                                        new char[]{input[index],
                                                input[index + 1],
                                                input[index + 2],
                                                input[index + 3],
                                                input[index + 4]}
                                );

                                if (!vfalse.equals("false"))
                                    throw new RuntimeException();

                                result.add(vfalse);
                                index += 4;
                                state = VALUE_TAIL;
                                continue main;

                            case '"':
                                StringBuilder builder = new StringBuilder();
                                final int SCREEN = 0;
                                final int NORMAL = 1;
                                int reader = NORMAL;

                                reader:
                                while (true)
                                    switch (reader) {
                                        case SCREEN:
                                            reader = NORMAL;
                                            switch (input[++index]) {
                                                case 'r':
                                                    builder.append('\r');
                                                    continue reader;
                                                case 'n':
                                                    builder.append('\n');
                                                    continue reader;
                                                case 't':
                                                    builder.append('\t');
                                                    continue reader;
                                                case 'u':
                                                    String unicode = new String(new char[]{input[index + 1], input[index + 2], input[index + 3], input[index + 4]});
                                                    if (!unicode.matches("[0-9]{4}"))
                                                        throw new RuntimeException();

                                                    index += 4;
                                                    builder.append((char) Integer.parseInt(unicode, 16));
                                                    continue reader;

                                                case '\\':
                                                    builder.append('\\');
                                                    continue reader;
                                                case '"':
                                                    builder.append('"');
                                                    continue reader;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NORMAL:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '\\':
                                                        reader = SCREEN;
                                                        continue reader;
                                                    case '"':
                                                        result.add(builder.toString());
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    default:
                                                        builder.append(input[index]);
                                                }
                                        default:
                                            throw new RuntimeException();
                                    }

                            default:
                                throw new RuntimeException();
                        }
                    throw new RuntimeException();

            }
    }
}
