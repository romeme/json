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
public class Json {

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

    public interface Map extends java.util.Map<String, String> {
        <O> O get(String key, Function<String, O> mapper);
    }

    public interface List extends java.util.List<String> {
        <O> O get(int index, Function<String, O> mapper);
    }

    public static class Object implements Map {

        @NotNull
        @Contract(pure = true)
        public static Map parse(@NotNull String input) {
            return parse(input.toCharArray());
        }

        @NotNull
        @Contract(pure = true)
        public static Map parse(@NotNull char[] input) {
            return new Object(parse_object(input));
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
                                                            int array_end = Array.validate(input, index - 1);

                                                            result.add(new UnmodifiableEntry(key,
                                                                    new String(input, index, array_end - index + 1),
                                                                    SAME));

                                                            index = array_end;
                                                            state = VALUE_TAIL;
                                                            continue main;

                                                        case '{':
                                                            int object_end = validate(input, index - 1);

                                                            result.add(new UnmodifiableEntry(key,
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


                                                            result.add(new UnmodifiableEntry(key,
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

                                                            result.add(new UnmodifiableEntry(key,
                                                                    "true",
                                                                    SAME));

                                                            index += 3;
                                                            state = VALUE_TAIL;
                                                            continue main;

                                                        case 'f':
                                                            if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                                throw new RuntimeException();

                                                            result.add(new UnmodifiableEntry(key,
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

                                                                                    result.add(new UnmodifiableEntry(key,
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

        private static int validate(char[] input, int index) {
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
                                                            index = Array.validate(input, index - 1);
                                                            state = VALUE_TAIL;
                                                            continue main;
                                                        case '{':
                                                            index = validate(input, index - 1);
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

        public interface Builder {

            Builder put(String key, int value);

            Builder put(String key, float value);

            Builder put(String key, double value);

            Builder put(String key, boolean value);

            Builder put(String key, String value);

            Builder put(String key, java.util.Map<String, ?> map);

            Builder put(String key, Iterable<?> iterable);

            Map build();
        }

        public static Builder builder() {
            return builder(new HashMap<String, java.lang.Object>());
        }

        public static Builder builder(@NotNull java.util.Map<String, ?> map) {
            Builder sub = new Builder() {

                private final Set<Map.Entry<String, String>> source = new HashSet<>();

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, int value) {

                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(value),
                            SAME));

                    return this;
                }

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, float value) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(value),
                            SAME));
                    return this;
                }

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, double value) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(value),
                            SAME));
                    return this;
                }

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, boolean value) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(value),
                            SAME));
                    return this;
                }

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, String value) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(value),
                            WRAP));
                    return this;
                }


                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, java.util.Map<String, ?> map) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(String.valueOf(builder(map).build())),
                            SAME));

                    return this;
                }

                @Contract("_, _ -> this")
                @Override
                public Builder put(String key, Iterable<?> iterable) {
                    source.add(new UnmodifiableEntry(key,
                            String.valueOf(String.valueOf(Array.builder(iterable).build())),
                            SAME));
                    return this;
                }

                @NotNull
                @Contract(value = " -> new", pure = true)
                @Override
                public Map build() {
                    return new Object(source);
                }
            };

            for (Map.Entry<String, ?> entry : map.entrySet()) {
                java.lang.Object value = entry.getValue();

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
                else if (value instanceof java.util.Map<?, ?>) {
                    java.util.Map<String, java.lang.Object> copy = new HashMap<>();
                    for (Map.Entry<?, ?> inner : ((java.util.Map<?, ?>) value).entrySet())
                        copy.put(String.valueOf(inner.getKey()), inner.getValue());
                    sub.put(entry.getKey(), copy);
                }
                else if (value != null)
                    throw new UnsupportedOperationException("Unsupported type");

            }
            return sub;
        }

        final static class UnmodifiableEntry implements Map.Entry<String, String> {

            private final String key;
            private final String value;
            private final Function<String, String> mapping;

            @Contract(pure = true)
            UnmodifiableEntry(String key, String value, Function<String, String> mapping) {
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
            public boolean equals(java.lang.Object in) {
                if (!(in instanceof Map.Entry)) return false;
                Map.Entry entry = (Map.Entry) in;
                return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), value);
            }


            @Override
            public int hashCode() {
                return 13 * UnmodifiableEntry.class.hashCode() +
                        19 * key.hashCode();
            }

            @Override
            public String toString() {
                return String.format("%s:%s", wrap(key), mapping.apply(value));
            }
        }

        private final Set<Map.Entry<String, String>> source;

        @Contract(pure = true)
        public Object(String input) {
            this(input.toCharArray());
        }

        @Contract(pure = true)
        public Object(char[] input) {
            this(parse_object(input));
        }

        @Contract(pure = true)
        Object(Set<Map.Entry<String, String>> source) {
            this.source = Collections.unmodifiableSet(source);
        }

        @Contract(pure = true)
        @Override
        public String toString() {
            java.util.List<String> content = new ArrayList<>();
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
        public boolean containsKey(java.lang.Object key) {
            for (Map.Entry<String, String> entry : source)
                if (entry.getKey().equals(key)) return true;
            return false;
        }

        @Contract(pure = true)
        @Override
        public boolean containsValue(java.lang.Object value) {
            for (Map.Entry<String, String> entry : source)
                if (entry.getValue().equals(value)) return true;
            return false;
        }

        @Override
        public <O> O get(String key, Function<String, O> mapper) {
            return mapper.apply(get(key));
        }

        @Nullable
        @Override
        public String get(java.lang.Object key) {
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
        public String remove(java.lang.Object key) {
            throw new UnsupportedOperationException();
        }

        @Contract("_, _ -> fail")
        @Override
        public boolean remove(java.lang.Object key, java.lang.Object value) {
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
        public void putAll(java.util.Map<? extends String, ? extends String> m) {
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
        public boolean equals(java.lang.Object in) {
            return in instanceof Map && in.hashCode() == hashCode();
        }

        @Contract(pure = true)
        @Override
        public int hashCode() {
            int sum = 0;
            for (Entry<String, String> entry : source)
                sum += 19 * entry.hashCode();

            return 13 * Map.class.hashCode() + sum;
        }


    }

    public static class Array implements List {
        @NotNull
        @Contract(pure = true)
        public static List parse(@NotNull String input) {
            return parse(input.toCharArray());
        }

        @NotNull
        @Contract("_ -> new")
        public static List parse(@NotNull char[] input) {
            return new Array(parse_array(input));
        }

        @NotNull
        @Contract(value = "_ -> new", pure = true)
        private static java.util.List<UnmodifiableEntry> parse_array(@NotNull char[] input) {
            int index = -1;
            int state = INIT;

            java.util.List<UnmodifiableEntry> mapping = new ArrayList<>();

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
                                    int array_end = Array.validate(input, index - 1);
                                    String array = new String(input, index, array_end - index + 1);
                                    mapping.add(new UnmodifiableEntry(array, SAME));
                                    index = array_end;
                                    state = VALUE_TAIL;
                                    continue main;

                                case '{':
                                    int object_end = Object.validate(input, index - 1);
                                    String object = new String(input, index, object_end - index + 1);
                                    mapping.add(new UnmodifiableEntry(object, SAME));

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
                                    mapping.add(new UnmodifiableEntry(number, SAME));

                                    state = VALUE_TAIL;
                                    continue main;

                                case 'n':
                                    if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                        throw new RuntimeException();

                                    mapping.add(new UnmodifiableEntry(null, NULL));

                                    index += 3;
                                    state = VALUE_TAIL;
                                    continue main;

                                case 't':
                                    if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                        throw new RuntimeException();

                                    mapping.add(new UnmodifiableEntry("true", SAME));

                                    index += 3;
                                    state = VALUE_TAIL;
                                    continue main;

                                case 'f':
                                    if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                        throw new RuntimeException();

                                    mapping.add(new UnmodifiableEntry("false", SAME));

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
                                                            mapping.add(new UnmodifiableEntry(string, WRAP));

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

        private static int validate(char[] input, int index) {
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
                                    index = validate(input, index - 1);
                                    state = VALUE_TAIL;
                                    continue main;

                                case '{':
                                    index = Object.validate(input, index - 1);
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

        public interface Builder {

            Builder put(int value);

            Builder put(float value);

            Builder put(double value);

            Builder put(boolean value);

            Builder put(String value);

            Builder put(java.util.Map<String, ?> map);

            Builder put(Iterable<?> iterable);

            List build();
        }

        public static Builder builder() {
            return builder(new ArrayList<>());
        }

        public static Builder builder(@NotNull Iterable<?> iterable) {

            java.util.List<UnmodifiableEntry> mapping = new ArrayList<>();
            Builder sub = new Builder() {

                @Contract("_ -> this")
                @Override
                public Builder put(int value) {
                    mapping.add(new UnmodifiableEntry(String.valueOf(value), SAME));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(float value) {
                    mapping.add(new UnmodifiableEntry(String.valueOf(value), SAME));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(double value) {
                    mapping.add(new UnmodifiableEntry(String.valueOf(value), SAME));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(boolean value) {
                    mapping.add(new UnmodifiableEntry(String.valueOf(value), SAME));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(String value) {
                    mapping.add(new UnmodifiableEntry(String.valueOf(value), WRAP));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(java.util.Map<String, ?> map) {
                    String object = String.valueOf(Object.builder(map).build());
                    mapping.add(new UnmodifiableEntry(String.valueOf(object), SAME));
                    return this;
                }

                @Contract("_ -> this")
                @Override
                public Builder put(Iterable<?> iterable) {
                    String array = String.valueOf(builder(iterable).build());
                    mapping.add(new UnmodifiableEntry(String.valueOf(array), SAME));
                    return this;
                }

                @Contract(value = " -> new", pure = true)
                @NotNull
                @Override
                public List build() {
                    return new Array(mapping);
                }
            };

            for (java.lang.Object value : iterable) {
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
                else if (value instanceof java.util.Map<?, ?>) {
                    java.util.Map<String, java.lang.Object> copy = new HashMap<>();
                    for (Map.Entry<?, ?> inner : ((java.util.Map<?, ?>) value).entrySet())
                        copy.put(String.valueOf(inner.getKey()), inner.getValue());
                    sub.put(copy);

                }
                else if (value == null)
                    mapping.add(new UnmodifiableEntry(null, NULL));
                else
                    throw new UnsupportedOperationException("Unsupported type");

            }

            return sub;
        }


        private final UnmodifiableEntry[] mapping;

        @Contract(pure = true)
        public Array(@NotNull String input) {
            this(input.toCharArray());
        }

        @Contract(pure = true)
        public Array(@NotNull char[] input) {
            this(parse_array(input));
        }

        @Contract(pure = true)
        Array(@NotNull java.util.List<UnmodifiableEntry> mapping) {
            this.mapping = mapping.toArray(new UnmodifiableEntry[0]);
        }

        @Contract(pure = true)
        @Override
        public String toString() {
            java.util.List<String> content = new ArrayList<>();
            for (Map.Entry<String, Function<String, String>> entry : mapping)
                content.add(entry.getValue().apply(entry.getKey()));

            return String.format("[%s]", String.join(",", content));
        }

        @Override
        public int size() {
            return mapping.length;
        }

        @Override
        public boolean isEmpty() {
            return mapping.length == 0;
        }

        @Override
        public boolean contains(java.lang.Object o) {
            for (String key : this)
                if (Objects.equals(key, o)) return true;

            return false;
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
                    return mapping[index++].getKey();
                }
            };
        }

        @NotNull
        @Override
        public java.lang.Object[] toArray() {
            String[] out = new String[mapping.length];
            for (int index = 0; index < size(); index++) {
                out[index] = get(index);
            }

            return out;
        }

        @SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy", "ConstantConditions"})
        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] input) {
            int size = size();
            String[] elementData = new String[mapping.length];
            for (int index = 0; index < size; index++)
                elementData[index] =get(index);

            if (input.length < mapping.length)
                // Make a new array of a's runtime type, but my contents:
                return (T[]) Arrays.copyOf(elementData, size, input.getClass());
            System.arraycopy(elementData, 0, input, 0, size);
            if (input.length > size)
                input[size] = null;
            return input;
        }

        @Override
        public boolean equals(java.lang.Object in) {
           if (!( in instanceof java.util.List)) return false;

           List other = (List)in;

           return size() == other.size() && containsAll(other);
        }

        @Contract("_ -> fail")
        @Override
        public boolean add(String s) {
            throw new UnsupportedOperationException();
        }

        @Contract("_ -> fail")
        @Override
        public boolean remove(java.lang.Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> collection) {

            main:
            for (java.lang.Object other : collection) {
                for (String me : this) {
                    if (Objects.equals(me, other))
                        continue main;
                }
                return false;
            }

            return true;
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

        @Override
        public int indexOf(java.lang.Object o) {
            for (int index = 0; index < size(); index++)
                if (Objects.equals(mapping[index].getKey(), o))
                    return index;
            return -1;
        }

        @Override
        public int lastIndexOf(java.lang.Object o) {
            for (int index = size() - 1; index <= 0; index--)
                if (Objects.equals(mapping[index].getKey(), o))
                    return index;
            return -1;
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
            if (in >= size() || in < 0)
                throw new RuntimeException();

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

        @NotNull
        @Override
        public java.util.List<String> subList(int fromIndex, int toIndex) {
            Array.UnmodifiableEntry[] copy = Arrays.copyOfRange(mapping, fromIndex, toIndex);

            java.util.List<String> sub = new ArrayList<>();
            for (UnmodifiableEntry aCopy : copy)
                sub.add(aCopy.getKey());

            return sub;
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

        @Override
        public <O> O get(int index, Function<String, O> mapper) {
            return mapper.apply(mapping[index].getKey());
        }

        @Override
        public String get(int index) {
            return mapping[index].getKey();
        }

        @Contract("_, _ -> fail")
        @Override
        public String set(int index, String element) {
            throw new UnsupportedOperationException();
        }

        final static class UnmodifiableEntry implements Map.Entry<String, Function<String, String>> {

            private final String key;
            private final Function<String, String> mapping;

            @Contract(pure = true)
            UnmodifiableEntry(String key, Function<String, String> mapping) {
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
            public boolean equals(java.lang.Object in) {
                if (!(in instanceof Map.Entry)) return false;
                Map.Entry entry = (Map.Entry) in;
                return Objects.equals(entry.getKey(), key) && Objects.equals(entry.getValue(), mapping);
            }

            @Override
            public int hashCode() {
                return 13 * UnmodifiableEntry.class.hashCode() +
                        19 * key.hashCode() +
                        23 * mapping.hashCode();
            }

            @Override
            public String toString() {
                return String.format("%s -> %s", key, mapping.apply(key));
            }
        }
    }


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


    interface Function<I, O> {
        O apply(I in);
    }

    static Function<String, String> SAME = in -> in;
    static Function<String, String> NULL = in -> "null";
    static Function<String, String> WRAP = Json::wrap;
}
