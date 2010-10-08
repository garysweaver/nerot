package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;

/**
 * A Task that can be executed.
 */
public interface Task {
    public void execute();
}
