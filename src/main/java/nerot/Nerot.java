package nerot;

import com.sun.syndication.feed.synd.SyndFeed;
import nerot.store.Storable;
import nerot.store.Store;
import nerot.task.HttpGetTask;
import nerot.task.Primable;
import nerot.task.RssTask;
import nerot.task.Task;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Nerot lets you to easily schedule tasks on the fly (using Quartz and Spring), calling
 * any object instance's method with any arguments you specify (class method or instance
 * method) and can cache the result for access in a separate thread with built-in RSS
 * retrieval and caching (using Rome). Scheduling supports the Quartz cron-like syntax.
 * It uses an in-memory store for quick asynchronous retrieval, but Tasks and Storing are
 * fully customizable and extensible via the API (uses interfaces, etc.).
 */
public class Nerot {

    /**
     * The Quartz job group used by Nerot.
     */
    private static final String JOB_GROUP = "Nerot";

    private static final Logger LOG = LoggerFactory.getLogger(Nerot.class);

    Store store = null;
    Scheduler scheduler = null;
    Map<String, String> jobIds = new HashMap();

    /**
     * Creates and schedules an HttpGetTask. For convenience, it uses url as the storeKey and jobId. Uses BaseTask defaults so may block thread for a defined amount of time to wait on
     * the first run validation. By defining your own HttpGetTask and using schedule(...), you will have much more control over this. This calls schedule(...) with cronSchedule.
     *
     * @param url          URL of the external resource
     * @param cronSchedule A <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html">Quartz cron schedule</a>. essentially like unix CRON except it adds an additional prefix for seconds like "0/5 * * * * ?" for every five seconds.
     */
    public void scheduleHttpGet(String url, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        HttpGetTask task = new HttpGetTask();
        task.setStore(store);
        task.setStoreKey(url);
        task.setUrl(url);
        schedule(url, task, cronSchedule);
    }

    /**
     * Creates and schedules an HttpGetTask. For convenience, it uses url as the storeKey and jobId. Uses BaseTask defaults so may block thread for a defined amount of time to wait on
     * the first run validation. By defining your own HttpGetTask and using schedule(...), you will have much more control over this. This calls schedule(...) with an interval in milliseconds.
     *
     * @param url              URL of the external resource
     * @param intervalInMillis desired interval between task executions in milliseconds
     */
    public void scheduleHttpGet(String url, long intervalInMillis) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        HttpGetTask task = new HttpGetTask();
        task.setStore(store);
        task.setStoreKey(url);
        task.setUrl(url);
        schedule(url, task, intervalInMillis);
    }

    /**
     * Get the response body from an HTTP GET from the Store using the specified url as the key.
     * This is equivalent to calling get(url) but it casts the result as String.
     *
     * @param url URL of the external resource
     * @return the response body
     */
    public String getHttpResponseBodyFromStore(String url) {
        String result = null;
        if (store != null) {
            result = (String) store.get(url);
        }
        return result;
    }

    /**
     * Creates and schedules an RssTask. For convenience, it uses url as the storeKey and jobId. Uses BaseTask defaults so may block thread for a defined amount of time to wait on
     * the first run validation. By defining your own RssTask and using schedule(...), you will have much more control over this. This calls schedule(...) with cronSchedule.
     *
     * @param url          an RSS feed URL
     * @param cronSchedule A <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html">Quartz cron schedule</a>. essentially like unix CRON except it adds an additional prefix for seconds like "0/5 * * * * ?" for every five seconds.
     */
    public void scheduleRss(String url, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        RssTask task = new RssTask();
        task.setStore(store);
        task.setStoreKey(url);
        task.setUrl(url);
        schedule(url, task, cronSchedule);
    }

    /**
     * Creates and schedules an RssTask. For convenience, it uses url as the storeKey and jobId. Uses BaseTask defaults so may block thread for a defined amount of time to wait on
     * the first run validation. By defining your own RssTask and using schedule(...), you will have much more control over this. This calls schedule(...) with an interval in milliseconds.
     *
     * @param url              an RSS feed URL
     * @param intervalInMillis desired interval between task executions in milliseconds
     */
    public void scheduleRss(String url, long intervalInMillis) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        RssTask task = new RssTask();
        task.setStore(store);
        task.setStoreKey(url);
        task.setUrl(url);
        schedule(url, task, intervalInMillis);
    }

    /**
     * Get a SyndFeed from the Store using the specified url as the key.
     * This is equivalent to calling get(url), casting the result as SyndFeed.
     *
     * @param url an RSS feed URL that was scheduled
     * @return the SyndFeed result of Rome RSS parsing the feed URL
     */
    public SyndFeed getRssFromStore(String url) {
        SyndFeed result = null;
        if (store != null) {
            result = (SyndFeed) store.get(url);
        }
        return result;
    }

    /**
     * Schedule any Task with a cronSchedule. If you are looking for a generic way to call any object, see the GenericTask wrapper.
     * If task implements Primable and isPrimeRunOnStart() it will spawn a thread to execute and will delay/check for first result
     * as specified via Primable. Note: there is a possibility of the prime run on start executing around the same time as the
     * first scheduled job.
     *
     * @param jobId        an arbitrary jobId that can be used to ensure that: the same job is not started twice if called twice, the schedule can be changed for that job just by calling schedule again, and the job can be cancelled without having to cancel all.
     * @param task         the Task to execute
     * @param cronSchedule A <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html">Quartz cron schedule</a>. essentially like unix CRON except it adds an additional prefix for seconds like "0/5 * * * * ?" for every five seconds.
     */
    public void schedule(String jobId, Task task, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        checkParam("jobId", jobId);
        checkParam("task", task);
        checkParam("cronSchedule", cronSchedule);

        if (task instanceof Storable) {
            ((Storable) task).setStore(store);
        }

        if (task instanceof Primable) {
            Primable p = (Primable) task;
            if (p.isPrimeRunOnStart()) {
                executePrimeRun(p, jobId);
            }
        }

        schedule(jobId, task, "execute", cronSchedule);
    }

    /**
     * Schedule any Task with a defined interval. If you are looking for a generic way to call any object, see the GenericTask wrapper.
     * If task implements Primable and isPrimeRunOnStart() it will spawn a thread to execute and will delay/check for first result,
     * as specified via Primable, and will schedule Quartz to start with the start time defined as new Date(System.currentTimeMillis() + intervalInMillis).
     * If task isn't Primable or isPrimeRunOnStart() returns false, it will not delay the Quartz start time and will start immediately
     * with the specified interval.
     *
     * @param jobId            an arbitrary jobId that can be used to ensure that: the same job is not started twice if called twice, the schedule can be changed for that job just by calling schedule again, and the job can be cancelled without having to cancel all.
     * @param task             the Task to execute
     * @param intervalInMillis desired interval between task executions in milliseconds
     */
    public void schedule(String jobId, Task task, long intervalInMillis) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        checkParam("jobId", jobId);
        checkParam("task", task);
        checkParam("intervalInMillis", intervalInMillis);

        Date startTime = null;

        if (task instanceof Storable) {
            ((Storable) task).setStore(store);
        }

        if (task instanceof Primable) {
            Primable p = (Primable) task;
            if (p.isPrimeRunOnStart()) {
                startTime = new Date(System.currentTimeMillis() + intervalInMillis);
                executePrimeRun(p, jobId);
            }
        }

        schedule(jobId, task, "execute", intervalInMillis, startTime);
    }

    private void checkParam(String paramName, Object paramValue) throws IllegalArgumentException {
        if (paramValue == null) {
            throw new IllegalArgumentException("'" + paramName + "' cannot be null.");
        }
    }

    private void executePrimeRun(Primable p, String jobId) {
        p.primeRun();

        if (p.isPrimeRunValid()) {
            return;
        }

        for (int count = 1; count <= p.getMaxPrimeRunValidationAttempts(); count++) {
            try {
                Thread.sleep(p.getPrimeRunValidationAttemptIntervalMillis());
            }
            catch (Throwable t) {
                LOG.error("Sleep between prime run validations interrupted for job '" + jobId + "'", t);
            }

            if (p.isPrimeRunValid()) {
                LOG.info("Prime run of job '" + jobId + "' had valid result (on check " + count + " of " + p.getMaxPrimeRunValidationAttempts() + ")");
                return;
            }
        }

        LOG.info("Prime run of job '" + jobId + "' was unable to be validated. You may want to try increasing maxPrimeRunValidationAttempts (currently set to " + p.getMaxPrimeRunValidationAttempts() + ") and/or primeRunValidationAttemptIntervalMillis (currently set to " + p.getPrimeRunValidationAttemptIntervalMillis() + ").");
    }

    private String jobScheduleId(String jobId, Object obj, String method, String cronSchedule) {
        return obj.getClass().getName() + "|" + method + "|cron:" + cronSchedule;
    }

    private String jobScheduleId(String jobId, Object obj, String method, long intervalInMillis) {
        return obj.getClass().getName() + "|" + method + "|interval:" + intervalInMillis;
    }

    /**
     * Schedule any object (doesn't even have to be a Task) calling the method on it that you specify with no arguments. You
     * probably want to use the other schedule method that takes a Task and use the GenericTask wrapper instead, as it zero-to-many
     * arguments you specify as well as class or instance methods. This is just a simple way of scheduling execution of a no arg method,
     * without caring about the implementation of the method (so it doesn't have to store the result, but it could).
     *
     * @param jobId        an arbitrary jobId that can be used to ensure that: the same job is not started twice if called twice, the schedule can be changed for that job just by calling schedule again, and the job can be cancelled without having to cancel all.
     * @param obj          any object that it will call no-arg method
     * @param method       the method name to call on obj
     * @param cronSchedule A <a href="http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html">Quartz cron schedule</a>. essentially like unix CRON except it adds an additional prefix for seconds like "0/5 * * * * ?" for every five seconds.
     */
    private void schedule(String jobId, Object obj, String method, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        String jobScheduleId = jobScheduleId(jobId, obj, method, cronSchedule);
        String existingJobScheduleId = jobIds.get(jobId);
        if (existingJobScheduleId != null && !existingJobScheduleId.equals(jobScheduleId)) {
            // change of schedule to existing job, so unschedule and schedule. This may result with slight overlap of both tasks,
            // assuming it is asynchronous.
            unschedule(jobId);
        }

        if (jobIds.get(jobId) == null) {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(obj);
            jobDetail.setTargetMethod(method);
            jobDetail.setName(jobId);
            jobDetail.setGroup(JOB_GROUP);
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            CronTriggerBean trigger = new CronTriggerBean();
            trigger.setBeanName("nerot-ct_" + jobId);
            trigger.setJobDetail((JobDetail) jobDetail.getObject());
            trigger.setCronExpression(cronSchedule);
            trigger.afterPropertiesSet();

            jobIds.put(jobId, jobScheduleId);
            scheduler.scheduleJob((JobDetail) jobDetail.getObject(), trigger);
            LOG.info("Scheduled job '" + jobId + "' with schedule '" + cronSchedule + "'");
        }
    }

    /**
     * Schedule any object (doesn't even have to be a Task) calling the method on it that you specify with no arguments. You
     * probably want to use the other schedule method that takes a Task and use the GenericTask wrapper instead, as it zero-to-many
     * arguments you specify as well as class or instance methods. This is just a simple way of scheduling execution of a no arg method,
     * without caring about the implementation of the method (so it doesn't have to store the result, but it could).
     *
     * @param jobId            an arbitrary jobId that can be used to ensure that: the same job is not started twice if called twice, the schedule can be changed for that job just by calling schedule again, and the job can be cancelled without having to cancel all.
     * @param obj              any object that it will call no-arg method
     * @param method           the method name to call on obj
     * @param intervalInMillis desired interval between task executions in milliseconds
     * @param startTime        desired start date and time or null if start immediately
     */
    private void schedule(String jobId, Object obj, String method, long intervalInMillis, Date startTime) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        // startTime only called internally and we know it will vary, so we don't use it as part of the description of the job schedule,
        // otherwise might usually create multiple jobs when called with same interval at different times.
        String jobScheduleId = jobScheduleId(jobId, obj, method, intervalInMillis);
        String existingJobScheduleId = jobIds.get(jobId);
        if (existingJobScheduleId != null && !existingJobScheduleId.equals(jobScheduleId)) {
            // change of schedule to existing job, so unschedule and schedule. This may result with slight overlap of both tasks,
            // assuming it is asynchronous.
            unschedule(jobId);
        }

        if (jobIds.get(jobId) == null) {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(obj);
            jobDetail.setTargetMethod(method);
            jobDetail.setName(jobId);
            jobDetail.setGroup(JOB_GROUP);
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            SimpleTriggerBean trigger = new SimpleTriggerBean();
            trigger.setBeanName("nerot-st_" + jobId);
            trigger.setJobDetail((JobDetail) jobDetail.getObject());
            trigger.setRepeatCount(SimpleTriggerBean.REPEAT_INDEFINITELY);
            trigger.setRepeatInterval(intervalInMillis);
            if (startTime != null) {
                trigger.setStartTime(startTime);
            }
            trigger.afterPropertiesSet();

            jobIds.put(jobId, jobScheduleId);
            scheduler.scheduleJob((JobDetail) jobDetail.getObject(), trigger);
            if (startTime != null) {
                LOG.info("Scheduled job '" + jobId + "' with interval '" + intervalInMillis + "' to start at " + startTime);
            } else {
                LOG.info("Scheduled job '" + jobId + "' with interval '" + intervalInMillis + "' to start immediately");
            }
        }
    }

    /**
     * Get an Object from the Store that was set by a Task.
     *
     * @param key the key set on the GenericTask, the url, or whatever key was used by the Task/called object as the key for the value in the Store.
     * @return result from Store for the specified key
     */
    public Object getResultFromStore(String key) {
        Object result = null;
        if (store != null) {
            result = store.get(key);
        }
        return result;
    }

    /**
     * This unschedules a task, by calling deleteJob on the Quartz scheduler.
     *
     * @param jobId the job id specified previously in a schedule method.
     */
    public void unschedule(String jobId) throws org.quartz.SchedulerException {
        LOG.info("Unscheduling job '" + jobId + "'");
        scheduler.deleteJob(jobId, JOB_GROUP);
        jobIds.put(jobId, null);
    }

    /**
     * This unschedules all tasks, using the unschedule method.
     */
    public void unscheduleAll() throws org.quartz.SchedulerException {
        for (Iterator iter = jobIds.keySet().iterator(); iter.hasNext();) {
            unschedule((String) iter.next());
        }
    }

    /**
     * Gets the Store used by Nerot's get methods. You shouldn't need to access this directly.
     *
     * @return the Store
     */
    public Store getStore() {
        return store;
    }

    /**
     * Sets the Store used by Nerot's get methods. You shouldn't need to access this directly. It is set via Spring.
     *
     * @param store the Store
     */
    public void setStore(Store store) {
        this.store = store;
    }

    /**
     * Gets the Quartz Scheduler instance. You shouldn't need to access this directly.
     *
     * @return the Scheduler
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * Sets the Quartz Scheduler instance. You shouldn't need to access this directly. It is set via Spring.
     *
     * @param scheduler the Scheduler
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}