package com.anewtech.clientregister.Model;

import java.util.List;

/**
 * Database/visitor template
 */

public class HostModel {
    public String address;
    public String company;
    public String email;
    public String hp;
    public String ic;
    public String id;
    public String imgpath;
    public String name;
    public String position;

    public HostModel(){

    }

    @Override
    public String toString()
    {
        return "HostModel [position = "+position+", id = "+id+", ic = "+ic+", hp = "+hp+", imgpath = "+imgpath+", email = "+email+", address = "+address+", name = "+name+", company = "+company+"]";
    }
}
