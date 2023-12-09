package tcpserver;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TCPServer {

    public static void main(String[] args) {
        try {

            JFrame jframe = new JFrame("Server");
            jframe.setSize(640, 480);
            jframe.setLocationRelativeTo(null);
            jframe.setVisible(true);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            ServerSocket serverSocket = new ServerSocket(8888);
            Socket socket;

            // Mở webcam
            Webcam wCam;
            wCam = Webcam.getDefault();
            wCam.setViewSize(WebcamResolution.VGA.getSize());
            wCam.open();
            JPanel panel = null;

            while (true) {
                socket = serverSocket.accept();
                System.out.println("client at: " + socket.getInetAddress() + " connected");
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());


                // nhận
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                System.err.println(dis.read(data, 0, data.length));
//                while ((nRead = dis.read(data, 0, data.length)) != -1) {
//                    buffer.write(data, 0, nRead);
//                    System.err.println("");
//                }
//                buffer.flush();
                byte[] imageReceive = buffer.toByteArray();
                System.out.println(imageReceive.length);

                ByteArrayInputStream bis = new ByteArrayInputStream(imageReceive);

                BufferedImage img = ImageIO.read(bis);

                // hiển thị
                panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.drawImage(img, 0, 0, null);
                    }

                };

                jframe.add(panel);
                jframe.revalidate();
                System.out.println("tcpserver.TCPServer.main()");
                
                
                 // gửi
                            BufferedImage frame = wCam.getImage(); // nhận hình ảnh từ webcam
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            ImageIO.write(frame, "jpg", baos);
                            byte[] imageData = baos.toByteArray();
                            System.out.println(imageData.length);
                            dos.write(imageData);
                            dos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImage() {

    }

}
