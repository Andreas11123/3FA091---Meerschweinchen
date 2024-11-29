import DataConnection.DataConnection;


public class Main {

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.name"));
        DataConnection con = new DataConnection();
        con.getConnection();

    }
}
