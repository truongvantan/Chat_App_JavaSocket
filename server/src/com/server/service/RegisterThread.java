package com.server.service;

import com.socket.model.Model_ImageFPS;
import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JTextArea;

public class RegisterThread extends Thread {

    private Socket client;
    private JTextArea textArea;
    private ServiceUser serviceUser;

    public RegisterThread() {
    }

    public RegisterThread(Socket client, JTextArea textArea) {
        this.client = client;
        this.textArea = textArea;
        this.serviceUser = new ServiceUser();
    }

    public ServiceUser getServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

//    @Override
//    public void run() {
//        try {
//            while (true) {
//                ois = new ObjectInputStream(socket.getInputStream());
//                oos = new ObjectOutputStream(socket.getOutputStream());
////                ois = new MyObjectInputStream(socket.getInputStream());
////                oos = new MyObjectOutputStream(socket.getOutputStream());
//                // nhận dữ liệu từ client gửi lên
////                Object obj = (Model_Register) ois.readObject();
////
////                if (obj instanceof Model_Register) {
////                    Model_Register data = (Model_Register) ois.readObject();
////                    Model_Message message = serviceUser.register(data);
////                    System.out.println("Data from client: " + data.toString());
////
////                    if (message.isAction()) {
////                        textArea.append("Data from client at " + socket.getRemoteSocketAddress() + " : " + data.toString());
////                        // gửi dữ liệu list user cho tất cả client đang kết nối
////
////                    }
//////
//////                // Gửi dữ liệu về lại Client
////                    oos.writeObject(message);
////                    oos.flush();
////                    System.out.println("Server response: " + message.toString());
////                } else {
////                    System.err.println("InputStream is not Model_Register");
////                }
//
//                Model_Register data = (Model_Register) ois.readObject();
//                Model_Message message = serviceUser.register(data);
//                System.out.println("Data from client: " + data.toString());
//
//                if (message.isAction()) {
//                    textArea.append("Data from client at " + socket.getRemoteSocketAddress() + " : " + data.toString());
//                    // gửi dữ liệu list user cho tất cả client đang kết nối
//
//                }
////
////                // Gửi dữ liệu về lại Client
//                oos.writeObject(message);
//                oos.flush();
//                System.out.println("Server response: " + message.toString());
////
//                ListUserThread listUserThread = new ListUserThread(socket, serviceUser, message);
//                listUserThread.start();
//            }
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void run() {
        try {
//            while (true) {
                InputStream inputStream = client.getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object obj = objectInputStream.readObject();

                if (obj instanceof Model_Register) {
                    Model_Register data = (Model_Register) obj;
                    Model_Message message = serviceUser.register(data);
                    System.out.println("Data from client: " + data.toString());
                    if (message.isAction()) {
                        textArea.append("Data from client at " + client.getRemoteSocketAddress() + " : " + data.toString());
                        // gửi dữ liệu list user cho tất cả client đang kết nối
                    }
                    
                    // Gửi dữ liệu Model_Message về lại Client
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();

                    byte[] serializedData = byteStream.toByteArray();
                    OutputStream outputStream = client.getOutputStream();
                    outputStream.write(serializedData);
                    outputStream.flush();
                    
                    
//                    ListUserThread listUserThread = new ListUserThread(client, serviceUser, message);
//                    listUserThread.start();
                } else {
                    System.err.println("Object received not Model_Register");
                }
//            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
