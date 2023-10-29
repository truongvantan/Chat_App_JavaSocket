package com.pbl4.service;

import com.pbl4.event.PublicEvent;
import com.socket.model.Model_User_Account;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class ListUserThread extends Thread {

    private Socket client;

    public ListUserThread() {
    }

    public ListUserThread(Socket client) {
        this.client = client;
    }

//    @Override
//    public void run() {
//        try {
////            ois = new ObjectInputStream(socket.getInputStream());
//            ois = new MyObjectInputStream(socket.getInputStream());
//
//            // nhận list user từ server
//            List<Model_User_Account> users = (List<Model_User_Account>) ois.readObject();
//            System.out.println("List user from server: " + Arrays.toString(users.toArray()));
//            PublicEvent.getInstance().getEventMenuLeft().newUser(users);
//        } catch (IOException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }
    @Override
    public void run() {
        try {
            InputStream inputStream = client.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//            Object obj = objectInputStream.readObject();

            // nhận list user từ server
            List<Model_User_Account> users = (List<Model_User_Account>) objectInputStream.readObject();
            System.out.println("List user from server: " + Arrays.toString(users.toArray()));
            PublicEvent.getInstance().getEventMenuLeft().newUser(users);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
