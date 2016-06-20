package com.example.christina.carna_ui;

/**
 * Created by oguzbinbir on 14.06.16.
 */


public class AngelMemoUser {

    private int userId;
    private String userName;


    public AngelMemoUser(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }


    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getuserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId= userId;
    }


    @Override
    public String toString() {
        String output = userId + " : " + userName;
        return output;
    }
}