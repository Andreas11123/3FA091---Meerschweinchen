import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

public class ReadingDAO {
    private final Connection connection;

    public ReadingDAO(Connection connection) {
        this.connection = connection;
    }

    // Add a new reading
    public void addReading(UUID meterId, UUID customerId, LocalDate dateOfReading, KindOfMeter kindOfMeter, double meterCount, boolean substitute, String comment) throws SQLException {
        // Prüfe, ob der Kunde existiert
        String checkCustomerQuery = "SELECT COUNT(*) FROM Kunde WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
            checkStmt.setObject(1, customerId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Füge den Kunden ein, falls er nicht existiert
                CustomerDAO customerDAO = new CustomerDAO(connection);
                customerDAO.addCustomer(customerId, "Unbekannt", "Unbekannt", LocalDate.now(), Gender.U);  // Beispiel für unbekannte Daten
            }
        }

        // Füge die Ablesung ein
        String query = "INSERT INTO Ablesung (meterId, customerId, dateOfReading, kindOfMeter, meterCount, substitute, comment) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, meterId);
            stmt.setObject(2, customerId);
            stmt.setDate(3, Date.valueOf(dateOfReading));
            stmt.setString(4, kindOfMeter.name());
            stmt.setDouble(5, meterCount);
            stmt.setBoolean(6, substitute);
            stmt.setString(7, comment);
            stmt.executeUpdate();
        }
    }

    // Read a reading by meterId
    public IReading getReadingById(UUID meterId) throws SQLException {
        String query = "SELECT * FROM Ablesung WHERE meterId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, meterId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Annahme: IReading ist eine konkrete Klasse Reading
                return new Reading(
                        (UUID) rs.getObject("meterId"),
                        (UUID) rs.getObject("customerId"),
                        rs.getDate("dateOfReading").toLocalDate(),
                        KindOfMeter.valueOf(rs.getString("kindOfMeter")),
                        rs.getDouble("meterCount"),
                        rs.getBoolean("substitute"),
                        rs.getString("comment")
                );
            }
        }
        return null;
    }

    // Update an existing reading
    public void updateReading(UUID meterId, UUID customerId, LocalDate dateOfReading, KindOfMeter kindOfMeter, double meterCount, boolean substitute, String comment) throws SQLException {
        String query = "UPDATE Ablesung SET customerId = ?, dateOfReading = ?, kindOfMeter = ?, meterCount = ?, substitute = ?, comment = ? WHERE meterId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, customerId);
            stmt.setDate(2, Date.valueOf(dateOfReading));
            stmt.setString(3, kindOfMeter.name());
            stmt.setDouble(4, meterCount);
            stmt.setBoolean(5, substitute);
            stmt.setString(6, comment);
            stmt.setObject(7, meterId);
            stmt.executeUpdate();
        }
    }

    // Delete a reading
    public void deleteReading(UUID meterId) throws SQLException {
        String query = "DELETE FROM Ablesung WHERE meterId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, meterId);
            stmt.executeUpdate();
        }
    }
}
