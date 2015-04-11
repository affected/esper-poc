package io.mikael.nioth2015;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.google.common.collect.ImmutableList;
import io.mikael.nioth2015.model.SensorAnalysis;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class EventProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(EventProcessor.class);

    private EPServiceProvider esperProvider;

    final ConcurrentMap<String, Double> min = new ConcurrentHashMap<>();

    final ConcurrentMap<String, Double> max = new ConcurrentHashMap<>();

    final ConcurrentMap<String, Double> avg = new ConcurrentHashMap<>();

    final ConcurrentMap<String, Double> latest = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.addEventType(TemperatureEvent.class);
        esperProvider = EPServiceProviderManager.getDefaultProvider(config);

        final String expression = "select min(value), max(value), avg(value), sensorId " +
                " from io.mikael.nioth2015.model.TemperatureEvent.win:time(30 sec) " +
                " GROUP BY sensorId ";

        final EPStatement statement = esperProvider.getEPAdministrator().createEPL(expression);
        statement.addListener(new StatementAwareUpdateListener() {
            @Override
            public void update(final EventBean[] newEvents, final EventBean[] oldEvents, final EPStatement statement, final EPServiceProvider epServiceProvider) {
                for (final EventBean eb : newEvents) {
                    final String sensorId = (String) eb.get("sensorId");
                    min.put(sensorId, (Double) eb.get("min(value)"));
                    max.put(sensorId, (Double) eb.get("max(value)"));
                    avg.put(sensorId, (Double) eb.get("avg(value)"));
                }
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
        latest.put(event.getSensorId(), event.getValue());
        esperProvider.getEPRuntime().sendEvent(
                new CurrentTimeEvent(event.getTimeCreated().toInstant().toEpochMilli()));
        esperProvider.getEPRuntime().sendEvent(event);
    }

    public List<SensorAnalysis> readLists() {
        final SensorAnalysis sa1 = new SensorAnalysis();
        sa1.id = "100";
        sa1.apartmentName = "A1";
        sa1.insideTemperature = 20D;
        sa1.getInsideTemperatureComparison = 1;
        sa1.radiatorTemperature = 34D;
        sa1.getRadiatorTemperatureComparison = 1;
        sa1.emoticon = 0;
        sa1.alert = "Open Window";

        final SensorAnalysis sa2 = new SensorAnalysis();
        sa2.id = "101";
        sa2.apartmentName = "A2";
        sa2.insideTemperature = 20D;
        sa2.getInsideTemperatureComparison = 1;
        sa2.radiatorTemperature = 32D;
        sa2.getRadiatorTemperatureComparison = 1;
        sa2.emoticon = 1;
        sa2.alert = "";

        final SensorAnalysis sa3 = new SensorAnalysis();
        sa3.id = "103";
        sa3.apartmentName = "A3";
        sa3.insideTemperature = 20D;
        sa3.getInsideTemperatureComparison = 1;
        sa3.radiatorTemperature = 32D;
        sa3.getRadiatorTemperatureComparison = 1;
        sa3.emoticon = 1;
        sa3.alert = "";

        final SensorAnalysis sa4 = new SensorAnalysis();
        sa4.id = "104";
        sa4.apartmentName = "A4";
        sa4.insideTemperature = 20D;
        sa4.getInsideTemperatureComparison = 1;
        sa4.radiatorTemperature = 32D;
        sa4.getRadiatorTemperatureComparison = 1;
        sa4.emoticon = 1;
        sa4.alert = "";

        final SensorAnalysis sa5 = new SensorAnalysis();
        sa5.id = "105";
        sa5.apartmentName = "A5";
        sa5.insideTemperature = 20D;
        sa5.getInsideTemperatureComparison = 1;
        sa5.radiatorTemperature = 32D;
        sa5.getRadiatorTemperatureComparison = 1;
        sa5.emoticon = 1;
        sa5.alert = "";

        final SensorAnalysis sa6 = new SensorAnalysis();
        sa6.id = "106";
        sa6.apartmentName = "A6";
        sa6.insideTemperature = 22D;
        sa6.getInsideTemperatureComparison = 1;
        sa6.radiatorTemperature = 31D;
        sa6.getRadiatorTemperatureComparison = 1;
        sa6.emoticon = 1;
        sa6.alert = "";

        final SensorAnalysis sa7 = new SensorAnalysis();
        sa7.id = "107";
        sa7.apartmentName = "A7";
        sa7.insideTemperature = 21D;
        sa7.getInsideTemperatureComparison = 1;
        sa7.radiatorTemperature = 33D;
        sa7.getRadiatorTemperatureComparison = 1;
        sa7.emoticon = 1;
        sa7.alert = "";

        final SensorAnalysis sa8 = new SensorAnalysis();
        sa8.id = "108";
        sa8.apartmentName = "A8";
        sa8.insideTemperature = 19D;
        sa8.getInsideTemperatureComparison = 1;
        sa8.radiatorTemperature = 34D;
        sa8.getRadiatorTemperatureComparison = 1;
        sa8.emoticon = 1;
        sa8.alert = "";

        return ImmutableList.of(sa1, sa2, sa3, sa4, sa5, sa6, sa7, sa8);
    }
}
