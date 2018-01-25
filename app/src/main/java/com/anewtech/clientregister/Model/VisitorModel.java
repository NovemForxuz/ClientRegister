package com.anewtech.clientregister.Model;

/**
 * Created by heriz on 22/1/2018.
 */

public class VisitorModel {

    private String name;
    private String email;
    private String number;
    private String company;
    private String timenow;
    private String imgpath;
    private String position;
    private String address;
    private String ic;
    private String hostid;

    public VisitorModel(){

    }

    public VisitorModel(ClientInfoModel cm){
        this.name = cm.getName();
        this.email = cm.getEmail();
        this.number = cm.getPhoneNo();
        this.company = cm.getCompanyName();
        this.timenow = cm.getTimenow();
        this.hostid = cm.getHostId();
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

    public String getTimenow() {
        return timenow;
    }

    public String getHostid() {
        return hostid;
    }

}
