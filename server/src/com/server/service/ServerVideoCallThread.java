package com.server.service;

import com.socket.model.Model_ImageFPS;
import com.socket.model.MyObjectInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.zip.GZIPInputStream;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class ServerVideoCallThread extends javax.swing.JFrame implements Runnable {

    private Socket client;
    private JPanel panel = new JPanel();

    //public static JLabel img_server = new JLabel();
    public ServerVideoCallThread(Socket client) {
        initComponents();
        this.client = client;
        this.setTitle("server");

    }

//    @Override
//    public void run() {
//        System.out.println("Video call connected...");
//        try {
//            InputStream rawInputStream = client.getInputStream();
//            ObjectInputStream in = new ObjectInputStream(rawInputStream);
//            // Create a GZIPInputStream to decompress the data
//            GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);
//
//            // Wrap the GZIPInputStream with ObjectInputStream
//            ObjectInputStream decompressedIn = new ObjectInputStream(gzipInputStream);
//
//            this.setSize(320, 300);
//            this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
//
//            JLayeredPane jlp = new JLayeredPane();
//            jlp.setLayout(new BoxLayout(jlp, BoxLayout.Y_AXIS));
//            jlp.setBounds(0, 0, 320, 240);
//
//            JLabel l = new JLabel();
//            l.setBounds(0, 0, 320, 30);
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
//            while (true) {
//                try {
//                    Object compressedData = decompressedIn.readObject();
//                    if (compressedData instanceof Model_ImageFPS) {
//                        Model_ImageFPS mifps = (Model_ImageFPS) compressedData;
//                        l.setIcon(mifps.getIcon());
//                        double FPS = mifps.getFPS();
//                        l2.setText("FPS: " + FPS);
//
//                    }
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                    decompressedIn.close();
//                }
//            }
//
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//    }
    @Override
    public void run() {
        System.out.println("Video call connected...");
        try {            
            InputStream rawInputStream = client.getInputStream();
            ObjectInputStream in = new ObjectInputStream(rawInputStream);
            
            // Create a GZIPInputStream to decompress the data
            GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);

            // Wrap the GZIPInputStream with ObjectInputStream
            ObjectInputStream decompressedIn = new ObjectInputStream(gzipInputStream);

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
            while (true) {
                try {
                    Object compressedData = decompressedIn.readObject();
                    if (compressedData instanceof Model_ImageFPS) {
                        Model_ImageFPS mifps = (Model_ImageFPS) compressedData;
                        l.setIcon(mifps.getIcon());
                        double FPS = mifps.getFPS();
                        l2.setText("FPS: " + FPS);

                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    decompressedIn.close();
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        pack();
    }// </editor-fold>                        

//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://d...content-available-to-author-only...e.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ServerVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ServerVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ServerVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ServerVideoCall.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new ServerVideoCall().setVisible(true);
//            }
//
//        });
//    }
    // Variables declaration - do not modify                     
    // End of variables declaration                   
}
