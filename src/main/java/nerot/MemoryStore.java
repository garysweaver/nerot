package nerot;

import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory Store, backed by a HashMap.
 */
public class MemoryStore implements Store {

    private Map map = new HashMap();

    /**
     * Store the key and value in a HashMap in-memory.
     *
     * @param key the key to be stored
     * @param value the value to be stored for the specified key
     */
    public void set(String key, Object value) {
        map.put(key, value);
    }

    /**
     * Get the value for the specified key.
     *
     * @param key the key to use to lookup the specified value
     * @return the value of the key, or null if none if found
     */
    public Object get(String key) {
        return map.get(key);
    }
}
