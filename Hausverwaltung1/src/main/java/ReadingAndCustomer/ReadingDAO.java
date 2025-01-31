package ReadingAndCustomer;

import java.sql.*;
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
        String query = "INSERT INTO Reading (id, customer_Id, date_of_reading, meter_count, kind_of_meter, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, reading.getMeterId());
            pst.setObject(2, reading.getCustomer().getId());
            pst.setDate(3, Date.valueOf(reading.getDateOfReading()));
            pst.setDouble(4, reading.getMeterCount());
            pst.setString(5, reading.getKindOfMeter().name());
            pst.setBoolean(6, reading.getSubstitute());
            pst.setString(7, reading.getComment());
            pst.executeUpdate();
        }
    }

    // READ (get reading by meterId)
    public IReading getReadingById(UUID meterId) throws SQLException {
        String query = "SELECT * FROM Reading WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, meterId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Reading reading = new Reading();
                reading.setId(UUID.fromString(rs.getString("id")));
                reading.setComment(rs.getString("comment"));
                reading.setDateOfReading(rs.getDate("date_of_reading").toLocalDate());
                //IReading.KindOfMeter.valueOf(rs.getString("kind_of_meter"));
                reading.setMeterCount(rs.getDouble("meter_count"));
                reading.setMeterId(rs.getString("meter_id"));
                reading.setSubstitute(rs.getBoolean("substitute"));

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
                IReading reading = new Reading(
                        (UUID) rs.getObject("Id"),
                        rs.getString("comment"),
                        null, // Den Kunden später laden, falls nötig
                        rs.getDate("date_of_reading").toLocalDate(),
                        IReading.KindOfMeter.valueOf(rs.getString("kind_of_meter")),
                        rs.getDouble("meter_count"),
                        rs.getString("meter_id"),
                        rs.getBoolean("substitute")
                );
                readings.add(reading);
            }
        }
        return readings;
    }

    // UPDATE
    public void updateReading(IReading reading) throws SQLException {
        String query = "UPDATE Reading SET  customer_id = ?, date_of_reading = ?, kind_of_meter = ?, meter_count = ?, substitute = ?, comment = ? WHERE meter_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, reading.getCustomer().getId());
            pst.setDate(2, Date.valueOf(reading.getDateOfReading()));
            pst.setString(3, reading.getKindOfMeter().name());
            pst.setDouble(4, reading.getMeterCount());
            pst.setBoolean(5, reading.getSubstitute());
            pst.setString(6, reading.getComment());
            pst.setObject(7, reading.getMeterId());
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
                "    meter_id VARCHAR(32),\n" +
                "    substitute BOOLEAN,\n" +
                "    FOREIGN KEY (customer_id) REFERENCES Customer(id) ON DELETE SET NULL)";
        Statement stmt = connection.createStatement();
        stmt.execute(query);
    }
}

