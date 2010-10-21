package nerot.spring;

import nerot.Nerot;
import nerot.task.Task;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This bean lets you schedule Nerot by just defining a bean (e.g. in the root context of your application). The
 * properties that must be set on the bean are the same as the ones on the corresponding schedule method in Nerot:
 * <p>
 * nerot.schedule(jobId, task, cronSchedule);
 * </p>
 * The following are properties that must be set on this bean:
 * <ul>
 * <li>jobId</li>
 * <li>task</li>
 * <li>intervalInMillis</li>
 * </ul>
 * It also has a "nerot" property, but that is @Autowired.
 * <p/>
 * (See the corresponding schedule method in Nerot for more details on the types and functions of that method.)
 */
public class IntervalScheduler implements InitializingBean {

    @Autowired
    private Nerot nerot;
    private String jobId;
    private Task task;
    private long intervalInMillis;

    public void afterPropertiesSet() throws Exception {
        nerot.schedule(jobId, task, intervalInMillis);
    }

    protected void finalize() throws Throwable {
        try {
            nerot.unschedule(jobId);
        }
        finally {
            super.finalize();
        }
    }

    public Nerot getNerot() {
        return nerot;
    }

    public void setNerot(Nerot nerot) {
        this.nerot = nerot;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public long getIntervalInMillis() {
        return intervalInMillis;
    }

    public void setIntervalInMillis(long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }
}
