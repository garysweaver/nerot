package nerot;

import com.sun.syndication.feed.synd.SyndFeed;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean;
import org.springframework.scheduling.quartz.CronTriggerBean;

public class Nerot {
    
    public static final String JOB_GROUP = "Nerot";

    Store store = null;
    Scheduler scheduler = null;
    List<String> jobIds = new ArrayList<String>();
    
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
        
        jobIds.add(jobId);
        scheduler.scheduleJob((JobDetail) jobDetail.getObject(), trigger);
    }
    
    public void unschedule(String jobId) throws org.quartz.SchedulerException {
        scheduler.deleteJob(jobId, JOB_GROUP);
    }
    
    public void unscheduleAll() throws org.quartz.SchedulerException {
        for (int i=0; i<jobIds.size(); i++) {
            unschedule(jobIds.get(i));
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