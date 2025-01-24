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
            customerDAO.createCustomerTable();
            readingDAO.createReadingTable();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Database setup completed successfully");
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error setting up database: " + e.getMessage());
            return Response.serverError()
                    .entity(error)
                    .build();
        }
    }
}