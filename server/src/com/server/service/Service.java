package com.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JTextArea;

public class Service {

    private ServerSocket server;
    private final int PORT_NUMBER = 9999;
    private  JTextArea textArea;

    public JTextArea getTextArea() {
        return textArea;
    }

    public Service(JTextArea textArea) {
        this.textArea = textArea;
    }

    public void startServer() {
        try {
            server = new ServerSocket(PORT_NUMBER);
            textArea.append("Server is running on port: " + PORT_NUMBER + "\n");
            ServerThread serverThread;
            while (true) {
                Socket socket = server.accept();
                serverThread = new ServerThread(socket, textArea);
                serverThread.start();
                
                textArea.append("Client at " + socket.getRemoteSocketAddress().toString() + " connected\n");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
}
