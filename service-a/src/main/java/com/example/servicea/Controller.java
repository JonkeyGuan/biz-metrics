package com.example.servicea;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@RestController
public class Controller {

    private static Logger log = LoggerFactory.getLogger(Controller.class);

    private final Counter publishMessageCounter;
    private final AtomicLong publishMessageTimeSpent;
    private final Timer publishMessageServiceCallTimeSpent;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.url}")
    private String serviceURL;

    @Autowired
    public Controller(MeterRegistry meterRegistry) {
        publishMessageCounter = meterRegistry.counter("publish_mesasge_count");
        publishMessageTimeSpent = meterRegistry.gauge("publish_message_time_spent", new AtomicLong(0));
        publishMessageServiceCallTimeSpent = meterRegistry.timer("publish_message_service_call_time_spent");
    }

    @GetMapping("/message/{message}")
    public String publishMessage(@PathVariable("message") String message) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        log.info("received message: {}", message);

        publishMessageServiceCallTimeSpent
                .record(() -> restTemplate.getForObject(serviceURL + "/" + message, String.class));

        stopWatch.stop();

        publishMessageCounter.increment();
        publishMessageTimeSpent.set(stopWatch.getTotalTimeMillis());

        return "Published " + message + " Successfully.";
    }

    @GetMapping("/echo/{message}")
    public String echo(@PathVariable("message") String message) {
        log.info("echo message: {}", message);
        return "echo " + message;
    }
}
