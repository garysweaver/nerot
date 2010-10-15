package nerot;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import nerot.task.GenericTask;
import nerot.task.RssTask;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Iterator;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"nerot.xml"})
public class SystemTest extends AbstractDependencyInjectionSpringContextTests {

    private Nerot nerot;

    @After
    public void cleanUpJobs() throws Throwable {
        nerot.unscheduleAll();
    }

    @Autowired
    public void setNerot(Nerot nerot) {
        this.nerot = nerot;
    }

    public Nerot getNerot() {
        return nerot;
    }

    @Test
    public void testRssConvenienceMethods() throws Throwable {
        String url = "http://news.google.com/news?ned=us&topic=t&output=rss";

        // Schedule job and wait up to 5 seconds for valid result.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.scheduleRss(url, "0 * * * * ?");

        // Validate it ran task and stored result.
        // If feed is slow or down, this may fail.
        SyndFeed feed = nerot.getRssFromStore(url);
        assertNotNull("Feed was null", feed);
        print(feed);
    }

    @Test
    public void testRssWithoutPrimeRun() throws Throwable {
        String url = "http://news.google.com/news?ned=us&topic=t&output=rss";

        // Schedule job.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        RssTask task = new RssTask();
        task.setStoreKey(url);
        task.setUrl(url);
        task.setPrimeRunOnStart(false);

        nerot.schedule(url, task, "0/1 * * * * ?");

        // Since we specifically told it not to do a prime run (and wait on valid response) we'll wait here.
        waitForNerot(url);

        // Validate it ran task and stored result.
        // If feed is slow or down, this may fail.
        SyndFeed feed = nerot.getRssFromStore(url);
        assertNotNull("Feed was null", feed);
        print(feed);
    }

    @Test
    public void testHttpGetConvenienceMethods() throws Throwable {
        String url = "http://www.google.com/";

        // Schedule job and wait up to 5 seconds for valid result.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.scheduleHttpGet(url, "0 * * * * ?");

        // Validate it ran task and stored result.
        // If feed is slow or down, this may fail.
        String responseBody = nerot.getHttpResponseBodyFromStore(url);
        assertNotNull("Result was null", responseBody);
        System.err.println(responseBody);
    }

    @Test
    public void testStaticMethod() throws Throwable {
        String storeKey = "foo";

        // Wrap Math.random().
        GenericTask task = new GenericTask();
        task.setStoreKey(storeKey);
        task.setActor(Math.class);
        task.setMethod("random");

        // Schedule job.
        // syntax: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.schedule("myJob", task, "0 * * * * ?");

        // Since we specifically told it not to do a prime run (and wait on valid response) we'll wait here.
        waitForNerot(storeKey);

        // Validate it ran task and stored result.
        Object result = nerot.getResultFromStore(storeKey);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }

    @Test
    public void testStaticMethodSettingAllTaskParams() throws Throwable {
        String storeKey = "foo";

        // Wrap Math.random().
        GenericTask task = new GenericTask();
        task.setStoreKey(storeKey);
        task.setActor(Math.class);
        task.setMethod("random");
        task.setPrimeRunOnStart(true);
        task.setMaxPrimeRunValidationAttempts(20);
        task.setPrimeRunValidationAttemptIntervalMillis(100);

        // Schedule job and it will wait on result.
        // syntax: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.schedule("myJob", task, "0 * * * * ?");

        // Validate it ran task and stored result.
        Object result = nerot.getResultFromStore(storeKey);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }

    @Test
    public void testInstanceMethod() throws Throwable {
        String storeKey = "foo";

        // Wrap a sample object with GenericTask
        GenericTask task = new GenericTask();
        task.setStoreKey(storeKey);
        task.setActor(new SampleObjectToWrap());
        task.setMethod("someFantasticMethod");
        task.setArgs(new Object[]{"s", new Date(), true, 'c', 1.0D, new Character('c')});

        // Schedule job and wait default amount of time on result.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.schedule("myJob", task, "0/1 * * * * ?");

        // Since we specifically told it not to do a prime run (and wait on valid response) we'll wait here.
        waitForNerot(storeKey);

        // Validate it ran task and stored result.
        Object result = nerot.getResultFromStore(storeKey);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }

    private void waitForNerot(String storeKey) {
        try {
            // Longer than I needed, but may need to set this higher if your machine is really slow. Wait on Quartz thread to spin up, read schedule, and perform task (usually < 3 sec).
            Thread.sleep(1000);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        for (int i = 0; i < 50; i++) {
            try {
                Thread.sleep(100);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }

            if (nerot.getResultFromStore(storeKey) != null) {
                return;
            }
        }
    }

    private void print(SyndFeed feed) {
        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
            SyndEntry entry = (SyndEntry) i.next();
            System.out.println(entry.getTitle());
        }
    }
}
