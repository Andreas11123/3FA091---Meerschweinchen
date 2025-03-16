package ReadingAndCustomer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ReadingDAO {
    Connection connection;

    // Konstruktor
    public ReadingDAO(Connection connection) {
        this.connection = connection;
    }

    // CREATE
    public void addReading(IReading reading) throws SQLException {
        String checkCustomerQuery = "SELECT COUNT(*) FROM Customer WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
            checkStmt.setObject(1, reading.getCustomer().getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Füge den Kunden ein, falls er nicht existiert
                String addCustomerQuery = "INSERT INTO Customer (id, firstName, lastName, birthDate, gender) VALUES (?, 'Unbekannt', 'Unbekannt', NOW(), 'U')";
                try (PreparedStatement addStmt = connection.prepareStatement(addCustomerQuery)) {
                    addStmt.setObject(1, reading.getCustomer().getId());
                    addStmt.executeUpdate();
                }
            }
        }

        // Füge die Ablesung ein
        String query = "INSERT INTO Reading (id, customer_Id, date_of_reading, meter_count, kind_of_meter, substitute, comment, meter_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, reading.getId());
            pst.setObject(2, reading.getCustomer().getId());
            pst.setDate(3, Date.valueOf(reading.getDateOfReading()));
            pst.setDouble(4, reading.getMeterCount());
            pst.setString(5, reading.getKindOfMeter().name());
            pst.setBoolean(6, reading.getSubstitute());
            pst.setString(7, reading.getComment());
            pst.setString(8, reading.getMeterId());
            pst.executeUpdate();
        }
    }

    // READ (get reading by meterId)
    public IReading getReadingById(UUID meterId) throws SQLException {
        String query = "SELECT r.*, ct.* \n" +
                "                FROM Reading r \n" +
                "                LEFT JOIN Customer ct ON r.customer_id = ct.id \n" +
                "                WHERE r.id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, meterId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reading reading = new Reading();
                reading.setId(UUID.fromString(rs.getString("id")));
                reading.setComment(rs.getString("comment"));
                reading.setDateOfReading(rs.getDate("date_of_reading").toLocalDate());
                reading.setKindOfMeter(IReading.KindOfMeter.valueOf(rs.getString("kind_of_meter")));
                reading.setMeterCount(rs.getDouble("meter_count"));
                reading.setMeterId(rs.getString("meter_id"));
                reading.setSubstitute(rs.getBoolean("substitute"));
                Customer customer = new Customer();
                customer.setFirstname(rs.getString("firstname"));
                customer.setId(UUID.fromString(rs.getString("customer_id")));
                customer.setLastname(rs.getString("lastname"));
                customer.setBirthdate(LocalDate.parse(rs.getString("birthdate")));
                customer.setGender(ICustomer.Gender.valueOf(rs.getString("gender")));
                reading.setCustomer(customer);
                return reading;
            }
        }
        return null;
    }

    // READ (get all readings)
    public List<IReading> getAllReadings() throws SQLException {
        String query = "SELECT * FROM Reading";
        List<IReading> readings = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
            }
        }
        return readings;
    }

    // UPDATE
    public void updateReading(IReading reading) throws SQLException {
        String query = "UPDATE Reading SET  customer_id = ?, date_of_reading = ?, kind_of_meter = ?, meter_count = ?, meter_id = ?, substitute = ?, comment = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, reading.getCustomer().getId());
            pst.setDate(2, Date.valueOf(reading.getDateOfReading()));
            pst.setString(3, reading.getKindOfMeter().name());
            pst.setDouble(4, reading.getMeterCount());
            pst.setObject(5, reading.getMeterId());
            pst.setBoolean(6, reading.getSubstitute());
            pst.setString(7, reading.getComment());
            pst.setString(8, reading.getId().toString());
            pst.executeUpdate();
        }
    }

    // DELETE
    public void deleteReading(UUID meterId) throws SQLException {
        String query = "DELETE FROM Reading WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, meterId);
            pst.executeUpdate();
        }
    }

    public void createReadingTable() throws SQLException {
        String query = "CREATE TABLE Reading (\n" +
                "    id UUID PRIMARY KEY,\n" +
                "    comment VARCHAR(32),\n" +
                "    customer_id UUID,\n" +
                "    date_of_reading DATE,\n" +
                "    kind_of_meter VARCHAR(32),\n" +
                "    meter_count DOUBLE PRECISION,\n" +
                "    meter_id VARCHAR(64),\n" +
                "    substitute BOOLEAN,\n" +
                "    FOREIGN KEY (customer_id) REFERENCES Customer(id) ON DELETE SET NULL)";
        Statement stmt = connection.createStatement();
        stmt.execute(query);
    }

    public void dropReadingTable() throws SQLException {
        String query = "DROP TABLE IF EXISTS reading";
        Statement stmt = connection.createStatement();
        stmt.execute(query);
    }

    public List<IReading> filterReadings(UUID customerId, LocalDate startDate,
                                         LocalDate endDate, IReading.KindOfMeter meterType) {
        String query = "SELECT r.*, c.* FROM Reading r LEFT JOIN Customer c ON r.customer_id = c.id WHERE 1=1";

        if (customerId != null) query += " AND r.customer_id = ? ";
        if (startDate != null) query += " AND r.date_of_reading >= ? ";
        if (endDate != null) query += " AND r.date_of_reading <= ? ";
        if (meterType != null) query += " AND r.kind_of_meter = ? ";

        try (PreparedStatement pst = connection.prepareStatement(query)) {
            int paramIndex = 1;
            if (customerId != null) pst.setObject(paramIndex++, customerId);
            if (startDate != null) pst.setObject(paramIndex++, startDate);
            if (endDate != null) pst.setObject(paramIndex++, endDate);
            if (meterType != null) pst.setString(paramIndex++, meterType.toString());

            try (ResultSet rs = pst.executeQuery()) {
                List<IReading> readings = new ArrayList<>();
                while (rs.next()) {
                    readings.add(mapToReading(rs));
                }
                return readings;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering readings: " + e.getMessage(), e);
        }
    }

    private IReading mapToReading(ResultSet rs) throws SQLException {
        IReading reading = new Reading();

        // Reading fields
        reading.setId(UUID.fromString(rs.getString("id")));
        reading.setComment(rs.getString("comment"));
        reading.setDateOfReading(rs.getDate("date_of_reading").toLocalDate());
        reading.setKindOfMeter(IReading.KindOfMeter.valueOf(rs.getString("kind_of_meter")));
        reading.setMeterCount(rs.getDouble("meter_count"));
        reading.setMeterId(rs.getString("meter_id"));
        reading.setSubstitute(rs.getBoolean("substitute"));

        // Customer fields (if available)
        if (rs.getObject("c.id") != null) {
            Customer customer = new Customer();
            customer.setId(UUID.fromString(rs.getString("c.id")));
            customer.setFirstname(rs.getString("c.firstname"));
            customer.setLastname(rs.getString("c.lastname"));
            customer.setBirthdate(LocalDate.parse(rs.getString("c.birthdate")));
            customer.setGender(ICustomer.Gender.valueOf(rs.getString("c.gender")));
            reading.setCustomer(customer);
        }

        return reading;
    }
}


