import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class ReadingDAOTest {
    private static Connection connection;
    private static ReadingDAO readingDAO;
    private static CustomerDAO customerDAO;

    @BeforeAll
    public static void setUp() throws SQLException {
        // Verbindung zur MariaDB-Datenbank herstellen
        connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/Hausverwaltung",
                "root",
                "Meerschweinchen20+");

        customerDAO = new CustomerDAO(connection);
        readingDAO = new ReadingDAO(connection);

        // Check if the table exists
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

    @AfterAll
    static void closeDatabase() throws SQLException {
        connection.close();
    }

    @Test
    void testAddAndReadReading() throws SQLException {
        UUID meterId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // Customer hinzufügen
        /*
        connection.createStatement().execute(
                "INSERT INTO Customer (id, firstName, lastName, birthDate, gender) VALUES (" +
                        "'" + customerId + "', 'Jane', 'Doe', '1990-01-01', " + ICustomer.Gender.W);

         */

        // Reading erstellen
        IReading reading = new Reading(
                meterId,
                "Test comment",
                new Customer("Jane", "Doe", LocalDate.of(1990, 1, 1), ICustomer.Gender.W),
                LocalDate.of(2020, 1, 1),
                IReading.KindOfMeter.HEIZUNG,
                123.45,
                meterId.toString(),
                false
        );
        System.out.println(reading);

        // Add Reading
        readingDAO.addReading(reading);

        // Read Reading
        IReading retrievedReading = readingDAO.getReadingById(reading.getId());

        assertNotNull(retrievedReading);
        assertEquals(reading.getId(), retrievedReading.getId());
    }

    @AfterEach
    void cleanupDatabase() throws SQLException {
        // Lösche Testdaten nach jedem Test
        connection.createStatement().execute("DELETE FROM Reading");
        connection.createStatement().execute("DELETE FROM Customer");
    }

    @Test
    void testUpdateReading() throws SQLException {
        UUID meterId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // Customer hinzufügen
        connection.createStatement().execute(
                "INSERT INTO Customer (id, firstName, lastName, birthDate, gender) VALUES (" +
                        "'" + customerId + "', 'Mark', 'Smith', '1980-05-15', 'M')"
        );


        // Reading erstellen
        IReading reading = new Reading(
                meterId,
                "Initial comment",
                new Customer("Mark", "Smith", LocalDate.of(1980, 5, 15), ICustomer.Gender.M),
                LocalDate.now(),
                IReading.KindOfMeter.STROM,
                200.0,
                meterId.toString(),
                true
        );

        // Add Reading
        readingDAO.addReading(reading);

        // Update Reading
        reading.setComment("Updated comment");
        reading.setMeterCount(250.0);
        readingDAO.updateReading(reading);

        // Verify Update
        IReading updatedReading = readingDAO.getReadingById(meterId);
        assertNotNull(updatedReading);
        assertEquals(200.0, updatedReading.getMeterCount());
    }

    @Test
    void testDeleteReading() throws SQLException {
        UUID meterId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        // Customer hinzufügen
        connection.createStatement().execute(
                "INSERT INTO Customer (id, firstName, lastName, birthDate, gender) VALUES (" +
                        "'" + customerId + "', 'Anna', 'Brown', '1995-10-10', 'F')"
        );

        // Reading erstellen
        IReading reading = new Reading(
                meterId,
                "To delete",
                new Customer("Anna", "Brown", LocalDate.of(1995, 10, 10), ICustomer.Gender.W),
                LocalDate.now(),
                IReading.KindOfMeter.WASSER,
                50.0,
                meterId.toString(),
                false
        );

        // Add Reading
        readingDAO.addReading(reading);

        // Delete Reading
        readingDAO.deleteReading(meterId);

        // Verify Deletion
        IReading deletedReading = readingDAO.getReadingById(meterId);
        assertNull(deletedReading);
    }
}
