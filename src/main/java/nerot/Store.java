package nerot;

public interface Store {
    
    public void set(String key, Object value);
    public Object get(String key);
}
