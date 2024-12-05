package dev.bsinfo.ressource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("Hausveraltung")
public class HausverwaltungRessource {

    @Path("Data")
    @GET
    public String getHausverwaltungData(){
        return "Data";
    }
}
