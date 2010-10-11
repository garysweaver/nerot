package nerot;

/**
 * Interface for something that stores values via key/value.
 */
public interface Store {

    /**
     * Store the key and value.
     *
     * @param key the key to be stored
     * @param value the value to be stored for the specified key
     */
    public void set(String key, Object value);

    /**
     * Get the value for the specified key.
     * @param key the key to use to lookup the specified value
     * @return the value of the key, or null if none if found
     */
    public Object get(String key);
}
