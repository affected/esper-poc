package io.mikael.nioth2015;

import io.mikael.nioth2015.model.TemperatureEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    public static void main(final String ... args) {
        SpringApplication.run(Application.class, args);
    }

}
