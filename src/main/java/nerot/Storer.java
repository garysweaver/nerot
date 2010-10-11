package nerot;

import java.io.IOException;
import java.util.Date;

/**
 * Something that can have a Store.
 */
public interface Storer {
    
    /**
     * Sets the Store.
     *
     * @param store the Store to set.
     */
    public void setStore(Store store);
}
