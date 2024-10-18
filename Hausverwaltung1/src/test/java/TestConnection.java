import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
/*
public class TestConnection {

    private DataConnection dataConnection;
    private Connection connection;

    @BeforeEach
    public void setUp() {
        // Instanz von DataConnection erstellen
        dataConnection = new DataConnection();
    }

    @Test
    public void testGetConnection() {
        try {
            // Verbindung testen
            connection = dataConnection.getConnection();

            // Sicherstellen, dass die Verbindung nicht null ist
            assertNotNull(connection, "Die Verbindung sollte nicht null sein");

            // Überprüfen, ob die Verbindung geöffnet ist
            assertTrue(!connection.isClosed(), "Die Verbindung sollte geöffnet sein");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void tearDown() throws SQLException {
        // Verbindung nach dem Test schließen
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }

    }


}
*/
