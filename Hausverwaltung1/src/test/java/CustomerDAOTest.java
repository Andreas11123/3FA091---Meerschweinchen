import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class CustomerDAOTest {
    private static Connection connection;
    private static CustomerDAO customerDAO;
    private static ReadingDAO readingDAO;

    @BeforeAll
    public static void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/Hausverwaltung",
                "root",
                "Meerschweinchen20+");

        customerDAO = new CustomerDAO(connection);
        readingDAO = new ReadingDAO(connection);

        // Tabellen erstellen, falls sie nicht existieren
        String tableName = "customer";
        String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableQuery)) {
            boolean tableExists = rs.next() && rs.getInt(1) > 0;
            if (!tableExists) {
                customerDAO.createCustomerTable();
                readingDAO.createReadingTable();
            }
        }
    }

    @Test
    public void testDeleteCustomer() throws SQLException {
        // Arrange - Kunde erstellen und hinzufügen
        ICustomer customer = new Customer("Mark", "Smith", LocalDate.of(2020, 1, 8), ICustomer.Gender.M);
        customerDAO.addCustomer(customer);

        // Ablesung für den Kunden erstellen und hinzufügen
        IReading reading = new Reading(
                UUID.randomUUID(),
                "Testablesung",
                customer,
                LocalDate.now(),
                IReading.KindOfMeter.STROM,
                100.0,
                "TestMeter",
                false
        );
        readingDAO.addReading(reading);

        // Act - Kunde löschen
        customerDAO.deleteCustomer(customer.getId());

        // Assert
        // Prüfen, ob der Kunde wirklich gelöscht wurde
        ICustomer deletedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNull(deletedCustomer, "Der Kunde sollte gelöscht sein");

        // Prüfen, ob die Ablesung noch existiert aber keine Kundenreferenz mehr hat
        IReading retrievedReading = readingDAO.getReadingById(reading.getId());
        assertNotNull(retrievedReading, "Die Ablesung sollte noch existieren");
        assertNull(retrievedReading.getCustomer(), "Die Kundenreferenz sollte null sein");
    }


    @AfterEach
    public void cleanupDatabase() throws SQLException {
        // Testdaten nach jedem Test löschen
        connection.createStatement().execute("DELETE FROM Reading");
        connection.createStatement().execute("DELETE FROM Customer");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        connection.close();
    }
}