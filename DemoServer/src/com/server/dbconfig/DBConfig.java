package com.server.dbconfig;

import com.mysql.jdbc.Connection;
import java.sql.SQLException;

public class DBConfig {

    private Connection conn;

    public DBConfig() {

    }

    // lấy kết nối DB
    public Connection getConnection() {
        String server = "localhost";
        String port = "3306";
        String dbName = "pbl4";
        String userName = "root";
        String password = "";
        
        try {
            conn = (Connection) java.sql.DriverManager.getConnection("jdbc:mysql://" + server + ":" + port + "/" + dbName, userName, password);
            System.out.println("Connect DB Successfully.");
        } catch (SQLException e) {
            displayError(e);
            e.printStackTrace();
        }
        return conn;
    }

    // hiển thị lỗi kết nối DB
    public void displayError(SQLException ex) {
        System.out.println(" Error Message:" + ex.getMessage());
        System.out.println(" SQL State:" + ex.getSQLState());
        System.out.println(" Error Code:" + ex.getErrorCode());
    }
}
