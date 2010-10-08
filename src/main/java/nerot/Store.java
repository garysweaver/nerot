package nerot;

/**
 * Interface for something that stores values via key/value.
 */
public interface Store {

    public void set(String key, Object value);

    public Object get(String key);
}
