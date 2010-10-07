package nerot;

import com.sun.syndication.feed.synd.SyndFeed;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerBean;

public class Nerot {
    
    public static final String JOB_GROUP = "Nerot";

    Store store = null;
    Scheduler scheduler = null;
    Map<String, String> jobIds = new HashMap();
    
    public void scheduleRss(String feedUrl, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        RssUpdateTask task = new RssUpdateTask();
        task.setStore(store);
        task.setFeedUrl(feedUrl);        
        schedule(feedUrl, task, "execute", cronSchedule);
    }
        
    public SyndFeed getRss(String feedUrl) {
        SyndFeed result = null;
        if (store != null) {
            result = (SyndFeed)store.get(feedUrl);
        }
        return result;
    }

    public void schedule(String jobId, Task task, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        if (task instanceof Storer) {
            ((Storer)task).setStore(store);
        }
        
        schedule(jobId, task, "execute", cronSchedule);
    }
    
    public Object get(String key) {
        Object result = null;
        if (store != null) {
            result = store.get(key);
        }
        return result;
    }
    
    public void schedule(String jobId, Object obj, String method, String cronSchedule) throws java.text.ParseException, org.quartz.SchedulerException, java.lang.ClassNotFoundException, java.lang.NoSuchMethodException {
        String existingCronSchedule = jobIds.get(jobId);
        if (existingCronSchedule!=null && !existingCronSchedule.equals(cronSchedule)) {
            // change of schedule to existing job, so unschedule so we'll schedule with newer cronSchedule. This may result with slight overlap of both tasks,
            // assuming it is asynchronous.
            unschedule(jobId);
        }
        
        if (jobIds.get(jobId)==null) {
            MethodInvokingJobDetailFactoryBean jobDetail = new MethodInvokingJobDetailFactoryBean();
            jobDetail.setTargetObject(obj);
            jobDetail.setTargetMethod(method);
            jobDetail.setName(jobId);
            jobDetail.setGroup(JOB_GROUP);
            jobDetail.setConcurrent(false);
            jobDetail.afterPropertiesSet();

            CronTriggerBean trigger = new CronTriggerBean();
            trigger.setBeanName("nerot-t_" + jobId);
            trigger.setJobDetail((JobDetail) jobDetail.getObject());
            trigger.setCronExpression(cronSchedule);
            trigger.afterPropertiesSet();
        
            jobIds.put(jobId, cronSchedule);
            scheduler.scheduleJob((JobDetail) jobDetail.getObject(), trigger);
            System.err.println("Scheduled job '" + jobId + "'");
        }
    }
    
    public void unschedule(String jobId) throws org.quartz.SchedulerException {
        scheduler.deleteJob(jobId, JOB_GROUP);
        jobIds.put(jobId, null);
    }
    
    public void unscheduleAll() throws org.quartz.SchedulerException {
        for (Iterator iter=jobIds.keySet().iterator(); iter.hasNext();) {
            unschedule((String)iter.next());
        }
    }

    public Store getStore() {
        return store;
    }
    
    public void setStore(Store store) {
        this.store = store;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }
    
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
}