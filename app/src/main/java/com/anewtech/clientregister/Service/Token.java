package com.anewtech.clientregister.Service;

import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;

import com.anewtech.clientregister.Model.ClientInfoModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by heriz on 26/1/2018.
 */

public class Token implements Runnable {

    private final boolean LOG_ON_TOKEN = true;

    private ClientInfoModel cm = ClientInfoModel.getInstance();
    private Observer<String> Observer;
    private FirebaseFirestore mRef;

    private boolean isTokenGenerated = false;

    public Token(FirebaseFirestore mRef, Observer<String> Observer){
        this.Observer = Observer;
        this.mRef = mRef;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        toLog("Thread started");
        while (!Thread.currentThread().isInterrupted()) {
            try{
                Thread.sleep(5000);
                mRef.collection("logs").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    toLog("task successful");
                                    for(DocumentSnapshot document : task.getResult()){
                                        String time = document.get("timein").toString();
                                        if (time != null){
                                            toLog("Log: "+time);
                                        }
                                        String currentTime = cm.getTimenow();
                                        if(currentTime != null){
//                                            toLog("Now: "+currentTime);
                                            if(time.equals(cm.getTimenow())){
                                                toLog("Got it");
                                                isTokenGenerated = true;
                                                observable(document.get("vtoken").toString())
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(Observer);

                                                break;
                                            }
                                        }
                                    }
                                    if(isTokenGenerated){
                                        stop();
                                    }else{
                                        try {
                                            Thread.sleep(1500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        run();
                                    }
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }else {
                                    toLog("not sucessfull: "+task.getException());
                                }
                            }
                        });

                stop();
            }catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        }
        toLog("Thread ended");
    }

    private void stop(){
        Thread.currentThread().interrupt();
    }

    private Observable<String> observable(String token){
        return io.reactivex.Observable.just(token);
    }

    private void toLog(String msg){
        if(LOG_ON_TOKEN){
            Log.e("Token", msg);
        }
    }
}
