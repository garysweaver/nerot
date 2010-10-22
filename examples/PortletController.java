package nerot.example;

import com.sun.syndication.feed.synd.SyndFeed;
import nerot.Nerot;
import nerot.store.Storer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortletController extends AbstractController {
    private static final Logger LOG = LoggerFactory.getLogger(UgradEdNewsController.class);

    @Autowired
    private Nerot nerot;

    @Autowired
    @Qualifier("rssScheduler")
    private Storer storer;

    protected ModelAndView handleRenderRequestInternal(RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
        String feedUrl = storer.getStoreKey();
        Map<String, Object> params = new HashMap<String, Object>();
        SyndFeed feed = nerot.getRssFromStore(feedUrl);

        if (feed==null) {
            logger.warn("Got null feed from Nerot for '" + feedUrl + "'");
            params.put("entries", new ArrayList());
        }
        else {
            logger.info("Got feed from Nerot for '" + feedUrl + "'");
            List entries = feed.getEntries();
            params.put("entries", feed.getEntries());
        }
        
        return new ModelAndView("view", params);
    }
}
