package com.anewtech.clientregister.Service;

import android.os.Process;
import android.support.constraint.ConstraintLayout;
import android.util.Log;

import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Model.VisitorLogoutModel;
import com.anewtech.clientregister.Model.VisitorModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by heriz on 29/1/2018.
 */

public class PostLogout implements Runnable {

    private final boolean LOG_ON_LOGOUT = true;

    ClientInfoModel cim = ClientInfoModel.getInstance();
    String uToken;

    public PostLogout(String userToken){
        this.uToken = userToken;
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while(!Thread.currentThread().isInterrupted()){
            try{
                getCurrentTime();

                stop();

            }catch (Exception e){
                Thread.currentThread().interrupt();
            }
        }
    }

    public void stop(){
        Thread.currentThread().interrupt();
    }

    private void getCurrentTime(){
        Retrofit retrofitGet = new Retrofit.Builder()
                .baseUrl("https://us-central1-vmsystem-4aa54.cloudfunctions.net/")
                .build();

        Api apiGet = retrofitGet.create(Api.class);

        apiGet.getPost().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null){
                    try{
                        timeObservable(response.body().string())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(timeObserver());
                        toLog(response.body().string());

                    }catch(IOException e){
                        toLog("RetrofitError: "+response.errorBody());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private Observable<String> timeObservable(String time){
        return Observable.just(time);
    }

    private Observer<String> timeObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String currentTime) {
                cim.setTimenow(currentTime);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ClientInfoModel c = cim;
                postVisitorInfo(c);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void postVisitorInfo(ClientInfoModel cmodel){
        Retrofit retrofitPost = new Retrofit.Builder()
                .baseUrl("https://us-central1-vmsystem-4aa54.cloudfunctions.net/")
                .build();

        Api apiPost = retrofitPost.create(Api.class);

        ObjectMapper mapper = new ObjectMapper();
        VisitorLogoutModel obj = new VisitorLogoutModel(cim, uToken);
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonInString);
        apiPost.postVisitor(requestBody).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    toLog("Retrofit: "+response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                toLog(t.getMessage());
            }
        });
    }

    private void toLog(String msg){
        if(LOG_ON_LOGOUT){
            Log.e("Logout", msg);
        }
    }
}
