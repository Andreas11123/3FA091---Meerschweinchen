import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;


public class Main {

    public static void main(String[] args) {

        //System.out.println(System.getProperty("user.name"));
        DataConnection con = new DataConnection();
        con.getConnection();
    }
}
