package nerot.rss;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.net.URL;

public class RssClient {

    public SyndFeed getSyndFeed(String url) throws java.io.IOException, java.net.MalformedURLException, com.sun.syndication.io.FeedException {
        SyndFeed result = null;
        XmlReader reader = null;
        try {
            reader = new XmlReader(new URL(url));
            result = new SyndFeedInput().build(reader);
        } finally {
            if (reader != null) {
            }
            reader.close();
        }
        return result;
    }
}
