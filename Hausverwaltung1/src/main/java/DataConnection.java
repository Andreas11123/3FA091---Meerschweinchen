import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DataConnection implements IDatabaseConnection {


    public void getConnection() {
        try {
            final Connection con = Util.getConnection("Hausverwaltung");
            System.out.println("... connected");

            //Class.forName("org.mariadb.jdbc.Driver");

            final DatabaseMetaData meta = con.getMetaData();
            System.out.format("Driver : %s %s %s\n", meta.getDriverName(),
                    meta.getDriverMajorVersion(), meta.getDriverMinorVersion());
            System.out.format("DB    : %s %s.%s  (%s)\n",
                    meta.getDatabaseProductName(),
                    meta.getDatabaseMajorVersion(),
                    meta.getDatabaseMinorVersion(),
                    meta.getDatabaseProductVersion());
            Util.close(con);

        } catch (final SQLException /*| ClassNotFoundException*/ e) {
            System.out.format("Fehler: " + e.getMessage());

        }
    }


    @Override
    public IDatabaseConnection openConnection(Properties properties) {
        return null;
    }

    @Override
    public void createrAllTables() {

    }

    @Override
    public void truncateAllTables() {

    }

    @Override
    public void removeAllTables() {

    }

    @Override
    public void closeConnection() {

    }
}

