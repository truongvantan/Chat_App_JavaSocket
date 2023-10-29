/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import app.MessageType;
import event.PublicEvent;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import model.Model_File;
import model.Model_File_Sender;
import model.Model_Package_Sender;
import model.Model_Receive_Message;
import model.Model_Send_Message;
import model.Model_User_Account;
import model.Model_Voice_Receive;
import swing.Recoder;

public class Service {

    private static Service instance;
    private Socket client;
    private final int PORT_NUMBER = 9999;
//    private final String IP = "192.168.224.134"; //ip nối đến máy ảo 
    private final String IP = "localhost";
    private Model_User_Account user;
    private List<Model_File_Sender> fileSender;
    private ServiceFile serviceFile;
    private Recoder recoder;

    public static Service getInstance() {
        if (instance == null) {
            instance = new Service();
        }
        return instance;
    }

    private Service() {
        fileSender = new ArrayList<>();
        serviceFile = new ServiceFile();
        recoder = new Recoder();
    }

    public void startServer() {
        //xóa file cache trong folder client_data
        File f = new File("client_data");
        for (File fs : f.listFiles()) {
            fs.delete();
        }

        try {
            client = IO.socket("http://" + IP + ":" + PORT_NUMBER);//kết nối đến server với ip và cổng đã khai báo
            client.on("list_user", new Emitter.Listener() {//bắt đầu chạy program thì gửi request : list_user đến server và chờ phản hồi từ server
                @Override
                public void call(Object... os) {
                    //list user nhận từ server
                    List<Model_User_Account> users = new ArrayList<>();
                    for (Object o : os) {
                        Model_User_Account u = new Model_User_Account(o);
                        if (u.getUserID() != user.getUserID()) {// lấy all list info client khác đang có trong db gán vào array list trừ info của client hiện tại
                            users.add(u);
                        }

                    }
                    PublicEvent.getInstance().getEventMenuLeft().newUser(users);

                }
            });

            client.on("user_status", new Emitter.Listener() {
                @Override
                public void call(Object... os) {

                    boolean status = (Boolean) os[1];
                    if (status) {
                        //connect
                        Model_User_Account user = new Model_User_Account(os[0]);
                        PublicEvent.getInstance().getEventMenuLeft().userConnect(user);
                    } else {
                        //disconnect
                        int userID = (Integer) os[0];
                        PublicEvent.getInstance().getEventMenuLeft().userDisconnect(userID);

                    }
                }
            });
            client.on("receive_data", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    Model_File dataFile = new Model_File(os[0]);
                    try {
                        serviceFile.initFile(dataFile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

            client.on("receive_ms", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    Model_Receive_Message message = new Model_Receive_Message(os[0]);

                    if (message.getMessageType().getValue() == MessageType.IMAGE.getValue()) {
                        try {
                            File file = serviceFile.receiveFile(message.getDataImage());
                            PublicEvent.getInstance().getEventChat().receiveMessage(message, file);

                        } catch (IOException ex) {
                            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    } else if (message.getMessageType().getValue() == MessageType.FILE.getValue()) {
                        PublicEvent.getInstance().getEventChat().receiveMessage(message, null);
                    } else {

                        PublicEvent.getInstance().getEventChat().receiveMessage(message, null);
                    }
                }

            });
            client.on("receive_voice", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    Model_Voice_Receive voice = new Model_Voice_Receive(os[0]);
                    PublicEvent.getInstance().getEventChat().receiveMessage(voice);
                }
            });

            client.on("GetFile", new Emitter.Listener() {
                @Override
                public void call(Object... os) {
                    Model_Package_Sender data = new Model_Package_Sender(os[0]);
                    saveFileIntoFolder(data);
                }
            });
            client.open();
        } catch (URISyntaxException e) {
            error(e);
        }
    }

    public Model_File_Sender addFile(File file, Model_Send_Message message) throws IOException {
        Model_File_Sender data = new Model_File_Sender(file, client, message);
        message.setFile(data);
        fileSender.add(data);
        // cho send file từng cái một
        if (fileSender.size() == 1) {
            data.initSend();
        }
        return data;
    }

    public void fileSendFinish(Model_File_Sender data) throws IOException {
        fileSender.remove(data);
        if (!fileSender.isEmpty()) {
            // bắt đầu gửi file mới khi file cũ đã gửi hoàn thành
            fileSender.get(0).initSend();
        }
    }

    private void saveFileIntoFolder(Model_Package_Sender data) {
        try {
            File file = new File(data.getFileName());
            FileOutputStream out = new FileOutputStream(file);
            out.write(data.getData());
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Socket getClient() {
        return client;
    }

    public Model_User_Account getUser() {
        return user;
    }

    public void setUser(Model_User_Account user) {
        this.user = user;
    }

    public Recoder getRecoder() {
        return recoder;
    }

    public void setRecoder(Recoder aRecoder) {
        recoder = aRecoder;
    }

    private void error(Exception e) {
        System.err.println(e);
    }
}
