package com.anewtech.clientregister;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.anewtech.clientregister.Adapter.CustomViewAdapter;
import com.anewtech.clientregister.Adapter.SectionsPagerAdapter;
import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Model.HostDataModel;
import com.anewtech.clientregister.Service.Api;
import com.anewtech.clientregister.Service.Host;
import com.anewtech.clientregister.Service.StaffDataService;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private CustomViewAdapter cva;
    private StaffDataService sds;
    private ClientInfoModel cim = ClientInfoModel.getInstance();
    private FirebaseFirestore db;
    private Thread background;
    private HostDataModel details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        db = FirebaseFirestore.getInstance();
        File hoster = new File(getExternalFilesDir("Hosts"), "hosts.json");
        Host host = new Host(db, hoster);
        background = new Thread(host);
        background.start();

        sds = new StaffDataService();
        try {
            sds.setJsonData(loadJsonFromFile());

        } catch (Exception e) {
            e.printStackTrace();
        }

        initializeCva();

        resetClientInstance();
//        if(!fileExistsInSD("jackBlack.png")){
//            toLog("File doesn't exist");
//        }else{
//            toLog("File exist");
//        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // recreate activity when NewClicked was just changed
        if (getIntent().getBooleanExtra("NewClicked", false)) {
            finish(); // finish and create a new Instance
            Intent restarter = new Intent(MainActivity.this, MainActivity.class);
            startActivity(restarter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        background.interrupt();
    }

    private void initializeCva(){
        details = sds.getHosts();
        cva = CustomViewAdapter.getInstance();
        cva.setInitial(true);
        cva.initialize(this, details);
        cva.setPm(getPackageManager());
    }

    private void resetClientInstance(){
        //Reset client model to null, since it's a singleton
        cim.setName(null);
        cim.setEmail(null);
        cim.setPhoneNo(null);
        cim.setCompanyName(null);
        cim.setStaffSeeking(null);
        cim.setPhotoId(null);
        cim.setTimenow(null);
        cim.setSignedIn(0,false);
        cim.setSignedIn(1,false);
    }

    public String loadJsonFromFile()throws Exception{
        String json = null;
        File directory = new File(String.valueOf(getExternalFilesDir("Hosts")), "hosts.json");
        FileInputStream fis;
        try {
            fis = new FileInputStream(directory);
            json = convertStreamToString(fis);
            fis.close();
            toLog(json);
        }catch(IOException e){
            toLog(e.getMessage());
        }return json;
    }
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private void toLog(String msg){
        Log.e("main", msg);
    }

}
