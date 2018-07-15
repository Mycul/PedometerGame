package com.example.michael.pedometertest;

import android.support.annotation.NonNull;

/**
 * Created by Michael on 7/12/2018.
 */

public class UserModel implements Comparable {
    private String userID;
    private String userName;
    private int userTotalSteps;
    private int userCurrentLevel;
    private int userCurrentProgress;
    private int userMaxSetting;
    private int userCurrentCurrency;
    public UserModel(){
    }
    public UserModel(String userID, String userName, int userTotalSteps, int userCurrentLevel, int userCurrentProgress, int userMaxSetting, int userCurrentCurrency){
        this.userID = userID;
        this.userName = userName;
        this.userTotalSteps = userTotalSteps;
        this.userCurrentLevel = userCurrentLevel;
        this.userCurrentProgress = userCurrentProgress;
        this.userMaxSetting = userMaxSetting;
        this.userCurrentCurrency =userCurrentCurrency;
    }

    public String getUserID(){return userID;}
    public String getUserName(){
        return userName;
    }
    public int getUserTotalSteps(){return userTotalSteps;}
    public int getUserCurrentLevel(){return userCurrentLevel;}
    public int getUserCurrentProgress(){return userCurrentProgress;}
    public int getUserMaxSetting(){return userMaxSetting;}
    public int getUserCurrentCurrency(){return  userCurrentCurrency;}

    public void setUserID(String userID){this.userID = userID;}
    public void setUserName(String userName){this.userName = userName;}
    public void setUserTotalSteps(int userTotalSteps){this.userTotalSteps = userTotalSteps;}
    public void setUserCurrentLevel(int userCurrentLevel){this.userCurrentLevel = userCurrentLevel;}
    public void setUserCurrentProgress(int userCurrentProgress){this.userCurrentProgress = userCurrentProgress;}
    public void setUserMaxSetting(int userMaxSetting){this.userMaxSetting = userMaxSetting;}
    public void setUserCurrentCurrency(int userCurrentCurrency){this.userCurrentCurrency = userCurrentCurrency;}


    @Override
    public int compareTo(@NonNull Object o) {
        int compareSteps =((UserModel) o).getUserTotalSteps();
        return compareSteps - this.userTotalSteps;
    }
}
