package io.mikael.nioth2015;

import com.google.common.collect.ImmutableList;
import io.mikael.nioth2015.model.SensorAnalysis;
import io.mikael.nioth2015.model.TemperatureEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private EventProcessor eventProcessor;

    @Autowired
    private EventStreamConnection connection;

    @RequestMapping(value="/events/{id}", method=RequestMethod.GET)
    public ResponseEntity<TemperatureEvent> readEvent(final @PathVariable String id) {
        return new ResponseEntity<>((TemperatureEvent)null, HttpStatus.OK);
    }

    @RequestMapping(value="/events/", method=RequestMethod.POST)
    public ResponseEntity<TemperatureEvent> createEvent(final @RequestBody TemperatureEvent event) {
        eventProcessor.submitEvent(event);
        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    @RequestMapping(value="/analysis/", method=RequestMethod.GET)
    public ResponseEntity<String> showAnalysis() {
        return new ResponseEntity<>(Double.toString(eventProcessor.avg), HttpStatus.OK);
    }

    @RequestMapping(value="/lists/", method=RequestMethod.GET)
    public ResponseEntity<List<SensorAnalysis>> showLists() {
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

        return new ResponseEntity<>(ImmutableList.of(sa1, sa2), HttpStatus.OK);
    }

    @Bean
    public Filter corsFilter() {
        return new Filter() {
            @Override
            public void init(final FilterConfig filterConfig) throws ServletException {
            }

            @Override
            public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
                final HttpServletResponse response = (HttpServletResponse) res;
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
                chain.doFilter(req, res);
            }

            @Override
            public void destroy() {
            }
        };
    }

    public static void main(final String ... args) {
        SpringApplication.run(Application.class, args);
    }

}
