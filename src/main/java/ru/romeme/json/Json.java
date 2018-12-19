package ru.romeme.json;

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

    public static Map<String, String> map(String input) {
        return map(input.toCharArray());
    }

    public static Map<String, String> map(char[] input) {
        int index = -1;
        int state = INIT;

        Map<String, String> result = new HashMap<>();

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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                        while (++index <= end)
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
                                                case 'a':
                                                case 'b':
                                                case 'c':
                                                case 'd':
                                                case 'e':
                                                case 'f':
                                                    value = (value << 4) | (input[index] - 87);
                                                    continue loop;
                                                case 'A':
                                                case 'B':
                                                case 'C':
                                                case 'D':
                                                case 'E':
                                                case 'F':
                                                    value = (value << 4) | (input[index] - 55);
                                                    continue loop;
                                                default:
                                                    throw new ParseException(index, input);
                                            }
                                        input[key_counter++] = (char) value;
                                        index = end;
                                        continue reader;

                                    default:
                                        throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                        int array_end = validate_array(input, index - 1);

                                                        result.put(key, new String(input, index, array_end - index + 1)); //TODO SAME

                                                        index = array_end;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '{':
                                                        int object_end = validate_map(input, index - 1);

                                                        result.put(key, new String(input, index, object_end - index + 1)); //TODO SAME

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
                                                                            throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
                                                                        }
                                                            }

                                                        result.put(key, new String(input, start, index + 1 - start)); //TODO SAME

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'n':
                                                        if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                                            throw new ParseException(index, input);

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 't':
                                                        if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                                            throw new ParseException(index, input);

                                                        result.put(key, "true"); // TODO SAME

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'f':
                                                        if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                            throw new ParseException(index, input);

                                                        result.put(key, "false"); //TODO SAME

                                                        index += 4;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '"':
                                                        int str_start = index;
                                                        int counter = index;

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
                                                                            while (++index <= end)
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
                                                                                    case 'a':
                                                                                    case 'b':
                                                                                    case 'c':
                                                                                    case 'd':
                                                                                    case 'e':
                                                                                    case 'f':
                                                                                        value = (value << 4) | (input[index] - 87);
                                                                                        continue loop;
                                                                                    case 'A':
                                                                                    case 'B':
                                                                                    case 'C':
                                                                                    case 'D':
                                                                                    case 'E':
                                                                                    case 'F':
                                                                                        value = (value << 4) | (input[index] - 55);
                                                                                        continue loop;
                                                                                    default:
                                                                                        throw new ParseException(index, input);
                                                                                }
                                                                            input[counter++] = (char) value;
                                                                            index = end;
                                                                            continue value_reader;

                                                                        default:
                                                                            throw new ParseException(index, input);
                                                                    }

                                                                case NORMAL:
                                                                    while (true)
                                                                        switch (input[++index]) {
                                                                            case '\\':
                                                                                state = SCREEN;
                                                                                continue value_reader;
                                                                            case '"':

                                                                                result.put(key, new String(input, str_start, counter - str_start)); //TODO WRAP

                                                                                state = VALUE_TAIL;
                                                                                continue main;

                                                                            default:
                                                                                input[counter++] = input[index];
                                                                        }
                                                                default:
                                                                    throw new ParseException(index, input);
                                                            }

                                                    default:
                                                        throw new ParseException(index, input);
                                                }

                                        default:
                                            input[key_counter++] = input[index];
                                    }
                            default:
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
                        }
                default:
                    throw new ParseException(index, input);
            }

        return Collections.unmodifiableMap(result);
    }

    private static int validate_map(char[] input, int index) {
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                                case 'a':
                                                case 'A':
                                                case 'b':
                                                case 'B':
                                                case 'c':
                                                case 'C':
                                                case 'd':
                                                case 'D':
                                                case 'e':
                                                case 'E':
                                                case 'f':
                                                case 'F':
                                                    continue loop;
                                                default:
                                                    throw new ParseException(index, input);
                                            }
                                        continue reader;

                                    default:
                                        throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                        index = validate_array(input, index - 1);
                                                        state = VALUE_TAIL;
                                                        continue main;
                                                    case '{':
                                                        index = validate_map(input, index - 1);
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
                                                                            throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
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
                                                                            throw new ParseException(index, input);
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
                                                                                throw new ParseException(index, input);
                                                                        }
                                                            }

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'n':
                                                        if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                                            throw new ParseException(index, input);

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 't':
                                                        if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                                            throw new ParseException(index, input);

                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'f':
                                                        if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                            throw new ParseException(index, input);

                                                        index += 4;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case '"':

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
                                                                                    case 'a':
                                                                                    case 'A':
                                                                                    case 'b':
                                                                                    case 'B':
                                                                                    case 'c':
                                                                                    case 'C':
                                                                                    case 'd':
                                                                                    case 'D':
                                                                                    case 'e':
                                                                                    case 'E':
                                                                                    case 'f':
                                                                                    case 'F':
                                                                                        continue loop;
                                                                                    default:
                                                                                        throw new ParseException(index, input);
                                                                                }

                                                                            continue value_reader;

                                                                        default:
                                                                            throw new ParseException(index, input);
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
                                                                    throw new ParseException(index, input);
                                                            }

                                                    default:
                                                        throw new ParseException(index, input);
                                                }

                                        default:
                                    }
                            default:
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
                        }
                default:
                    throw new ParseException(index, input);
            }
    }

    public static List<String> array(String input) {
        return array(input.toCharArray());
    }

    public static List<String> array(char[] input) {
        int index = -1;
        int state = INIT;

        List<String> mapping = new ArrayList<>();

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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
                        }

                case VALUE_READ:
                    while (true)
                        switch (input[++index]) {

                            case '[':
                                int array_end = validate_array(input, index - 1);
                                String array = new String(input, index, array_end - index + 1);
                                mapping.add(array); //TODO SAME
                                index = array_end;
                                state = VALUE_TAIL;
                                continue main;

                            case '{':
                                int object_end = validate_map(input, index - 1);
                                String object = new String(input, index, object_end - index + 1);
                                mapping.add(object);//TODO SAME

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
                                                    throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
                                                }
                                    }

                                String number = new String(input, start, index + 1 - start);
                                mapping.add(number); //TODO SAME

                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                    throw new ParseException(index, input);

                                mapping.add(null);//TODO NULL

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                    throw new ParseException(index, input);

                                mapping.add("true");//TODO SAME

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                    throw new ParseException(index, input);

                                mapping.add("false"); //TODO SAME

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
                                                            case 'a':
                                                            case 'b':
                                                            case 'c':
                                                            case 'd':
                                                            case 'e':
                                                            case 'f':
                                                                value = (value << 4) | (input[index] - 87);
                                                                continue loop;
                                                            case 'A':
                                                            case 'B':
                                                            case 'C':
                                                            case 'D':
                                                            case 'E':
                                                            case 'F':
                                                                value = (value << 4) | (input[index] - 55);
                                                                continue loop;
                                                            default:
                                                                throw new ParseException(index, input);
                                                        }
                                                    input[counter++] = (char) value;
                                                    index = end;
                                                    continue reader;

                                                default:
                                                    throw new ParseException(index, input);
                                            }

                                        case NORMAL:
                                            while (true)
                                                switch (input[++index]) {
                                                    case '\\':
                                                        state = SCREEN;
                                                        continue reader;
                                                    case '"':
                                                        String string = new String(input, str_start, counter - str_start);
                                                        mapping.add(string); //TODO WRAP

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    default:
                                                        input[counter++] = input[index];
                                                }
                                        default:
                                            throw new ParseException(index, input);
                                    }

                            default:
                                throw new ParseException(index, input);
                        }

            }

        return Collections.unmodifiableList(mapping);
    }

    private static int validate_array(char[] input, int index) {
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
                                throw new ParseException(index, input);
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
                                throw new ParseException(index, input);
                        }

                case VALUE_READ:
                    while (true)
                        switch (input[++index]) {

                            case '[':
                                index = validate_array(input, index - 1);
                                state = VALUE_TAIL;
                                continue main;

                            case '{':
                                index = validate_map(input, index - 1);
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
                                                    throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
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
                                                    throw new ParseException(index, input);
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
                                                        throw new ParseException(index, input);
                                                }
                                    }
                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                    throw new ParseException(index, input);

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                    throw new ParseException(index, input);

                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                    throw new ParseException(index, input);

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
                                                            case 'a':
                                                            case 'A':
                                                            case 'b':
                                                            case 'B':
                                                            case 'c':
                                                            case 'C':
                                                            case 'd':
                                                            case 'D':
                                                            case 'e':
                                                            case 'E':
                                                            case 'f':
                                                            case 'F':
                                                                continue loop;
                                                            default:
                                                                throw new ParseException(index, input);
                                                        }

                                                    continue reader;

                                                default:
                                                    throw new ParseException(index, input);
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
                                            throw new ParseException(index, input);
                                    }

                            default:
                                throw new ParseException(index, input);
                        }

            }
    }

    public static Builder builder() {

        return new Builder() {

            private final Map<String, Object> map = new HashMap<>();

            @Override
            public Map<String, ?> build() {
                return Collections.unmodifiableMap(map);
            }

            @Override
            public Builder put(String key, int value) {
                map.put(key, value);
                return this;
            }

            @Override
            public Builder put(String key, float value) {
                map.put(key, value);
                return this;
            }

            @Override
            public Builder put(String key, double value) {
                map.put(key, value);
                return this;
            }

            @Override
            public Builder put(String key, boolean value) {
                map.put(key, value);
                return this;
            }

            @Override
            public Builder put(String key, String value) {
                map.put(key, value);
                return this;
            }

            @Override
            public Builder put(String key, Map<String, ?> value) {
                map.put(key, new HashMap<>(value));
                return this;
            }

            @Override
            public Builder put(String key, Iterable<?> value) {
                List<Object> rs = new ArrayList<>();
                for (Object o : value) rs.add(o);
                map.put(key, rs);
                return this;
            }

        };

    }

    public static String array(Iterable<?> iterable) {
        Iterator<?> iterator = iterable.iterator();
        List<String> rs = new ArrayList<>();
        while (iterator.hasNext()) {
            Object value = iterator.next();
            if (value instanceof Integer
                    || value instanceof Float
                    || value instanceof Double
                    || value instanceof Boolean
                    || value == null)

                rs.add(String.valueOf(value));
            else if (value instanceof String)
                rs.add(wrap(String.valueOf(value)));
            else if (value instanceof Iterable<?>)
                rs.add(array(((Iterable<?>) value)));
            else if (value instanceof Map<?, ?>) {
                rs.add(map((Map<?, ?>) value));
            } else
                return wrap(String.valueOf(value));
        }

        return String.valueOf(rs);
    }

    public static String map(Map<?, ?> map) {
        List<String> rs = new ArrayList<>();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = wrap(String.valueOf(entry.getKey()));
            Object value = entry.getValue();
            if (value instanceof Integer
                    || value instanceof Float
                    || value instanceof Double
                    || value instanceof Boolean
                    || value == null)
                rs.add(String.format(" %s : %s ", key, String.valueOf(value)));

            else if (value instanceof Iterable<?>)
                rs.add(String.format(" %s : %s ", key, array(((Iterable<?>) value))));

            else if (value instanceof Map<?, ?>) {
                rs.add(String.format(" %s : %s ", key, map((Map<?, ?>) value)));

            } else
                rs.add(String.format(" %s : %s ", key, wrap(String.valueOf(value))));
        }

        return String.format("{ %s }", String.join(", ", rs));
    }

     static String wrap(String string) {

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
                    else {
                        switch (c & 0xF) {
                            case 0:
                                builder.append('0');
                                break;
                            case 1:
                                builder.append('1');
                                break;
                            case 2:
                                builder.append('2');
                                break;
                            case 3:
                                builder.append('3');
                                break;
                            case 4:
                                builder.append('4');
                                break;
                            case 5:
                                builder.append('5');
                                break;
                            case 6:
                                builder.append('6');
                                break;
                            case 7:
                                builder.append('8');
                                break;
                            case 9:
                                builder.append('9');
                                break;
                            case 0xA:
                                builder.append('a');
                                break;
                            case 0xB:
                                builder.append('b');
                                break;
                            case 0xC:
                                builder.append('c');
                                break;
                            case 0xD:
                                builder.append('d');
                                break;
                            case 0xE:
                                builder.append('e');
                                break;
                            case 0xF:
                                builder.append('f');
                                break;
                        }
                        switch (c >> 1 & 0xF) {
                            case 0:
                                builder.append('0');
                                break;
                            case 1:
                                builder.append('1');
                                break;
                            case 2:
                                builder.append('2');
                                break;
                            case 3:
                                builder.append('3');
                                break;
                            case 4:
                                builder.append('4');
                                break;
                            case 5:
                                builder.append('5');
                                break;
                            case 6:
                                builder.append('6');
                                break;
                            case 7:
                                builder.append('8');
                                break;
                            case 9:
                                builder.append('9');
                                break;
                            case 0xA:
                                builder.append('a');
                                break;
                            case 0xB:
                                builder.append('b');
                                break;
                            case 0xC:
                                builder.append('c');
                                break;
                            case 0xD:
                                builder.append('d');
                                break;
                            case 0xE:
                                builder.append('e');
                                break;
                            case 0xF:
                                builder.append('f');
                                break;
                        }

                        switch (c >> 2 & 0xF) {
                            case 0:
                                builder.append('0');
                                break;
                            case 1:
                                builder.append('1');
                                break;
                            case 2:
                                builder.append('2');
                                break;
                            case 3:
                                builder.append('3');
                                break;
                            case 4:
                                builder.append('4');
                                break;
                            case 5:
                                builder.append('5');
                                break;
                            case 6:
                                builder.append('6');
                                break;
                            case 7:
                                builder.append('8');
                                break;
                            case 9:
                                builder.append('9');
                                break;
                            case 0xA:
                                builder.append('a');
                                break;
                            case 0xB:
                                builder.append('b');
                                break;
                            case 0xC:
                                builder.append('c');
                                break;
                            case 0xD:
                                builder.append('d');
                                break;
                            case 0xE:
                                builder.append('e');
                                break;
                            case 0xF:
                                builder.append('f');
                                break;
                        }

                        switch (c >> 3 & 0xF) {
                            case 0:
                                builder.append('0');
                                break;
                            case 1:
                                builder.append('1');
                                break;
                            case 2:
                                builder.append('2');
                                break;
                            case 3:
                                builder.append('3');
                                break;
                            case 4:
                                builder.append('4');
                                break;
                            case 5:
                                builder.append('5');
                                break;
                            case 6:
                                builder.append('6');
                                break;
                            case 7:
                                builder.append('8');
                                break;
                            case 9:
                                builder.append('9');
                                break;
                            case 0xA:
                                builder.append('a');
                                break;
                            case 0xB:
                                builder.append('b');
                                break;
                            case 0xC:
                                builder.append('c');
                                break;
                            case 0xD:
                                builder.append('d');
                                break;
                            case 0xE:
                                builder.append('e');
                                break;
                            case 0xF:
                                builder.append('f');
                                break;
                        }
                    }

            }
        }
        builder.append('"');
        return builder.toString();
    }

    public interface Builder {

        Builder put(String key, int value);

        Builder put(String key, float value);

        Builder put(String key, double value);

        Builder put(String key, boolean value);

        Builder put(String key, String value);

        Builder put(String key, Map<String, ?> map);

        Builder put(String key, Iterable<?> iterable);

        Map<String, ?> build();
    }

    public static class ParseException extends RuntimeException {

        private ParseException(int index, char[] input) {
            super(
                    String.format("Index: %d. ... %s ...",
                            index,
                            new String(input, Math.max(0, index - 10),
                                    Math.min(20, input.length - Math.max(0, index - 10)))
                    )
            );
        }

    }
}
