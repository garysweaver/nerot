package com.acme;

import com.sun.syndication.feed.synd.SyndFeed;
import nerot.Nerot;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortletController extends AbstractController implements InitializingBean {

    private Nerot nerot;
    private String feedUrl = null;
    private String feedSchedule = null;
    
    public Nerot getNerot() {
        return nerot;
    }
    
    public void setNerot(Nerot nerot) {
        this.nerot = nerot;
    }
    
    public String getFeedSchedule() {
        return feedSchedule;
    }
    
    public void setFeedSchedule(String feedSchedule) {
        this.feedSchedule = feedSchedule;
    }
    
    public String getFeedUrl() {
        return feedUrl;
    }
    
    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }
    
    public void afterPropertiesSet() throws Exception {
    
        // This schedules the feed immediately after the bean is instantiated and it should run at least once to start
        nerot.scheduleRss(feedUrl, feedSchedule);
    }
    
    protected ModelAndView handleRenderRequestInternal(RenderRequest renderRequest, RenderResponse renderResponse)
            throws Exception {
    
        Map<String, Object> params = new HashMap<String, Object>();
        
        // This gets the feed from the store
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