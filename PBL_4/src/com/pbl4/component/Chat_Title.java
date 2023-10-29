package com.pbl4.component;

import com.pbl4.service.Service;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.swing.JOptionPane;

public class Chat_Title extends javax.swing.JPanel {

    public Chat_Title() {
        initComponents();
    }

    public void setUserName(String userName) {
        lbName.setText(userName);
    }

    public void statusActive() {
        lbStatus.setText("Active now");
        lbStatus.setForeground(new Color(40, 145, 60));
    }

    public void setStatusText(String text) {
        lbStatus.setText(text);
        lbStatus.setForeground(new Color(160, 160, 160));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        layer = new javax.swing.JLayeredPane();
        lbName = new javax.swing.JLabel();
        lbStatus = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setBackground(new java.awt.Color(230, 230, 230));

        layer.setLayout(new java.awt.GridLayout(0, 1));

        lbName.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbName.setForeground(new java.awt.Color(66, 66, 66));
        lbName.setText("Name");
        layer.add(lbName);

        lbStatus.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lbStatus.setForeground(new java.awt.Color(40, 145, 60));
        lbStatus.setText("Active now");
        layer.add(lbStatus);

        jButton1.setText("video");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("voice");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(layer, javax.swing.GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE)))
                .addGap(3, 3, 3))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        int result = JOptionPane.showConfirmDialog(this,
                "Bạn muốn thực hiện chức năng video call?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                // gửi yêu cầu video call
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream);
                objectOutputStream.writeObject("startvideo");
                objectOutputStream.flush();

                byte[] serializedData = byteStream.toByteArray();
                OutputStream outputStream = Service.getInstance().getClient().getOutputStream();
                outputStream.write(serializedData);
                outputStream.flush();

                // nhận phản hồi start video từ server
                InputStream inputStream = Service.getInstance().getClient().getInputStream();
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                Object obj = objectInputStream.readObject();
                if (obj instanceof String) {
                    String respose = (String) obj;
                    if ("okvideo".equalsIgnoreCase(respose)) {
                        Runnable videoCall = new Video_Call(Service.getInstance().getClient());
                        new Thread(videoCall).start();
                    } else {
                        System.err.println("Không thể nhận phản hồi video call từ server.");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else if (result == JOptionPane.NO_OPTION) {
            System.out.println("Bạn chọn : No");
        } else {
            System.out.println("Bạn ko chọn");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
       int result = JOptionPane.showConfirmDialog(this,
                "Bạn muốn thực hiện chức năng video call?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            try {
                Runnable voiceChat = new Voice_Chat(Service.getInstance().getClient());
                Thread t = new Thread(voiceChat);
                t.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result == JOptionPane.NO_OPTION) {
            System.out.println("Bạn chọn : No");
        } else {
            System.out.println("Bạn ko chọn");
        }
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLayeredPane layer;
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbStatus;
    // End of variables declaration//GEN-END:variables
}
