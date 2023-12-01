package com.server.controller;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class UDPVoiceServer extends Thread {

    DatagramSocket socket;
    byte[] buffer = new byte[1024];
    public UDPVoiceServer(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // nhận
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                InetAddress address = response.getAddress();
                int port = response.getPort();
//	        	System.out.println("address: " + address + ", port: " + port);
                System.out.println("data: " + response.getData() + ", length: " + response.getLength());

                // gửi
                DatagramPacket request = new DatagramPacket(response.getData(), response.getLength(), address, port);
                socket.send(request);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
