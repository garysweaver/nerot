package nerot.rss;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.net.URL;

public class RssClient {

    /**
     * Uses Rome RSS to parse a feed at the given URL.
     *
     * @param feedUrl the Feed URL
     * @return the SyndFeed 
     */
    public SyndFeed getSyndFeed(String feedUrl) throws java.io.IOException, java.net.MalformedURLException, com.sun.syndication.io.FeedException {
        SyndFeed result = null;
        XmlReader reader = null;
        try {
            reader = new XmlReader(new URL(feedUrl));
            result = new SyndFeedInput().build(reader);
        } finally {
            if (reader != null) {
            }
            reader.close();
        }
        return result;
    }
}
