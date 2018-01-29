package com.anewtech.clientregister.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by heriz on 22/1/2018.
 */

public class VisitorModel {

    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("number")
    private String number;
    @JsonProperty("company")
    private String company;
    @JsonProperty("timenow")
    private String timenow;
    private String imgpath;
    private String position;
    private String address;
    private String ic;
    @JsonProperty("hostid")
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
