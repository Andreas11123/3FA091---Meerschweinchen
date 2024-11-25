import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Util {
    //singleton
    private static Connection con = null;

    //factory methode
    public static Connection getConnection(final String db) {

        if (con == null) {
            try {
                String fullPath = System.getProperty("user.home") + File.separator + db + ".properties";
                final Properties prop = new Properties();
                prop.load(new FileReader(fullPath));
                final String dburl = prop.getProperty("Schueler.db.url");
                final String dbuser = prop.getProperty("Schueler.db.user");
                final String dbpw = prop.getProperty("Schueler.db.pw");

                con = DriverManager.getConnection(dburl, dbuser, dbpw);

            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }
        return con;
    }

    // close
    public static void close(final AutoCloseable obj) {
        if (obj != null) {
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
