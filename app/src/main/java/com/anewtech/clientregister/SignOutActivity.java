package com.anewtech.clientregister;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Service.PostLogout;
import com.qihancloud.opensdk.base.TopBaseActivity;

/**
 * Created by heriz on 11/1/2018.
 */

public class SignOutActivity extends TopBaseActivity {

    private static final boolean LOG_ON_SIGN_OUT = true;

    private EditText uToken;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        uToken = findViewById(R.id.logout_token);
    }

    @Override
    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onMainServiceConnected() {

    }

    public String getToken(){
        String token = "";
        token = uToken.getText().toString();
         if(token != null && !token.equals("")){
             return token;
         }
         return "empty";
    }

    public void logout(View v){
        PostLogout postLogout = new PostLogout(getToken());
        Thread thread = new Thread(postLogout);
        thread.start();

        Intent intent = new Intent(SignOutActivity.this, MainActivity.class);
        intent.putExtra("NewClicked", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void toLog(String msg){
        if(LOG_ON_SIGN_OUT){
            Log.e("Sign Out", msg);
        }
    }
}
