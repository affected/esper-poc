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

        final AtomicReference<Double> avg1 = new AtomicReference<>(0D);
        final AtomicReference<Double> avg2 = new AtomicReference<>(0D);

        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.addEventType(TemperatureEvent.class);
        final EPServiceProvider esperProvider = EPServiceProviderManager.getDefaultProvider(config);

        final String expression = "select avg(value), sensorId " +
                " from io.mikael.nioth2015.model.TemperatureEvent.win:time(30 sec) " +
                " GROUP BY sensorId ";
        final EPStatement statement = esperProvider.getEPAdministrator().createEPL(expression);

        statement.addListener(new StatementAwareUpdateListener() {
            @Override
            public void update(final EventBean[] newEvents, final EventBean[] oldEvents, final EPStatement statement1, final EPServiceProvider epServiceProvider) {
                System.err.println(Arrays.toString(newEvents));
                for (final EventBean eb : newEvents) {
                    final String sensorId = (String) eb.get("sensorId");
                    final Double avg = (Double) eb.get("avg(value)");
                    System.err.println(avg + " " + sensorId);
                    if ("1".equals(sensorId)) {
                        avg1.set(avg);
                    } else {
                        avg2.set(avg);
                    }
                }
            }
        });

        for (int i = 0; i < TEMPS.length; i++) {
            final TemperatureEvent e1 = new TemperatureEvent();
            e1.setSensorId("1");
            e1.setTimeCreated(now.plusSeconds(i * 10));
            e1.setValue(TEMPS[i]);
            final TemperatureEvent e2 = new TemperatureEvent();
            e2.setSensorId("2");
            e2.setTimeCreated(now.plusSeconds(i * 10));
            e2.setValue(TEMPS[i] + 10);
            esperProvider.getEPRuntime().sendEvent(
                    new CurrentTimeEvent(e1.getTimeCreated().toInstant().toEpochMilli()));
            esperProvider.getEPRuntime().sendEvent(e1);
            esperProvider.getEPRuntime().sendEvent(e2);
        }

        Assert.assertEquals("average temperature should be 20", 20D, avg1.get(), 0.000001D);
        Assert.assertEquals("average temperature should be 30", 30D, avg2.get(), 0.000001D);
    }

}
