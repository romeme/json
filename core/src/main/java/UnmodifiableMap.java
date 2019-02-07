import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UnmodifiableMap {

    public static Builder builder() {

        class Shadow implements Builder {

            private final Map<String, Object> map;

            private Shadow(Map<String, Object> map) {this.map = map;}

            @Override
            public <T> Builder put(String key, T vv) {
                return new Shadow(new HashMap<>(map) {{ put(key, vv); }});
            }

            @Override
            public Map<String, ?> get() {
                return Collections.unmodifiableMap(map);
            }
        }

        return new Shadow(new HashMap<>());
    }

    public interface Builder extends Supplier<Map<String, ?>> {

        <T> Builder put(String key, T vv);
    }
}
