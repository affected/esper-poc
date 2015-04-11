package io.mikael.nioth2015;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class EventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessor.class);

    private EPServiceProvider esperProvider;

    public volatile double avg = 0D;

    @PostConstruct
    public void postConstruct() {
        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.addEventType(TemperatureEvent.class);
        esperProvider = EPServiceProviderManager.getDefaultProvider(config);

        final String expression = "select avg(value) from io.mikael.nioth2015.model.TemperatureEvent.win:time(30 sec)";
        final EPStatement statement = esperProvider.getEPAdministrator().createEPL(expression);
        statement.addListener(new StatementAwareUpdateListener() {
            @Override
            public void update(final EventBean[] newEvents, final EventBean[] oldEvents, final EPStatement statement, final EPServiceProvider epServiceProvider) {
                EventBean event = newEvents[0];
                final Double newAvg = (Double) event.get("avg(value)");
                System.out.println("avg=" + newAvg + " " + newAvg.getClass());
                LOG.debug("avg=" + newAvg + " " + newAvg.getClass());
                avg = newAvg;
            }
        });
    }

    public void submitMessage(final String jsonPayload) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JSR310Module());
        try {
            submitEvent(mapper.readValue(jsonPayload, TemperatureEvent.class));
        } catch (final IOException e) {
            throw new RuntimeException("temperature event json deserialization failed", e);
        }
    }

    public void submitEvent(final TemperatureEvent event) {
        esperProvider.getEPRuntime().sendEvent(new CurrentTimeEvent(event.getTimeCreated().toInstant().toEpochMilli()));
        esperProvider.getEPRuntime().sendEvent(event);
    }

}
