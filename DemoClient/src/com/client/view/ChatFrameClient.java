package com.client.view;

import com.client.customswing.ChatLeft;
import com.client.customswing.ChatRight;
import com.client.customswing.ComponentResizer;
import com.client.customswing.JIMSendTextPane;
import com.client.customswing.ScrollBar;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class ChatFrameClient extends javax.swing.JFrame {

    private String username;
    private DataInputStream dis;
    private DataOutputStream dos;

    private ChatLeft chatLeftItem;
    private ChatRight chatRightItem;

    private HashMap<String, JPanel> chatWindows = new HashMap<String, JPanel>();

    Thread receiver;
    Thread sendVoice;
    Thread sendVideo;
    Thread sendStop;

    JFrame jframe;
    JButton btnStop;

    AudioFormat af1, af2;
    DataLine.Info info1, info2;
    TargetDataLine microphone;
    SourceDataLine inSpeaker;

    Webcam wCam;

    public ChatFrameClient(String username, DataInputStream dis, DataOutputStream dos) {
        initComponents();
        init();

        this.dis = dis;
        this.dos = dos;
        this.username = username;
        lbUsername.setText(username);
        lbReceiver.setText(" ");

        receiver = new Thread(new Receiver(dis));
        receiver.start();

        sendVoice = new Thread(new SendVoice(dos));
        sendVideo = new Thread(new SendVideo(dos));
        sendStop = new Thread(new SendStop(dos));

        jframe = new JFrame();
        btnStop = new JButton("Stop");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private void init() {
        this.setTitle("CHAT APP");
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setIconImage(new ImageIcon(getClass().getResource("/com/client/icon/icon.png")).getImage());
        ComponentResizer com = new ComponentResizer();
        com.registerComponent(this);
        com.setMinimumSize(new Dimension(900, 500));
        com.setMaximumSize(Toolkit.getDefaultToolkit().getScreenSize());
        com.setSnapSize(new Dimension(10, 10));

        settingPanelBottom();
        settingPanelBody();
        settingPanelEmoji();
        this.setDefaultLookAndFeelDecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void settingPanelBottom() {
        panelBottom.setLayout(new MigLayout("fillx, filly", "0[fill]0[]0[]2", "2[fill]2"));
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);

        JIMSendTextPane txtMessage = new JIMSendTextPane();
        txtMessage.setBorder(new EmptyBorder(5, 5, 5, 5));
        txtMessage.setHintText("Write message here...");
        txtMessage.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        scroll.setViewportView(txtMessage);

        ScrollBar sb = new ScrollBar();
        sb.setBackground(new Color(230, 230, 230));
        sb.setPreferredSize(new Dimension(2, 10));
        scroll.setVerticalScrollBar(sb);

        panelBottom.add(sb);
        panelBottom.add(scroll, "w 100%");

        JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("filly", "0[]0", "0[bottom]0"));
        panel.setPreferredSize(new Dimension(30, 30));
        panel.setBackground(Color.WHITE);

        JButton btnSend = new JButton();
        btnSend.setEnabled(false);
        btnSend.setBorder(null);
        btnSend.setContentAreaFilled(false);
        btnSend.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSend.setIcon(new ImageIcon(getClass().getResource("/com/client/icon/send.png")));

        JButton btnFile = new JButton();
//        btnFile.setEnabled(false);
        btnFile.setBorder(null);
        btnFile.setContentAreaFilled(false);
        btnFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFile.setIcon(new ImageIcon(getClass().getResource("/com/client/icon/link.png")));

        panel.add(btnFile);
        panel.add(btnSend);

        panelBottom.add(panel);

        // Thay đổi cửa sổ chat
        chatWindows.put(" ", new JPanel());
        body = chatWindows.get(" ");

        // Xử lí sự kiện cho các button
        txtMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                refresh();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (txtMessage.getText().isBlank() || lbReceiver.getText().isBlank()) {
                    btnSend.setEnabled(false);
                } else {
                    btnSend.setEnabled(true);
                }
            }
        });

        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = txtMessage.getText();

                try {
                    dos.writeUTF("Text");
                    dos.writeUTF(lbReceiver.getText());
                    dos.writeUTF(txtMessage.getText());
                    dos.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    addItemRight("Network error!");
                }
                addItemRight(text);
            }

        });

        btnFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiển thị hộp thoại cho người dùng chọn file để gửi
                JFileChooser fileChooser = new JFileChooser();
                int rVal = fileChooser.showOpenDialog(body.getParent());
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    byte[] selectedFile = new byte[(int) file.length()];
                    BufferedInputStream bis;

                    try {
                        bis = new BufferedInputStream(new FileInputStream(fileChooser.getSelectedFile()));
                        // Đọc dữ liệu buffer
                        bis.read(selectedFile, 0, selectedFile.length);
                        dos.writeUTF("File");
                        dos.writeUTF(lbReceiver.getText());
                        dos.writeUTF(fileChooser.getSelectedFile().getName());
                        dos.writeUTF(String.valueOf(selectedFile.length));

                        System.out.println("Send: " + fileChooser.getSelectedFile().getName() + "," + selectedFile.length);

                        int size = selectedFile.length;
                        int bufferSize = 2048;
                        int offset = 0;

                        // Lần lượt gửi cho server từng buffer đã đọc
                        while (size > 0) {
                            dos.write(selectedFile, offset, Math.min(size, bufferSize));
                            offset += Math.min(size, bufferSize);
                            size -= bufferSize;
                        }
                        dos.flush();

                        bis.close();
                        // In ra màn hình file
                        addItemRightFile("", username, fileChooser.getSelectedFile().getName(), selectedFile, getFileSizeMB(file));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

        });

        btnVoice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiển thị hộp thoại cho người dùng xác nhận
                int result = JOptionPane.showConfirmDialog(body,
                        "Do you want to call " + lbReceiver.getText() + "?",
                        "Confirm voice call",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        // Gửi yêu cầu video call đến server
                        dos.writeUTF("Voice chat");
                        dos.writeUTF(lbReceiver.getText());
                        dos.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnVideo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Hiển thị hộp thoại cho người dùng xác nhận
                int result = JOptionPane.showConfirmDialog(body,
                        "Do you want to make a video call to " + lbReceiver.getText() + "?",
                        "Confirm Video call",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    try {
                        // Gửi yêu cầu video call đến server
                        dos.writeUTF("Video call");
                        dos.writeUTF(lbReceiver.getText());
                        dos.flush();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnVoice.setEnabled(false);
        btnVideo.setEnabled(false);

        // Thay đổi cửa sổ chat khi combobox user online thay đổi
        onlineUsers.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    lbReceiver.setText((String) onlineUsers.getSelectedItem());
                    if (body != chatWindows.get(lbReceiver.getText())) {
                        txtMessage.setText("");
                        body = chatWindows.get(lbReceiver.getText());
//                        body.removeAll();
                        body.repaint();
                        body.revalidate();
                        sp.setViewportView(body);

                    }

                    if (lbReceiver.getText().isBlank()) {
                        btnSend.setEnabled(false);
                        btnFile.setEnabled(false);
                        btnVideo.setEnabled(false);
                        btnVoice.setEnabled(false);
                        txtMessage.setEnabled(false);
                        emojis.setVisible(false);
                    } else {
                        btnSend.setEnabled(true);
                        btnFile.setEnabled(true);
                        btnVideo.setEnabled(true);
                        btnVoice.setEnabled(true);
                        txtMessage.setEnabled(true);
                        emojis.setVisible(true);
                    }
                }

            }
        });

        this.getRootPane().setDefaultButton(btnSend);

        // Nếu client đóng cửa sổ chat thì sẽ ngắt luồng giao tiếp với server và cập nhật lại danh sách user online ở các client khác.
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {

                try {
                    dos.writeUTF("Log out");
                    dos.flush();

                    try {
                        receiver.join();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                    if (dos != null) {
                        dos.close();
                    }
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void settingPanelEmoji() {
        emojis.setLayout(new FlowLayout(FlowLayout.LEFT));
        emojis.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel smileIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/smile.png")));
        smileIcon.addMouseListener(new IconListener(smileIcon.getIcon().toString().substring(49)));
        emojis.add(smileIcon);

        JLabel bigSmileIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/big-smile.png")));
        bigSmileIcon.addMouseListener(new IconListener(bigSmileIcon.getIcon().toString().substring(49)));
        emojis.add(bigSmileIcon);

        JLabel happyIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/happy.png")));
        happyIcon.addMouseListener(new IconListener(happyIcon.getIcon().toString().substring(49)));
        emojis.add(happyIcon);

        JLabel loveIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/love.png")));
        loveIcon.addMouseListener(new IconListener(loveIcon.getIcon().toString().substring(49)));
        emojis.add(loveIcon);

        JLabel sadIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/sad.png")));
        sadIcon.addMouseListener(new IconListener(sadIcon.getIcon().toString().substring(49)));
        emojis.add(sadIcon);

        JLabel madIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/mad.png")));
        madIcon.addMouseListener(new IconListener(madIcon.getIcon().toString().substring(49)));
        emojis.add(madIcon);

        JLabel suspiciousIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/suspicious.png")));
        suspiciousIcon.addMouseListener(new IconListener(suspiciousIcon.getIcon().toString().substring(49)));
        emojis.add(suspiciousIcon);

        JLabel angryIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/angry.png")));
        angryIcon.addMouseListener(new IconListener(angryIcon.getIcon().toString().substring(49)));
        emojis.add(angryIcon);

        JLabel confusedIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/confused.png")));
        confusedIcon.addMouseListener(new IconListener(confusedIcon.getIcon().toString().substring(49)));
        emojis.add(confusedIcon);

        JLabel unhappyIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/unhappy.png")));
        unhappyIcon.addMouseListener(new IconListener(unhappyIcon.getIcon().toString().substring(49)));
        emojis.add(unhappyIcon);

        JLabel appleIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/apple.png")));
        appleIcon.addMouseListener(new IconListener(appleIcon.getIcon().toString().substring(49)));
        emojis.add(appleIcon);

        JLabel orangeIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/orange.png")));
        orangeIcon.addMouseListener(new IconListener(orangeIcon.getIcon().toString().substring(49)));
        emojis.add(orangeIcon);

        JLabel cherryIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/cherry.png")));
        cherryIcon.addMouseListener(new IconListener(cherryIcon.getIcon().toString().substring(49)));
        emojis.add(cherryIcon);

        JLabel cakeIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/cake.png")));
        cakeIcon.addMouseListener(new IconListener(cakeIcon.getIcon().toString().substring(49)));
        emojis.add(cakeIcon);

        JLabel vietnamIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/vietnam.png")));
        vietnamIcon.addMouseListener(new IconListener(vietnamIcon.getIcon().toString().substring(49)));
        emojis.add(vietnamIcon);

        JLabel usIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/us.png")));
        usIcon.addMouseListener(new IconListener(usIcon.getIcon().toString().substring(49)));
        emojis.add(usIcon);

        JLabel ukIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/uk.png")));
        ukIcon.addMouseListener(new IconListener(ukIcon.getIcon().toString().substring(49)));
        emojis.add(ukIcon);

        JLabel canadaIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/canada.png")));
        canadaIcon.addMouseListener(new IconListener(canadaIcon.getIcon().toString().substring(49)));
        emojis.add(canadaIcon);

        JLabel italyIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/italy.png")));
        italyIcon.addMouseListener(new IconListener(italyIcon.getIcon().toString().substring(49)));
        emojis.add(italyIcon);

        JLabel spainIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/spain.png")));
        spainIcon.addMouseListener(new IconListener(spainIcon.getIcon().toString().substring(49)));
        emojis.add(spainIcon);

        JLabel egyptIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/egypt.png")));
        egyptIcon.addMouseListener(new IconListener(egyptIcon.getIcon().toString().substring(49)));
        emojis.add(egyptIcon);

        JLabel swedenIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/sweden.png")));
        swedenIcon.addMouseListener(new IconListener(swedenIcon.getIcon().toString().substring(49)));
        emojis.add(swedenIcon);

        JLabel australiaIcon = new JLabel(new ImageIcon(getClass().getResource("/com/client/icon/emoji/australia.png")));
        australiaIcon.addMouseListener(new IconListener(australiaIcon.getIcon().toString().substring(49)));
        emojis.add(australiaIcon);
    }

    private void refresh() {
        revalidate();
    }

    private void settingPanelBody() {
        body.setLayout(new MigLayout("fillx", "", "5[]5"));
        sp.setVerticalScrollBar(new ScrollBar());
        sp.getVerticalScrollBar().setBackground(Color.WHITE);
    }

    // hiển thị tin nhắn từ client đang nhắn tin.
    public void addItemLeft(String text, String user, Icon... image) {
        chatLeftItem = new ChatLeft();
        chatLeftItem.setText(text);
        chatLeftItem.setImage(image);
        chatLeftItem.setTime();

        body.add(chatLeftItem, "wrap, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public void addItemLeftFile(String text, String user, String fileName, byte[] file, String fileSize) {
        chatLeftItem = new ChatLeft();
        chatLeftItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatLeftItem.setText(text);
        chatLeftItem.setFile(fileName, fileSize);
        chatLeftItem.setTime();
        chatLeftItem.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                HyperlinkListener hyperlinkListener = new HyperlinkListener(fileName, file);
                hyperlinkListener.execute();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        body.add(chatLeftItem, "wrap, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    // hiển thị tin nhắn từ người gửi.
    public void addItemRight(String text, Icon... image) {
        chatRightItem = new ChatRight();
        chatRightItem.setText(text);
        chatRightItem.setImage(image);
        chatRightItem.setTime();

        body.add(chatRightItem, "wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();

        scrollToBottom();
    }

    public void addItemRightFile(String text, String user, String fileName, byte[] file, String fileSize) {
        chatRightItem = new ChatRight();
        chatRightItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chatRightItem.setText(text);
        chatRightItem.setFile(fileName, fileSize);
        chatRightItem.setTime();

        chatRightItem.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                HyperlinkListener hyperlinkListener = new HyperlinkListener(fileName, file);
                hyperlinkListener.execute();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        body.add(chatRightItem, "wrap, al right, w 100::80%");
        body.repaint();
        body.revalidate();
    }

    public String getFileSizeMB(File file) {
        double fileSize = (double) file.length() / (1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String formattedFileSize = decimalFormat.format(fileSize);
        return formattedFileSize + "Mb";
    }

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

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelLeft = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lbUsername = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        onlineUsers = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        panelRight = new javax.swing.JPanel();
        panelHeader = new javax.swing.JPanel();
        lbReceiver = new javax.swing.JLabel();
        btnVoice = new javax.swing.JButton();
        btnVideo = new javax.swing.JButton();
        sp = new javax.swing.JScrollPane();
        body = new javax.swing.JPanel();
        emojis = new javax.swing.JPanel();
        panelBottom = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        panelLeft.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/client/icon/user.png"))); // NOI18N

        lbUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbUsername.setText("Username");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("ONLINE USERS");

        onlineUsers.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/client/icon/logo_theme.png"))); // NOI18N

        javax.swing.GroupLayout panelLeftLayout = new javax.swing.GroupLayout(panelLeft);
        panelLeft.setLayout(panelLeftLayout);
        panelLeftLayout.setHorizontalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelLeftLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                    .addComponent(onlineUsers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelLeftLayout.setVerticalGroup(
            panelLeftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLeftLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbUsername)
                .addGap(62, 62, 62)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(onlineUsers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelHeader.setBackground(new java.awt.Color(191, 220, 241));

        lbReceiver.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbReceiver.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbReceiver.setText("Receiver");

        btnVoice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/client/icon/iconCall.png"))); // NOI18N

        btnVideo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/client/icon/iconVideo.png"))); // NOI18N

        javax.swing.GroupLayout panelHeaderLayout = new javax.swing.GroupLayout(panelHeader);
        panelHeader.setLayout(panelHeaderLayout);
        panelHeaderLayout.setHorizontalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbReceiver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVoice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnVideo))
        );
        panelHeaderLayout.setVerticalGroup(
            panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnVoice)
                    .addComponent(btnVideo)
                    .addComponent(lbReceiver))
                .addContainerGap())
        );

        sp.setBackground(new java.awt.Color(255, 255, 255));

        body.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout bodyLayout = new javax.swing.GroupLayout(body);
        body.setLayout(bodyLayout);
        bodyLayout.setHorizontalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 682, Short.MAX_VALUE)
        );
        bodyLayout.setVerticalGroup(
            bodyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 362, Short.MAX_VALUE)
        );

        sp.setViewportView(body);

        emojis.setBackground(new java.awt.Color(230, 230, 247));

        javax.swing.GroupLayout emojisLayout = new javax.swing.GroupLayout(emojis);
        emojis.setLayout(emojisLayout);
        emojisLayout.setHorizontalGroup(
            emojisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        emojisLayout.setVerticalGroup(
            emojisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 29, Short.MAX_VALUE)
        );

        panelBottom.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout panelBottomLayout = new javax.swing.GroupLayout(panelBottom);
        panelBottom.setLayout(panelBottomLayout);
        panelBottomLayout.setHorizontalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelBottomLayout.setVerticalGroup(
            panelBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelRightLayout = new javax.swing.GroupLayout(panelRight);
        panelRight.setLayout(panelRightLayout);
        panelRightLayout.setHorizontalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sp)
            .addComponent(emojis, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelBottom, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelRightLayout.setVerticalGroup(
            panelRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRightLayout.createSequentialGroup()
                .addComponent(panelHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(sp, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(emojis, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panelLeft, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLeft, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel body;
    private javax.swing.JButton btnVideo;
    private javax.swing.JButton btnVoice;
    private javax.swing.JPanel emojis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lbReceiver;
    private javax.swing.JLabel lbUsername;
    private javax.swing.JComboBox<String> onlineUsers;
    private javax.swing.JPanel panelBottom;
    private javax.swing.JPanel panelHeader;
    private javax.swing.JPanel panelLeft;
    private javax.swing.JPanel panelRight;
    private javax.swing.JScrollPane sp;
    // End of variables declaration//GEN-END:variables

    // Luồng nhận tin nhắn từ server của mỗi client
    class Receiver implements Runnable {

        private DataInputStream dis;

        public Receiver(DataInputStream dis) {
            this.dis = dis;
        }

        public String getFileSizeMB(int fileLength) {
            double fileSize = (double) fileLength / (1024 * 1024);
            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            String formattedFileSize = decimalFormat.format(fileSize);
            return formattedFileSize + "Mb";
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Chờ phản hồi từ server
                    String method = dis.readUTF();
                    System.out.println("Server responded: " + method);

                    // Nhận một tin nhắn văn bản
                    if ("Text".equalsIgnoreCase(method)) {
                        String sender = dis.readUTF();
                        String message = dis.readUTF();
                        System.out.println("Server responded: " + sender + "," + message);
                        // In tin nhắn lên màn hình chat
                        addItemLeft(message, sender);
                    } // Nhận một tin nhắn Emoji
                    else if ("Emoji".equalsIgnoreCase(method)) {
                        String sender = dis.readUTF();
                        String message = dis.readUTF();
                        System.out.println("Server responded: " + sender + "," + message);
                        // In tin nhắn lên màn hình chat
                        ImageIcon icon = new ImageIcon(getClass().getResource(message));
                        addItemLeft("", sender, icon);
                        System.out.println(icon.getIconWidth() + ", " + icon.getIconHeight());
                    } // Nhận một file
                    else if ("File".equalsIgnoreCase(method)) {
                        String sender = dis.readUTF();
                        String filename = dis.readUTF();
                        int size = Integer.parseInt(dis.readUTF());
                        System.out.println("Server responded: " + sender + "," + filename + "," + size);

                        int bufferSize = 2048;
                        byte[] buffer = new byte[bufferSize];
                        ByteArrayOutputStream file = new ByteArrayOutputStream();

                        while (size > 0) {
                            dis.read(buffer, 0, Math.min(bufferSize, size));
                            file.write(buffer, 0, Math.min(bufferSize, size));
                            size -= bufferSize;
                        }

                        // In ra màn hình file vừa nhận
                        addItemLeftFile(sender, sender, filename, file.toByteArray(), String.valueOf(getFileSizeMB(file.toByteArray().length)));

                    } // Nhận yêu cầu cập nhật danh sách người dùng trực tuyến
                    else if ("Online users".equalsIgnoreCase(method)) {
                        String[] users = dis.readUTF().split(",");
                        onlineUsers.removeAllItems();
                        String chatting = lbReceiver.getText();
                        boolean isChattingOnline = false;

                        for (String user : users) {
                            // Cập nhật danh sách các người dùng trực tuyến vào ComboBox, trừ bản thân người gửi
                            if (!user.equals(username)) {
                                onlineUsers.addItem(user);
                                if (chatWindows.get(user) == null) {
                                    JPanel temp = new JPanel();
                                    temp.setLayout(new MigLayout("fillx", "", "5[]5"));
                                    sp.setVerticalScrollBar(new ScrollBar());
                                    sp.getVerticalScrollBar().setBackground(Color.WHITE);
                                    chatWindows.put(user, temp);

                                }
                            }
                            if (chatting.equals(user)) {
                                isChattingOnline = true;
                            }
                        }

                        /**
                         * Nếu người đang chat không online thì chuyển hướng về màn hình mặc định và thông báo cho người dùng
                         */
                        if (!isChattingOnline) {
                            onlineUsers.setSelectedItem(" ");
                            JOptionPane.showMessageDialog(null, chatting + " is offline!\nYou will be redirect to default chat window");
                        } else {
                            onlineUsers.setSelectedItem(chatting);
                        }

                        onlineUsers.validate();
                    } // Thông báo voice chat
                    else if ("Voice chat".equalsIgnoreCase(method)) {
                        // nhận tên người gửi
                        String sender = dis.readUTF();
                        System.out.println("Server responded: " + sender + " is waiting voice calling ");

                        int result = JOptionPane.showConfirmDialog(body,
                                sender + " wants to do voice chat with you?",
                                "Confirm",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {
                            sendVoice.start();
                            af1 = new AudioFormat(8000.0f, 8, 1, true, false);
                            info1 = new DataLine.Info(SourceDataLine.class, af1);
                            inSpeaker = (SourceDataLine) AudioSystem.getLine(info1);
                            inSpeaker.open(af1);

                            int bytesRead = 0;
                            byte[] inSound = new byte[1];
                            inSpeaker.start();
                            jframe.setTitle("Voice call with " + lbReceiver.getText());
                            jframe.setSize(400, 400);
                            jframe.setLocationRelativeTo(null);
                            btnStop.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    try {
                                        sendStop.start();
                                        if (microphone != null) {
                                            microphone.stop();
                                            microphone.close();
                                        }
                                        if (inSpeaker != null) {
                                            inSpeaker.stop();
                                            inSpeaker.close();
                                        }
                                        if (jframe.isVisible()) {
                                            jframe.dispose();
                                        }
                                        if (sendVideo.isAlive()) {
                                            sendVideo.interrupt();
                                        }
                                        if (sendVoice.isAlive()) {
                                            sendVoice.interrupt();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                            jframe.add(btnStop);
                            jframe.setVisible(true);
                            while (bytesRead != -1) {
                                try {
                                    bytesRead = dis.read(inSound, 0, inSound.length);
                                    inSpeaker.write(inSound, 0, bytesRead);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
//                            sendVoice.interrupt();
                        }

                    } // Thông báo video call
                    else if ("Video call".equalsIgnoreCase(method)) {
                        // nhận tên người gửi
                        String sender = dis.readUTF();
                        System.out.println("Server responded: " + sender + " is waiting video call.");

                        int result = JOptionPane.showConfirmDialog(body,
                                sender + " wants to do video call with you?",
                                "Confirm",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);
                        if (result == JOptionPane.YES_OPTION) {

                            sendVideo.start();
                            // Hiển thị hình ảnh
                            jframe.setTitle("Video call with " + lbReceiver.getText());
                            jframe.setSize(640, 480);
                            jframe.setLocationRelativeTo(null);
                            jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                            jframe.setLayout(new BorderLayout());
                            ImageDisplayPanel imageDisplayPanel = new ImageDisplayPanel();
                            jframe.add(imageDisplayPanel, BorderLayout.CENTER);

                            btnStop.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    try {
                                        sendStop.start();
                                        if (wCam != null) {
                                            wCam.close();
                                            System.err.println("Web cam is closed.");
                                        }
                                        if (jframe.isVisible()) {
                                            jframe.dispose();
                                        }
                                        if (sendVideo.isAlive()) {
                                            sendVideo.interrupt();
                                        }
                                        if (sendVoice.isAlive()) {
                                            sendVoice.interrupt();
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                            jframe.add(btnStop, BorderLayout.SOUTH);
                            jframe.setVisible(true);
                            int byteRead = 1;
                            while (byteRead != 0) {
                                byte[] bytes = dis.readAllBytes();
                                byteRead = bytes.length;
                                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                                BufferedImage frame = ImageIO.read(bis);
                              

                                imageDisplayPanel.setBackground(frame);
                            }
                        } else {
//                            sendVideo.interrupt();
                        }
                    } // Thông báo stop voice chat/videocall
                    else if ("Stop".equalsIgnoreCase(method)) {
                        String sender = dis.readUTF();
                        System.out.println("Server responded: " + sender + " stop call.");
                        if (microphone != null) {
                            microphone.stop();
                            microphone.close();
                        }
                        if (inSpeaker != null) {
                            inSpeaker.stop();
                            inSpeaker.close();
                        }
                        if (jframe.isVisible()) {
                            jframe.dispose();
                        }
                        if (sendVideo.isAlive()) {
                            sendVideo.interrupt();
                        }
                        if (sendVoice.isAlive()) {
                            sendVoice.interrupt();
                        }
                    } // Thông báo có thể thoát
                    else if ("Safe to leave".equalsIgnoreCase(method)) {
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (dis != null) {
                        dis.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public int[] convert(byte[] input) {
            int[] ret = new int[input.length];
            for (int i = 0; i < input.length; i++) {
                ret[i] = input[i] & 0xff; // Range 0 to 255, not -128 to 127
            }
            return ret;
        }

    }

    class HyperlinkListener extends AbstractAction {

        String filename;
        byte[] file;

        public HyperlinkListener(String filename, byte[] file) {
            this.filename = filename;
            this.file = Arrays.copyOf(file, file.length);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            execute();
        }

        public void execute() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(filename));
            int rVal = fileChooser.showSaveDialog(body.getParent());
            if (rVal == JFileChooser.APPROVE_OPTION) {

                // Mở file đã chọn sau đó lưu thông tin xuống file đó
                File saveFile = fileChooser.getSelectedFile();
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(saveFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // Hiển thị JOptionPane cho người dùng có muốn mở file vừa tải về không
                int nextAction = JOptionPane.showConfirmDialog(null,
                        "Saved file to " + saveFile.getAbsolutePath() + "\nDo you want to open this file?",
                        "Successful", JOptionPane.YES_NO_OPTION);
                if (nextAction == JOptionPane.YES_OPTION) {
                    try {
                        Desktop.getDesktop().open(saveFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (bos != null) {
                    try {
                        bos.write(this.file);
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    class IconListener extends MouseAdapter {

        String emoji;

        public IconListener(String emoji) {
            this.emoji = emoji;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                dos.writeUTF("Emoji");
                dos.writeUTF(lbReceiver.getText());
                dos.writeUTF(this.emoji);
                dos.flush();
            } catch (IOException e1) {
                addItemRight("Network error!");
                e1.printStackTrace();
            }
            ImageIcon icon = new ImageIcon(getClass().getResource(this.emoji));
            // In Emoji lên màn hình chat với người nhận
            addItemRight("", icon);
        }
    }

    class SendVoice implements Runnable {

        private DataOutputStream dos;

        public SendVoice(DataOutputStream dos) {
            this.dos = dos;
        }

        @Override
        public void run() {
            try {
                // Gửi yêu cầu voice chat đến server
                dos.writeUTF("Voice chat");
                dos.writeUTF(lbReceiver.getText());

                af2 = new AudioFormat(8000.0f, 8, 1, true, false);
                info2 = new DataLine.Info(TargetDataLine.class, af2);
                microphone = (TargetDataLine) AudioSystem.getLine(info2);
                microphone.open(af2);
                microphone.start();
                int bytesRead = 0;
                byte[] soundData = new byte[1];
                while (bytesRead != -1) {
                    bytesRead = microphone.read(soundData, 0, soundData.length);
                    if (bytesRead >= 0) {
                        dos.write(soundData, 0, bytesRead);
                    }
                }
                dos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class SendVideo implements Runnable {

        private DataOutputStream dos;

        public SendVideo(DataOutputStream dos) {
            this.dos = dos;
        }

        @Override
        public void run() {

            try {
                // Gửi yêu cầu video call đến server
                dos.writeUTF("Video call");
                dos.writeUTF(lbReceiver.getText());

                // Mở webcam
                wCam = Webcam.getDefault();
                wCam.setViewSize(WebcamResolution.VGA.getSize());
                wCam.open();
                int byteReads = 1;

                // gửi hình ảnh từ webcam lên server
                while (byteReads != 0) {
                    BufferedImage frame = wCam.getImage(); // nhận hình ảnh từ webcam
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(frame, "jpg", baos);
                    byte[] bytes = baos.toByteArray();
                    byteReads = bytes.length;

                    dos.write(bytes, 0, bytes.length);
                    dos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (wCam != null) {
                    wCam.close();
                    System.err.println("Web cam is closed.");
                }
            }
        }

    }

    // Vẽ các pixel ảnh lên JPanel
    class ImageDisplayPanel extends JPanel {

        private final Object BACKGROUND_LOCK = new Object();
        private BufferedImage background = null;

        public ImageDisplayPanel() throws HeadlessException {
            this.setDoubleBuffered(true); // tránh tình trạng nhấp nháy hình ảnh
        }

        public void setBackground(Image newBackground) {
            synchronized (BACKGROUND_LOCK) {
                if (background == null) {
                    background = new BufferedImage(newBackground.getWidth(null), newBackground.getHeight(null), BufferedImage.TYPE_INT_RGB);
                } else if (background.getWidth() != newBackground.getWidth(null) || background.getHeight() != newBackground.getHeight(null)) {
                    background.flush();
                    background = new BufferedImage(newBackground.getWidth(null), newBackground.getHeight(null), BufferedImage.TYPE_INT_RGB);
                }
                Graphics graphics = background.createGraphics();
                graphics.drawImage(newBackground, 0, 0, null);
            }
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            synchronized (BACKGROUND_LOCK) {
                if (background != null) {
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
                }
            }
        }
    }

    class SendStop implements Runnable {

        private DataOutputStream dos;

        public SendStop(DataOutputStream dos) {
            this.dos = dos;
        }

        @Override
        public void run() {
            try {
                dos.writeUTF("Stop");
                dos.writeUTF(lbReceiver.getText());
                dos.flush();
                System.out.println(username + "stop voice.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
