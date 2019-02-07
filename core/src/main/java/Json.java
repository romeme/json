import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Roman.
 * r.alt.ctrl@gmail.com
 */
@SuppressWarnings("Duplicates")
class Json {

    private static final int EXIT = 0;
    private static final int INIT = 1;

    static final int OBJECT = INIT << 1;
    private static final int OBJECT_NEXT_KEY_OR_END = OBJECT << 1;
    private static final int OBJECT_NEXT_KEY = OBJECT_NEXT_KEY_OR_END << 1;
    private static final int OBJECT_MAPPING = OBJECT_NEXT_KEY << 1;
    private static final int OBJECT_VALUE = OBJECT_MAPPING << 1;
    private static final int OBJECT_DIVIDER_OR_END = OBJECT_VALUE << 1;
    private static final int OBJECT_APPEND = OBJECT_DIVIDER_OR_END << 1;

    static final int ARRAY = OBJECT_APPEND << 1;
    private static final int ARRAY_NEXT_OR_END = ARRAY << 1;
    private static final int ARRAY_NEXT = ARRAY_NEXT_OR_END << 1;
    private static final int ARRAY_DIVIDER_OR_END = ARRAY_NEXT << 1;
    private static final int ARRAY_APPEND = ARRAY_DIVIDER_OR_END << 1;

    private static final int NUMBER = ARRAY_APPEND << 1;
    private static final int NUMBER_INIT = NUMBER << 1;
    private static final int NUMBER_PREFIX = NUMBER_INIT << 1;
    private static final int NUMBER_PREFIX_OR_END = NUMBER_PREFIX << 1;
    private static final int NUMBER_SUFFIX = NUMBER_PREFIX_OR_END << 1;
    private static final int NUMBER_SUFFIX_OR_END = NUMBER_SUFFIX << 1;
    private static final int NUMBER_SIGN_OR_NUM = NUMBER_SUFFIX_OR_END << 1;
    private static final int NUMBER_EXP = NUMBER_SIGN_OR_NUM << 1;
    private static final int NUMBER_EXIT = NUMBER_EXP << 1;

    private static final int STRING = NUMBER_EXIT << 1;

    private static final int SCREEN = STRING << 1;
    private static final int NORMAL = SCREEN << 1;
    private static final int UNICODE_END = NORMAL << 1;
    private static final int UNICODE = UNICODE_END << 4;

    private static final int TRUE = UNICODE << 1;
    private static final int FALSE = TRUE << 1;

    static <K> Optional<String> encode(K e) {
        if (Objects.isNull(e))
            return Optional.of("null");
        else if (e instanceof Number || e instanceof Boolean)
            return Optional.of(String.valueOf(e));
        else if (e instanceof CharSequence)
            return string(String.valueOf(e));
        else if (e instanceof List<?>)
            return ((List<?>) e).stream().collect(JSArray.collector());
        else if (e instanceof Map<?, ?>)
            return ((Map<?, ?>) e).entrySet().stream().collect(JSObject.collector());
        else
            return Optional.empty();
    }

    static Optional<String> decode(String input) {
        return Optional.ofNullable(input)
                .map(String::toCharArray)
                .flatMap(arr -> {
                    int state = INIT;

                    StringBuilder builder = new StringBuilder();
                    int unicode = 0;

                    string:
                    for (int index = 0; index < arr.length; index++) {
                        char ch = arr[index];

                        switch (state) {
                            case 0:
                                return Optional.empty();
                            case INIT:
                                state = ch == '"' ? NORMAL : 0;
                                continue string;
                            case UNICODE_END:
                                index--;
                                builder.append((char) unicode);
                                state >>= 1;
                                continue string;

                            case UNICODE:
                            case UNICODE >> 1:
                            case UNICODE >> 2:
                            case UNICODE >> 3:
                                switch (ch) {
                                    case '0':
                                        unicode <<= 4;
                                        state >>= 1;
                                        continue string;
                                    case '1':
                                        unicode = (unicode << 4) | 0x1;
                                        state >>= 1;
                                        continue string;
                                    case '2':
                                        unicode = (unicode << 4) | 0x2;
                                        state >>= 1;
                                        continue string;
                                    case '3':
                                        unicode = (unicode << 4) | 0x3;
                                        state >>= 1;
                                        continue string;
                                    case '4':
                                        unicode = (unicode << 4) | 0x4;
                                        state >>= 1;
                                        continue string;
                                    case '5':
                                        unicode = (unicode << 4) | 0x5;
                                        state >>= 1;
                                        continue string;
                                    case '6':
                                        unicode = (unicode << 4) | 0x6;
                                        state >>= 1;
                                        continue string;
                                    case '7':
                                        unicode = (unicode << 4) | 0x7;
                                        state >>= 1;
                                        continue string;
                                    case '8':
                                        unicode = (unicode << 4) | 0x8;
                                        state >>= 1;
                                        continue string;
                                    case '9':
                                        unicode = (unicode << 4) | 0x9;
                                        state >>= 1;
                                        continue string;
                                    case 'A':
                                    case 'a':
                                        unicode = (unicode << 4) | 0xA;
                                        state >>= 1;
                                        continue string;
                                    case 'B':
                                    case 'b':
                                        unicode = (unicode << 4) | 0xB;
                                        state >>= 1;
                                        continue string;
                                    case 'C':
                                    case 'c':
                                        unicode = (unicode << 4) | 0xC;
                                        state >>= 1;
                                        continue string;
                                    case 'D':
                                    case 'd':
                                        unicode = (unicode << 4) | 0xD;
                                        state >>= 1;
                                        continue string;
                                    case 'E':
                                    case 'e':
                                        unicode = (unicode << 4) | 0xE;
                                        state >>= 1;
                                        continue string;
                                    case 'F':
                                    case 'f':
                                        unicode = (unicode << 4) | 0xF;
                                        state >>= 1;
                                        continue string;
                                    default:
                                        return Optional.empty();
                                }

                            case SCREEN:

                                switch (ch) {
                                    case 'r':
                                        builder.append('\r');
                                        state = NORMAL;
                                        continue string;
                                    case 'n':
                                        builder.append('\n');
                                        state = NORMAL;
                                        continue string;
                                    case 't':
                                        builder.append('\t');
                                        state = NORMAL;
                                        continue string;
                                    case 'b':
                                        builder.append('\b');
                                        state = NORMAL;
                                        continue string;
                                    case 'f':
                                        builder.append('\f');
                                        state = NORMAL;
                                        continue string;
                                    case '\\':
                                        builder.append('\\');
                                        state = NORMAL;
                                        continue string;
                                    case '"':
                                        builder.append('"');
                                        state = NORMAL;
                                        continue string;
                                    case 'u':
                                        unicode = 0;
                                        state = UNICODE;
                                        continue string;
                                    default:
                                        return Optional.empty();
                                }

                            case NORMAL:
                                switch (ch) {
                                    case '\\':
                                        state = SCREEN;
                                        continue string;
                                    case '"':
                                        return Optional.of(builder.toString());
                                    default:
                                        builder.append(ch);
                                        continue string;
                                }

                            default:
                                return Optional.empty();
                        }
                    }
                    return Optional.empty();
                });
    }

    private static Optional<String> string(String op) {
        return Optional.ofNullable(op)
                .map(input ->
                {
                    StringBuilder builder = new StringBuilder();
                    builder.append("\"");

                    for (char ch : input.toCharArray()) {
                        switch (ch) {
                            case '\"':
                                builder.append("\\\"");
                                break;
                            case '\\':
                                builder.append("\\\\");
                                break;
                            case '\n':
                                builder.append("\\n");
                                break;
                            case '\r':
                                builder.append("\\r");
                                break;
                            case '\t':
                                builder.append("\\t");
                                break;
                            case '\b':
                                builder.append("\\b");
                                break;
                            case '\f':
                                builder.append("\\f");
                                break;
                            default:
                                if (ch <= 127)
                                    builder.append(ch);
                                else {
                                    builder.append("\\u");
                                    for (int round = 12; round >= 0; round -= 4)
                                        switch ((ch >> round) & 0xF) {
                                            case 0x0:
                                                builder.append("0");
                                                break;
                                            case 0x1:
                                                builder.append('1');
                                                break;
                                            case 0x2:
                                                builder.append('2');
                                                break;
                                            case 0x3:
                                                builder.append('3');
                                                break;
                                            case 0x4:
                                                builder.append('4');
                                                break;
                                            case 0x5:
                                                builder.append('5');
                                                break;
                                            case 0x6:
                                                builder.append('6');
                                                break;
                                            case 0x7:
                                                builder.append('7');
                                                break;
                                            case 0x8:
                                                builder.append('8');
                                                break;
                                            case 0x9:
                                                builder.append('9');
                                                break;
                                            case 0xA:
                                                builder.append('A');
                                                break;
                                            case 0xB:
                                                builder.append('B');
                                                break;
                                            case 0xC:
                                                builder.append('C');
                                                break;
                                            case 0xD:
                                                builder.append('D');
                                                break;
                                            case 0xE:
                                                builder.append("E");
                                                break;
                                            case 0xF:
                                                builder.append("F");
                                                break;
                                        }
                                }
                        }

                    }

                    builder.append("\"");
                    return builder.toString();
                });
    }

    static <K> Optional<K> read(String input, Accumulator<K> accumulator, int st) {
        return read(input.toCharArray(), accumulator, st);
    }

    private static <K> Optional<K> read(char[] input, Accumulator<K> accumulator, int st) {
        Stack<Accumulator<?>> accumulators = new Stack<>();
        accumulators.push(accumulator);
        Stack<Integer> states = new Stack<>();
        states.push(EXIT);
        states.push(st);

        main:
        for (int index = 0; index < input.length; ) {

            switch (states.peek()) {
                case OBJECT:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
                            case '{':
                                index++;
                                states.push(OBJECT_NEXT_KEY_OR_END);
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_NEXT_KEY_OR_END:
                    states.pop();
                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
                            case '"':
                                states.push(OBJECT_MAPPING);
                                states.push(STRING);
                                continue main;
                            case '}':
                                index++;
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_NEXT_KEY:
                    states.pop();
                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
                            case '"':
                                states.push(OBJECT_MAPPING);
                                states.push(STRING);
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_MAPPING:
                    states.pop();
                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
                            case ':':
                                index++;
                                states.push(OBJECT_VALUE);
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_VALUE:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
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
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(NUMBER);
                                continue main;
                            case 't':
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(TRUE);
                                continue main;
                            case 'f':
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(FALSE);
                                continue main;
                            case 'n':
                                states.push(OBJECT_DIVIDER_OR_END);

                                int state = 1 << 4;
                                nloop:
                                for (; index < input.length; index++) {
                                    ch = input[index];
                                    switch (state | ch) {
                                        case 1 << 4 | 'n':
                                        case 1 << 3 | 'u':
                                        case 1 << 2 | 'l':
                                            state >>= 1;
                                            continue nloop;
                                        case 1 << 1 | 'l':
                                            index++;
                                            accumulators.peek().append("null");
                                            continue main;
                                    }
                                }
                                return Optional.empty();

                            case '"':
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(STRING);
                                continue main;
                            case '{':
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(OBJECT_APPEND);
                                states.push(OBJECT);
                                accumulators.push(new MapAccumulator());

                                continue main;
                            case '[':
                                states.push(OBJECT_DIVIDER_OR_END);
                                states.push(ARRAY_APPEND);
                                states.push(ARRAY);
                                accumulators.push(new ArrayAccumulator());

                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_DIVIDER_OR_END:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;

                            case ',':
                                index++;
                                states.push(OBJECT_NEXT_KEY);
                                continue main;
                            case '}':
                                index++;
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case OBJECT_APPEND:
                    states.pop();
                    Optional<String> map = accumulators.pop().collect().map(String::valueOf);
                    if (map.isEmpty())
                        return Optional.empty();

                    accumulators.peek().append(map.get());
                    continue main;

                case ARRAY:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
                            case '[':
                                index++;
                                states.push(ARRAY_NEXT_OR_END);
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case ARRAY_NEXT_OR_END:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
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
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(NUMBER);
                                continue main;

                            case 't':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(TRUE);
                                continue main;
                            case 'f':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(FALSE);
                                continue main;
                            case 'n':
                                states.push(ARRAY_DIVIDER_OR_END);
                                int state = 1 << 4;
                                nloop:
                                for (; index < input.length; index++) {
                                    ch = input[index];
                                    switch (state | ch) {
                                        case 1 << 4 | 'n':
                                        case 1 << 3 | 'u':
                                        case 1 << 2 | 'l':
                                            state >>= 1;
                                            continue nloop;
                                        case 1 << 1 | 'l':
                                            index++;
                                            accumulators.peek().append("null");
                                            continue main;
                                    }
                                }
                                return Optional.empty();

                            case '"':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(STRING);
                                continue main;
                            case '{':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(OBJECT_APPEND);
                                states.push(OBJECT);
                                accumulators.push(new MapAccumulator());
                                continue main;
                            case '[':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(ARRAY_APPEND);
                                states.push(ARRAY);
                                accumulators.push(new ArrayAccumulator());

                                continue main;

                            case ']':
                                index++;
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case ARRAY_NEXT:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;
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
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(NUMBER);
                                continue main;
                            case 't':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(TRUE);
                                continue main;
                            case 'f':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(FALSE);
                                continue main;
                            case 'n':
                                states.push(ARRAY_DIVIDER_OR_END);
                                int state = 1 << 4;
                                nloop:
                                for (; index < input.length; index++) {
                                    ch = input[index];
                                    switch (state | ch) {
                                        case 1 << 4 | 'n':
                                        case 1 << 3 | 'u':
                                        case 1 << 2 | 'l':
                                            state >>= 1;
                                            continue nloop;
                                        case 1 << 1 | 'l':
                                            index++;
                                            accumulators.peek().append("null");
                                            continue main;
                                    }
                                }
                                return Optional.empty();

                            case '"':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(STRING);
                                continue main;
                            case '{':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(OBJECT_APPEND);
                                states.push(OBJECT);
                                accumulators.push(new MapAccumulator());

                                continue main;
                            case '[':
                                states.push(ARRAY_DIVIDER_OR_END);
                                states.push(ARRAY_APPEND);
                                states.push(ARRAY);
                                accumulators.push(new ArrayAccumulator());

                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case ARRAY_DIVIDER_OR_END:
                    states.pop();

                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (ch) {
                            case ' ':
                            case '\r':
                            case '\n':
                            case '\f':
                            case '\t':
                            case '\b':
                                continue loop;

                            case ',':
                                index++;
                                states.push(ARRAY_NEXT);
                                continue main;
                            case ']':
                                index++;
                                continue main;
                            default:
                                return Optional.empty();
                        }
                    }

                    return Optional.empty();

                case ARRAY_APPEND:
                    states.pop();
                    Optional<String> arr = accumulators.pop().collect().map(String::valueOf);
                    if (arr.isEmpty())
                        return Optional.empty();

                    accumulators.peek().append(arr.get());
                    continue main;

                case STRING: {

                    states.pop();
                    int state = INIT;
                    int start = index;

                    string:
                    for (; index < input.length; index++) {
                        char ch = input[index];

                        switch (state) {
                            case 0:
                                return Optional.empty();
                            case INIT:
                                state = ch == '"' ? NORMAL : 0;
                                continue string;
                            case UNICODE_END:
                                index--;
                                state >>= 1;
                                continue string;

                            case UNICODE:
                            case UNICODE >> 1:
                            case UNICODE >> 2:
                            case UNICODE >> 3:
                                state >>= 1;
                                continue string;

                            case SCREEN:

                                switch (ch) {
                                    case 'r':
                                    case 'n':
                                    case 't':
                                    case 'b':
                                    case 'f':
                                    case '\\':
                                    case '"':
                                        state = NORMAL;
                                        continue string;
                                    case 'u':
                                        state = UNICODE;
                                        continue string;
                                    default:
                                        return Optional.empty();
                                }

                            case NORMAL:
                                switch (ch) {
                                    case '\\':
                                        state = SCREEN;
                                        continue string;
                                    case '"':
                                        index++;
                                        accumulators.peek().append(new String(input, start, index - start));
                                        continue main;
                                    default:
                                        continue string;
                                }

                            default:
                                return Optional.empty();
                        }
                    }
                    return Optional.empty();
                }

                case NUMBER: {

                    states.pop();
                    int start = index;
                    int state = NUMBER_INIT;
                    number:
                    for (; index < input.length; index++) {
                        switch (state) {
                            case NUMBER_INIT:
                                switch (input[index]) {
                                    case '-':
                                        state = NUMBER_PREFIX;
                                        continue number;

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
                                        continue number;
                                    default:
                                        return Optional.empty();
                                }

                            case NUMBER_PREFIX:

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
                                        state = NUMBER_PREFIX_OR_END;
                                        continue number;
                                    default:
                                        return Optional.empty();
                                }
                            case NUMBER_PREFIX_OR_END:

                                loop:
                                for (; index < input.length; index++) {
                                    char ch = input[index];
                                    switch (ch) {
                                        case '.':
                                            state = NUMBER_SUFFIX;
                                            continue number;
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
                                            index--; // todo
                                            state = NUMBER_EXIT;
                                            continue number;
                                        default:
                                            return Optional.empty();
                                    }
                                }
                                return Optional.empty();

                            case NUMBER_SUFFIX:
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
                                        state = NUMBER_SUFFIX_OR_END;
                                        continue number;
                                    default:
                                        return Optional.empty();
                                }

                            case NUMBER_SUFFIX_OR_END:

                                loop:
                                for (; index < input.length; index++) {
                                    switch (input[index]) {
                                        case 'e':
                                        case 'E':
                                            state = NUMBER_SIGN_OR_NUM;
                                            continue number;
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
                                            index--; // todo
                                            state = NUMBER_EXIT;
                                            continue number;
                                        default:
                                            return Optional.empty();
                                    }
                                }
                                return Optional.empty();

                            case NUMBER_SIGN_OR_NUM:
                                switch (input[index]) {
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
                                        state = NUMBER_EXP;
                                        continue number;
                                    default:
                                        return Optional.empty();
                                }

                            case NUMBER_EXP:
                                loop:
                                for (; index < input.length; index++)
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
                                        case ' ':
                                        case '\r':
                                        case '\n':
                                        case '\f':
                                        case '\t':
                                        case '\b':
                                        case ',':
                                        case ']':
                                        case '}':
                                            index--; // todo
                                            state = NUMBER_EXIT;
                                            continue number;
                                        default:
                                            return Optional.empty();
                                    }

                            case NUMBER_EXIT: {
                                accumulators.peek().append(new String(input, start, index - start));
                                continue main;
                            }
                        }
                    }

                    return Optional.empty();

                }

                case TRUE: {
                    states.pop();
                    int state = 1 << 4;
                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (state | ch) {
                            case 1 << 4 | 't':
                            case 1 << 3 | 'r':
                            case 1 << 2 | 'u':
                                state >>= 1;
                                continue loop;
                            case 1 << 1 | 'e':
                                index++;
                                accumulators.peek().append("true");
                                continue main;
                        }
                    }
                    return Optional.empty();
                }
                case FALSE: {
                    states.pop();
                    int state = 1 << 5;
                    loop:
                    for (; index < input.length; index++) {
                        char ch = input[index];
                        switch (state | ch) {
                            case 1 << 5 | 'f':
                            case 1 << 4 | 'a':
                            case 1 << 3 | 'l':
                            case 1 << 2 | 's':
                                state >>= 1;
                                continue loop;
                            case 1 << 1 | 'e':
                                index++;
                                accumulators.peek().append("false");
                                continue main;
                        }
                    }
                    return Optional.empty();
                }

                case EXIT:
                    return accumulator.collect();
            }

        }

        return accumulator.collect();
    }

    interface Accumulator<R> {

        void append(String key);

        Optional<R> collect();
    }

    static class MapAccumulator implements Accumulator<List<String>> {

        private List<String> map = new ArrayList<>();

        @Override
        public void append(String key) {
            map.add(key);
        }

        @Override
        public Optional<List<String>> collect() {
            return Optional.of(map)
                    .filter(mp -> mp.size() % 2 == 0)
                    .map(mp ->
                            new ArrayList<String>(mp) {
                                @Override
                                public String toString() {
                                    return String.format("{ %s }",
                                            IntStream.range(0, size() / 2)
                                                    .boxed()
                                                    .map(index -> String.format("%s : %s", get(index * 2), get(index * 2 + 1)))
                                                    .collect(Collectors.joining(", ")));
                                }
                            }
                    );
        }
    }

    private static class JsonMap extends HashMap<String, String> {

        JsonMap(Map<String, String> map) {
            super(map);
        }

        @Override
        public String toString() {
            return String.format("{ %s }",
                    entrySet()
                            .stream()
                            .map(en ->
                                    String.format("%s : %s", en.getKey(), en.getValue()))
                            .collect(Collectors.joining(", ")));
        }
    }

    static class ArrayAccumulator implements Accumulator<List<String>> {

        private List<String> array = new ArrayList<>();

        @Override
        public void append(String value) {
            array.add(value);
        }

        @Override
        public Optional<List<String>> collect() {
            return Optional.of(new ArrayList<String>() {
                @Override
                public String toString() {
                    return String.format("[ %s ]", String.join(", ", array));
                }
            });
        }
    }

}
