package DataConnection;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.net.ssl.SSLContext;
import java.net.URI;

public class Server {
    private static HttpServer server;

    // Startet den Server mit der angegebenen URL
    public static void startServer(String url) {
        // Implementierung hier
        if (server != null) {
            System.out.println("Server ist bereits gestartet.");
            return;
        }
        try {

            final String pack = "dev.bsinfo.ressource";
            System.out.println("Start Server...");
            System.out.println("URL: " + url);

            SSLContext sslContext = createInsecureSSLContext();

            final ResourceConfig rc = new ResourceConfig().packages(pack);
            server = JdkHttpServerFactory.createHttpServer(URI.create(url), rc, sslContext);
            ;

            System.out.println("Server bereit für Anfragen...");

        } catch (Exception e) {
            System.err.println("Fehler beim Starten" + e.getMessage());
            e.printStackTrace();
        }
    }


    // Stoppt den Server
    public static void stopServer() {
        // Implementierung hier
        if (server == null) {
            System.out.println("Keine laufender Server, der gestoppt werden kann.");
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
                new javax.net.ssl.TrustManager[]{
                        new javax.net.ssl.X509TrustManager() {
                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return null;
                            }

                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                // Keine Prüfung
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                                // Keine Prüfung
                            }
                        }
                },
                new java.security.SecureRandom()
        );
        return sslContext;
    }
}