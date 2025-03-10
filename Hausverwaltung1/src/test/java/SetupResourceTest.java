import DataConnection.Util;
import ReadingAndCustomer.CustomerDAO;
import ReadingAndCustomer.ReadingDAO;
import dev.bsinfo.ressource.SetupResource;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SetupResourceTest {

    private SetupResource setupResource;
    private CustomerDAO customerDAO;
    private ReadingDAO readingDAO;
    private Connection connection;

    @BeforeAll
    void setup() throws Exception {
        // In-Memory-Datenbankverbindung herstellen (oder echte DB-Verbindung je nach Bedarf)
        connection = Util.getConnection("Hausverwaltung"); // Falls du eine echte DB verwendest, stelle sicher, dass der Connection-String korrekt ist

        // DAO-Objekte initialisieren
        customerDAO = new CustomerDAO(connection);
        readingDAO = new ReadingDAO(connection);

        // SetupResource-Instanz mit den DAOs
        setupResource = new SetupResource();
        // Oder falls du die DAOs spezifisch injizieren musst:
        // setupResource.setCustomerDAO(customerDAO);
        // setupResource.setReadingDAO(readingDAO);

        // Sicherstellen, dass alle alten Tabellen vor dem Test gelöscht werden
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS reading");
            stmt.execute("DROP TABLE IF EXISTS customer");
        }
    }

    @test
    @Order(1)
    void testSetupDatabase_Success() {
        // Test, ob die setupDatabase-Methode erfolgreich funktioniert und Tabellen erstellt
        Response response = setupResource.setupDatabase();
        assertEquals(200, response.getStatus());

        // Überprüfe, ob die Antwort die richtige Nachricht enthält
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertEquals("Database setup completed successfully", responseBody.get("message"));

        // Optional: Überprüfen, ob die Tabellen wirklich erstellt wurden
        try (Statement stmt = connection.createStatement()) {
            java.sql.ResultSet result = stmt.executeQuery("SHOW TABLES LIKE 'customer'");
            assertTrue(result.next(), "Customer table should be created");
            result = stmt.executeQuery("SHOW TABLES LIKE 'reading'");
            assertTrue(result.next(), "Reading table should be created");
        } catch (Exception e) {
            fail("Error verifying table creation: " + e.getMessage());
        }
    }

    @test
    @Order(2)
    void testSetupDatabase_Failure() throws Exception {
        // Fehlerfall, falls eine Exception geworfen wird
        // Zum Beispiel die Verbindung könnte geschlossen werden, um zu sehen, was passiert
        connection.close(); // Schließt die Verbindung für den Fehlerfall

        // Aufrufen der setupDatabase-Methode, um zu überprüfen, ob sie einen Fehler zurückgibt
        Response response = setupResource.setupDatabase();
        assertEquals(500, response.getStatus());

        // Überprüfen, ob die Fehlermeldung im Response stimmt
        Map<String, String> responseBody = (Map<String, String>) response.getEntity();
        assertTrue(responseBody.containsKey("error"));
        assertTrue(responseBody.get("error").contains("Error setting up database"));
    }

    @AfterAll
    void tearDown() throws Exception {
        // Hier kannst du sicherstellen, dass die Verbindung nach den Tests geschlossen wird
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
