package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.CustomerDAO;
import ReadingAndCustomer.ReadingDAO;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Path("/setupDB")
public class SetupResource {
    private final CustomerDAO customerDAO;
    private final ReadingDAO readingDAO;

    public SetupResource() {
        Connection conn = Util.getConnection("Hausverwaltung");
        this.customerDAO = new CustomerDAO(conn);
        this.readingDAO = new ReadingDAO(conn);
    }

    @DELETE
    public Response setupDatabase() {
        try {
            // Delete tables first
            readingDAO.dropReadingTable();
            customerDAO.dropCustomerTable();

            // Create tables again
            customerDAO.createCustomerTable();
            readingDAO.createReadingTable();


            //response.put("message", "Database setup completed successfully");
            return Response.status(Response.Status.OK).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            //error.put("error", "Error setting up database: " + e.getMessage());

        }
    }
}