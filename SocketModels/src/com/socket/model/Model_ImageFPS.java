package com.socket.model;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Model_ImageFPS implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ImageIcon icon;
    private Double FPS;

    public Model_ImageFPS() {
    }

    public Model_ImageFPS(ImageIcon icon, Double FPS) {
        this.icon = icon;
        this.FPS = FPS;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public Double getFPS() {
        return FPS;
    }

    public void setFPS(Double FPS) {
        this.FPS = FPS;
    }

    @Override
    public String toString() {
        return "Model_ImageFPS{" + "icon=" + icon + ", FPS=" + FPS + '}';
    }
    
    
}
