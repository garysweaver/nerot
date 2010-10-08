package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;

/**
 * Something that can have a Store.
 */
public interface Storer {
    public void setStore(Store store);
}
