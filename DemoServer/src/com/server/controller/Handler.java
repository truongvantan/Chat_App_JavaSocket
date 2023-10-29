package com.server.controller;

// Luồng riêng để giao tiếp giữa server với từng user
import com.server.model.bean.User;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Handler implements Runnable {

    private Object lock;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private User user;
    private boolean isLoggedIn;

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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Handler(User user, boolean isLoggedIn, Object lock) {
        this.user = user;
        this.isLoggedIn = isLoggedIn;
        this.lock = lock;
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

                    AudioFormat af = new AudioFormat(8000.0f, 8, 1, true, false);
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, af);
                    SourceDataLine inSpeaker = (SourceDataLine) AudioSystem.getLine(info);
                    int bytesRead = 0;
                    byte[] inSound = new byte[1];
                    
                    for (Handler client : Server.clients) {
                        if (client.getUser().getUsername().equals(receiver)) {
                            synchronized (lock) {
                                client.getDos().writeUTF("Voice chat");
                                client.getDos().writeUTF(this.user.getUsername());
                                while (bytesRead != -1) {
                                    try {
                                        // nhận buffer voice từ người gửi
                                        bytesRead = dis.read(inSound, 0, inSound.length);
                                        // gửi lại buffer vừa đọc cho người nhận
                                        client.getDos().write(inSound, 0, bytesRead);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
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
