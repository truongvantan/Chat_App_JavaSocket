package com.server.controller;

// Luồng riêng để giao tiếp giữa server với từng user
import com.server.model.bean.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Handler implements Runnable {

    private Object lock;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private User user;
    private boolean isLoggedIn;
    private DatagramSocket socketUDP;

    public Handler() {

    }

    public Handler(Socket socket, User user, boolean isLoggedIn, Object lock) {
        try {
            this.socket = socket;
            this.user = user;
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.isLoggedIn = isLoggedIn;
            this.lock = lock;
//            this.socketUDP = socketUDP;
//            if (this.socketUDP == null) {
//                this.socketUDP = new DatagramSocket(5555);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Handler(User user, boolean isLoggedIn, Object lock) {
        this.user = user;
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;
//        try {
//            if (this.socketUDP == null) {
//                this.socketUDP = new DatagramSocket(5555);
//            }
//        } catch (Exception e) {
//        }
    }

    public void setSocket(Socket socket) {
        this.socket = socket;

        try {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public User getUser() {
        return user;
    }

    public boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Đóng kết nối với client, được gọi khi client tắt ứng dụng.
     */
    public void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        // Chờ xử lí các yêu cầu từ phía client
        while (true) {
            try {
                String message = null;

                // Đọc yêu cầu từ phía client
                message = dis.readUTF();
                System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + message);

                // Yêu cầu đăng xuất từ client
                if ("Log out".equalsIgnoreCase(message)) {
                    // Thông báo cho user có thể đăng xuất
                    dos.writeUTF("Safe to leave");
                    dos.flush();

                    // Đóng socket và chuyển trạng thái thành offline
                    socket.close();
                    this.isLoggedIn = false;

                    // Thông báo cho các client khác cập nhật danh sách người dùng trực tuyến
                    Server.updateOnlineUsers();
                    break;
                } // Yêu cầu gửi tin nhắn dạng văn bản
                else if ("Text".equalsIgnoreCase(message)) {
                    String receiver = dis.readUTF();
                    String content = dis.readUTF();
                    System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + receiver + "," + content);

                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Text");
                                client.getDos().writeUTF(this.user.getUsername());
                                client.getDos().writeUTF(content);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                } // Yêu cầu gửi tin nhắn dạng Emoji
                else if ("Emoji".equalsIgnoreCase(message)) {
                    String receiver = dis.readUTF();
                    String emoji = dis.readUTF();
                    System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + receiver + "," + emoji);

                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Emoji");
                                client.getDos().writeUTF(this.user.getUsername());
                                client.getDos().writeUTF(emoji);
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                } // Yêu cầu gửi File
                else if ("File".equalsIgnoreCase(message)) {
                    // Đọc các header của tin nhắn gửi file
                    String receiver = dis.readUTF(); // người nhận tin nhắn
                    String filename = dis.readUTF();
                    int size = Integer.parseInt(dis.readUTF());
                    System.out.println("Client at: " + socket.getRemoteSocketAddress() + " send: " + receiver + "," + filename + "," + size);

                    int bufferSize = 2048;
                    byte[] buffer = new byte[bufferSize];

                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("File");
                                client.getDos().writeUTF(this.user.getUsername());
                                client.getDos().writeUTF(filename);
                                client.getDos().writeUTF(String.valueOf(size));

                                while (size > 0) {
                                    // Gửi lần lượt từng buffer cho người nhận
                                    dis.read(buffer, 0, Math.min(size, bufferSize));
                                    client.getDos().write(buffer, 0, Math.min(size, bufferSize));
                                    size -= bufferSize;
                                }
                                client.getDos().flush();
                                break;
                            }
                        }
                    }
                } // Yêu cầu gửi voice chat
                else if ("Voice chat".equalsIgnoreCase(message)) {
                    // Đọc các header của tin nhắn gửi voice
                    String receiver = dis.readUTF();
                    System.out.println(message + "," + receiver);
                    int bytesRead = 0;
                    byte[] buffer = new byte[1024];

                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            client.getDos().writeUTF("Voice chat");
                            client.getDos().writeUTF(this.user.getUsername());
                            InetAddress sendTo = client.getSocket().getInetAddress();
                            int port = 5555;
                            System.out.println(client.getUser().getUsername() + " send voice.");
                            while (bytesRead != -1) {
                                try {
                                    // nhận buffer voice từ người gửi
                                    DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                                    socketUDP.receive(response);
                                    // gửi lại buffer vừa đọc cho người nhận
                                    DatagramPacket request = new DatagramPacket(response.getData(), response.getLength(), sendTo, port);
                                    socketUDP.send(request);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            client.getDos().flush();
                            break;

                        }
                    }
                } else if ("Video call".equalsIgnoreCase(message)) {
                    // Đọc các header của tin nhắn gửi video
                    String receiver = dis.readUTF();
                    System.out.println(message + "," + receiver);

                    // nhận hình ảnh webcam từ client
                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {

                            client.getDos().writeUTF("Video call");
                            client.getDos().writeUTF(this.user.getUsername());
                            System.out.println(client.getUser().getUsername() + " send video.");

//                            while (true) {
                            try {
                                int frameWidth = 640;
                                int frameHeight = 480;

                                int[] pixelData = new int[frameWidth * frameHeight];
                                for (int i = 0; i < pixelData.length; i++) {

                                    pixelData[i] = dis.readInt();
//                                    pixelData[i] = Integer.parseInt(dis.readUTF());
                                    System.out.println(this.user.getUsername() + "send: " + pixelData[i]);
                                    client.getDos().writeInt(pixelData[i]);
//                                    client.getDos().writeUTF(String.valueOf(pixelData[i]));
                                }
                                client.getDos().flush();

                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                            }

                        }
                    }
                } else if ("Stop".equalsIgnoreCase(message)) {
                    // Đọc các header của tin nhắn dừng voice chat/video call
                    String receiver = dis.readUTF();
                    System.out.println(message + "," + receiver);
                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Stop");
                                client.getDos().writeUTF(this.user.getUsername());
                                System.out.println(client.getUser().getUsername() + " Stop.");
                                client.getDos().flush();
                                break;
                            }

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
