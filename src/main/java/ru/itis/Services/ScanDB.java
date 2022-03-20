package ru.itis.Services;

import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


@Component
public class ScanDB {

    private static Connection connection;
    public HashMap<String, HashSet<String>> tables;

    public ScanDB() {
        getAllTables();
        connection = getConnection();
    }

    public void getAllTables() {
        // Структура для хранения имен таблиц и полей (в HashSet)
        tables = new HashMap<>();

        try (Connection connection = getConnection()) {

            List<String> tbls = getTables(connection);


            for (String table : tbls) {

                List<String> fields = getFields(table);

                HashSet<String> hashSetFields = new HashSet<>(fields);

                tables.put(table, hashSetFields);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                return connection;
            }

            Class.forName("org.postgresql.Driver");
            String dbURL = "jdbc:postgresql://localhost:5432/testentity";
            connection = DriverManager.getConnection(dbURL, "postgres", "asas1212");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return connection;
    }

    public static List<String> getTables(Connection connection) throws SQLException {

        List<String> lst = new ArrayList<>();

        PreparedStatement st = connection.prepareStatement(
                "SELECT table_name FROM information_schema.tables " +
                        "WHERE table_type = 'BASE TABLE' AND" +
                        " table_schema NOT IN ('pg_catalog', 'information_schema')");

        ResultSet resultSet = st.executeQuery();

        while (resultSet.next()) {
            String s = resultSet.getString("table_name");
            lst.add(s);
        }

        st.close();
        return lst;
    }

    public List<String> getFields(String tableName) {

        List<String> lst = new ArrayList<>();

        PreparedStatement st;
        try {
            st = connection.prepareStatement(
                    "SELECT a.attname " +
                            "FROM pg_catalog.pg_attribute a " +
                            "WHERE a.attrelid = (SELECT c.oid FROM pg_catalog.pg_class c " +
                            "LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace " +
                            " WHERE pg_catalog.pg_table_is_visible(c.oid) AND c.relname = ? )" +
                            " AND a.attnum > 0 AND NOT a.attisdropped");
            st.setString(1, tableName);
            ResultSet resultSet = st.executeQuery();

            while (resultSet.next()) {
                String s = resultSet.getString("attname");
                lst.add(s);
            }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lst;
    }

    public void runQuery(String sql) {
        try {
            Statement statement = connection.createStatement();

            statement.execute(sql);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet runExecuteQuery(String sql) {
        ResultSet rs = null;
        try {
            Statement statement = connection.createStatement();

            rs = statement.executeQuery(sql);


        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return rs;
    }

    public String existsQuery(String tableName, Long id) {
        try {
            String sql = "select exists(select 1 from " + tableName + " where id=" + id + ")";
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
