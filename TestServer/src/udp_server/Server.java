package udp_server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Server {

    public static final int PORT = 7777;

    public static void main(String[] args) {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, true);
//		AudioFormat format = new AudioFormat(8000.0f, 8, 1, true, false);
        TargetDataLine microphone;
        SourceDataLine speakers;
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);

            int numBytesRead = 0;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[microphone.getBufferSize() / 5];
            microphone.start();

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            speakers.open(format);
            speakers.start();

            // khởi tạo socket
            DatagramSocket socket = new DatagramSocket(5555);
            System.out.println("server is running at PORT: " + socket.getLocalPort());
            byte[] buffer = new byte[1024];
            
            while (true) {
                // nhận
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);
                InetAddress address = response.getAddress();
                int port = response.getPort();
//	        	System.out.println("address: " + address + ", port: " + port);
                System.out.println("data: " + response.getData() + ", length: " + response.getLength());
                speakers.write(response.getData(), 0, response.getLength());

                // gửi
                numBytesRead = microphone.read(data, 0, CHUNK_SIZE);

                DatagramPacket request = new DatagramPacket(data, numBytesRead, address, port);
                socket.send(request);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
