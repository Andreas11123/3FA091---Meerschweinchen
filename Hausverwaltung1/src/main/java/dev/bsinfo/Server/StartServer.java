package dev.bsinfo.Server;

import DataConnection.Server;

public class StartServer {
    public static void main(String[] args) {
        Server.startServer("http://localhost:8000/rest");
        //Server.stopServer();
    }
}
