import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CustomerDAO {

    Connection connection;

    public CustomerDAO(Connection connection) { this.connection = connection; }


    // CREATE
    public void addCustomer(ICustomer customer) throws SQLException {
        String query = "INSERT INTO customer (firstname,lastname,birthdate, gender, id) VALUES (?, ?, ?, ?, ?)";
        //try (Connection con = DataConnection.getConnection();
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
        //try (Connection con = DataConnection.getConnection();
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customerId.toString());
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return new Customer();
        }
        return null; // Customer nicht gefunden
    }


    // READ (get all customers)
    public List<ICustomer> getAllCustomers() throws SQLException {
        String query = "SELECT * FROM customer";
        List<ICustomer> customers = new ArrayList<>();
        //try (Connection con = DataConnection.getConnection();
        PreparedStatement pst = connection.prepareStatement(query);
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ICustomer customer = new Customer();
                customers.add(customer);
            }
        }
        return customers;
    }

    // UPDATE
    public void updateCustomer(ICustomer customer) throws SQLException {
        String query = "UPDATE customer SET name = ?, gender = ? WHERE id = ?";
        //try (Connection con = DataConnection.getConnection();
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customer.getFirstName());
        pst.setString(1, customer.getLastName());
        pst.setString(2, customer.getGender().toString());
        pst.setString(3, customer.getId().toString());
        pst.executeUpdate();
    }

    // DELETE
    public void deleteCustomer(UUID customerId) throws SQLException {
        String query = "DELETE FROM customer WHERE id = ?";
        //try (Connection con = DataConnection.getConnection();
        PreparedStatement pst = connection.prepareStatement(query);
        pst.setString(1, customerId.toString());
        pst.executeUpdate();
    }


}





