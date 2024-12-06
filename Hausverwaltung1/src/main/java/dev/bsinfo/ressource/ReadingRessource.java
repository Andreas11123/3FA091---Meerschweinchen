package dev.bsinfo.ressource;

import ReadingAndCustomer.Customer;
import ReadingAndCustomer.ICustomer;
import ReadingAndCustomer.IReading;
import ReadingAndCustomer.Reading;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.awt.*;
import java.time.LocalDate;
import java.util.UUID;

@Path("reading")
public class ReadingRessource {
    @GET
    @Path("reading")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response getReading(){
        Reading reading = new Reading();
        return Response.status(Response.Status.OK).entity(reading).build();
    }
}
