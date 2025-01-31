package dev.bsinfo.ressource;

import DataConnection.Util;
import ReadingAndCustomer.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
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

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReading(Map<String, Reading> requestMap) {
        try {
            if (requestMap == null || !requestMap.containsKey("reading")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid request format. Expected: {\"reading\": {...}}");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            Reading reading = requestMap.get("reading");
            if (reading == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "No reading data provided");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(error)
                        .build();
            }

            if (reading.getId() == null) {
                reading.setId(UUID.randomUUID());
            }

            if (reading.getCustomer() != null) {
                ICustomer customer = reading.getCustomer();
                if (customer.getId() == null) {
                    customer.setId(UUID.randomUUID());
                }
                ICustomer existingCustomer = customerDAO.getCustomerById(customer.getId());
                if (existingCustomer == null) {
                    customerDAO.addCustomer(customer);
                }
            }

            readingDAO.addReading(reading);
            Map<String, Object> response = new HashMap<>();
            response.put("reading", reading);
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

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Response updateReading(Map<String, Reading> requestMap) {
        try {
            if (requestMap == null || !requestMap.containsKey("reading")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid request format. Expected: {\"reading\": {...}}")
                        .build();
            }

            Reading reading = requestMap.get("reading");
            if (reading == null || reading.getId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid reading data")
                        .build();
            }

            IReading existingReading = readingDAO.getReadingById(reading.getId());
            if (existingReading == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Reading not found")
                        .build();
            }

            readingDAO.updateReading(reading);
            return Response.ok("Reading updated successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Error: " + e.getMessage())
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
            // Parse parameters
            final LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            final LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;
            final IReading.KindOfMeter meterType = kindOfMeter != null ?
                    IReading.KindOfMeter.valueOf(kindOfMeter.toUpperCase()) : null;
            final UUID custId = customerId != null ? UUID.fromString(customerId) : null;

            // Get base list of readings
            List<IReading> readings = new ArrayList<>(readingDAO.getAllReadings());

            // Apply filters
            if (custId != null) {
                readings = readings.stream()
                        .filter(r -> r.getCustomer() != null &&
                                r.getCustomer().getId().equals(custId))
                        .collect(Collectors.toList());
            }

            if (start != null) {
                readings = readings.stream()
                        .filter(r -> !r.getDateOfReading().isBefore(start))
                        .collect(Collectors.toList());
            }

            if (end != null) {
                readings = readings.stream()
                        .filter(r -> !r.getDateOfReading().isAfter(end))
                        .collect(Collectors.toList());
            }

            if (meterType != null) {
                readings = readings.stream()
                        .filter(r -> r.getKindOfMeter() == meterType)
                        .collect(Collectors.toList());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("readings", readings);
            return Response.ok(response).build();

        } catch (DateTimeParseException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid date format. Use yyyy-MM-dd");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid parameter format");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
                    .build();
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error)
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