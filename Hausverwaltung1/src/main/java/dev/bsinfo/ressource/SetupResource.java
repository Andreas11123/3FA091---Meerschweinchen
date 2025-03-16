package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.CustomerDAO;
import ReadingAndCustomer.ReadingDAO;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;

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

            return Response.status(Response.Status.OK).entity("Database setup completed successfully").build();

        } catch (Exception e) {
            System.out.println("test");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error setting up database: " + e.getMessage()).build();
        }
    }
}