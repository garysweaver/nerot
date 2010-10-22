package nerot.spring;

import nerot.Nerot;
import nerot.store.Storer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath*:nerot/nerot.xml",
        "classpath:nerot/spring/intervalScheduler.xml"})
public class IntervalSchedulerTest extends AbstractDependencyInjectionSpringContextTests {

    @Autowired
    public Nerot nerot;

    @Autowired
    @Qualifier("intervalScheduler")
    public Storer storer;

    @Test
    public void testIntervalScheduler() {
        assertNotNull(nerot.getRssFromStore(storer.getStoreKey()));
    }
}
