import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

public class CustomerDAOTest {

    private Connection connection;
    private CustomerDAO customerDAO;

    @BeforeEach
    public void setUp() throws SQLException {
        // Verbindung zur MariaDB-Datenbank herstellen
        connection = DriverManager.getConnection(
                "jdbc:mariadb://localhost:3306/Hausverwaltung", // Ihre Testdatenbank
                "root",  // Ihr Testbenutzer
                "Meerschweinchen20+");  // Passwort für den Testbenutzer
        customerDAO = new CustomerDAO(connection);

        // Testtabelle vorbereiten (falls erforderlich)
       /* String createTableSQL = "CREATE TABLE IF NOT EXISTS customers (id CHAR(36) PRIMARY KEY, " +
                "first_name VARCHAR(255), " +
                "last_name VARCHAR(255), " +
                "gender VARCHAR(10)";
        Statement statement = connection.createStatement();
        statement.execute(createTableSQL);
        statement.close();

        */
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Testdaten löschen, damit die Tests isoliert bleiben
        connection.createStatement().execute("DELETE FROM customer");
        connection.close();
    }

    @Test
    public void testAddCustomer() throws SQLException {
        // Arrange
        ICustomer customer = new Customer("John", "Doe", LocalDate.of(2020, Month.JANUARY, 8), ICustomer.Gender.M);
        // Act
        customerDAO.addCustomer(customer);


        // Assert
        ICustomer retrievedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getFirstName());
        assertEquals("Doe", retrievedCustomer.getLastName());
    }
/*
    @Test
    public void testUpdateCustomer() throws SQLException {
        // Arrange
        ICustomer customer = new Customer("Jane", "Doe",LocalDate.parse("2020-1-08") ,ICustomer.Gender.W);
        customerDAO.addCustomer(customer);

        // Act - Aktualisiere den Namen
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
        ICustomer customer = new Customer("Mark", "Smith",LocalDate.parse("2020-1-08") ,ICustomer.Gender.M);
        customerDAO.addCustomer(customer);

        // Act
        customerDAO.deleteCustomer(customer.getId());

        // Assert
        ICustomer deletedCustomer = customerDAO.getCustomerById(customer.getId());
        assertNull(deletedCustomer);  // Prüfen, ob der Kunde gelöscht wurde
    }
*/
}