package nerot.spring;

import nerot.Nerot;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This bean lets you schedule Nerot by just defining a bean (e.g. in the root context of your application). The
 * properties that must be set on the bean are the same as the ones on the corresponding schedule method in Nerot:
 * <p>
 * nerot.scheduleRss(url, cronSchedule);
 * </p>
 * The following are properties that must be set on this bean:
 * <ul>
 * <li>url</li>
 * <li>cronSchedule</li>
 * </ul>
 * It also has a "nerot" property, but that is @Autowired.
 * <p/>
 * (See the corresponding schedule method in Nerot for more details on the types and functions of that method.)
 */
public class RssCronScheduler implements InitializingBean {

    @Autowired
    private Nerot nerot;
    private String url;
    private String cronSchedule;

    public void afterPropertiesSet() throws Exception {
        nerot.scheduleRss(url, cronSchedule);
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

    public String getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }
}
