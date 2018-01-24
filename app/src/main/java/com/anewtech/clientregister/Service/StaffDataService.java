package com.anewtech.clientregister.Service;

import android.util.Log;

import com.anewtech.clientregister.Model.HostDataModel;
import com.google.gson.Gson;

/**
 * Created by heriz on 9/1/2018.
 */

public class StaffDataService {
    private Gson g;
    private HostDataModel hmList;

    public StaffDataService(){
        g = new Gson();
    }

    public void setJsonData(String requiredJson){
        hmList = g.fromJson(requiredJson,  HostDataModel.class);
    }

    public HostDataModel getHosts(){
        return hmList;
    }

    public void intoString(){
        Log.e("HostDataModel",hmList.hostModels.toString());
    }
}
