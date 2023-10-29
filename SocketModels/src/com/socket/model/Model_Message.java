package com.socket.model;

import java.io.Serializable;

public class Model_Message implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private boolean action;
    private String message;
    private Object data;

    public Model_Message() {
    }

    public Model_Message(boolean action, String message, Object data) {
        this.action = action;
        this.message = message;
        this.data = data;
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Model_Message{" + "action=" + action + ", message=" + message + ", data=" + data + '}' + "\n";
    }
    
    
}
