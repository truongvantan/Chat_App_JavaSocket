package com.server.service;

import com.socket.model.Model_Message;
import com.socket.model.Model_User_Account;
import com.socket.model.MyObjectInputStream;
import com.socket.model.MyObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ListUserThread extends Thread {

    private Socket client;
    private ServiceUser serviceUser;
    private Model_Message message;

    public ListUserThread() {
    }

    public ListUserThread(Socket client, ServiceUser serviceUser, Model_Message message) {
        this.client = client;
        this.serviceUser = serviceUser;
        this.message = message;
    }

//    @Override
//    public void run() {
//        try {
//            oos = new ObjectOutputStream(socket.getOutputStream());
////            oos = new MyObjectOutputStream(socket.getOutputStream());
//
//            if (message.getData() != null) {
//                Model_User_Account account = (Model_User_Account) message.getData();
//                List<Model_User_Account> list = serviceUser.getUser(account.getUserID());
//
//                // gửi dữ liệu list user cho client
//                oos.writeObject(list);
//                oos.flush();
//                System.out.println("Server response: " + Arrays.toString(list.toArray()));
//
//            } else {
//                System.err.println("Account is null");
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
    @Override
    public void run() {
        try {
            if (message.getData() != null) {
                Model_User_Account account = (Model_User_Account) message.getData();
                List<Model_User_Account> list = serviceUser.getUser(account.getUserID());

                // Gửi dữ liệu list user về client.
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject(list);
                objectOutputStream.flush();
                byte[] serializedData = byteStream.toByteArray();
                OutputStream outputStream = client.getOutputStream();
                outputStream.write(serializedData);
                outputStream.flush();
                System.out.println("Server response: " + Arrays.toString(list.toArray()));
            } else {
                System.err.println("Không thể nhận account từ client.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
