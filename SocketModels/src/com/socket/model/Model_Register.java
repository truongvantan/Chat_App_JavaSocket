package com.socket.model;

import java.io.Serializable;

public class Model_Register implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;

    public Model_Register() {
    }

    public Model_Register(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Model_Register(Model_Register copyModel_Register) {
        this.userName = copyModel_Register.getUserName();
        this.password = copyModel_Register.getPassword();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Model_Register{" + "userName=" + userName + ", password=" + password + '}' + "\n";
    }

}
