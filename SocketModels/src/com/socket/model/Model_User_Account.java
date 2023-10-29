package com.socket.model;

import java.io.Serializable;

public class Model_User_Account implements Serializable {

    private static final long serialVersionUID = 1L;

    private int userID;
    private String userName;
    private String gender;
    private String image;
    private boolean status;

    public Model_User_Account() {
    }

    public Model_User_Account(int userID, String userName, String gender, String image, boolean status) {
        this.userID = userID;
        this.userName = userName;
        this.gender = gender;
        this.image = image;
        this.status = status;
    }

    public Model_User_Account(Model_User_Account copyAccount) {
        this.userID = copyAccount.getUserID();
        this.userName = copyAccount.getUserName();
        this.gender = copyAccount.getGender();
        this.image = copyAccount.getImage();
        this.status = copyAccount.isStatus();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Model_User_Account{" + "userID=" + userID + ", userName=" + userName + ", gender=" + gender + ", image=" + image + ", status=" + status + '}' + "\n";
    }

}
