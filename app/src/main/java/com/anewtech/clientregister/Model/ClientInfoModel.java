package com.anewtech.clientregister.Model;

import android.arch.lifecycle.ViewModel;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by heriz on 9/1/2018.
 * This model class only allows a single instance of itself
 * Stores client info
 */

public class ClientInfoModel {

    private static ClientInfoModel INSTANCE = null;
    private ClientInfoModel(){}
    public static ClientInfoModel getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ClientInfoModel();
        }
        return(INSTANCE);
    }

    private String name;
    private String email;
    private String phoneNo;
    private String companyName;
    private String staffSeeking;
    private Bitmap photoId;
    private ArrayList<Boolean> isSignedIn = new ArrayList<>(); //index: 0 - registering, 1 - signed in

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStaffSeeking() {
        return staffSeeking;
    }

    public void setStaffSeeking(String staffSeeking) {
        this.staffSeeking = staffSeeking;
    }

    public Bitmap getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Bitmap photoid) {
        this.photoId = photoid;
    }

    public ArrayList<Boolean> isSignedIn() {
        return isSignedIn;
    }

    public void setSignedIn(int index, boolean signedIn) {
        isSignedIn.add(index, signedIn);
    }
}
