package com.anewtech.clientregister.Model;

/**
 * Created by heriz on 22/1/2018.
 */

public class VisitorModel {

    private String name;
    private String email;
    private String number;
    private String company;

    public VisitorModel(){

    }

    public VisitorModel(ClientInfoModel cm){
        this.name = cm.getName();
        this.email = cm.getEmail();
        this.number = cm.getPhoneNo();
        this.company = cm.getCompanyName();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getCompany() {
        return company;
    }
}
