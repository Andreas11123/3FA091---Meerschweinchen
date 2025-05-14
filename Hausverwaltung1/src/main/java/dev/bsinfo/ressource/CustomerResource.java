package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
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

    @OPTIONS
    @Path("{path:.*}")
    public Response options() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept")
                .build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(Customer customer) {
        try {
            System.out.println("Received customer for creation: " + (customer != null ? customer.toString() : "null"));

            if (customer == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No customer data provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Log important fields
            System.out.println("Customer ID: " + customer.getId());
            System.out.println("First Name: " + customer.getFirstname());
            System.out.println("Last Name: " + customer.getLastname());
            System.out.println("Birth Date: " + customer.getBirthdate());
            System.out.println("Gender: " + customer.getGender());

            if (customer.getId() == null) {
                customer.setId(UUID.randomUUID());
            }

            customerDAO.addCustomer(customer);
            System.out.println("Customer created successfully with ID: " + customer.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("customer", customer);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            System.out.println("Error creating customer:");
            e.printStackTrace();

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
            System.out.println("Getting all customers");
            List<ICustomer> customers = customerDAO.getAllCustomers();
            System.out.println("Retrieved " + customers.size() + " customers");

            Map<String, Object> response = new HashMap<>();
            response.put("customers", customers);
            return Response.ok(response).build();
        } catch (Exception e) {
            System.out.println("Error getting all customers:");
            e.printStackTrace();

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
    public Response getCustomer(@PathParam("uuid") String uuid) {
        try {
            System.out.println("Getting customer with ID: " + uuid);

            ICustomer customer = customerDAO.getCustomerById(UUID.fromString(uuid));
            if (customer == null) {
                System.out.println("Customer not found: " + uuid);
                Map<String, String> error = new HashMap<>();
                error.put("error", "Customer not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            System.out.println("Found customer: " + customer.getFirstname() + " " + customer.getLastname());
            Map<String, Object> response = new HashMap<>();
            response.put("customer", customer);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format: " + uuid);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid UUID format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        } catch (Exception e) {
            System.out.println("Error getting customer:");
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Customer not found");
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(Customer customer) {
        try {
            System.out.println("Received update data: " + customer);

            if (customer == null || customer.getId() == null) {
                System.out.println("Invalid customer data");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid customer data");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Log wichtige Felder
            System.out.println("Customer ID: " + customer.getId());
            System.out.println("First Name: " + customer.getFirstname());
            System.out.println("Last Name: " + customer.getLastname());
            System.out.println("Birth Date: " + customer.getBirthdate());
            System.out.println("Gender: " + customer.getGender());

            ICustomer existingCustomer = customerDAO.getCustomerById(customer.getId());
            if (existingCustomer == null) {
                System.out.println("Customer not found: " + customer.getId());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Customer not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            customerDAO.updateCustomer(customer);
            System.out.println("Customer updated successfully: " + customer.getId());

            // JSON-Antwort statt Plaintext
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Customer updated successfully");
            response.put("customer", customer);
            return Response.ok(response).build();
        } catch (Exception e) {
            System.out.println("Error updating customer:");
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating customer: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("uuid") String uuid) {
        try {
            System.out.println("Deleting customer with ID: " + uuid);

            UUID customerId = UUID.fromString(uuid);
            ICustomer customer = customerDAO.getCustomerById(customerId);
            if (customer == null) {
                System.out.println("Customer not found: " + uuid);
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
            System.out.println("Customer deleted successfully: " + uuid);

            Map<String, Object> response = new HashMap<>();
            response.put("customer", customer);
            response.put("readings", readings);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID format: " + uuid);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid UUID format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        } catch (Exception e) {
            System.out.println("Error deleting customer:");
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error)
                    .build();
        }
    }
}