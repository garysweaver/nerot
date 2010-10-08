package nerot;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import java.util.Date;
import java.io.IOException;
import nerot.rss.RssClient;

public class RssUpdateTask extends BaseTask {

    private String feedUrl;
    
    /**
     * Creates a new RssClient, gets the feed, and sets the result in the Store.
     */
    public void execute() {    
        try {
            RssClient client = new RssClient();
            Object val = client.getSyndFeed(feedUrl);
            getStore().set(feedUrl, client.getSyndFeed(feedUrl));
            System.err.println("stored feed: " + feedUrl);
        } catch (Throwable t) {
            System.err.println("failed to get feed: " + feedUrl);
            t.printStackTrace();
        }
    }

    /**
     * Gets the FeedUrl.
     */
    public String getFeedUrl() {
        return feedUrl;
    }
    
    /**
     * Sets the FeedUrl.
     */
    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
}