package com.pbl4.service;

import com.pbl4.event.EventMessage;
import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import com.socket.model.Model_User_Account;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Service { // Singleton

    private static Service intance;
    private Socket client;
    private final int PORT_NUMBER = 9999;
    private final String IP = "localhost";
//    private final String IP = "192.168.34.64";

    private Model_User_Account user;

    public static Service getInstance() {
        if (intance == null) {
            intance = new Service();
        }
        return intance;
    }

    private Service() {
        try {
            client = new Socket(IP, PORT_NUMBER);
//            ois = new ObjectInputStream(client.getInputStream());
//            oos = new ObjectOutputStream(client.getOutputStream());
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

//    public void clientProcess(Model_Register data, EventMessage message) {
//
//        try {
//            // Gửi  yêu cầu đăng nhập
//            dos = new DataOutputStream(client.getOutputStream());
//            dos.writeUTF("register");
//            // Nhận phản hồi từ server
//            dis = new DataInputStream(client.getInputStream());
//            String messageFromServer = dis.readUTF();
//            System.out.println("from server: " + messageFromServer);
//            if ("okregister".equalsIgnoreCase(messageFromServer)) {
//                // Gửi dữ liệu Model_Register lên server
//                oos = new ObjectOutputStream(client.getOutputStream());
//                oos.writeObject(data);
//                oos.flush();
//                System.out.println("client send: " + data);
//            }
//
//            // Nhận dữ liệu Model_Message từ server
//            ois = new ObjectInputStream(client.getInputStream());
//            Model_Message ms = (Model_Message) ois.readObject();
//            message.callMessage(ms);
//            if (ms.isAction()) {
//                Model_User_Account user = new Model_User_Account((Model_User_Account) ms.getData());
//                System.out.println(user.getUserID() + " is UserID");
//                Service.getInstance().setUser(user);
//            }
//
////            ListUserThread listUserThread = new ListUserThread(client);
////            listUserThread.start();
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//    public void clientProcess(Model_Register data, EventMessage message) {
//
//        try {
//            // Gửi dữ liệu Model_Register lên server
//            oos = new ObjectOutputStream(client.getOutputStream());
//            oos.writeObject(data);
//            oos.flush();
//            System.out.println("client send: " + data);
//
//            // Nhận dữ liệu Model_Message từ server
//            ois = new ObjectInputStream(client.getInputStream());
//            Model_Message ms = (Model_Message) ois.readObject();
//            message.callMessage(ms);
//            if (ms.isAction()) {
//                Model_User_Account user = new Model_User_Account((Model_User_Account) ms.getData());
//                System.out.println(user.getUserID() + " is UserID");
//                Service.getInstance().setUser(user);
//                
//                
//                ListUserThread listUserThread = new ListUserThread(client);
//                listUserThread.start();
//            }
//
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    public void clientProcess(Model_Register data, EventMessage message) {

        try {
            // Gửi dữ liệu Model_Register lên server
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();

            byte[] serializedData = byteStream.toByteArray();
            OutputStream outputStream = client.getOutputStream();
            outputStream.write(serializedData);
            outputStream.flush();

            // Nhận dữ liệu Model_Message từ server
            InputStream inputStream = client.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Object obj = objectInputStream.readObject();
            if (obj instanceof Model_Message) {
                Model_Message ms = (Model_Message) obj;
                message.callMessage(ms);
                if (ms.isAction()) {
                Model_User_Account user = new Model_User_Account((Model_User_Account) ms.getData());
                System.out.println(user.getUserID() + " is UserID");
                Service.getInstance().setUser(user);

//                ListUserThread listUserThread = new ListUserThread(client);
//                listUserThread.start();
            }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void clientProcess() {

    }

}
