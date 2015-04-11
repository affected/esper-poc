package io.mikael.nioth2015;

import com.google.common.collect.ImmutableMap;
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
import java.util.Map;

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
    public ResponseEntity<Map> showAnalysis() {
        return new ResponseEntity<>(
                ImmutableMap.of("min", eventProcessor.min,
                        "max", eventProcessor.max,
                        "avg", eventProcessor.avg,
                        "latest", eventProcessor.latest),
                HttpStatus.OK);
    }

    @RequestMapping(value="/lists/", method=RequestMethod.GET)
    public ResponseEntity<List<SensorAnalysis>> showLists() {
        return new ResponseEntity<>(eventProcessor.readLists(), HttpStatus.OK);
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
