public interface IDatabaseConnection {

    public  IDatabaseConnection openConnection(Properties properties);

    void createrAllTables();

     void truncateAllTables();

     void removeAllTables();

      void closeConnection();
}