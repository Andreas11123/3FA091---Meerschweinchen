import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {

    public void getConnection() {
        try {
            final Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/hausverwaltung?allowMultiQueries=true",
                    "root", "Meerschweinchen20+");
            System.out.println("... connected");

            Class.forName("org.mariadb.jdbc.Driver");

            final DatabaseMetaData meta = con.getMetaData();
            System.out.format("Driver : %s %s %s\n", meta.getDriverName(),
                    meta.getDriverMajorVersion(), meta.getDriverMinorVersion());
            System.out.format("DB    : %s %s.%s  (%s)\n",
                    meta.getDatabaseProductName(),
                    meta.getDatabaseMinorVersion(),
                    meta.getDatabaseProductVersion());
            con.close();

        } catch (final SQLException | ClassNotFoundException e) {
            System.out.format("Fehler: " + e.getMessage());

        }
    }
}

