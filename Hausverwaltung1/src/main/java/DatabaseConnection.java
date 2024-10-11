public interface DatabaseConnection {
    public void IDatabaseConnection(Properties properties);

    void createrAllTables();

     void truncateAllTables();

      void removeAllTables();

      void closeConnection();
}