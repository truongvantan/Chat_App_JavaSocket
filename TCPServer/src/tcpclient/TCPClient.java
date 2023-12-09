package tcpclient;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TCPClient {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888);
            JFrame jframe = new JFrame("Client");
            jframe.setSize(640, 480);
            jframe.setLocationRelativeTo(null);
            jframe.setVisible(true);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Mở webcam
            Webcam wCam;
            wCam = Webcam.getDefault();
            wCam.setViewSize(WebcamResolution.VGA.getSize());
            wCam.open();
            JPanel panel = null;

            while (true) {
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

                // gửi
                BufferedImage frame = wCam.getImage(); // nhận hình ảnh từ webcam
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(frame, "jpg", baos);
                byte[] imageData = baos.toByteArray();
                dos.write(imageData);
                dos.flush();

                // nhận
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = dis.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] imageReceive = buffer.toByteArray();
                
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
