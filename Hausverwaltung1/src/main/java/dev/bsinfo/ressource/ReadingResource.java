package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/readings")
public class ReadingResource {
    private final ReadingDAO readingDAO;
    private final CustomerDAO customerDAO;

    public ReadingResource() {
        this.readingDAO = new ReadingDAO(Util.getConnection("Hausverwaltung"));
        this.customerDAO = new CustomerDAO(Util.getConnection("Hausverwaltung"));
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
    public Response createReading(Reading reading) {
        try {
            // Enhanced debug logging
            System.out.println("Received reading object: " + (reading != null ? reading.toString() : "null"));

            if (reading == null) {
                System.out.println("Error: Reading object is null");
                Map<String, String> error = new HashMap<>();
                error.put("error", "No reading data provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Log all important fields for debugging
            System.out.println("Reading ID: " + reading.getId());
            System.out.println("Customer: " + (reading.getCustomer() != null ? reading.getCustomer().getId() : "null"));
            System.out.println("Date of Reading: " + reading.getDateOfReading());
            System.out.println("Kind of Meter: " + reading.getKindOfMeter());
            System.out.println("Meter Count: " + reading.getMeterCount());
            System.out.println("Meter ID: " + reading.getMeterId());
            System.out.println("Substitute: " + reading.getSubstitute());

            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }

            if (reading.getCustomer() != null) {
                ICustomer customer = reading.getCustomer();
                if (customer.getId() == null) {
                    customer.setId(UUID.randomUUID());
                }
                System.out.println("Looking up customer with ID: " + customer.getId());
                ICustomer existingCustomer = customerDAO.getCustomerById(customer.getId());
                if (existingCustomer == null) {
                    System.out.println("Customer not found, will create new customer");
                    customerDAO.addCustomer(customer);
                } else {
                    System.out.println("Found existing customer: " + existingCustomer.getFirstname() +
                            " " + existingCustomer.getLastname());
                }
            }

            System.out.println("Adding reading to database");
            readingDAO.addReading(reading);

            System.out.println("Reading added successfully with ID: " + reading.getId());
            Map<String, Object> response = new HashMap<>();
            response.put("reading", reading);
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (Exception e) {
            // Print the full stack trace for debugging
            System.out.println("Error creating reading:");
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Error creating reading: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        }
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateReading(Reading reading) {
        try {
            // Debug-Ausgabe
            System.out.println("Received reading object for update: " + (reading != null ? reading.toString() : "null"));

            if (reading == null || reading.getId() == null) {
                System.out.println("Invalid reading data for update");
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid reading data");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            // Log alle wichtigen Felder
            System.out.println("Reading ID: " + reading.getId());
            System.out.println("Customer: " + (reading.getCustomer() != null ? reading.getCustomer().getId() : "null"));
            System.out.println("Date of Reading: " + reading.getDateOfReading());
            System.out.println("Kind of Meter: " + reading.getKindOfMeter());
            System.out.println("Meter Count: " + reading.getMeterCount());
            System.out.println("Meter ID: " + reading.getMeterId());
            System.out.println("Substitute: " + reading.getSubstitute());

            IReading existingReading = readingDAO.getReadingById(reading.getId());
            if (existingReading == null) {
                System.out.println("Reading not found: " + reading.getId());
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reading not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            System.out.println("Existing reading found, updating...");

            readingDAO.updateReading(reading);
            System.out.println("Reading updated successfully");

            // JSON-Antwort statt Plaintext
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Reading updated successfully");
            response.put("reading", reading);
            return Response.ok(response).build();
        } catch (Exception e) {
            System.out.println("Error updating reading:");
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Error updating reading: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings(
            @QueryParam("customer") String customerId,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate,
            @QueryParam("kindOfMeter") String kindOfMeter) {
        try {
            System.out.println("Received GET request for readings with params:");
            System.out.println("- customer: " + customerId);
            System.out.println("- start: " + startDate);
            System.out.println("- end: " + endDate);
            System.out.println("- kindOfMeter: " + kindOfMeter);

            // Make date parameters optional with defaults
            final LocalDate start = (startDate == null || startDate.trim().isEmpty()) ?
                    LocalDate.of(2000, 1, 1) : LocalDate.parse(startDate); // Default to a past date

            final LocalDate end = (endDate == null || endDate.trim().isEmpty()) ?
                    LocalDate.now() : LocalDate.parse(endDate); // Default to today

            final IReading.KindOfMeter meterType = (kindOfMeter == null || kindOfMeter.trim().isEmpty()) ?
                    null : IReading.KindOfMeter.valueOf(kindOfMeter.toUpperCase());

            final UUID custId = (customerId == null || customerId.trim().isEmpty()) ?
                    null : UUID.fromString(customerId);

            System.out.println("Parsed parameters:");
            System.out.println("- custId: " + custId);
            System.out.println("- start: " + start);
            System.out.println("- end: " + end);
            System.out.println("- meterType: " + meterType);

            // Get base list of readings
            List<IReading> readings = readingDAO.filterReadings(custId, start, end, meterType);
            System.out.println("Retrieved " + readings.size() + " readings from database");

            // Apply filters in a single stream operation
            readings = readings.stream()
                    .filter(reading -> {
                        boolean matches = true;

                        if (custId != null) {
                            matches = reading.getCustomer() != null &&
                                    reading.getCustomer().getId().equals(custId);
                        }

                        if (matches && start != null) {
                            matches = !reading.getDateOfReading().isBefore(start);
                        }

                        if (matches && end != null) {
                            matches = !reading.getDateOfReading().isAfter(end);
                        }

                        if (matches && meterType != null) {
                            matches = reading.getKindOfMeter() == meterType;
                        }

                        return matches;
                    })
                    .collect(Collectors.toList());

            System.out.println("After filtering: " + readings.size() + " readings");

            // Create a response wrapper
            Map<String, Object> response = new HashMap<>();
            response.put("readings", readings);

            // Return WITHOUT any CORS headers - let the filter handle this
            return Response.ok(response).build();

        } catch (DateTimeParseException e) {
            System.out.println("Date parse error: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid date format. Use yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        } catch (IllegalArgumentException e) {
            System.out.println("Argument error: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid parameter format: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        } catch (Exception e) {
            System.out.println("General error in getReadings:");
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorResponse)
                    .build();
        }
    }

    @DELETE
    @Path("/{uuid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteReading(@PathParam("uuid") String uuid) {
        try {
            UUID readingId = UUID.fromString(uuid);
            IReading reading = readingDAO.getReadingById(readingId);

            if (reading == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Reading not found");
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(error)
                        .build();
            }

            readingDAO.deleteReading(readingId);
            Map<String, Object> response = new HashMap<>();
            response.put("reading", reading);
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