package ru.romeme.json;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
@SuppressWarnings("Duplicates")
class Json {

    private static final byte INIT = 0;
    private static final byte SPACE_OR_END = 1;
    private static final byte FINISH = 2;
    private static final byte KEY_HEAD = 3;
    private static final byte KEY_READ = 4;

    private static final byte VALUE_HEAD = 6;
    private static final byte VALUE_READ = 7;
    private static final byte VALUE_TAIL = 8;

    private static final byte NUM_INIT = 9;
    private static final byte NUMBER_PREFIX = 10;
    private static final byte NUMBER_PREFIX_OR_END = 11;
    private static final byte NUMBER_SUFFIX = 12;
    private static final byte NUMBER_SUFFIX_OR_END = 13;
    private static final byte PLUS_MINUS_OR_NUM = 14;
    private static final byte EXP = 15;

    private static final byte SCREEN = 16;
    private static final byte NORMAL = 17;


    @NotNull
    @Contract(pure = true)
    public static String wrap(@NotNull String string) {

        StringBuilder builder = new StringBuilder();

        char c;
        int len = string.length();

        builder.append('"');
        for (int index = 0; index < len; index += 1) {

            c = string.charAt(index);
            switch (c) {
                case '\\':
                case '"':
                    builder.append('\\');
                    builder.append(c);
                    break;

                case '\b':
                    builder.append("\\b");
                    break;
                case '\t':
                    builder.append("\\t");
                    break;
                case '\n':
                    builder.append("\\n");
                    break;
                case '\f':
                    builder.append("\\f");
                    break;
                case '\r':
                    builder.append("\\r");
                    break;
                default:
                    if (c <= 127)
                        builder.append(c);
                    else
                        builder.append(String.format("\\u%04x", (int) c));
            }
        }
        builder.append('"');
        return builder.toString();
    }

    @NotNull
    @Contract(pure = true)
    public static List<String> array(@NotNull String input) {
        return array(input.toCharArray());
    }

    @NotNull
    @Contract("_ -> new")
    public static List<String> array(@NotNull char[] input) {
        return new UnmodifiableJsonList(parse_array(input));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static List<Map.Entry<String, Function<String, String>>> parse_array(@NotNull char[] input) {
        int index = -1;
        int state = INIT;

        List<Map.Entry<String, Function<String, String>>> mapping = new ArrayList<>();

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue init;
                            case '[':
                                state = SPACE_OR_END;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case SPACE_OR_END:

                    space_end:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue space_end;
                            case ']':
                                state = FINISH;
                                continue main;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }
                case FINISH:
                    int length = input.length;
                    finish:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue finish;
                            default:
                                throw new RuntimeException();
                        }
                    break main;


                case VALUE_HEAD:
                    value_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_head;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }

                case VALUE_TAIL:
                    value_tail:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_tail;
                            case ']':
                                break main;
                            case ',':
                                state = VALUE_HEAD;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case VALUE_READ:
                    while (true)
                        switch (input[++index]) {

                            case '[':
                                int array_end = array_validate(input, index - 1);
                                String array = new String(input, index, array_end - index + 1);
                                mapping.add(new UnmodifiableListEntry(array, SAME));
                                index = array_end;
                                state = VALUE_TAIL;
                                continue main;

                            case '{':
                                int object_end = object_validate(input, index - 1);
                                String object = new String(input, index, object_end - index + 1);
                                mapping.add(new UnmodifiableListEntry(object, SAME));

                                index = object_end;
                                state = VALUE_TAIL;
                                continue main;

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
                                --index;
                                state = NUM_INIT;
                                num_main:
                                while (true)
                                    switch (state) {
                                        case NUM_INIT:
                                            switch (input[++index]) {
                                                case '-':
                                                    state = NUMBER_PREFIX;
                                                    continue num_main;

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
                                                    state = NUMBER_PREFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NUMBER_PREFIX:
                                            switch (input[++index]) {
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
                                                    state = NUMBER_PREFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }
                                        case NUMBER_PREFIX_OR_END:

                                            loop:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '.':
                                                        state = NUMBER_SUFFIX;
                                                        continue num_main;
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }

                                        case NUMBER_SUFFIX:
                                            switch (input[++index]) {
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
                                                    state = NUMBER_SUFFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NUMBER_SUFFIX_OR_END:

                                            loop:
                                            while (true)
                                                switch (input[++index]) {
                                                    case 'e':
                                                    case 'E':
                                                        state = PLUS_MINUS_OR_NUM;
                                                        continue num_main;
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }
                                        case PLUS_MINUS_OR_NUM:
                                            switch (input[++index]) {
                                                case '+':
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
                                                    state = EXP;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case EXP:
                                            loop:
                                            while (true)
                                                switch (input[++index]) {
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }
                                    }

                                String number = new String(input, start, index + 1 - start);
                                mapping.add(new UnmodifiableListEntry(number, SAME));

                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                    throw new RuntimeException();

                                mapping.add(new UnmodifiableListEntry(null, i -> "null"));

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                    throw new RuntimeException();

                                mapping.add(new UnmodifiableListEntry("true", SAME));

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                    throw new RuntimeException();

                                mapping.add(new UnmodifiableListEntry("false", SAME));

                                index += 4;
                                state = VALUE_TAIL;
                                continue main;

                            case '"':
                                int str_start = index;
                                int counter = index;

                                state = NORMAL;
                                reader:
                                while (true)
                                    switch (state) {
                                        case SCREEN:
                                            state = NORMAL;
                                            switch (input[++index]) {
                                                case 'r':
                                                    input[counter++] = '\r';
                                                    continue reader;
                                                case 'n':
                                                    input[counter++] = '\n';
                                                    continue reader;
                                                case 't':
                                                    input[counter++] = '\t';
                                                    continue reader;
                                                case 'f':
                                                    input[counter++] = '\f';
                                                    continue reader;
                                                case 'b':
                                                    input[counter++] = '\b';
                                                    continue reader;
                                                case '\\':
                                                    input[counter++] = '\\';
                                                    continue reader;
                                                case '"':
                                                    input[counter++] = '"';
                                                    continue reader;
                                                case 'u':
                                                    int end = index + 4;
                                                    int value = 0;
                                                    loop:
                                                    while (++index < end)
                                                        switch (input[index]) {
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
                                                                value = (value << 4) | (input[index] - 48);
                                                                continue loop;
                                                            default:
                                                                throw new RuntimeException();
                                                        }
                                                    input[counter++] = (char) value;
                                                    index = end;
                                                    continue reader;

                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NORMAL:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '\\':
                                                        state = SCREEN;
                                                        continue reader;
                                                    case '"':
                                                        String string = new String(input, str_start, counter - str_start);
                                                        mapping.add(new UnmodifiableListEntry(string, WRAP));

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    default:
                                                        input[counter++] = input[index];
                                                }
                                        default:
                                            throw new RuntimeException();
                                    }

                            default:
                                throw new RuntimeException();
                        }

            }

        return mapping;
    }

    @NotNull
    @Contract(pure = true)
    public static Map<String, String> object(@NotNull String input) {
        return object(input.toCharArray());
    }

    @NotNull
    @Contract(pure = true)
    public static Map<String, String> object(@NotNull char[] input) {
        return new UnmodifiableJsonMap(parse_object(input));
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    private static Set<Map.Entry<String, String>> parse_object(@NotNull char[] input) {
        int index = -1;
        int state = INIT;

        Set<Map.Entry<String, String>> result = new HashSet<>();

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue init;
                            case '{':
                                state = SPACE_OR_END;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case SPACE_OR_END:

                    space_end:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue space_end;
                            case '}':
                                state = FINISH;
                                continue main;
                            case '"':
                                state = KEY_READ;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                case FINISH:
                    int length = input.length;
                    finish:
                    while (++index < length)
                        switch (input[index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue finish;
                            default:
                                throw new RuntimeException();
                        }
                    break main;

                case KEY_HEAD:
                    key_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue key_head;
                            case '"':
                                state = KEY_READ;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                case KEY_READ:
                    int key_start = index;
                    int key_counter = index;

                    state = NORMAL;
                    reader:
                    while (true)
                        switch (state) {
                            case SCREEN:
                                state = NORMAL;
                                switch (input[++index]) {
                                    case 'r':
                                        input[key_counter++] = '\r';
                                        continue reader;
                                    case 'n':
                                        input[key_counter++] = '\n';
                                        continue reader;
                                    case 'b':
                                        input[key_counter++] = '\b';
                                        continue reader;
                                    case 'f':
                                        input[key_counter++] = '\f';
                                        continue reader;
                                    case 't':
                                        input[key_counter++] = '\t';
                                        continue reader;
                                    case '\\':
                                        input[key_counter++] = '\\';
                                        continue reader;
                                    case '"':
                                        input[key_counter++] = '"';
                                        continue reader;
                                    case 'u':
                                        int end = index + 4;
                                        int value = 0;
                                        loop:
                                        while (++index < end)
                                            switch (input[index]) {
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
                                                    value = (value << 4) | (input[index] - 48);
                                                    continue loop;
                                                default:
                                                    throw new RuntimeException();
                                            }
                                        input[key_counter++] = (char) value;
                                        index = end;
                                        continue reader;

                                    default:
                                        throw new RuntimeException();
                                }

                            case NORMAL:
                                while (true)
                                    switch (input[++index]) {
                                        case '\\':
                                            state = SCREEN;
                                            continue reader;
                                        case '"':

                                            String key = new String(input, key_start, key_counter - key_start);

                                            divider:
                                            while (true)
                                                switch (input[++index]) {
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                        continue divider;
                                                    case ':':
                                                        break divider;
                                                    default:
                                                        throw new RuntimeException();
                                                }

                                            value_head:
                                            while (true)
                                                switch (input[++index]) {
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                        continue value_head;
                                                    default:
                                                        index--;
                                                        break value_head;
                                                }

                                            while (true)
                                                switch (input[++index]) {

                                                    case '[':
                                                        int array_end = array_validate(input, index - 1);

                                                        result.add(new UnmodifiableMapEntry(key,
                                                                new String(input, index, array_end - index + 1),
                                                                SAME));

                                                        index = array_end;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '{':
                                                        int object_end = object_validate(input, index - 1);

                                                        result.add(new UnmodifiableMapEntry(key,
                                                                new String(input, index, object_end - index + 1),
                                                                SAME));

                                                        index = object_end;
                                                        state = VALUE_TAIL;
                                                        continue main;

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
                                                        --index;
                                                        state = NUM_INIT;
                                                        num_main:
                                                        while (true)
                                                            switch (state) {
                                                                case NUM_INIT:
                                                                    switch (input[++index]) {
                                                                        case '-':
                                                                            state = NUMBER_PREFIX;
                                                                            continue num_main;

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
                                                                            state = NUMBER_PREFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NUMBER_PREFIX:
                                                                    switch (input[++index]) {
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
                                                                            state = NUMBER_PREFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }
                                                                case NUMBER_PREFIX_OR_END:

                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case '.':
                                                                                state = NUMBER_SUFFIX;
                                                                                continue num_main;
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }

                                                                case NUMBER_SUFFIX:
                                                                    switch (input[++index]) {
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
                                                                            state = NUMBER_SUFFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NUMBER_SUFFIX_OR_END:

                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case 'e':
                                                                            case 'E':
                                                                                state = PLUS_MINUS_OR_NUM;
                                                                                continue num_main;
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }
                                                                case PLUS_MINUS_OR_NUM:
                                                                    switch (input[++index]) {
                                                                        case '+':
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
                                                                            state = EXP;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case EXP:
                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }
                                                            }


                                                        result.add(new UnmodifiableMapEntry(key,
                                                                new String(input, start, index + 1 - start),
                                                                SAME));

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'n':
                                                        if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                                            throw new RuntimeException();

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 't':
                                                        if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                                            throw new RuntimeException();

                                                        result.add(new UnmodifiableMapEntry(key,
                                                                "true",
                                                                SAME));

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'f':
                                                        if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                            throw new RuntimeException();

                                                        result.add(new UnmodifiableMapEntry(key,
                                                                "false",
                                                                SAME));

                                                        index += 4;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '"':
                                                        int str_start = index;
                                                        int counter = index;

                                                        state = NORMAL;
                                                        value_reader:
                                                        while (true)
                                                            switch (state) {
                                                                case SCREEN:
                                                                    state = NORMAL;
                                                                    switch (input[++index]) {
                                                                        case 'r':
                                                                            input[counter++] = '\r';
                                                                            continue value_reader;
                                                                        case 'n':
                                                                            input[counter++] = '\n';
                                                                            continue value_reader;
                                                                        case 't':
                                                                            input[counter++] = '\t';
                                                                            continue value_reader;
                                                                        case 'b':
                                                                            input[counter++] = '\b';
                                                                            continue value_reader;
                                                                        case 'f':
                                                                            input[counter++] = '\f';
                                                                            continue value_reader;
                                                                        case '\\':
                                                                            input[counter++] = '\\';
                                                                            continue value_reader;
                                                                        case '"':
                                                                            input[counter++] = '"';
                                                                            continue value_reader;
                                                                        case 'u':
                                                                            int end = index + 4;
                                                                            int value = 0;
                                                                            loop:
                                                                            while (++index < end)
                                                                                switch (input[index]) {
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
                                                                                        value = (value << 4) | (input[index] - 48);
                                                                                        continue loop;
                                                                                    default:
                                                                                        throw new RuntimeException();
                                                                                }
                                                                            input[counter++] = (char) value;
                                                                            index = end;
                                                                            continue value_reader;

                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NORMAL:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case '\\':
                                                                                state = SCREEN;
                                                                                continue value_reader;
                                                                            case '"':

                                                                                result.add(new UnmodifiableMapEntry(key,
                                                                                        new String(input, str_start, counter - str_start),
                                                                                        WRAP));

                                                                                state = VALUE_TAIL;
                                                                                continue main;

                                                                            default:
                                                                                input[counter++] = input[index];
                                                                        }
                                                                default:
                                                                    throw new RuntimeException();
                                                            }

                                                    default:
                                                        throw new RuntimeException();
                                                }


                                        default:
                                            input[key_counter++] = input[index];
                                    }
                            default:
                                throw new RuntimeException();
                        }


                case VALUE_TAIL:
                    value_tail:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_tail;
                            case '}':
                                break main;
                            case ',':
                                state = KEY_HEAD;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                default:
                    throw new RuntimeException();
            }

        return result;
    }

    private static int object_validate(char[] input, int index) {
        int state = INIT;

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue init;
                            case '{':
                                state = SPACE_OR_END;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case SPACE_OR_END:

                    space_end:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue space_end;
                            case '}':
                                return index;
                            case '"':
                                state = KEY_READ;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case KEY_HEAD:
                    key_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue key_head;
                            case '"':
                                state = KEY_READ;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                case KEY_READ:
                    state = NORMAL;
                    reader:
                    while (true)
                        switch (state) {
                            case SCREEN:
                                state = NORMAL;
                                switch (input[++index]) {
                                    case 'r':
                                    case 'n':
                                    case 'f':
                                    case 't':
                                    case 'b':
                                    case '\\':
                                    case '"':
                                        continue reader;
                                    case 'u':
                                        int end = index + 4;
                                        loop:
                                        while (++index < end)
                                            switch (input[index]) {
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
                                                    continue loop;
                                                default:
                                                    throw new RuntimeException();
                                            }
                                        continue reader;

                                    default:
                                        throw new RuntimeException();
                                }

                            case NORMAL:
                                while (true)
                                    switch (input[++index]) {
                                        case '\\':
                                            state = SCREEN;
                                            continue reader;
                                        case '"':
                                            divider:
                                            while (true)
                                                switch (input[++index]) {
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                        continue divider;
                                                    case ':':
                                                        break divider;
                                                    default:
                                                        throw new RuntimeException();
                                                }

                                            value_head:
                                            while (true)
                                                switch (input[++index]) {
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                        continue value_head;
                                                    default:
                                                        index--;
                                                        break value_head;
                                                }

                                            while (true)
                                                switch (input[++index]) {

                                                    case '[':
                                                        index = array_validate(input, index - 1);
                                                        state = VALUE_TAIL;
                                                        continue main;
                                                    case '{':
                                                        index = object_validate(input, index - 1);
                                                        state = VALUE_TAIL;
                                                        continue main;

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
                                                        --index;
                                                        state = NUM_INIT;
                                                        num_main:
                                                        while (true)
                                                            switch (state) {
                                                                case NUM_INIT:
                                                                    switch (input[++index]) {
                                                                        case '-':
                                                                            state = NUMBER_PREFIX;
                                                                            continue num_main;

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
                                                                            state = NUMBER_PREFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NUMBER_PREFIX:
                                                                    switch (input[++index]) {
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
                                                                            state = NUMBER_PREFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }
                                                                case NUMBER_PREFIX_OR_END:

                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case '.':
                                                                                state = NUMBER_SUFFIX;
                                                                                continue num_main;
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }

                                                                case NUMBER_SUFFIX:
                                                                    switch (input[++index]) {
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
                                                                            state = NUMBER_SUFFIX_OR_END;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NUMBER_SUFFIX_OR_END:

                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case 'e':
                                                                            case 'E':
                                                                                state = PLUS_MINUS_OR_NUM;
                                                                                continue num_main;
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }
                                                                case PLUS_MINUS_OR_NUM:
                                                                    switch (input[++index]) {
                                                                        case '+':
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
                                                                            state = EXP;
                                                                            continue num_main;
                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case EXP:
                                                                    loop:
                                                                    while (true)
                                                                        switch (input[++index]) {
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
                                                                                continue loop;
                                                                            case ' ':
                                                                            case '\r':
                                                                            case '\n':
                                                                            case '\f':
                                                                            case '\t':
                                                                            case '\b':
                                                                            case ',':
                                                                            case ']':
                                                                            case '}':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }
                                                            }

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'n':
                                                        if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                                            throw new RuntimeException();

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 't':
                                                        if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                                            throw new RuntimeException();

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'f':
                                                        if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                            throw new RuntimeException();

                                                        index += 4;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '"':

                                                        state = NORMAL;
                                                        value_reader:
                                                        while (true)
                                                            switch (state) {
                                                                case SCREEN:
                                                                    state = NORMAL;
                                                                    switch (input[++index]) {
                                                                        case 'r':
                                                                        case 'n':
                                                                        case 't':
                                                                        case 'b':
                                                                        case 'f':
                                                                        case '\\':
                                                                        case '"':
                                                                            continue value_reader;
                                                                        case 'u':
                                                                            int end = index + 4;
                                                                            loop:
                                                                            while (++index < end)
                                                                                switch (input[index]) {
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
                                                                                        continue loop;
                                                                                    default:
                                                                                        throw new RuntimeException();
                                                                                }

                                                                            continue value_reader;

                                                                        default:
                                                                            throw new RuntimeException();
                                                                    }

                                                                case NORMAL:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case '\\':
                                                                                state = SCREEN;
                                                                                continue value_reader;
                                                                            case '"':
                                                                                state = VALUE_TAIL;
                                                                                continue main;

                                                                            default:
                                                                        }
                                                                default:
                                                                    throw new RuntimeException();
                                                            }

                                                    default:
                                                        throw new RuntimeException();
                                                }


                                        default:
                                    }
                            default:
                                throw new RuntimeException();
                        }


                case VALUE_TAIL:
                    value_tail:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_tail;
                            case '}':
                                return index;
                            case ',':
                                state = KEY_HEAD;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }
                default:
                    throw new RuntimeException();
            }
    }

    private static int array_validate(char[] input, int index) {
        int state = INIT;

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue init;
                            case '[':
                                state = SPACE_OR_END;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case SPACE_OR_END:

                    space_end:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue space_end;
                            case ']':
                                return index;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }

                case VALUE_HEAD:
                    value_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_head;
                            default:
                                index--;
                                state = VALUE_READ;
                                continue main;
                        }

                case VALUE_TAIL:
                    value_tail:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue value_tail;
                            case ']':
                                return index;
                            case ',':
                                state = VALUE_HEAD;
                                continue main;
                            default:
                                throw new RuntimeException();
                        }

                case VALUE_READ:
                    while (true)
                        switch (input[++index]) {

                            case '[':
                                index = array_validate(input, index - 1);
                                state = VALUE_TAIL;
                                continue main;

                            case '{':
                                index = object_validate(input, index - 1);
                                state = VALUE_TAIL;
                                continue main;
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
                                --index;
                                state = NUM_INIT;
                                num_main:
                                while (true)
                                    switch (state) {
                                        case NUM_INIT:
                                            switch (input[++index]) {
                                                case '-':
                                                    state = NUMBER_PREFIX;
                                                    continue num_main;

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
                                                    state = NUMBER_PREFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NUMBER_PREFIX:
                                            switch (input[++index]) {
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
                                                    state = NUMBER_PREFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }
                                        case NUMBER_PREFIX_OR_END:

                                            loop:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '.':
                                                        state = NUMBER_SUFFIX;
                                                        continue num_main;
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }

                                        case NUMBER_SUFFIX:
                                            switch (input[++index]) {
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
                                                    state = NUMBER_SUFFIX_OR_END;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NUMBER_SUFFIX_OR_END:

                                            loop:
                                            while (true)
                                                switch (input[++index]) {
                                                    case 'e':
                                                    case 'E':
                                                        state = PLUS_MINUS_OR_NUM;
                                                        continue num_main;
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }
                                        case PLUS_MINUS_OR_NUM:
                                            switch (input[++index]) {
                                                case '+':
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
                                                    state = EXP;
                                                    continue num_main;
                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case EXP:
                                            loop:
                                            while (true)
                                                switch (input[++index]) {
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
                                                        continue loop;
                                                    case ' ':
                                                    case '\r':
                                                    case '\n':
                                                    case '\f':
                                                    case '\t':
                                                    case '\b':
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }
                                    }
                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                    throw new RuntimeException();

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                    throw new RuntimeException();

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                    throw new RuntimeException();

                                index += 4;
                                state = VALUE_TAIL;
                                continue main;

                            case '"':

                                state = NORMAL;
                                reader:
                                while (true)
                                    switch (state) {
                                        case SCREEN:
                                            state = NORMAL;
                                            switch (input[++index]) {
                                                case 'r':
                                                case 'b':
                                                case 'f':
                                                case 'n':
                                                case 't':
                                                case '\\':
                                                case '"':
                                                    continue reader;

                                                case 'u':
                                                    int end = index + 4;
                                                    loop:
                                                    while (++index < end)
                                                        switch (input[index]) {
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
                                                                continue loop;
                                                            default:
                                                                throw new RuntimeException();
                                                        }

                                                    continue reader;

                                                default:
                                                    throw new RuntimeException();
                                            }

                                        case NORMAL:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '\\':
                                                        state = SCREEN;
                                                        continue reader;
                                                    case '"':
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    default:
                                                }
                                        default:
                                            throw new RuntimeException();
                                    }

                            default:
                                throw new RuntimeException();
                        }

            }
    }


    interface JsonBuilder {

        JsonBuilder put(String key, int value);

        JsonBuilder put(String key, float value);

        JsonBuilder put(String key, double value);

        JsonBuilder put(String key, boolean value);

        JsonBuilder put(String key, String value);

        JsonBuilder put(String key, Map<String, ?> map);

        JsonBuilder put(String key, Iterable<?> iterable);

        Map<String, String> build();
    }

    interface ArrayBuilder {

        ArrayBuilder put(int value);

        ArrayBuilder put(float value);

        ArrayBuilder put(double value);

        ArrayBuilder put(boolean value);

        ArrayBuilder put(String value);

        ArrayBuilder put(Map<String, ?> map);

        ArrayBuilder put(Iterable<?> iterable);

        List<String> build();
    }

    public static JsonBuilder object() {
        return object(new HashMap<>());
    }

    public static JsonBuilder object(@NotNull Map<String, ?> map) {
        JsonBuilder sub = new JsonBuilder() {

            private final Set<Map.Entry<String, String>> source = new HashSet<>();

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, int value) {

                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(value),
                        SAME));

                return this;
            }

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, float value) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(value),
                        SAME));
                return this;
            }

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, double value) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(value),
                        SAME));
                return this;
            }

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, boolean value) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(value),
                        SAME));
                return this;
            }

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, String value) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(value),
                        WRAP));
                return this;
            }


            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, Map<String, ?> map) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(String.valueOf(object(map).build())),
                        SAME));

                return this;
            }

            @Contract("_, _ -> this")
            @Override
            public JsonBuilder put(String key, Iterable<?> iterable) {
                source.add(new UnmodifiableMapEntry(key,
                        String.valueOf(String.valueOf(array(iterable).build())),
                        SAME));
                return this;
            }

            @NotNull
            @Contract(value = " -> new", pure = true)
            @Override
            public Map<String, String> build() {
                return new UnmodifiableJsonMap(source);
            }
        };

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Integer)
                sub.put(entry.getKey(), (int) value);
            else if (value instanceof Float)
                sub.put(entry.getKey(), (float) value);
            else if (value instanceof Double)
                sub.put(entry.getKey(), (double) value);
            else if (value instanceof Boolean)
                sub.put(entry.getKey(), (boolean) value);
            else if (value instanceof String)
                sub.put(entry.getKey(), (String) value);
            else if (value instanceof Iterable<?>)
                sub.put(entry.getKey(), (Iterable<?>) value);
            else if (value instanceof Map<?, ?>) {
                Map<String, Object> copy = new HashMap<>();
                for (Map.Entry<?, ?> inner : ((Map<?, ?>) value).entrySet())
                    copy.put(String.valueOf(inner.getKey()), inner.getValue());
                sub.put(entry.getKey(), copy);
            }
            else if (value != null)
                throw new UnsupportedOperationException("Unsupported type");

        }
        return sub;
    }

    public static ArrayBuilder array() {
        return array(new ArrayList<>());
    }

    public static ArrayBuilder array(@NotNull Iterable<?> iterable) {

        List<Map.Entry<String, Function<String, String>>> mapping = new ArrayList<>();
        ArrayBuilder sub = new ArrayBuilder() {

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(int value) {
                mapping.add(new UnmodifiableListEntry(String.valueOf(value), SAME));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(float value) {
                mapping.add(new UnmodifiableListEntry(String.valueOf(value), SAME));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(double value) {
                mapping.add(new UnmodifiableListEntry(String.valueOf(value), SAME));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(boolean value) {
                mapping.add(new UnmodifiableListEntry(String.valueOf(value), SAME));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(String value) {
                mapping.add(new UnmodifiableListEntry(String.valueOf(value), WRAP));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(Map<String, ?> map) {
                String object = String.valueOf(object(map).build());
                mapping.add(new UnmodifiableListEntry(String.valueOf(object), SAME));
                return this;
            }

            @Contract("_ -> this")
            @Override
            public ArrayBuilder put(Iterable<?> iterable) {
                String array = String.valueOf(array(iterable).build());
                mapping.add(new UnmodifiableListEntry(String.valueOf(array), SAME));
                return this;
            }

            @Contract(value = " -> new", pure = true)
            @NotNull
            @Override
            public List<String> build() {
                return new UnmodifiableJsonList(mapping);
            }
        };

        for (Object value : iterable) {
            if (value instanceof Integer)
                sub.put((int) value);
            else if (value instanceof Float)
                sub.put((float) value);
            else if (value instanceof Double)
                sub.put((double) value);
            else if (value instanceof Boolean)
                sub.put((boolean) value);
            else if (value instanceof String)
                sub.put((String) value);
            else if (value instanceof Iterable<?>)
                sub.put((Iterable<?>) value);
            else if (value instanceof Map<?, ?>) {
                Map<String, Object> copy = new HashMap<>();
                for (Map.Entry<?, ?> inner : ((Map<?, ?>) value).entrySet())
                    copy.put(String.valueOf(inner.getKey()), inner.getValue());
                sub.put(copy);

            }
            else if (value == null)
                mapping.add(new UnmodifiableListEntry(null, i -> null));
            else
                throw new UnsupportedOperationException("Unsupported type");

        }

        return sub;
    }

   public final static class UnmodifiableJsonList extends ArrayList<String> {

        private final List<Map.Entry<String, Function<String, String>>> mapping;

       @Contract(pure = true)
       UnmodifiableJsonList(@NotNull String input) {
           this(input.toCharArray());
       }

       @Contract(pure = true)
       UnmodifiableJsonList(@NotNull char[] input) {
           this(parse_array(input));
       }

        @Contract(pure = true)
        UnmodifiableJsonList(@NotNull List<Map.Entry<String, Function<String, String>>> mapping) {
            List<String> source = new ArrayList<>();
            for (Map.Entry<String, Function<String, String>> entry : mapping)
                source.add(entry.getKey());
            super.addAll(source);
            this.mapping = mapping;
        }

        @Contract(pure = true)
        @Override
        public String toString() {
            List<String> content = new ArrayList<>();
            for (Map.Entry<String, Function<String, String>> entry : mapping)
                content.add(entry.getValue().apply(entry.getKey()));

            return String.format("[%s]", String.join(",", content));
        }

        @Contract(value = " -> new", pure = true)
        @NotNull
        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                int index;

                @Override
                public boolean hasNext() {
                    return index < size();
                }

                @Override
                public String next() {
                    return get(index++);
                }
            };
        }

        @Contract("_ -> fail")
        @Override
        public boolean add(String s) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public boolean addAll(Collection<? extends String> c) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public boolean addAll(int index, Collection<? extends String> c) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public void add(int index, String element) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public String remove(int index) {
            throw new UnsupportedOperationException();
        }


        @Contract(pure = true)
        @NotNull
        @Override
        public ListIterator<String> listIterator() {
            return listIterator(0);
        }

        @Contract(value = "_ -> new", pure = true)
        @NotNull
        @Override
        public ListIterator<String> listIterator(int in) {
            return new ListIterator<String>() {
                int index = in;

                @Override
                public boolean hasNext() {
                    return index < size();
                }

                @Override
                public String next() {
                    return get(index++);
                }

                @Override
                public boolean hasPrevious() {
                    return index > 0;
                }

                @Override
                public String previous() {
                    return get(--index);
                }

                @Override
                public int nextIndex() {
                    return hasNext() ? index + 1 : -1;
                }

                @Override
                public int previousIndex() {
                    return hasPrevious() ? index - 1 : -1;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void set(String s) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(String s) {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Contract("_ -> fail")
        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Contract(" -> fail")
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public String set(int index, String element) {
            throw new UnsupportedOperationException();
        }
    }

    final static class UnmodifiableMapEntry implements Map.Entry<String, String> {

        private final String key;
        private final String value;
        private final Function<String, String> mapping;

        @Contract(pure = true)
        UnmodifiableMapEntry(String key, String value, Function<String, String> mapping) {
            this.key = key;
            this.value = value;
            this.mapping = mapping;
        }

        @Contract(pure = true)
        @Override
        public String getKey() {
            return key;
        }

        @Contract(pure = true)
        @Override
        public String getValue() {
            return value;
        }


        @Contract("_ -> fail")
        @Override
        public String setValue(String value) {
            throw new UnsupportedOperationException();
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(Object in) {
            if (!(in instanceof Map.Entry)) return false;
            Map.Entry entry = (Map.Entry) in;
            return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), value);
        }

        @Override
        public int hashCode() {
            return 13 * UnmodifiableMapEntry.class.hashCode() +
                    19 * key.hashCode();
        }

        @Override
        public String toString() {
            return String.format("%s:%s", wrap(key), mapping.apply(value));
        }
    }

    final static class UnmodifiableListEntry implements Map.Entry<String, Function<String, String>> {

        private final String key;
        private final Function<String, String> mapping;

        @Contract(pure = true)
        UnmodifiableListEntry(String key, Function<String, String> mapping) {
            this.key = key;
            this.mapping = mapping;
        }

        @Contract(pure = true)
        @Override
        public String getKey() {
            return key;
        }

        @Contract(pure = true)
        @Override
        public Function<String, String> getValue() {
            return mapping;
        }


        @Contract("_ -> fail")
        @Override
        public Function<String, String> setValue(Function<String, String> value) {
            throw new UnsupportedOperationException();
        }

        @Contract(value = "null -> false", pure = true)
        @Override
        public boolean equals(Object in) {
            if (!(in instanceof Map.Entry)) return false;
            Map.Entry entry = (Map.Entry) in;
            return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), mapping);
        }

        @Override
        public int hashCode() {
            return 13 * UnmodifiableMapEntry.class.hashCode() +
                    19 * key.hashCode() +
                    23 * mapping.hashCode();
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", key, mapping.apply(key));
        }
    }

    public final static class UnmodifiableJsonMap implements Map<String, String> {

        private final Set<Map.Entry<String, String>> source;

        @Contract(pure = true)
       public UnmodifiableJsonMap(String  input) {
            this(input.toCharArray());
        }

        @Contract(pure = true)
       public UnmodifiableJsonMap(char[] input) {
            this(parse_object(input));
        }

        @Contract(pure = true)
        UnmodifiableJsonMap(Set<Map.Entry<String, String>> source) {
            this.source = Collections.unmodifiableSet(source);
        }

        @Contract(pure = true)
        @Override
        public String toString() {
            List<String> content = new ArrayList<>();
            for (Entry<String, String> entry : entrySet())
                content.add(entry.toString());

            return String.format("{%s}", String.join(",", content));
        }

        @Contract(pure = true)
        @Override
        public int size() {
            return source.size();
        }

        @Contract(pure = true)
        @Override
        public boolean isEmpty() {
            return source.isEmpty();
        }

        @Contract(pure = true)
        @Override
        public boolean containsKey(Object key) {
            for (Map.Entry<String, String> entry : source)
                if (entry.getKey().equals(key)) return true;
            return false;
        }

        @Contract(pure = true)
        @Override
        public boolean containsValue(Object value) {
            for (Map.Entry<String, String> entry : source)
                if (entry.getValue().equals(value)) return true;
            return false;
        }

        @Nullable
        @Override
        public String get(Object key) {
            for (Map.Entry<String, String> entry : source)
                if (entry.getKey().equals(key)) return entry.getValue();
            return null;
        }


        @Contract("_, _ -> fail")
        @Override
        public String put(String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public String remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _, _ -> fail")
        @Override
        public boolean replace(String key, String oldValue, String newValue) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public String replace(String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public void putAll(Map<? extends String, ? extends String> m) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public String putIfAbsent(String key, String value) {
            throw new UnsupportedOperationException();
        }

        @Contract(" -> fail")
        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Contract(pure = true)
        @Override
        public Set<String> keySet() {
            Set<String> keys = new HashSet<>();
            for (Map.Entry<String, String> entry : source)
                keys.add(entry.getKey());

            return keys;
        }

        @NotNull
        @Contract(pure = true)
        @Override
        public Collection<String> values() {
            Collection<String> values = new ArrayList<>();
            for (Map.Entry<String, String> entry : source) {
                String key = entry.getValue();
                if (!values.contains(key))
                    values.add(key);
            }

            return values;
        }

        @NotNull
        @Contract(pure = true)
        @Override
        public Set<Entry<String, String>> entrySet() {
            return source;
        }

        @Contract(pure = true)
        @Override
        public boolean equals(Object in) {
            return in instanceof UnmodifiableJsonMap && in.hashCode() == hashCode();
        }

        @Contract(pure = true)
        @Override
        public int hashCode() {
            int sum = 0;
            for (Entry<String, String> entry : source)
                sum += 19 * entry.hashCode();

            return 13 * UnmodifiableJsonMap.class.hashCode() + sum;
        }
    }

    interface Function<I, O> {
        O apply(I in);
    }

    static Function<String, String> SAME = i -> i;
    static Function<String, String> WRAP = Json::wrap;
}
