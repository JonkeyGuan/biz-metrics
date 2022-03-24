package com.example;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/")
public class MessageResource {

    private static final Logger log = Logger.getLogger(MessageResource.class);

    @Inject
    @RestClient
    private RestService service;

    private long procesMesasgeTimeSpent;

    @GET
    @Path("/message/{message}")
    @Counted(name = "process_message_count")
    public String processMessage(@PathParam("message") String message) {
        long startTime = System.currentTimeMillis();

        log.infof("received message: %s", message);

        invokeService(message);

        long endTime = System.currentTimeMillis();
        procesMesasgeTimeSpent = endTime - startTime;

        return "Published " + message + " Successfully.";
    }

    @Timed(name = "process_message_service_call_time_spent", unit = MetricUnits.MILLISECONDS)
    protected String invokeService(String message) {
        return service.echo(message);
    }

    @Gauge(name = "proces_mesasge_time_spent", unit = MetricUnits.NONE)
    public long procesTimeSpent() {
        return procesMesasgeTimeSpent;
    }
}
