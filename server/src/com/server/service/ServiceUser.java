package com.server.service;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.server.connection.DatabaseConnection;
import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import com.socket.model.Model_User_Account;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser {

    private final String INSERT_USER = "INSERT INTO user (UserName, `Password`) VALUES (?, ?)";
    private final String CHECK_USER = "SELECT UserID FROM user WHERE UserName = ? LIMIT 1";
    private final String INSERT_USER_ACCOUNT = "INSERT INTO user_account (UserID, UserName) VALUES (?,?)";
    private final String SELECT_USER_ACCOUNT = "SELECT UserID, UserName, Gender, ImageString FROM user_account WHERE user_account.`Status`='1' AND UserID<>?";
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
                // Thêm user mới
                conn.setAutoCommit(false);
                p = (PreparedStatement) conn.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS);
                p.setString(1, data.getUserName());
                p.setString(2, data.getPassword());
                p.execute();
                r = (ResultSetImpl) p.getGeneratedKeys();
                r.first();
                int userID = r.getInt(1);

                // Tạo tài khoản mới nếu đăng kí thành công
                p = (PreparedStatement) conn.prepareStatement(INSERT_USER_ACCOUNT);
                p.setInt(1, userID);
                p.setString(2, data.getUserName());
                p.execute();
                conn.commit();
                conn.setAutoCommit(true);
                message.setAction(true);
                message.setMessage("Register Successfully");
                message.setData(new Model_User_Account(userID, data.getUserName(), "", "", true));
            }
            r.close();
            p.close();
            System.out.println("action: " + message.isAction());
            System.out.println("message: " + message.getMessage());
        } catch (SQLException e) {
            message.setAction(false);
            message.setMessage("Server error");
            try {
                if (!conn.getAutoCommit()) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }

        return message;
    }

    public List<Model_User_Account> getUser(int existUser) {
        List<Model_User_Account> list = new ArrayList<Model_User_Account>();
        
        try {
            PreparedStatement p = (PreparedStatement) conn.prepareStatement(SELECT_USER_ACCOUNT);
            p.setInt(1, existUser);
            ResultSetImpl r = (ResultSetImpl) p.executeQuery();
            while (r.next()) {
                int userID = r.getInt(1);
                String userName = r.getString(2);
                String gender = r.getString(3);
                String image = r.getString(4);
                list.add(new Model_User_Account(userID, userName, gender, image, true));
            }
            r.close();
            p.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return list;
    }
}
