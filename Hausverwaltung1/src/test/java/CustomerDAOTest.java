import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import java.util.List;

public class CustomerDAOTest {

    private Connection connection;
    private CustomerDAO customerDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        // Verbindung zur MariaDB-Datenbank herstellen
        connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/Hausverwaltung",
                "root",
                "Meerschweinchen20+");

        customerDAO = new CustomerDAO(connection);

        // Check if the table exists
        String tableName = "customer";
        String checkTableQuery = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkTableQuery)) {

            boolean tableExists = rs.next() && rs.getInt(1) > 0;

            if (!tableExists) {
                customerDAO.createCustomerTable();
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
        // Arrange
        ICustomer customer = new Customer("Mark", "Smith", LocalDate.of(2020, Month.JANUARY, 8), ICustomer.Gender.M);

        // Act - Add customer first
        customerDAO.addCustomer(customer);
        customerDAO.deleteCustomer(customer.getId());

        // Assert
        ICustomer deletedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNull(deletedCustomer);  // Prüfen, ob der Kunde gelöscht wurde
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Drop the table
        String tableName = "customer";
        String dropTableQuery = "DROP TABLE IF EXISTS " + tableName;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropTableQuery);
        }
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


}