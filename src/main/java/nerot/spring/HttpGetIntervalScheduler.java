package nerot.spring;

import nerot.Nerot;
import nerot.store.Storer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This bean lets you schedule Nerot by just defining a bean (e.g. in the root context of your application). The
 * properties that must be set on the bean are the same as the ones on the corresponding schedule method in Nerot:
 * <p>
 * nerot.scheduleHttpGet(url, intervalInMillis);
 * </p>
 * The following are properties that must be set on this bean:
 * <ul>
 * <li>url</li>
 * <li>intervalInMillis</li>
 * </ul>
 * It also has a "nerot" property, but that is @Autowired.
 * <p/>
 * (See the corresponding schedule method in Nerot for more details on the types and functions of that method.)
 */
public class HttpGetIntervalScheduler implements InitializingBean, Storer {

    @Autowired
    private Nerot nerot;
    private String url;
    private long intervalInMillis;

    public void afterPropertiesSet() throws Exception {
        nerot.scheduleHttpGet(url, intervalInMillis);
    }

    protected void finalize() throws Throwable {
        try {
            nerot.unschedule(url);
        }
        finally {
            super.finalize();
        }
    }

    public Nerot getNerot() {
        return nerot;
    }

    public void setNerot(Nerot nerot) {
        this.nerot = nerot;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getIntervalInMillis() {
        return intervalInMillis;
    }

    public void setIntervalInMillis(long intervalInMillis) {
        this.intervalInMillis = intervalInMillis;
    }

    /**
     * Gets the store key. In this case, the url.
     *
     * @return the store key
     */
    public String getStoreKey() {
        return url;
    }
}
