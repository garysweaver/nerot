package nerot;

import java.io.IOException;
import java.util.Date;

/**
 * A task that has a Store.
 */
public abstract class BaseTask implements Task, Storer {

    private Store store;

    /**
     * Get the Store.
     *
     * @return the Store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Sets the Store.
     *
     * @param store Set the Store.
     */
    public void setStore(Store store) {
        this.store = store;
    }
}