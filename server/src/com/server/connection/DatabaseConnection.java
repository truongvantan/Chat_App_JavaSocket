package com.server.connection;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;

public class DatabaseConnection { // Singleton

    private static DatabaseConnection instance;
    private Connection connection;

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    private DatabaseConnection() {

    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void connectToDatabase() {
        String server = "localhost";
        String port = "3306";
        String dbName = "pbl4";
        String userName = "root";
        String password = "";

        try {
            connection = (Connection) java.sql.DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + dbName, userName, password);
            System.out.println("Ket noi Database thanh cong");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
