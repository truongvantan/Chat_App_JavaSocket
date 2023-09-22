package com.socket.model;

import java.io.Serializable;

public class Model_Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private boolean action;
    private String message;

    public Model_Message() {
    }

    public Model_Message(boolean action, String message) {
        this.action = action;
        this.message = message;
    }

    public boolean isAction() {
        return action;
    }

    public void setAction(boolean action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
