package com.anewtech.clientregister;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Model.LoginViewModel;
import com.anewtech.clientregister.Service.PostLogin;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qihancloud.opensdk.base.TopBaseActivity;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by heriz on 11/1/2018.
 */

public class SignInActivity extends TopBaseActivity {

    LoginViewModel lvModel;
    ClientInfoModel cim = ClientInfoModel.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        lvModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        FirebaseFirestore ff = FirebaseFirestore.getInstance();

        PostLogin postLogin = new PostLogin(ff, observer());
        Thread postThread = new Thread(postLogin);
        postThread.start();

        initializeUI();
    }

    @Override
    protected void onMainServiceConnected() {

    }

    private void initializeUI(){



        TextView desc = findViewById(R.id.desc_tv);

//        Button confirm = findViewById(R.id.cofirmLoginBtn);
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
//                intent.putExtra("NewClicked", true);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//            }
//        });
    }

    public void confirmLogin(View v){
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        intent.putExtra("NewClicked", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public Observer<String> observer(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                setContentView(R.layout.activity_signin);
                TextView tokentv = findViewById(R.id.token_tv);
                tokentv.setText(s);
                cim.setToken(s);
                TextView warntv = findViewById(R.id.loginwarning_tv);
                warntv.setText(R.string.login_warning);
                int color = getResources().getColor(R.color.colorAccent);
                warntv.setTextColor(color);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
