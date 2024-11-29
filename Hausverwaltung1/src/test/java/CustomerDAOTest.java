import static org.junit.jupiter.api.Assertions.*;

import ReadingAndCustomer.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import java.util.List;

public class CustomerDAOTest {

    private static Connection connection;
    private static CustomerDAO customerDAO;
    private static ReadingDAO readingDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        // Verbindung zur MariaDB-Datenbank herstellen
        connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/Hausverwaltung",
                "root",
                "Meerschweinchen20+");

        customerDAO = new CustomerDAO(connection);
        readingDAO = new ReadingDAO(connection);

        // Bestehende Tabellen löschen, um sie neu zu erstellen
        try {
            connection.createStatement().execute("DROP TABLE IF EXISTS Reading");
            connection.createStatement().execute("DROP TABLE IF EXISTS Customer");
        } catch (SQLException e) {
            // Ignorieren, falls Tabellen nicht existieren
        }
        // Tabellen neu erstellen
        customerDAO.createCustomerTable();
        readingDAO.createReadingTable();
        // Optional: Überprüfung, ob die Tabellen erfolgreich erstellt wurden
        try (Statement stmt = connection.createStatement()) {
            ResultSet rsCustomer = stmt.executeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'customer'");
            ResultSet rsReading = stmt.executeQuery(
                    "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'reading'");
            if (!rsCustomer.next() || !rsReading.next()) {
                throw new SQLException("Tabellen konnten nicht erstellt werden");
            }
        }
    }

    @Test
    public void testAddCustomer() throws SQLException {
        // Arrange
        ICustomer customer = new Customer("John", "Doe", LocalDate.of(2020, 1, 8), ICustomer.Gender.M);

        // Act
        customerDAO.addCustomer(customer);

        // Assert
        ICustomer retrievedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNotNull(retrievedCustomer);
        assertEquals(customer.getFirstName(), retrievedCustomer.getFirstName());
        assertEquals(customer.getLastName(), retrievedCustomer.getLastName());
        assertEquals(customer.getBirthDate(), retrievedCustomer.getBirthDate());
        assertEquals(customer.getGender(), retrievedCustomer.getGender());
        assertEquals(customer.getId(), retrievedCustomer.getId());
    }

    @Test
    public void testUpdateCustomer() throws SQLException {
        // Arrange
        ICustomer customer = new Customer("Jane", "Doe", LocalDate.of(2020, Month.JANUARY, 8), ICustomer.Gender.W);

        // Act - Add customer first
        customerDAO.addCustomer(customer);
        customer.setFirstName("Janet");
        customerDAO.updateCustomer(customer);

        // Assert
        ICustomer updatedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNotNull(updatedCustomer);
        assertEquals("Janet", updatedCustomer.getFirstName());
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        // Arrange - Kunde erstellen und hinzufügen
        ICustomer customer = new Customer("Mark", "Smith", LocalDate.of(2020, 1, 8), ICustomer.Gender.M);
        customerDAO.addCustomer(customer);

        UUID readingId = UUID.randomUUID(); // Generiere eine UUID für die Ablesung

        // Ablesung für den Kunden erstellen und hinzufügen
        IReading reading = new Reading(
                readingId,                    // ID für die Ablesung
                "Testablesung",
                customer,
                LocalDate.now(),
                IReading.KindOfMeter.STROM,
                100.0,
                readingId.toString(),         // MeterId als String der UUID
                false
        );
        readingDAO.addReading(reading);
        // Act - Kunde löschen
        customerDAO.deleteCustomer(customer.getId());

        // Assert
        // Prüfen, ob der Kunde wirklich gelöscht wurde
        ICustomer deletedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNull(deletedCustomer);  // Prüfen, ob der Kunde gelöscht wurde
        assertNull(deletedCustomer, "Der Kunde sollte gelöscht sein");
        // Prüfen, ob die Ablesung noch existiert aber keine Kundenreferenz mehr hat
        IReading retrievedReading = readingDAO.getReadingById(readingId);
        assertNotNull(retrievedReading, "Die Ablesung sollte noch existieren");
        assertNull(retrievedReading.getCustomer(), "Die Kundenreferenz sollte null sein");
    }

    @AfterEach
    public void cleanupDatabase() throws SQLException {
        // Testdaten nach jedem Test löschen
        connection.createStatement().execute("DELETE FROM Reading");
        connection.createStatement().execute("DELETE FROM Customer");
    }

    @Test
    public void testGetAllCustomers() throws SQLException {
        // Setup: Add some sample customers
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        ICustomer customer1 = new Customer("John", "Smith", LocalDate.of(1990, 1, 1), ICustomer.Gender.M);
        ICustomer customer2 = new Customer("Jane", "Adam", LocalDate.of(1995, 6, 15), ICustomer.Gender.W);

        customerDAO.addCustomer(customer1);
        customerDAO.addCustomer(customer2);

        // Test: Get all customers
        List<ICustomer> allCustomers = customerDAO.getAllCustomers();

        // Assertions
        assertNotNull("All customers should not be null", String.valueOf(allCustomers));
        assertEquals("Should return exactly two customers " + 2, "Should return exactly two customers " + allCustomers.size());


        // Cleanup: Remove added customers
        customerDAO.deleteCustomer(customer1.getId());
        customerDAO.deleteCustomer(customer2.getId());
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }
}