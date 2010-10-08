package nerot;

import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory Store, backed by a HashMap.
 */
public class MemoryStore implements Store {

    private Map map = new HashMap();

    public void set(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }
}
