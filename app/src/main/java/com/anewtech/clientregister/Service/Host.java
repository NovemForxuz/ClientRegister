package com.anewtech.clientregister.Service;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.anewtech.clientregister.Adapter.CustomViewAdapter;
import com.anewtech.clientregister.Model.HostDataModel;
import com.anewtech.clientregister.Model.HostModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by heriz on 17/1/2018.
 * This class retrieve data from server
 */

public class Host implements Runnable {

    private final boolean LOG_ON_HOST = false;
    FirebaseFirestore mRef;

    HostDataModel details;
    HostModel vModel;
    File hoster;
    boolean isReady;

    CustomViewAdapter cva = CustomViewAdapter.getInstance();

    public Host(FirebaseFirestore reference, File file){
        mRef = reference;
        hoster = file;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        toLog("Thread started");
        while(!Thread.currentThread().isInterrupted()){
            try{
                //Do something...
                details = new HostDataModel();
                mRef.collection("hosts")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    //Save to file as json
                                    int size = task.getResult().size();
                                    for(int i=0; i < size; i++){
                                        DocumentSnapshot document = task.getResult().getDocuments().get(i);
                                        vModel = document.toObject(HostModel.class);
//                                        String data = document.getData().toString();
//                                        toLog(data);
//                                        vModel = new HostModel();
//                                        vModel.name = getName(data);
//                                        vModel.imgpath = getPhotoUrl(data);
////                                        vModel.address = getAddress(data);
//                                        vModel.companyid = getCompanyId(data);
//                                        vModel.pemail = getEmail(data);
//                                        vModel.hp = getHp(data);
//                                        vModel.ic = getIc(data);
//                                        vModel.id = getId(data);
//                                        vModel.position= getPosition(data);

                                        details.hostModels.add(vModel);
                                        toLog("name: "+vModel.name);

                                        isReady = true;

                                        ObjectMapper objMapper = new ObjectMapper();
                                        if(i == size-1){

                                            listObservable(details.hostModels)
                                                    .subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe(listObserver());

                                            try {
                                                objMapper.writeValue(hoster, details);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                    }
                                } else {
                                    toLog( "Error getting documents. "+task.getException());
                                }
                            }
                        });

                Thread.sleep(5000);
                stop();
            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }

//        for(HostModel vm : cva.hostdetails){
//            toLog(vm.name);
//        }
        toLog("Thread interrupted");
    }

    public void stop(){
        Thread.currentThread().interrupt();
    }

    private Observable<List<HostModel>> listObservable(List<HostModel> list){
        return Observable.just(list);
    }

    private Observer<List<HostModel>> listObserver(){
        return new Observer<List<HostModel>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<HostModel> hostModels) {
                cva.initialize(hostModels);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    //TODO: create methods to get all elements in HostModel

    private String getName(String data){
        int btm = data.indexOf("name=");
        int upp = data.indexOf(", ic");
        String name = data.substring(btm+5, upp);
        toLog("name: "+name);
        return name;
    }

    private String getPhotoUrl(String data){
        int btm = data.indexOf("imgpath=");
        int upp = data.indexOf(", companyid");
        String photolink = data.substring(btm+8, upp);
        toLog("photo url: "+photolink);
        return photolink;
    }

//    private String getAddress(String data){
//        int btm = data.indexOf("address=");
//        int upp = data.indexOf(", id");
//        String address = data.substring(btm+8, upp);
//        return address;
//    }

    private String getCompanyId(String data){
        int btm = data.indexOf("companyid=");
        int upp = data.indexOf(", hp");
        String company = data.substring(btm+10, upp);
        return company;
    }

    private String getEmail(String data){
        int btm = data.indexOf("pemail=");
        int upp = data.indexOf(", name");
        String email = data.substring(btm+7, upp);
        return email;
    }

    private String getHp(String data){
        int btm = data.indexOf("hp=");
        int upp = data.indexOf("}");
        String hp = data.substring(btm+3, upp);
        return hp;
    }

    private String getIc(String data){
        int btm = data.indexOf("ic=");
        int upp = data.indexOf(", position");
        String ic = data.substring(btm+3, upp);
        return ic;
    }

    private String getId(String data){
        int btm = data.indexOf("id=");
        int upp = data.indexOf(", pemail");
        String id = data.substring(btm+3, upp);
        return id;
    }

    private String getPosition(String data){
        int btm = data.indexOf("position=");
        int upp = data.indexOf(", imgpath");
        String position = data.substring(btm+9, upp);
        return position;
    }

    private void toLog(String msg){
        if(LOG_ON_HOST){
            Log.e("Host", msg);
        }
    }
}
