package romeme.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("Duplicates")
public final class JSON {

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


    private JSON() {}

    public static List<String> array(String input) {
        return array(input.toCharArray());
    }

    private static List<String> array(char[] input) {
        LinkedList<String> result = new LinkedList<>();
        int index = -1;
        int state = INIT;

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
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
                                continue finish;
                            default:
                                throw new RuntimeException();
                        }
                    return result;


                case VALUE_HEAD:
                    value_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
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
                                continue value_tail;
                            case ']':
                                return result;
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
                                result.add(new String(input, index, array_end - index + 1));
                                index = array_end;
                                state = VALUE_TAIL;
                                continue main;
                            case '{':
                                int object_end = object_validate(input, index - 1);
                                result.add(new String(input, index, object_end - index + 1));
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
                                                    case ',':
                                                    case ']':
                                                        index--;
                                                        break num_main;
                                                    default:
                                                        throw new RuntimeException();
                                                }
                                    }

                                result.add(new String(input, start, index + 1 - start));

                                state = VALUE_TAIL;
                                continue main;

                            case 'n':
                                if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                    throw new RuntimeException();

                                result.add(null);
                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 't':
                                if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                    throw new RuntimeException();

                                result.add("true");
                                index += 3;
                                state = VALUE_TAIL;
                                continue main;

                            case 'f':
                                if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                    throw new RuntimeException();

                                result.add("false");
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
                                                    for (int i = index + 1; i <= end; i++)
                                                        switch (input[i]) {
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
                                                                value = (value << 4) | (input[i] - 48);
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
                                                        result.add(new String(input, str_start, counter - str_start));
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
    }

    public static Map<String, String> object(String input) {
        return object(input.toCharArray());
    }

    private static Map<String, String> object(char[] input) {
        Map<String, String> result = new HashMap<>();
        int index = -1;
        int state = INIT;

        main:
        while (true)
            switch (state) {
                case INIT:

                    init:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
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
                                continue finish;
                            default:
                                throw new RuntimeException();
                        }
                    return result;

                case KEY_HEAD:
                    key_head:
                    while (true)
                        switch (input[++index]) {
                            case ' ':
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
                                        for (int i = index + 1; i <= end; i++)
                                            switch (input[i]) {
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
                                                    value = (value << 4) | (input[i] - 48);
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
                                                        continue value_head;
                                                    default:
                                                        index--;
                                                        break value_head;
                                                }

                                            while (true)
                                                switch (input[++index]) {

                                                    case '[':
                                                        int array_end = array_validate(input, index - 1);
                                                        result.put(key, new String(input, index, array_end - index + 1));
                                                        index = array_end;
                                                        state = VALUE_TAIL;
                                                        continue main;
                                                    case '{':
                                                        int object_end = object_validate(input, index - 1);
                                                        result.put(key, new String(input, index, object_end - index + 1));
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
                                                                            case ',':
                                                                            case ']':
                                                                                index--;
                                                                                break num_main;
                                                                            default:
                                                                                throw new RuntimeException();
                                                                        }
                                                            }

                                                        result.put(key, new String(input, start, index + 1 - start));

                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'n':
                                                        if (input[index + 1] != 'u' || input[index + 2] != 'l' || input[index + 3] != 'l')
                                                            throw new RuntimeException();

                                                        result.put(key, null);
                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 't':
                                                        if (input[index + 1] != 'r' || input[index + 2] != 'u' || input[index + 3] != 'e')
                                                            throw new RuntimeException();

                                                        result.put(key, "true");
                                                        index += 3;
                                                        state = VALUE_TAIL;
                                                        continue main;

                                                    case 'f':
                                                        if (input[index + 1] != 'a' || input[index + 2] != 'l' || input[index + 3] != 's' || input[index + 4] != 'e')
                                                            throw new RuntimeException();

                                                        result.put(key, "false");
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
                                                                            for (int i = index + 1; i <= end; i++)
                                                                                switch (input[i]) {
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
                                                                                        value = (value << 4) | (input[i] - 48);
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
                                                                                result.put(key, new String(input, str_start, counter - str_start));
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
                                continue value_tail;
                            case '}':
                                return result;
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
                                    case 't':
                                    case '\\':
                                    case '"':
                                        continue reader;
                                    case 'u':
                                        int end = index + 4;
                                        loop:
                                        for (int i = index + 1; i <= end; i++)
                                            switch (input[i]) {
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
                                            divider:
                                            while (true)
                                                switch (input[++index]) {
                                                    case ' ':
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
                                                        value_reader:
                                                        while (true)
                                                            switch (state) {
                                                                case SCREEN:
                                                                    state = NORMAL;
                                                                    switch (input[++index]) {
                                                                        case 'r':
                                                                        case 'n':
                                                                        case 't':
                                                                        case '\\':
                                                                        case '"':
                                                                            continue value_reader;
                                                                        case 'u':
                                                                            int end = index + 4;
                                                                            loop:
                                                                            for (int i = index + 1; i <= end; i++)
                                                                                switch (input[i]) {
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
                                                case 'n':
                                                case 't':
                                                case '\\':
                                                case '"':
                                                    continue reader;

                                                case 'u':
                                                    loop:
                                                    for (int i = 1; i <= 4; i++)
                                                        switch (input[index + i]) {
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
                                                    index += 4;
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
}
