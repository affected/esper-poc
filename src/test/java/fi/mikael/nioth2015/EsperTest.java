package fi.mikael.nioth2015;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class EsperTest {

    private static final Logger LOG = LoggerFactory.getLogger(EsperTest.class);

    private static final double[] TEMPS = { 10D, 20D, 30D, 40D, 50D, 40D, 30D, 20D, 10D };

    private static final ZonedDateTime now = ZonedDateTime.now().minusSeconds(30);

    @Test
    public void testEvents() throws Exception {
        LOG.error("starting");

        final AtomicReference<Double> avg = new AtomicReference<>(0D);

        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.addEventType(TemperatureEvent.class);
        final EPServiceProvider esperProvider = EPServiceProviderManager.getDefaultProvider(config);

        final String expression = "select avg(value) from io.mikael.nioth2015.model.TemperatureEvent.win:time(30 sec)";
        final EPStatement statement = esperProvider.getEPAdministrator().createEPL(expression);

        statement.addListener(new StatementAwareUpdateListener() {
            @Override
            public void update(final EventBean[] newEvents, final EventBean[] oldEvents, final EPStatement statement1, final EPServiceProvider epServiceProvider) {
                final EventBean event = newEvents[0];
                avg.set((Double) event.get("avg(value)"));
            }
        });

        for (int i = 0; i < TEMPS.length; i++) {
            final TemperatureEvent event = new TemperatureEvent();
            event.setTimeCreated(now.plusSeconds(i * 10));
            event.setValue(TEMPS[i]);
            esperProvider.getEPRuntime().sendEvent(
                    new CurrentTimeEvent(event.getTimeCreated().toInstant().toEpochMilli()));
            esperProvider.getEPRuntime().sendEvent(event);
        }

        Assert.assertEquals("average temperature should be 20", 20D, (double) avg.get(), 0.000001D);
    }

}
