import DataConnection.Util;
import ReadingAndCustomer.*;
import dev.bsinfo.ressource.ReadingResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReadingResourceTest {

    private ReadingResource readingResource;
    private UUID testReadingId;
    private UUID testCustomerId;

    @BeforeAll
    void setup() {
        Util.getConnection("Hausverwaltung"); // Echte DB-Verbindung
        readingResource = new ReadingResource();
    }

    @Test
    @Order(1)
    void testCreateReading() {
        Map<String, Reading> request = new HashMap<>();
        Reading reading = new Reading();
        Customer customer = new Customer("Max", "Mustermann", LocalDate.of(1990, 5, 15), Customer.Gender.M);
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }
        customer.setId(UUID.randomUUID());
        reading.setCustomer(customer);
        if (reading.getId() == null) {
            reading.setId(UUID.randomUUID());
        }
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.HEIZUNG);
        reading.setMeterCount(123.45);
        reading.setSubstitute(false);
        request.put("reading", reading);

        Response response = readingResource.createReading(request);
        System.out.println(response.getEntity());
        System.out.println(response.getStatus());
        assertEquals(201, response.getStatus());

        Map<String, Object> responseBody = (Map<String, Object>) response.getEntity();
        assertNotNull(responseBody.get("reading"));
        testReadingId = ((Reading) responseBody.get("reading")).getId();
        testCustomerId = ((Reading) responseBody.get("reading")).getCustomer().getId();
    }

    @Test
    @Order(2)
    void testGetAllReadings() {
        Response response = readingResource.getReadings(null, null, null, null);
        assertEquals(200, response.getStatus());
    }

//    @Test
//    @Order(3)
//    void testGetReadingById() {
//        Response response = readingResource.getReading(testReadingId.toString());
//        assertEquals(200, response.getStatus());
//    }
//
//    @Test
//    @Order(4)
//    void testUpdateReading() {
//        Map<String, Reading> request = new HashMap<>();
//        Reading reading = new Reading();
//        reading.setId(testReadingId);
//        reading.setCustomer(new Customer("Max", "Mustermann", LocalDate.of(1990, 5, 15), Customer.Gender.M));
//        reading.setDateOfReading(LocalDate.now().minusDays(1));
//        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
//        reading.setMeterCount(200.00);
//        request.put("reading", reading);
//
//        Response response = readingResource.updateReading(request);
//        assertEquals(200, response.getStatus());
//        assertEquals("Reading updated successfully", response.getEntity());
//    }
//
//    @Test
//    @Order(5)
//    void testDeleteReading() {
//        Response response = readingResource.deleteReading(testReadingId.toString());
//        assertEquals(200, response.getStatus());
//    }
}