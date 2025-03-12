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
        Customer customer = new Customer("Max", "Mustermann", LocalDate.of(1990, 5, 30), Customer.Gender.M);
        customer.setId(Optional.ofNullable(customer.getId()).orElse(UUID.randomUUID()));


        reading.setCustomer(customer);
        reading.setId(Optional.ofNullable(reading.getId()).orElse(UUID.randomUUID()));
        System.out.println("Reading ID: " + reading.getId());
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.HEIZUNG);
        reading.setMeterCount(123.45);
        reading.setSubstitute(false);
        request.put("reading", reading);

        Response response = readingResource.createReading(reading);
        assertEquals(201, response.getStatus());

        Map<String, Object> responseBody = (Map<String, Object>) response.getEntity();
        assertNotNull(responseBody.get("reading"));
        testReadingId = ((Reading) responseBody.get("reading")).getId();
        testCustomerId = ((Reading) responseBody.get("reading")).getCustomer().getId();

        Reading createdReading = (Reading) responseBody.get("reading");
        assertNotNull(createdReading.getId());  // Sicherstellen, dass die ID zurückgegeben wird
        testReadingId = createdReading.getId();
        testCustomerId = createdReading.getCustomer().getId();

        System.out.println("Created Reading ID: " + testReadingId);
        testReadingId = reading.getId();
    }

    @Test
    @Order(2)
    void testCreateReadingBadRequest() {
        // Test: Fehlendes "reading"-Objekt
        Map<String, Reading> invalidRequest = new HashMap<>();
        Response response = readingResource.createReading(null);
        assertEquals(400, response.getStatus());

        Reading readingWithoutCustomer = new Reading();
        readingWithoutCustomer.setId(UUID.randomUUID());
        readingWithoutCustomer.setDateOfReading(LocalDate.now());
        readingWithoutCustomer.setKindOfMeter(IReading.KindOfMeter.WASSER);
        readingWithoutCustomer.setMeterCount(100.0);

// Jetzt wird ein einzelnes Reading-Objekt übergeben, nicht eine Map
        Response response2 = readingResource.createReading(readingWithoutCustomer);
        assertEquals(400, response2.getStatus());
    }

    @Test
    @Order(3)
    void testGetAllReadings() {
        Response response = readingResource.getReadings(null, null, null, null);
        assertEquals(200, response.getStatus());
    }

    @Test
    @Order(4)
    void testGetReadingById() {
        assertNotNull(testReadingId, "testReadingId should not be null");
        Response response = readingResource.getReading(testReadingId.toString());
        assertEquals(200, response.getStatus());
    }
    @Test
    @Order(5)
    void testGetReadingByIdBadRequest() {
        Response response = readingResource.getReading("invalid-uuid");

        // Akzeptiere sowohl 400 als auch 404, je nachdem, wie die API implementiert ist
        assertTrue(response.getStatus() == 400 || response.getStatus() == 404,
                "Expected 400 or 404, but got " + response.getStatus());
    }

    @Test
    @Order(6)
    void testUpdateReading() {
        Map<String, Reading> request = new HashMap<>();
        Reading reading = new Reading();
        reading.setId(testReadingId);
        reading.setCustomer(new Customer("Max", "Mustermann", LocalDate.of(1990, 5, 15), Customer.Gender.M));
        reading.setDateOfReading(LocalDate.now().minusDays(1));
        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
        reading.setMeterCount(200.00);
        reading.setSubstitute(false);
        request.put("reading", reading);

        assertNotNull(testReadingId, "testReadingId sollte nicht null sein!");
        Response response = readingResource.updateReading(request);
        assertEquals(200, response.getStatus());
        assertEquals("Reading updated successfully", response.getEntity());
//        System.out.println("Response Code: " + response.getStatus());
//        System.out.println("Response Body: " + response.getEntity());
    }
    @Test
    @Order(7)
    void testUpdateReadingBadRequest() {
        // Fehlendes "reading"-Objekt
        Map<String, Reading> invalidRequest = new HashMap<>();
        Response response = readingResource.updateReading(invalidRequest);
        assertEquals(400, response.getStatus());

        // Fehlende ID im Request
        Map<String, Reading> requestWithoutId = new HashMap<>();
        Reading reading = new Reading();
        reading.setCustomer(new Customer("Max", "Mustermann", LocalDate.of(1990, 5, 15), Customer.Gender.M));
        reading.setDateOfReading(LocalDate.now());
        reading.setKindOfMeter(IReading.KindOfMeter.WASSER);
        reading.setMeterCount(200.0);
        requestWithoutId.put("reading", reading);

        Response response2 = readingResource.updateReading(requestWithoutId);
        assertEquals(400, response2.getStatus());
    }

    @Test
    @Order(8)
    void testDeleteReading() {
        Response response = readingResource.deleteReading(testReadingId.toString());
        assertEquals(200, response.getStatus());
    }
    @Test
    @Order(9)
    void testDeleteReadingBadRequest() {
        Response response = readingResource.deleteReading("invalid-uuid");
        assertEquals(400, response.getStatus());
    }
}