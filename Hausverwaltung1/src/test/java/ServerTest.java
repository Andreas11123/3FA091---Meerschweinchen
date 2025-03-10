import DataConnection.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    private final String serverUrl = "https://localhost:8080/test";

    @BeforeEach
    public void setUp() {
        // Vor jedem Test starten wir den Server
        Server.startServer(serverUrl);
    }


    @Test
    public void testServerStart() {
        // Hier kannst du testen, ob der Server erfolgreich gestartet wurde
        // Ein einfacher Check, um sicherzustellen, dass der Server nicht null ist und gestartet wurde
        assertNotNull(Server.getServer(), "Server sollte nicht null sein, wenn er erfolgreich gestartet wurde.");
    }

    @Test
    public void testServerAlreadyRunning() {
        // Testen, dass der Server nicht doppelt gestartet werden kann
        Server.startServer(serverUrl);
        // Server sollte bereits laufen, also sollte keine neue Instanz erstellt werden
        assertEquals(1, Server.getServerCount(), "Es sollte nur ein Server laufen.");
    }

    @AfterEach
    public void tearDown() {
        // Nach jedem Test stoppen wir den Server
        Server.stopServer();
        assertNull(Server.getServer(), "Der Server sollte null sein, wenn er gestoppt wurde.");
    }

}
