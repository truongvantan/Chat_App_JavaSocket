package com.pbl4.component;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.socket.model.Model_ImageFPS;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class Video_Call extends javax.swing.JFrame implements Runnable {

    private WebcamPanel panel = null;
    private Webcam webcam = null;
    private Socket socket;
    //public static JLabel img_client = new JLabel();

    public Video_Call(Socket socket) throws IOException {
        this.socket = socket;
        initComponents();
        this.setTitle("client");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pack();
    }// </editor-fold>                        

//    @Override
//    public void run() {
//        try {
//            webcam = Webcam.getDefault();
//            Dimension size = WebcamResolution.VGA.getSize();
//            webcam.setViewSize(size);
//            System.out.println(size.getHeight() + " " + size.getWidth());
//            webcam.open();
//
//            BufferedImage bm;
//            ImageIcon im;
//            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
//            this.setSize(640, 550);
//            this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
//
//            JLayeredPane jlp = new JLayeredPane();
//            jlp.setLayout(new BoxLayout(jlp, BoxLayout.Y_AXIS));
//            jlp.setBounds(0, 0, 640, 480);
//
//            JLabel l = new JLabel();
//            l.setBounds(0, 0, 640, 400);
//            l.setVisible(true);
//
//            JLabel l2 = new JLabel();
//            l2.setBounds(50, 1000, 100, 20);
//
//            l2.setVisible(true);
//
//            jlp.add(l);
//            jlp.add(l2);
//            jlp.setVisible(true);
//            
//            this.add(jlp);
//            this.setLocationRelativeTo(null);
//            this.setVisible(true);
//            System.out.println("Camera OK");
//            while (true) {
//                bm = webcam.getImage();
//                im = new ImageIcon(bm);
//                l.setIcon(im);
//                double fps = webcam.getFPS();
//                l2.setText("FPS: " + fps);
//                
//                Model_ImageFPS mifps = new Model_ImageFPS(im, fps); 
//                oos.writeObject(mifps);
//                oos.flush();
//                System.out.println("send: " + im);
//            }
//        } catch (WebcamException | IOException e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void run() {
        try {
            webcam = Webcam.getDefault();
            Dimension size = WebcamResolution.VGA.getSize();
            webcam.setViewSize(new Dimension(320, 240));
            System.out.println(size.getWidth()+ " " + size.getHeight());
            webcam.open();

            BufferedImage bm;
            ImageIcon im;
            OutputStream rawOutputStream = socket.getOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(rawOutputStream);
            // Create a GZIPOutputStream to compress the data
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(rawOutputStream);

            // Wrap the GZIPOutputStream with ObjectOutputStream
            ObjectOutputStream compressedOut = new ObjectOutputStream(gzipOutputStream);

            this.setSize(320, 300);
            this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);

            JLayeredPane jlp = new JLayeredPane();
            jlp.setLayout(new BoxLayout(jlp, BoxLayout.Y_AXIS));
            jlp.setBounds(0, 0, 320, 240);

            JLabel l = new JLabel();
            l.setBounds(0, 0, 320, 30);
            l.setVisible(true);

            JLabel l2 = new JLabel();
            l2.setBounds(50, 1000, 100, 20);

            l2.setVisible(true);

            jlp.add(l);
            jlp.add(l2);
            jlp.setVisible(true);

            this.add(jlp);
            this.setLocationRelativeTo(null);
            this.setVisible(true);
            System.out.println("Camera OK");
            while (true) {
                bm = webcam.getImage();
                im = new ImageIcon(bm);
                l.setIcon(im);
                double fps = webcam.getFPS();
                l2.setText("FPS: " + fps);

                Model_ImageFPS mifps = new Model_ImageFPS(im, fps);
                compressedOut.writeObject(mifps);
                compressedOut.flush();
//                gzipOutputStream.finish();
                System.out.println("send: " + im);
            }
        } catch (WebcamException | IOException e) {
            e.printStackTrace();
        }
    }

}
