import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO {

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
            Customer customer = new Customer();
            customer.setFirstName(rs.getString("firstname"));
            customer.setId(UUID.fromString(rs.getString("id")));
            customer.setLastName(rs.getString("lastname"));
            customer.setBirthDate(LocalDate.parse(rs.getString("birthdate")));
            customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
            return  customer;
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
        connection.setAutoCommit(false);
        try {
            // Zuerst: Update der Kundenreferenz in den Ablesungen auf null
            String updateReadingsQuery = "UPDATE Reading SET customer_id = NULL WHERE customer_id = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateReadingsQuery)) {
                updateStmt.setString(1, customerId.toString());
                updateStmt.executeUpdate();
            }

            // Danach: Löschen des Kunden
            String deleteCustomerQuery = "DELETE FROM customer WHERE id = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteCustomerQuery)) {
                deleteStmt.setString(1, customerId.toString());
                deleteStmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            System.err.println("Fehler beim Löschen des Kunden: " + e.getMessage());
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // CREATE TABLE
    public void createCustomerTable() throws SQLException {
        String query = "CREATE TABLE customer (id UUID PRIMARY KEY, firstname VARCHAR(50) NOT NULL, " +
                "lastname VARCHAR(50) NOT NULL, birthdate DATE NOT NULL, gender VARCHAR(10) NOT NULL, email VARCHAR(100) UNIQUE, " +
                "phone VARCHAR(15), address VARCHAR(255) , created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        Statement stmt = connection.createStatement();
        stmt.execute(query);
    }
}
