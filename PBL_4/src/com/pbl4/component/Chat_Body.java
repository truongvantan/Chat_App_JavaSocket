package com.pbl4.component;

import com.pbl4.swing.ScrollBar;
import java.awt.Adjustable;
import java.awt.Color;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import javax.swing.Icon;
import javax.swing.JScrollBar;
import net.miginfocom.swing.MigLayout;

public class Chat_Body extends javax.swing.JPanel {

    public Chat_Body() {
        initComponents();
        init();
//        addItemRight("I have a JTextArea in Java. When I place a large amount of text in it, the text area provides horizontal scrolling.", new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/cat.png")), new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/dog.jpg")));
//        addItemLeft("hello\nasd\nasd", "Messi", new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/dog.jpg")), new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/dog.jpg")));
//        addItemRight("hello\nasd\nasd", new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/pic.jpg")));
//        addDate("14/09/2023");
//        String[] img = {"LRMj,K-:?G9G_JIon}WqD~ITRPs,", "LsJ8CZVs%goz_No}ozR*-;azRiRj"};
//        addItemLeft("I have a JTextArea in Java. When I place a large amount of text in it, the text area provides horizontal scrolling.", "Ronaldo", new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/dog.jpg")), new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/pic.jpg")));
//        addItemLeft("hello\nasd\nasd", "Halland", img);
//        addDate("Today");
        addItemRight("hello\nasd\nasd");

//        addItemLeft("", "Xavi", new ImageIcon(getClass().getResource("/com/pbl4/icon/testing/pic.jpg")));
//        addItemFile("", "M3P", "hello world.pdf", "2MB");
//        addItemFileRight("" ,"hello.zip", "20MB");
    }

    private void init() {
        body.setLayout(new MigLayout("fillx", "", "5[]5"));
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getVerticalScrollBar().setBackground(Color.WHITE);
    }

    public void addItemLeft(String text, String user, Icon... image) {
        Chat_Left_With_Profile item = new Chat_Left_With_Profile();
        item.setText(text);
        item.setImage(image);
        item.setTime();
        item.setUserProfile(user);

        body.add(item, "wrap, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public void addItemLeft(String text, String user, String[] image) {
        Chat_Left_With_Profile item = new Chat_Left_With_Profile();
        item.setText(text);
        item.setImage(image);
        item.setTime();
        item.setUserProfile(user);

        body.add(item, "wrap, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public void addItemFile(String text, String user, String fileName, String fileSize) {
        Chat_Left_With_Profile item = new Chat_Left_With_Profile();
        item.setText(text);
        item.setFile(fileName, fileSize);
        item.setTime();
        item.setUserProfile(user);

        body.add(item, "wrap, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public void addItemRight(String text, Icon... image) {
        Chat_Right item = new Chat_Right();
        item.setText(text);
        item.setImage(image);
        
        body.add(item, "wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
        
        item.setTime();
        scrollToBottom();
    }

    public void addItemFileRight(String text, String fileName, String fileSize) {
        Chat_Right item = new Chat_Right();
        item.setText(text);
        item.setFile(fileName, fileSize);

        body.add(item, "wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public void addDate(String date) {
        Chat_Date item = new Chat_Date();
        item.setDate(date);
        body.add(item, "wrap, al center");
        body.repaint();
        body.revalidate();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sp = new javax.swing.JScrollPane();
        body = new javax.swing.JPanel();

        setBackground(new java.awt.Color(230, 230, 230));

        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 628, Short.MAX_VALUE)
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );

        sp.setViewportView(body);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sp, javax.swing.GroupLayout.Alignment.TRAILING)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void scrollToBottom() {
        JScrollBar verticalBar = sp.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                Adjustable adjustable = e.getAdjustable();
                adjustable.setValue(adjustable.getMaximum());
                verticalBar.removeAdjustmentListener(this);
            }
        };
        verticalBar.addAdjustmentListener(downScroller);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private javax.swing.JScrollPane sp;
    // End of variables declaration//GEN-END:variables
}
