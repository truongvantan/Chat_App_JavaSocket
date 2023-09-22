package com.server.service;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.server.connection.DatabaseConnection;
import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import java.sql.SQLException;

public class ServiceUser {

    private final String INSERT_USER = "INSERT INTO user (UserName, `Password`) VALUES (?, ?)";
    private final String CHECK_USER = "SELECT UserID FROM user WHERE UserName = ? LIMIT 1";
    private final Connection conn;

    public ServiceUser() {
        this.conn = DatabaseConnection.getInstance().getConnection();
    }

    public Model_Message register(Model_Register data) {
        // Kiểm tra tài khoản đã tồn tại hay chưa ?
        Model_Message message = new Model_Message();

        try {
            PreparedStatement p = (PreparedStatement) conn.prepareStatement(CHECK_USER);
            p.setString(1, data.getUserName());

            ResultSetImpl r = (ResultSetImpl) p.executeQuery();

            if (r.first()) {
                message.setAction(false);
                message.setMessage("User is already Exists");
                System.out.println("UserID: " + r.getString(1) + " Tài khoản đã tồn tại");
            } else {
                message.setAction(true);
                System.out.println("Tài khoản hợp lệ");

            }

            if (message.isAction()) {
                p = (PreparedStatement) conn.prepareStatement(INSERT_USER);
                p.setString(1, data.getUserName());
                p.setString(2, data.getPassword());
                p.execute();
                p.close();

                message.setAction(true);
                message.setMessage("Register Successfully");
            }
            r.close();
            p.close();
            System.out.println("action: " + message.isAction());
            System.out.println("message: " + message.getMessage());
        } catch (SQLException e) {
            message.setAction(false);
            message.setMessage("Server error");
            e.printStackTrace();
        }

        return message;
    }

}
