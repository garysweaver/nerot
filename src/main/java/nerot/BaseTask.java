package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;

public abstract class BaseTask implements Task, Storer {

    private Store store;
    
    // package-level method to get Store called by Nerot
    public Store getStore() {
        return store;
    }
    
    // package-level method to set Store called by Nerot
    public void setStore(Store store) {
        this.store = store;
    }
}