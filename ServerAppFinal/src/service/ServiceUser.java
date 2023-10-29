/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.PreparedStatement;
import connection.DatabaseConnection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import model.Model_Client;
import model.Model_Login;
import model.Model_Message;
import model.Model_Register;
import model.Model_User_Account;
/**
 *
 * @author Admin
 */
public class ServiceUser {
    private final String PATH_FILE = "server_data/profile.png";
  //SQL truy vấn
    private final String LOGIN = "select UserID, user_account.UserName, Gender, Image from `user` join user_account using (UserID) where `user`.UserName=BINARY(?) and `user`.`Password`=BINARY(?) and user_account.`Status`='1'";
    private final String INSERT_USER = "insert into user (UserName, Password) values (?,?)";
    private final String INSERT_USER_ACCOUNT = "insert into user_account (UserID, UserName, Image) values (?,?,?)";
    private final String CHECK_USER = "select UserID from user where UserName =? limit 1";
    // lấy info các user còn lại trừ userID này ra
    private final String SELECT_USER_ACCOUNT = "select UserID, UserName, Gender, Image from user_account where user_account.`Status`='1' and UserID<>?";
    //khởi tạo connect sql
    private final Connection con;
    public ServiceUser() {
        this.con = DatabaseConnection.getInstance().getConnection();
    }
    public Model_Message register(Model_Register data) throws IOException{
    //kiểm tra user tồn tại
        Model_Message message = new Model_Message();
        try {
            
        File f = new File(PATH_FILE); 
         byte[] dataAvatar = Files.readAllBytes(f.toPath());
        PreparedStatement p = con.prepareStatement(CHECK_USER);
        p.setString(1, data.getUserName());
        ResultSet r = p.executeQuery();
        if(r.first()){  
             message.setAction(false);
             message.setMessage("Người dùng đã tồn tạiiiii");   
        }else{
             message.setAction(true);
        }
        r.close();
        p.close();
        if(message.isAction()){
         //insert user đang ký
         con.setAutoCommit(false);
         p = con.prepareStatement(INSERT_USER, PreparedStatement.RETURN_GENERATED_KEYS);
         p.setString(1, data.getUserName());
         p.setString(2, data.getPassword());
         p.execute();
         r = p.getGeneratedKeys();
         r.first();
         int userID = r.getInt(1);
         r.close();
         p.close();
         //create avatar image for new user register
       
         FileInputStream fis = new FileInputStream(f.getAbsolutePath());
         //tao user account
         p = con.prepareStatement(INSERT_USER_ACCOUNT);
         p.setInt(1, userID);
         p.setString(2, data.getUserName());
         p.setBinaryStream(3, fis);
         p.execute();
         p.close();
         con.commit(); //xác nhận commit thay đổi 
         con.setAutoCommit(true);          
             message.setAction(true);
             message.setMessage("Đăng ký tài khoản thành công !"    );
             message.setData(new Model_User_Account(userID, data.getUserName(), "", dataAvatar, true));//set list thông tin user để gửi về client hiển thị
       
       }
        } catch (SQLException e) {
            message.setAction(false);
            message.setMessage("Server error");
            try {
                if(con.getAutoCommit() == false){
                    con.rollback();
                    con.setAutoCommit(true);
                
                }
            } catch (Exception e1) {
            }
            
        }

       
       return message;
    
    }
    
    public Model_User_Account login(Model_Login login)throws SQLException, IOException{
    Model_User_Account data = null;
    PreparedStatement p = con.prepareStatement(LOGIN);
    p.setString(1, login.getUserName());
    p.setString(2, login.getPassword());
    ResultSet r = p.executeQuery();
    if(r.first()){
        int userID = r.getInt(1);
        String userName = r.getString(2);
        String gender = r.getString(3);
        InputStream input = r.getBinaryStream(4);
        byte[] imageAvatar = new byte[input.available()];
        imageAvatar = input.readAllBytes();
        data = new Model_User_Account(userID, userName, gender, imageAvatar, true);
        input.close();
    }
    r.close();
    p.close();
    return data;
    
    }
    
    public List<Model_User_Account> getUser(int exitUser) throws SQLException, IOException {
              List<Model_User_Account> list = new ArrayList<>();
              PreparedStatement p = con.prepareStatement(SELECT_USER_ACCOUNT);
              p.setInt(1, exitUser);
              ResultSet r = p.executeQuery();
              while(r.next()){
                  int userID = r.getInt(1);
                  String userName = r.getString(2);
                  String gender = r.getString(3);
                  InputStream input = r.getBinaryStream(4);
                  byte[] imageAvatar = new byte[input.available()];
                  imageAvatar = input.readAllBytes();
                  list.add(new Model_User_Account(userID, userName, gender, imageAvatar, checkUserStatus(userID)));
                  input.close();
              }
              r.close();
              p.close();
              
              return list;
    } 
   private boolean checkUserStatus(int userID){
       List<Model_Client> clients = Service.getInstance(null).getListClient();
       for(Model_Client c:clients ){
             if (c.getUser().getUserID() == userID) {
               return true;
           }
       }
       return false;
   }
    
}
