import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestConnection {

    private Connection connection;

    @BeforeEach
    public void setUp() throws SQLException {
        // JDBC-URL für MySQL
        String url = "jdbc:mariadb://localhost:3306/Hausverwaltung";
        String username = "root"; // Dein MySQL-Benutzername
        String password = "Meerschweinchen20+"; // Dein Passwort

        // Verbindung zur Datenbank herstellen
        connection = DriverManager.getConnection(url, username, password);
    }

    @Test
    public void testConnection() {
        // Überprüfen, ob die Verbindung nicht null ist
        assertNotNull(connection, "Connection should not be null");
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Verbindung nach dem Test schließen
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
