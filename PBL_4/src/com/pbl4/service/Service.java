package com.pbl4.service;

import com.pbl4.event.EventMessage;
import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Service { // Singleton

    private static Service intance;
    private Socket client;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private final int PORT_NUMBER = 9999;
    private final String IP = "localhost";

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

    public void clientProcess(Model_Register data, EventMessage message) {
//        try {
//            while (true) {
//                // nhận dữ liệu từ form
//                // gửi dữ liệu lên server
////                oos.writeObject(data);
////                oos.flush();
//                // nhận dữ liệu từ server
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try {
//            while (true) {                
//                oos = new ObjectOutputStream(client.getOutputStream());
//                oos.writeObject(data);
//                oos.flush();
//            }
            // Gửi dữ liệu lên server
            oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(data);
            oos.flush();
            System.out.println("client send: " + data);

            // Nhận dữ liệu từ server
            ois = new ObjectInputStream(client.getInputStream());
            Model_Message ms = (Model_Message) ois.readObject();
            message.callMessage(ms);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void clientProcess() {

    }
//    public void startServer() {
//        try {
//            client = new Socket(IP, PORT_NUMBER);
//            ois = new ObjectInputStream(client.getInputStream());
//            oos = new ObjectOutputStream(client.getOutputStream());
//        } catch (IOException ex) {
//            error(ex);
//        }
//    }
}
