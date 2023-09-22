package com.server.service;

import com.socket.model.Model_Message;
import com.socket.model.Model_Register;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JTextArea;

public class ServerProcess extends Thread {

    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private JTextArea textArea;

    public ServerProcess() {

    }

    public ServerProcess(Socket socket, JTextArea textArea) {
        this.socket = socket;
        this.textArea = textArea;
//        try {
//            ois = new ObjectInputStream(socket.getInputStream());
//            oos = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                // nhận dữ liệu từ client gửi lên
                ois = new ObjectInputStream(socket.getInputStream());
                oos = new ObjectOutputStream(socket.getOutputStream());
                Model_Register data = (Model_Register) ois.readObject();
                System.out.println("username: " + data.getUserName() + ", password: " + data.getPassword());
                textArea.append("username: " + data.getUserName() + ";password: " + data.getPassword() + "\n");

                Model_Message message = new ServiceUser().register(data);

                // Gửi dữ liệu về lại Client
                oos.writeObject(message);
                oos.flush();
                System.out.println("Server send: " + message);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
