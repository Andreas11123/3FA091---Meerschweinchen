import DataConnection.DataConnection;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class Main {

    public static void main(String[] args) {
        // Aktueller Code: Benutzername und Verbindung testen
        System.out.println(System.getProperty("user.name"));
        DataConnection con = new DataConnection();
        con.getConnection();


    }
}
