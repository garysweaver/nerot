package nerot;

import java.io.IOException;
import java.util.Date;

/**
 * A task that has a Store.
 */
public abstract class BaseTask implements Task, Storer {

    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }
}