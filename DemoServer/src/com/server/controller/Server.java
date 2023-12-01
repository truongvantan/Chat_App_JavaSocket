package com.server.controller;

import com.server.model.bean.User;
import com.server.model.dao.DAO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private Object lock;
    private ServerSocket s;
    private Socket socket;
    private final int PORT_NUMBER = 9999;
    private DatagramSocket socketUDP;

    static ArrayList<Handler> clients = new ArrayList<Handler>();

    public Server() {

        try {
            // Object dùng để đồng bộ luồng giao tiếp với các client
            lock = new Object();

            // Đọc danh sách tài khoản đã đăng ký
            this.loadAllUsers();

            // Mở kết nối socket server tại PORT 9999;
            s = new ServerSocket(PORT_NUMBER);
//            socketUDP = new DatagramSocket(5555);
            System.out.println("Server is running at PORT: " + PORT_NUMBER);

            while (true) {
                // Đợi yêu cầu đăng nhập/đăng xuất từ client
                socket = s.accept();
                System.out.println("Client at: " + socket.getRemoteSocketAddress() + " connected.");

                // tạo luồng đọc/ghi dữ liệu giữa server-client
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // Đọc yêu cầu đăng nhập/đăng xuất
                String request = dis.readUTF();
                System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + request);
                // Yêu cầu đăng ký từ client
                if ("Sign up".equalsIgnoreCase(request)) {
                    String username = dis.readUTF();
                    String password = dis.readUTF();

                    User user = new User(username, password);
                    System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + user.toString());

                    // Kiểm tra tên đăng nhập đã tồn tại hay chưa
                    if (!isExistedUser(username)) {
                        // Tạo một Handler để giải quyết các yêu từ user này
//                        Handler newHandler = new Handler(socket, user, true, lock, socketUDP);
                        Handler newHandler = new Handler(socket, user, true, lock);

                        clients.add(newHandler);

                        // Thêm tài khoản vừa tạo vào CSDL, gửi thông báo về phía client
                        if (this.addNewUser(user)) {
                            dos.writeUTF("Sign up successfully");
                            dos.flush();

                            // Tạo một Thread để giao tiếp với client này
                            Thread t = new Thread(newHandler);
                            t.start();

                            // Gửi thông báo cho các client đang online cập nhật danh sách người dùng trực tuyến
                            updateOnlineUsers();
                        } else { // đăng kí thất bại
                            dos.writeUTF("Sign up failed");
                            dos.flush();
                        }
                    } else {
                        dos.writeUTF("User is already exists");
                        dos.flush();
                    }
                } // Yêu cầu đăng nhập từ client
                else if ("Log in".equalsIgnoreCase(request)) {
                    String username = dis.readUTF();
                    String password = dis.readUTF();
                    System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + username + "," + password);
                    // Kiểm tra tên đăng nhập có tồn tại hay không
                    if (isExistedUser(username)) {
                        for (Handler client : clients) {
                            if (client.getUser().getUsername().equals(username)) {
                                // Kiểm tra mật khẩu
                                if (password.equals(client.getUser().getPassword())) {
                                    // Tạo Handler mới để giải quyết các request từ user này
                                    Handler newHandler = client;
                                    newHandler.setSocket(socket);
                                    newHandler.setIsLoggedIn(true);

                                    // Thông báo đăng nhập thành công cho client
                                    dos.writeUTF("Log in successfully");
                                    dos.flush();

                                    // Tạo một Thread để giao tiếp với client này
                                    Thread t = new Thread(newHandler);
                                    t.start();

                                    // Gửi thông báo cho các client đang online cập nhật danh sách người dùng trực tuyến
                                    updateOnlineUsers();
                                } else {
                                    dos.writeUTF("Password is wrong");
                                    dos.flush();
                                }
                                break;
                            }
                        }
                    } else {
                        dos.writeUTF("This user is not exist");
                        dos.flush();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        if (s != null) {
            try {
                s.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Lấy danh sách tài khoản
    public void loadAllUsers() {
        DAO dao = new DAO();
        ArrayList<User> listUsers = dao.getAllUsers();

        for (User user : listUsers) {
            clients.add(new Handler(user, false, lock));
        }
    }

    //
    public boolean isExistedUser(String username) {
        for (Handler client : clients) {
            if (client.getUser().getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    // Thêm tài khoản mới
    public boolean addNewUser(User user) {
        DAO dao = new DAO();
        return dao.addNewUser(user);
    }

    /**
     * Gửi yêu cầu các user đang online cập nhật lại danh sách người dùng trực tuyến Được gọi mỗi khi có 1 user online hoặc offline
     */
    public static void updateOnlineUsers() {
        String message = " ";

        for (Handler client : clients) {
            if (client.isIsLoggedIn()) {
                message += ",";
                message += client.getUser().getUsername();
            }
        }

        for (Handler client : clients) {
            if (client.isIsLoggedIn()) {
                try {
                    client.getDos().writeUTF("Online users");
                    client.getDos().writeUTF(message);
                    client.getDos().flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
