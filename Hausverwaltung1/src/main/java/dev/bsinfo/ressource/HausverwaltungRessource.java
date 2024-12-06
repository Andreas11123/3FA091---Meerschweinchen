package dev.bsinfo.ressource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("Hausverwaltung")
public class HausverwaltungRessource {

    @Path("Daten")
    @GET
    public String getHausverwaltungData(){
        return "Daten";
    }
}
