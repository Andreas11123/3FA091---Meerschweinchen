package dev.bsinfo.Server;

import DataConnection.Server;

public class StartServer {
    public static void main(String[] args) {
        try {
            // Server mit korrektem Pfad starten (beachte "resources" statt "ressources")
            Server.startServer("http://localhost:8080/test/resources");

            // Server läuft bis zum manuellen Stopp
            System.out.println("Server läuft. Drücken Sie STRG+C zum Beenden.");

            // Warten auf Shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                Server.stopServer();
            }));

            // Thread blockieren
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("Fehler beim Ausführen des Servers: " + e.getMessage());
            e.printStackTrace();
        }
    }
}