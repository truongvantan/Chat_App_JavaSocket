package com.server.service;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextArea;

public class ServerThread extends Thread {

    public static List<ServerThread> clientHandlers = new ArrayList<ServerThread>();
    private Socket client;
    private JTextArea textArea;

    public ServerThread() {

    }

    public ServerThread(Socket client, JTextArea textArea) {
        this.client = client;
        this.textArea = textArea;
        clientHandlers.add(this);
    }

    @Override
    public void run() {
//        try {
//            dis = new DataInputStream(socket.getInputStream());
//            dos = new DataOutputStream(socket.getOutputStream());
//
//            String messageFromClient = dis.readUTF();
//            System.out.println("from client: " + messageFromClient);
//            if ("video".equalsIgnoreCase(messageFromClient)) {
//                dos.writeUTF("okvideo");
//                Runnable videoCall = new ServerVideoCallThread(socket);
//                Thread t = new Thread(videoCall);
//                t.start();
//            } else if ("register".equalsIgnoreCase(messageFromClient)) {
//                dos.writeUTF("okregister");
//                RegisterThread registerThread = new RegisterThread(socket, textArea);
//                registerThread.start();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        RegisterThread registerThread = new RegisterThread(socket, textArea);
//        registerThread.start();

//        Runnable videoCall = new ServerVideoCallThread(socket);
//        Thread t = new Thread(videoCall);
//        t.start();

          Runnable voiceChat = new ServerVoiceChat(client);
          Thread t = new Thread(voiceChat);
          t.start();
        
//        ListUserThread listUserThread = new ListUserThread(socket);
//        listUserThread.start();
    }
    
//    @Override
//    public void run() {
//        try {
//            // gửi
//            ByteArrayOutputStream byteStream;
//            ObjectOutputStream objectOutputStream;
//            OutputStream outputStream;
//            // nhận
//            InputStream inputStream = client.getInputStream();
//            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
//            Object obj = objectInputStream.readObject();
//            if (obj instanceof String) {
//                String request = (String) obj;
//                if ("startvideo".equalsIgnoreCase(request)) {
////                    byteStream = new ByteArrayOutputStream();
////                    objectOutputStream = new ObjectOutputStream(byteStream);
////                    objectOutputStream.writeObject("okvideo");
////                    objectOutputStream.flush();
////                    System.out.println("Server send response: " + "okvideo");
////
////                    byte[] serializedData = byteStream.toByteArray();
////                    outputStream = client.getOutputStream();
////                    outputStream.write(serializedData);
////                    outputStream.flush();
////
////                    Runnable videoCall = new ServerVideoCallThread(client);
////                    Thread t = new Thread(videoCall);
////                    t.start();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                ByteArrayOutputStream byteStream;
//                                ObjectOutputStream objectOutputStream;
//                                OutputStream outputStream;
//                                byteStream = new ByteArrayOutputStream();
//                                objectOutputStream = new ObjectOutputStream(byteStream);
//                                objectOutputStream.writeObject("okvideo");
//                                objectOutputStream.flush();
//                                System.out.println("Server send response: " + "okvideo");
//
//                                byte[] serializedData = byteStream.toByteArray();
//                                outputStream = client.getOutputStream();
//                                outputStream.write(serializedData);
//                                outputStream.flush();
//
//                                Runnable videoCall = new ServerVideoCallThread(client);
//                                Thread t = new Thread(videoCall);
//                                t.start();
//                            } catch (IOException iOException) {
//                            }
//                        }
//                    }).start();
//                } else if ("startregister".equalsIgnoreCase(request)) {
//                    byteStream = new ByteArrayOutputStream();
//                    objectOutputStream = new ObjectOutputStream(byteStream);
//                    objectOutputStream.writeObject("okregister");
//                    objectOutputStream.flush();
//                    System.out.println("Server send response: " + "okregister");
//
//                    byte[] serializedData = byteStream.toByteArray();
//                    outputStream = client.getOutputStream();
//                    outputStream.write(serializedData);
//                    outputStream.flush();
//
//                    RegisterThread registerThread = new RegisterThread(client, textArea);
//                    registerThread.start();
//                } else {
//                    System.err.println("Không thể nhận yêu cầu từ client");
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }
//    }
}
