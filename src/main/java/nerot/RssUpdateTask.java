package nerot;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import nerot.rss.RssClient;

import java.io.IOException;
import java.util.Date;

public class RssUpdateTask extends BaseTask {

    private static final Log LOG = LogFactory.getLog(GenericTask.class);

    private String feedUrl;

    /**
     * Creates a new RssClient, gets the feed, and sets the result in the Store.
     */
    public void execute() {
        try {
            RssClient client = new RssClient();
            Object val = client.getSyndFeed(feedUrl);
            getStore().set(feedUrl, client.getSyndFeed(feedUrl));
            if (LOG.isDebugEnabled()) {
                LOG.debug("stored feed: " + feedUrl);
            }
        } catch (Throwable t) {
            LOG.error("failed to get feed: " + feedUrl, t);
        }
    }

    /**
     * Gets the FeedUrl.
     *
     * @return the Feed URL
     */
    public String getFeedUrl() {
        return feedUrl;
    }

    /**
     * Sets the FeedUrl.
     *
     * @param feedUrl the Feed URL
     */
    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
}