package nerot.task;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Gets an RSS feed.
 */
public class RssTask extends BaseTask {

    private static final Logger LOG = LoggerFactory.getLogger(RssTask.class);

    private String url;

    /**
     * Gets the feed, and sets the result in the Store.
     */
    public Object doExecute() throws java.io.IOException, java.net.MalformedURLException, com.sun.syndication.io.FeedException {
        SyndFeed result = null;
        XmlReader reader = null;
        try {
            reader = new XmlReader(new URL(url));
            result = new SyndFeedInput().build(reader);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    /**
     * Gets the Feed URL.
     *
     * @return the Feed URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the Feed URL.
     *
     * @param url the Feed URL
     */
    public void setUrl(String url) {
        this.url = url;
    }
}