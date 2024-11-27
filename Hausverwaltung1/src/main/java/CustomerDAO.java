import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO {

    /*public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection("jdbc:your_database_url", "username", "password")) {
            CustomerDAO customerDAO = new CustomerDAO(connection);

            // Tabelle erstellen
            customerDAO.createCustomerTable();

            // Beispiel-Customer hinzufügen
            ICustomer customer = new Customer(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 1), Gender.MALE);
            customerDAO.addCustomer(customer);

            // Alle Customers ausgeben
            List<ICustomer> customers = customerDAO.getAllCustomers();
            for (ICustomer c : customers) {
                System.out.println(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public void addCustomer(ICustomer customer) throws SQLException {
        String query = "INSERT INTO customer (firstname, lastname, birthdate, gender, id) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setDate(3, Date.valueOf(customer.getBirthDate()));
        pst.setString(1, customer.getFirstName());
        pst.setString(2, customer.getLastName());
        pst.setString(4, customer.getGender().toString());
        pst.setString(5, customer.getId().toString());
        pst.executeUpdate();
    }

    // READ (get customer by ID)
    public ICustomer getCustomerById(UUID customerId) throws SQLException {
        String query = "SELECT * FROM customer WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customerId.toString());
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return new Customer(); // Hier müsstest du die Customer-Attribute befüllen
        }
        return null; // Customer nicht gefunden
    }

    // READ (get all customers)
    public List<ICustomer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM customer";
        List<ICustomer> customers = new ArrayList<>();
        PreparedStatement pst = connection.prepareStatement(query);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ICustomer customer = new Customer(); // Hier müsstest du die Customer-Attribute befüllen
                customers.add(customer);
            }
        }
        return customers;
    }

    // UPDATE
    public void updateCustomer(ICustomer customer) throws SQLException {
        String query = "UPDATE customer SET firstname = ?, lastname = ?, gender = ? WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customer.getFirstName());
        pst.setString(2, customer.getLastName());
        pst.setString(3, customer.getGender().toString());
        pst.setString(4, customer.getId().toString());
        pst.executeUpdate();
    }

    // DELETE
    public void deleteCustomer(UUID customerId) throws SQLException {
        String query = "DELETE FROM customer WHERE id = ?";
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customerId.toString());
        pst.executeUpdate();
    }

    // CREATE TABLE
    public void createCustomerTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS customer (id UUID PRIMARY KEY, firstname VARCHAR(50) NOT NULL, " +
                "lastname VARCHAR(50) NOT NULL, birthdate DATE NOT NULL, gender VARCHAR(10) NOT NULL, email VARCHAR(100) UNIQUE, " +
                "phone VARCHAR(15), address VARCHAR(255) , created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        Statement stmt = connection.createStatement();
        stmt.execute(query);
    }
}
