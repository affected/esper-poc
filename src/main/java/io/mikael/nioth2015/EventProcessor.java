package io.mikael.nioth2015;

import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
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

    final ConcurrentMap<String, String> alerts = new ConcurrentHashMap<>();

    final ConcurrentMap<String, Integer> emoticons = new ConcurrentHashMap<>();

    @PostConstruct
    public void postConstruct() {
        final Configuration config = new Configuration();
        config.getEngineDefaults().getThreading().setInternalTimerEnabled(false);
        config.addEventType(TemperatureEvent.class);
        esperProvider = EPServiceProviderManager.getDefaultProvider(config);

        final String expression = "select min(value), max(value), avg(value), sensorId " +
                " from io.mikael.nioth2015.TemperatureEvent.win:time(30 sec) " +
                " GROUP BY sensorId ";

        final EPStatement statement = esperProvider.getEPAdministrator().createEPL(expression);

        statement.addListener((news, olds, st, ep) -> {
            for (final EventBean eb : news) {
                final String sensorId = (String) eb.get("sensorId");
                min.put(sensorId, (Double) eb.get("min(value)"));
                max.put(sensorId, (Double) eb.get("max(value)"));
                avg.put(sensorId, (Double) eb.get("avg(value)"));
            }
        });
    }

    public TemperatureEvent submitMessage(final String jsonPayload) {
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final TemperatureEvent ret = mapper.readValue(jsonPayload, TemperatureEvent.class);
            submitEvent(ret);
            return ret;
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
        sa1.insideTemperature = latest.getOrDefault("100", 0D);
        sa1.insideTemperatureMin = min.getOrDefault("100", 0D);
        sa1.insideTemperatureMax = max.getOrDefault("100", 0D);
        sa1.insideTemperatureAvg = avg.getOrDefault("100", 0D);
        sa1.radiatorTemperature = latest.getOrDefault("101", 0D);
        sa1.radiatorTemperatureMin = min.getOrDefault("101", 0D);
        sa1.radiatorTemperatureMax = max.getOrDefault("101", 0D);
        sa1.radiatorTemperatureAvg = avg.getOrDefault("101", 0D);
        sa1.emoticon = emoticons.getOrDefault("A1", 0);
        sa1.alert = alerts.getOrDefault("A1", "");

        final SensorAnalysis sa2 = new SensorAnalysis();
        sa2.id = "101";
        sa2.apartmentName = "A2";
        sa2.insideTemperature = 20D;
        sa2.insideTemperatureMin = 19D;
        sa2.insideTemperatureMax = 24D;
        sa2.insideTemperatureAvg = 20D;
        sa2.radiatorTemperature = 32D;
        sa2.radiatorTemperatureMin = 30D;
        sa2.radiatorTemperatureMax = 35D;
        sa2.radiatorTemperatureAvg = 32D;
        sa2.emoticon = 1;
        sa2.alert = "";

        final SensorAnalysis sa3 = new SensorAnalysis();
        sa3.id = "103";
        sa3.apartmentName = "A3";
        sa3.insideTemperature = 20D;
        sa3.insideTemperatureMin = 19D;
        sa3.insideTemperatureMax = 24D;
        sa3.insideTemperatureAvg = 20D;
        sa3.radiatorTemperature = 32D;
        sa3.radiatorTemperatureMin = 30D;
        sa3.radiatorTemperatureMax = 35D;
        sa3.radiatorTemperatureAvg = 32D;
        sa3.emoticon = 1;
        sa3.alert = "";

        final SensorAnalysis sa4 = new SensorAnalysis();
        sa4.id = "104";
        sa4.apartmentName = "A4";
        sa4.insideTemperature = 20D;
        sa4.insideTemperatureMin = 19D;
        sa4.insideTemperatureMax = 24D;
        sa4.insideTemperatureAvg = 20D;
        sa4.radiatorTemperature = 32D;
        sa4.radiatorTemperatureMin = 30D;
        sa4.radiatorTemperatureMax = 35D;
        sa4.radiatorTemperatureAvg = 32D;
        sa4.emoticon = 1;
        sa4.alert = "";

        final SensorAnalysis sa5 = new SensorAnalysis();
        sa5.id = "105";
        sa5.apartmentName = "A5";
        sa5.insideTemperature = 20D;
        sa5.insideTemperatureMin = 19D;
        sa5.insideTemperatureMax = 24D;
        sa5.insideTemperatureAvg = 20D;
        sa5.radiatorTemperature = 32D;
        sa5.radiatorTemperatureMin = 30D;
        sa5.radiatorTemperatureMax = 35D;
        sa5.radiatorTemperatureAvg = 32D;
        sa5.emoticon = 1;
        sa5.alert = "";

        final SensorAnalysis sa6 = new SensorAnalysis();
        sa6.id = "106";
        sa6.apartmentName = "A6";
        sa6.insideTemperature = 20D;
        sa6.insideTemperatureMin = 19D;
        sa6.insideTemperatureMax = 24D;
        sa6.insideTemperatureAvg = 20D;
        sa6.radiatorTemperature = 32D;
        sa6.radiatorTemperatureMin = 30D;
        sa6.radiatorTemperatureMax = 35D;
        sa6.radiatorTemperatureAvg = 32D;
        sa6.emoticon = 1;
        sa6.alert = "";

        final SensorAnalysis sa7 = new SensorAnalysis();
        sa7.id = "107";
        sa7.apartmentName = "A7";
        sa7.insideTemperature = 20D;
        sa7.insideTemperatureMin = 19D;
        sa7.insideTemperatureMax = 24D;
        sa7.insideTemperatureAvg = 20D;
        sa7.radiatorTemperature = 32D;
        sa7.radiatorTemperatureMin = 30D;
        sa7.radiatorTemperatureMax = 35D;
        sa7.radiatorTemperatureAvg = 32D;
        sa7.emoticon = 1;
        sa7.alert = "";

        final SensorAnalysis sa8 = new SensorAnalysis();
        sa8.id = "108";
        sa8.apartmentName = "A8";
        sa8.insideTemperature = 20D;
        sa8.insideTemperatureMin = 19D;
        sa8.insideTemperatureMax = 24D;
        sa8.insideTemperatureAvg = 20D;
        sa8.radiatorTemperature = 32D;
        sa8.radiatorTemperatureMin = 30D;
        sa8.radiatorTemperatureMax = 35D;
        sa8.radiatorTemperatureAvg = 32D;
        sa8.emoticon = 1;
        sa8.alert = "";

        return ImmutableList.of(sa1, sa2, sa3, sa4, sa5, sa6, sa7, sa8);
    }
}
