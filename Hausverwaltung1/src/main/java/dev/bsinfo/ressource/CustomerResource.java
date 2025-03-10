package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/customers")
public class CustomerResource {
    private final CustomerDAO customerDAO;
    private final ReadingDAO readingDAO;

    public CustomerResource() {
        this.customerDAO = new CustomerDAO(Util.getConnection("Hausverwaltung"));
        this.readingDAO = new ReadingDAO(Util.getConnection("Hausverwaltung"));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(/*Map<String, Customer> requestMap*/Customer customer) {
        try {
           /* if (requestMap == null || !requestMap.containsKey("customer")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid request format. Expected: {\"customer\": {...}}");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }*/

            //Customer customer = requestMap.get("customer");
            if (customer == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No customer data provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            if (customer.getId() == null) {
                customer.setId(UUID.randomUUID());
            }

            customerDAO.addCustomer(customer);
            Map<String, Object> response = new HashMap<>();
            response.put("customer", customer);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCustomers() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("customers", customerDAO.getAllCustomers());
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.serverError()
                    .entity(error)
                    .build();
        }
    }

    @GET
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReading(@PathParam("uuid") String uuid) {
        try {
            IReading reading = readingDAO.getReadingById(UUID.fromString(uuid));
            if (reading == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "reading not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }
            Map<String, Object> response = new HashMap<>();
            response.put("reading", reading);
            return Response.ok(response).build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Reading not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .build();
        }

    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateCustomer(/*Map<String, Customer> requestMap*/Customer customer) {
        try {
            /*if (requestMap == null || !requestMap.containsKey("customer")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request format. Expected: {\"customer\": {...}}")
                        .build();
            }*/

            //Customer customer = requestMap.get("customer");
            if (customer == null || customer.getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid customer data")
                        .build();
            }

            ICustomer existingCustomer = customerDAO.getCustomerById(customer.getId());
            if (existingCustomer == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Customer not found")
                        .build();
            }

            customerDAO.updateCustomer(customer);
            return Response.ok("Customer updated successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("uuid") String uuid) {
        try {
            UUID customerId = UUID.fromString(uuid);
            ICustomer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Customer not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            // Get customer's readings before deletion
            List<IReading> readings = readingDAO.getAllReadings().stream()
                    .filter(r -> r.getCustomer() != null && r.getCustomer().getId().equals(customerId))
                    .collect(Collectors.toList());

            customerDAO.deleteCustomer(customerId);

            Map<String, Object> response = new HashMap<>();
            response.put("customer", customer);
            response.put("readings", readings);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid UUID format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .build();
        }
    }
}