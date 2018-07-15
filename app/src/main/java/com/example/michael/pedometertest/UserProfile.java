package com.example.michael.pedometertest;

/**
 * Created by Michael on 7/14/2018.
 */

public class UserProfile {
    private String userID;
    private int userFunds;

    public UserProfile(){

    }

    public UserProfile(String userID, int userFunds){
        this.userID = userID;
        this.userFunds = userFunds;
    }

    public String getUserID(){return this.userID;}
    public int getUserFunds(){return this.userFunds;}

    public void setUserFunds(int userFunds){this.userFunds = userFunds;}
}
