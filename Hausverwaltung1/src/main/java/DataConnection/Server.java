package DataConnection;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.net.ssl.SSLContext;
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

            SSLContext sslContext = createInsecureSSLContext();

            // ResourceConfig mit allen benötigten Ressourcen
            final ResourceConfig rc = new ResourceConfig()
                    .packages(pack)
                    .register(JacksonFeature.class)
                    // Explizit die REST-Ressourcen registrieren
                    .register(dev.bsinfo.ressource.CustomerResource.class)
                    .register(dev.bsinfo.ressource.ReadingResource.class)
                    .register(dev.bsinfo.ressource.SetupResource.class);

            // Server erstellen und starten
            server = JdkHttpServerFactory.createHttpServer(
                    URI.create(url),
                    rc,
                    sslContext
            );

            System.out.println("Server bereit für Anfragen auf " + url);

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

    // Erstellt ein SSLContext
    private static SSLContext createInsecureSSLContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(
                null, // Keine KeyManager
                new javax.net.ssl.TrustManager[] {
                        new javax.net.ssl.X509TrustManager() {
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new java.security.cert.X509Certificate[0];
                            }

                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                // Entwicklungsumgebung - keine Client-Zertifikatsprüfung
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                // Entwicklungsumgebung - keine Server-Zertifikatsprüfung
                            }
                        }
                },
                new java.security.SecureRandom()
        );
        return sslContext;
    }

    // Hilfsmethoden für Tests
    public static HttpServer getServer() {
        return server;
    }

    public static int getServerCount() {
        return (server == null) ? 0 : 1;
    }
}