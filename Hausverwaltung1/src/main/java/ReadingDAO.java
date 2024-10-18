import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        String checkCustomerQuery = "SELECT COUNT(*) FROM Kunde WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
            checkStmt.setObject(1, reading.getCustomer().getId());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Füge den Kunden ein, falls er nicht existiert
                String addCustomerQuery = "INSERT INTO Kunde (id, firstName, lastName, birthDate, gender) VALUES (?, 'Unbekannt', 'Unbekannt', NOW(), 'U')";
                try (PreparedStatement addStmt = connection.prepareStatement(addCustomerQuery)) {
                    addStmt.setObject(1, reading.getCustomer().getId());
                    addStmt.executeUpdate();
                }
            }
        }

        // Füge die Ablesung ein
        String query = "INSERT INTO Ablesung (meterId, customerId, dateOfReading, kindOfMeter, meterCount, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, reading.getMeterId());
            pst.setObject(2, reading.getCustomer().getId());
            pst.setDate(3, Date.valueOf(reading.getDateOfReading()));
            pst.setString(4, reading.getKindOfMeter().name());
            pst.setDouble(5, reading.getMeterCount());
            pst.setBoolean(6, reading.getSubstitute());
            pst.setString(7, reading.getComment());
            pst.executeUpdate();
        }
    }

    // READ (get reading by meterId)
    public IReading getReadingById(UUID meterId) throws SQLException {
        String query = "SELECT * FROM Ablesung WHERE meterId = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, meterId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                IReading reading = new Reading(
                        (UUID) rs.getObject("meterId"),
                        rs.getString("comment"),
                        null, // Den Kunden später laden, falls nötig
                        rs.getDate("dateOfReading").toLocalDate(),
                        IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")),
                        rs.getDouble("meterCount"),
                        rs.getString("meterId"),
                        rs.getBoolean("substitute")
                );
                return reading;
            }
        }
        return null;
    }

    // READ (get all readings)
    public List<IReading> getAllReadings() throws SQLException {
        String query = "SELECT * FROM Ablesung";
        List<IReading> readings = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(query);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                IReading reading = new Reading(
                        (UUID) rs.getObject("meterId"),
                        rs.getString("comment"),
                        null, // Den Kunden später laden, falls nötig
                        rs.getDate("dateOfReading").toLocalDate(),
                        IReading.KindOfMeter.valueOf(rs.getString("kindOfMeter")),
                        rs.getDouble("meterCount"),
                        rs.getString("meterId"),
                        rs.getBoolean("substitute")
                );
                readings.add(reading);
            }
        }
        return readings;
    }

    // UPDATE
    public void updateReading(IReading reading) throws SQLException {
        String query = "UPDATE Ablesung SET customerId = ?, dateOfReading = ?, kindOfMeter = ?, meterCount = ?, substitute = ?, comment = ? WHERE meterId = ?";
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
        String query = "DELETE FROM Ablesung WHERE meterId = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setObject(1, meterId);
            pst.executeUpdate();
        }
    }
}

