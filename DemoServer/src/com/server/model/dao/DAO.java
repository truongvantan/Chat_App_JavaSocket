package com.server.model.dao;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.ResultSetImpl;
import com.server.dbconfig.DBConfig;
import com.server.model.bean.User;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSetImpl rs;

    public DAO() {

    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Lấy danh sách tài khoản
    public ArrayList<User> getAllUsers() {
        ArrayList<User> listUsers = new ArrayList<User>();
        String query = "SELECT * FROM user";

        try {
            conn = new DBConfig().getConnection();
            ps = (PreparedStatement) conn.prepareStatement(query);
            rs = (ResultSetImpl) ps.executeQuery();
            User user;
            while (rs.next()) {
                String username = rs.getString(2);
                String password = rs.getString(3);

                user = new User(username, password);
                listUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();

        return listUsers;
    }

    public boolean addNewUser(User user) {
        boolean isSuccessful = false;
        String query = "INSERT INTO user (UserName, `Password`) VALUES (?, ?)";

        try {
            conn = new DBConfig().getConnection();
            ps = (PreparedStatement) conn.prepareStatement(query);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.executeUpdate();
            isSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection();
            return false;
        }
        closeConnection();

        return isSuccessful;
    }

    public boolean isExistedUser(String username) {
        boolean isExisted = false;
        String query = "SELECT UserID FROM user WHERE UserName = ? LIMIT 1";

        try {
            conn = new DBConfig().getConnection();
            ps = (PreparedStatement) conn.prepareStatement(query);
            
            ps.setString(1, username);
            ResultSetImpl rs = (ResultSetImpl) ps.executeQuery();
            
            if (rs.first()) {
                isExisted = true;
            } else {
                isExisted = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();

        return isExisted;
    }
}
