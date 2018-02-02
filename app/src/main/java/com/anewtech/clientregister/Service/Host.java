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

                                    int size = task.getResult().size();
                                    for(int i=0; i < size; i++){
                                        DocumentSnapshot document = task.getResult().getDocuments().get(i);
                                        vModel = document.toObject(HostModel.class);

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

    private void toLog(String msg){
        if(LOG_ON_HOST){
            Log.e("Host", msg);
        }
    }
}
