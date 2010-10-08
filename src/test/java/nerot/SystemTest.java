package nerot;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedInput;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"../nerot.xml"})
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

    @Test public void testRss() throws Throwable {
        String url = "http://news.google.com/news?ned=us&topic=t&output=rss";
        
        // Schedule job.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.scheduleRss(url, "0/1 * * * * ?");
        
        // Ordinarily you wouldn't wait and would just call the get on a different thread whenever you wanted, but here we wait like any normal async method that isn't event-driven.
        waitForNerot();
        
        // Validate it ran task and stored result.
        // If feed is slow or down, this may fail.
        SyndFeed feed = nerot.getRss(url);
        assertNotNull("Feed was null", feed);
        print(feed);
    }
    
    @Test public void testStaticMethod() throws Throwable {
        String key = "foo";

        // Wrap Math.random().
        GenericTask task = new GenericTask();
        task.setKey(key);
        task.setActor(Math.class);
        task.setMethod("random");
        
        // Schedule job.
        // syntax: http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.schedule("myJob", task, "0/1 * * * * ?");
        
        // Ordinarily you wouldn't wait and would just call the get on a different thread whenever you wanted, but here we wait like any normal async method that isn't event-driven.
        waitForNerot();
        
        // Validate it ran task and stored result.
        Object result = nerot.get(key);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }
    
    @Test public void testInstanceMethod() throws Throwable {
        String key = "foo";        

        // Wrap a sample object with GenericTask
        GenericTask task = new GenericTask();
        task.setKey(key);
        task.setActor(new SampleObjectToWrap());
        task.setMethod("someFantasticMethod");
        task.setArgs(new Object[] {"s", new Date(), true, 'c', 1.0D, new Character('c')});
        
        // Schedule job.
        // Syntax at http://www.quartz-scheduler.org/docs/tutorials/crontrigger.html
        nerot.schedule("myJob", task, "0/1 * * * * ?");
        
        // Ordinarily you wouldn't wait and would just call the get on a different thread whenever you wanted, but here we wait like any normal async method that isn't event-driven.
        waitForNerot();
        
        // Validate it ran task and stored result.        
        Object result = nerot.get(key);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }
    
    private void waitForNerot() {
        try {
            // Longer than I needed, but may need to set this higher if your machine is really slow. Wait on Quartz thread to spin up, read schedule, and perform task (usually < 3 sec).
            Thread.sleep(1000);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void print(SyndFeed feed) {
        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
            SyndEntry entry = (SyndEntry) i.next();
            System.out.println(entry.getTitle());
        }
    }
}
