package io.mikael.nioth2015;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Component
public class EventProcessor {

    private EPServiceProvider esperProvider;

    @PostConstruct
    public void postConstruct() {
        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        esperProvider = EPServiceProviderManager.getDefaultProvider(config);
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
        esperProvider.getEPRuntime().sendEvent(new CurrentTimeEvent(event.timeCreated.toInstant().toEpochMilli()));
        esperProvider.getEPRuntime().sendEvent(event);
    }

}
