import org.junit.jupiter.api.*;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatebaseTest {

    private Connection connection;
    private ReadingDAO readingDAO;
    private CustomerDAO customerDAO;

    @BeforeAll
    public void setupDatabase() throws SQLException {
        // Verbindung zu MariaDB
        String url = "jdbc:mariadb://localhost:3306/Hausverwaltung";
        String username = "root";
        String password = "Meerschweinchen20+";

        connection = DriverManager.getConnection(url, username, password);

        // DAO-Instanzen erstellen
        readingDAO = new ReadingDAO(connection);
        customerDAO = new CustomerDAO(connection);

        // Tabellen erstellen
        customerDAO.createCustomerTable();
        readingDAO.createReadingTable();
    }

    @BeforeEach
    public void cleanupDatabase() throws SQLException {
        // Tabellen leeren vor jedem Test
        connection.createStatement().execute("DELETE FROM Reading");
        connection.createStatement().execute("DELETE FROM Customer");
    }

    @Test
    public void testNoExceptionsOnInvalidData() throws SQLException {
        ICustomer invalidCustomer = new Customer(null, "", null, null);
        IReading invalidReading = new Reading(UUID.randomUUID(),
                "Invalid reading",
                null,
                LocalDate.now(),
                null,
                0.0,
                "Meter123",
                false);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> customerDAO.addCustomer(invalidCustomer));
        assertThrows(NullPointerException.class, () -> readingDAO.addReading(invalidReading));
    }


    @Test
    public void testDeleteCustomerKeepsReadings() throws SQLException {
        // Kunde und Ablesung hinzufügen
        UUID customerId = UUID.randomUUID();
        UUID meterId = UUID.randomUUID();

        ICustomer customer = new Customer("Max", "Muster", LocalDate.of(1980, 1, 1), ICustomer.Gender.M);
        customerDAO.addCustomer(customer);

        IReading reading = new Reading(meterId, "Erste Ablesung", customer, LocalDate.now(), IReading.KindOfMeter.STROM, 200.0, meterId.toString(), false);
        readingDAO.addReading(reading);

        // Kunden löschen
        customerDAO.deleteCustomer(customerId);

        // Ablesung überprüfen
        IReading retrievedReading = readingDAO.getReadingById(meterId);
        assertNotNull(retrievedReading);
        assertNull(retrievedReading.getCustomer()); // Die Kundenreferenz muss NULL sein
    }

    @Test
    public void testCannotAddReadingWithoutCustomer() {
        // Versuch, eine Ablesung ohne gültigen Kunden hinzuzufügen
        UUID meterId = UUID.randomUUID();

        IReading reading = new Reading(meterId, "Ablesung ohne Kunde", null, LocalDate.now(), IReading.KindOfMeter.STROM, 150.0, meterId.toString(), false);

        // Erwartet, dass eine SQLException oder NullPointerException ausgelöst wird
        try {
            readingDAO.addReading(reading);
            fail("Expected SQLException or NullPointerException but didn't throw");
        } catch (Exception e) {
            if (e instanceof SQLException || e instanceof NullPointerException) {
                // This test passes if an expected exception is thrown
                return;
            }
            // This test fails if any other type of exception is thrown
            fail("Unexpected exception type", e);
        }

        // Additional checks
        reading.getMeterId();
        assertEquals("The date of reading should match", LocalDate.now(), String.valueOf(reading.getDateOfReading()));
    }


    @Test
    public void testAddReadingWithValidCustomer() throws SQLException {
        // Kunde und Ablesung hinzufügen
        UUID customerId = UUID.randomUUID();
        UUID meterId = UUID.randomUUID();

        ICustomer customer = new Customer("Anna", "Mustermann", LocalDate.of(1990, 5, 20), ICustomer.Gender.W);
        customerDAO.addCustomer(customer);

        IReading reading = new Reading(
                meterId,
                "Gültige Ablesung",
                customer,
                LocalDate.now(),
                IReading.KindOfMeter.STROM,
                300.0,
                meterId.toString(),
                false
        );

        try {
            readingDAO.addReading(reading);

            // Ablesung überprüfen
            IReading retrievedReading = readingDAO.getReadingById(meterId);
            assertNotNull("Retrieved reading should not be null", String.valueOf(retrievedReading));



            // Check if the customer ID matches
            assertEquals("The retrieved reading should have the correct customer ID",
                    customerId,
                    String.valueOf(retrievedReading.getCustomer().getId()));

            // Additional checks
            assertNotNull("The meter ID should not be null", retrievedReading.getMeterId());
            assertEquals("The meter ID should match", meterId.toString(), retrievedReading.getMeterId());
            assertEquals("The date of reading should match", LocalDate.now(), String.valueOf(retrievedReading.getDateOfReading()));

        } catch (SQLException e) {
            fail("An unexpected SQLException was thrown: " + e.getMessage());
        } catch (NullPointerException e) {
            fail("An unexpected NullPointerException was thrown: " + e.getMessage());
        }
    }

    @Test
    public void testAddCustomerAndReadingThenDeleteCustomer() throws SQLException {
        // Kunde und Ablesung hinzufügen
        UUID customerId = UUID.randomUUID();
        UUID meterId = UUID.randomUUID();

        ICustomer customer = new Customer("Paul", "Müller", LocalDate.of(1975, 3, 15), ICustomer.Gender.M);
        customerDAO.addCustomer(customer);

        IReading reading = new Reading(meterId, "Ablesung", customer, LocalDate.now(), IReading.KindOfMeter.HEIZUNG, 500.0, meterId.toString(), false);
        readingDAO.addReading(reading);

        // Kunden löschen
        customerDAO.deleteCustomer(customerId);

        // Ablesung überprüfen
        IReading retrievedReading = readingDAO.getReadingById(meterId);
        assertNotNull(retrievedReading);
        assertNull(retrievedReading.getCustomer());
    }

    @AfterAll
    public void tearDown() throws SQLException {
        // Drop the table
        String tableName = "reading";
        String tablename2 = "customer";
        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;
        String dropTableQuery2 = "DROP TABLE IF EXISTS " + tablename2;


        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropTableQuery);
            stmt.execute(dropTableQuery2);
        }
    }
}