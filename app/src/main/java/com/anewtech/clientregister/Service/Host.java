package com.anewtech.clientregister.Service;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.anewtech.clientregister.Adapter.CustomViewAdapter;
import com.anewtech.clientregister.Model.HostModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by heriz on 17/1/2018.
 */

public class Host implements Runnable {

    FirebaseFirestore mRef;

    List<HostModel> details;
    HostModel vModel;
    boolean isReady;

    CustomViewAdapter cva = CustomViewAdapter.getInstance();

    public Host(FirebaseFirestore reference){
        mRef = reference;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        toLog("Thread started");
        while(!Thread.currentThread().isInterrupted()){
            try{
                //Do something...
                details = new ArrayList<>();
                mRef.collection("visitors")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
//                                        HostModel host =
//                                        toLog("DocumentSnapshot added with ID: " + document.getId());
                                        toLog("DocumentSnapshot data: "+document.getData());
                                        String data = document.getData().toString();
                                        vModel = new HostModel();
                                        vModel.name = getName(data);
                                        vModel.imgpath = getPhotoUrl(data);
                                        details.add(vModel);
                                        toLog("name: "+vModel.name);

                                        isReady = true;

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
        //TODO: add observables for details
        listObservable(details)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listObserver());
//        for(HostModel vm : cva.hostdetails){
//            toLog(vm.name);
//        }
        toLog("Thread interrupted");
    }

    public void stop(){
        Thread.currentThread().interrupt();
    }

    private String getName(String data){
        int btm = data.indexOf("name=");
        int upp = data.indexOf(", email");
        String name = data.substring(btm+5, upp);
        toLog("name: "+name);
        return name;
    }

    private String getPhotoUrl(String data){
        int btm = data.indexOf("imgpath=");
        int upp = data.indexOf(", hp");
        String photolink = data.substring(btm+8, upp);
        toLog("photo url: "+photolink);
        return photolink;
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

    //TODO: create methods to get all other elements in HostModel

    private void toLog(String msg){
        Log.e("Host", msg);
    }
}
