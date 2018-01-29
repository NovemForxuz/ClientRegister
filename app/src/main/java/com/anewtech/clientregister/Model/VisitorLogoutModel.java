package com.anewtech.clientregister.Model;

/**
 * Created by heriz on 29/1/2018.
 */

public class VisitorLogoutModel {

    public String token;
    public String timeOut;

    public VisitorLogoutModel(ClientInfoModel clientInfoModel, String token){
        this.token = token;
        this.timeOut = clientInfoModel.getTimenow();
    }
}
