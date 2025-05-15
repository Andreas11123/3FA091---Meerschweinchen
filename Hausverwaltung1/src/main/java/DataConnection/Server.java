package DataConnection;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

public class Server {
    private static HttpServer server;

    // Startet den Server mit der angegebenen URL
    public static void startServer(String url) {
        if (server != null) {
            System.out.println("Server ist bereits gestartet.");
            return;
        }
        try {
            // Datenbankverbindung prüfen
            Util.getConnection("Hausverwaltung");

            final String pack = "dev.bsinfo.ressource";
            System.out.println("Start Server...");
            System.out.println("URL: " + url);

            // ResourceConfig mit allen benötigten Ressourcen
            final ResourceConfig rc = new ResourceConfig()
                    .packages(pack)
                    .register(JacksonFeature.class)
                    // Explizit die REST-Ressourcen registrieren
                    .register(dev.bsinfo.ressource.CustomerResource.class)
                    .register(dev.bsinfo.ressource.ReadingResource.class)
                    .register(dev.bsinfo.ressource.SetupResource.class)
                    .register(dev.bsinfo.ressource.CORSFilter.class);

            // Debug-Ausgabe der registrierten Ressourcen
            System.out.println("Registrierte Ressourcen:");
            rc.getClasses().forEach(System.out::println);

            // Server erstellen und starten
            server = JdkHttpServerFactory.createHttpServer(
                    URI.create(url),
                    rc
            );

            System.out.println("Server bereit für Anfragen auf " + url);
            System.out.println("Verfügbare Endpunkte:");
            System.out.println("- GET    " + url + "/customers");
            System.out.println("- POST   " + url + "/customers");
            System.out.println("- GET    " + url + "/readings");
            System.out.println("- POST   " + url + "/readings");
            System.out.println("- DELETE " + url + "/setupDB");

        } catch (Exception e) {
            System.err.println("Fehler beim Starten: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Server konnte nicht gestartet werden", e);
        }
    }

    // Stoppt den Server
    public static void stopServer() {
        if (server == null) {
            System.out.println("Kein laufender Server, der gestoppt werden kann.");
            return;


        }
        server.stop(0);
        server = null;
        System.out.println("Server gestoppt");
    }

    // Hilfsmethoden für Tests
    public static HttpServer getServer() {
        return server;
    }

    public static int getServerCount() {
        return (server == null) ? 0 : 1;
    }
}