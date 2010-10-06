package nerot;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
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


// RunWith is required to force what would otherwise look like a JUnit 3.x test
// to run with the JUnit 4 test runner.
@RunWith(SpringJUnit4ClassRunner.class)
//@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
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

    protected String[] getConfigLocations() {
        return new String[] {"classpath:nerot.xml"};
    }
    
    // just a test to allow you to manually eye results for now. this isn't really testing anything unless you eye it.
    // could add unit tests, etc. but it really doesn't do a whole lot.
    @Test public void testRss() throws Throwable {
        // every five seconds
        String url = "http://news.google.com/news?ned=us&topic=t&output=rss";
        nerot.scheduleRss(url, "0/1 * * * * ?");
        try {
            Thread.sleep(3000);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        SyndFeed feed = nerot.getRss(url);
        assertNotNull("Feed was null", feed);
        print(feed);
    }
    
    @Test public void testStaticMethod() throws Throwable {
        // every five seconds
        GenericTask task = new GenericTask();
        String key = "foo";
        String jobId = "myJob";
        task.setKey(key);
        task.setActor(Math.class);
        task.setMethod("random");
        task.setArgs(null);
        
        nerot.schedule(jobId, task, "0/1 * * * * ?");
        try {
            Thread.sleep(3000);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        Object result = nerot.get(key);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }
    
    @Test public void testNonstaticMethod() throws Throwable {
        // every five seconds
        GenericTask task = new GenericTask();
        String jobId = "myJob";
        String key = "foo";
        
        SampleObjectToWrap obj = new SampleObjectToWrap();
        
        task.setKey(key);
        task.setActor(obj);
        task.setMethod("someFantasticMethod");
        task.setArgs(new Object[] {"s", new Date(), true, 'c', 1.0D, new Character('c')});
        
        nerot.schedule(jobId, task, "0/1 * * * * ?");
        try {
            Thread.sleep(3000);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        Object result = nerot.get(key);
        assertNotNull("Result was null", result);
        System.err.println(result);
    }

    private void print(SyndFeed feed) {

        for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
            SyndEntry entry = (SyndEntry) i.next();
            System.out.println(entry.getTitle());
        }
    }
}
