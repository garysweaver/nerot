package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;

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