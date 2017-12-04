package io.mikael.nioth2015;

import com.google.common.collect.ImmutableMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@SpringBootApplication
@RestController
@CrossOrigin(maxAge = 3600L)
public class Application {

    private final EventProcessor ep;

    public Application(final EventProcessor ep) {
        this.ep = ep;
    }

    @RequestMapping(value="/events/", method=RequestMethod.POST)
    public ResponseEntity<TemperatureEvent> createEvent(final @RequestBody TemperatureEvent event) {
        ep.submitEvent(event);
        return ok().body(event);
    }

    @RequestMapping(value="/analysis/", method=RequestMethod.GET)
    public ResponseEntity<Map<String, ?>> showAnalysis() {
        return ok().body(ImmutableMap.of("min", ep.min, "max", ep.max, "avg", ep.avg, "latest", ep.latest));
    }

    @RequestMapping(value="/lists/", method=RequestMethod.GET)
    public ResponseEntity<List<SensorAnalysis>> showLists() {
        return ok().body(ep.readLists());
    }

    @RequestMapping(value="/alerts/{id}", method=RequestMethod.POST)
    public ResponseEntity<String> setStatus(final @PathVariable("id") String id, final @RequestBody String alert) {
        ep.alerts.put(id, alert);
        return ok().body("OK");
    }

    @RequestMapping(value="/emoticons/{id}", method=RequestMethod.POST)
    public ResponseEntity<String> setEmoticon(final @PathVariable("id") String id, final @RequestBody String emoticon) {
        ep.emoticons.put(id, Integer.parseInt(emoticon));
        return ok().body("OK");
    }

    public static void main(final String ... args) {
        SpringApplication.run(Application.class, args);
    }

}
