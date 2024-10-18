import java.io.IOException;
import java.sql.DriverManager;
import java.util.Properties;
/*
public class Util {
    //singleton
    private static Connection con = null;

    //factory methode
    public static Connection getConnection(final String db) {

        if (con == null) {
            try {
                final Properties prop = new Properties();
                prop.load(new FileReader(db + ".properties"));
                final String dburl = prop.getProperty("DBURL");
                final String dbuser = prop.getProperty("DBUSER");
                final String dbpw = prop.getProperty("DBPW");

                con = DriverManager.getConnection(dburl, dbuser, dbpw);

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return con;
    }
        // close
    public static void close(final AutoCloseable obj) {
        if (obj!= null) {
            try {
                    obj.close();
            } catch (final Exception e) {
                    // ignore
            }
        }
    }

    private Util() {

    }

}
*/