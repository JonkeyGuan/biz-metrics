package com.example;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient
@Path("/")
public interface RestService {

    @GET
    @Path("/echo/{message}")
    public String echo(@PathParam("message") String message);

}
