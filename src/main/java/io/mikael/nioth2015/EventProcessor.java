package io.mikael.nioth2015;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EventProcessor {

    public final EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();

    public void submitMessage(final String jsonPayload) {
        try {
            epService.getEPRuntime().sendEvent(new ObjectMapper().readValue(jsonPayload, TemperatureEvent.class));
        } catch (final IOException e) {
            throw new RuntimeException("temperature event json deserialization failed", e);
        }
    }

    public void submitEvent(final TemperatureEvent event) {
        epService.getEPRuntime().sendEvent(event);
    }

}
