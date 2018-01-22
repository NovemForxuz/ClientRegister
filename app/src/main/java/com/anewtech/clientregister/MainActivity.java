package com.anewtech.clientregister;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import com.anewtech.clientregister.Adapter.CustomViewAdapter;
import com.anewtech.clientregister.Adapter.SectionsPagerAdapter;
import com.anewtech.clientregister.Fragment.PlaceholderFragment;
import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Model.StaffDetails;
import com.anewtech.clientregister.Service.Api;
import com.anewtech.clientregister.Service.Host;
import com.anewtech.clientregister.Service.StaffDataService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    private List<StaffDetails> details;

    private final String URL = "url";

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
        Host host = new Host(db);
        background = new Thread(host);
        background.start();

        sds = new StaffDataService();
        sds.setJsonData(loadJsonFromAsset());
        sds.toLog();

        checkForImgFile("theenigma_pp","image1");
        checkForImgFile("staff_black", "jackBlack");

        initializeCva();
        loadStaffImgFromStrg("StaffImages");

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
        if(cim.isSignedIn().get(1)){
            finish();
        }
        background.interrupt();
    }

    private void initializeCva(){
        details = sds.getDetailsList();
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
        cim.setSignedIn(0,false);
        cim.setSignedIn(1,false);
    }

    //Get json from internet
    public void getJsonRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .build();

        Api api = retrofit.create(Api.class);

        api.getPost().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try{
                    toLog("RetrofitExample: "+response.body().string());
                }catch(IOException e){
                    toLog("RetrofitError: "+response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public String loadJsonFromAsset(){
        String json = null;
        try{
            InputStream is = getAssets().open("staffdetails.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }catch(IOException e){
            Log.e("xxx",e.getMessage());
            return null;
        }
        Log.e("xxx", json);
        return json;
    }

    private void checkForImgFile(String resourceName, String imageName){
        File file = new File("/StaffImages/c");
        if(!file.exists()){
            //Do something
            toLog("File doesn't exist.");
            int id = getBaseContext().getResources().getIdentifier(resourceName,"drawable", getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id);
            saveToInternalStorage(bitmap, imageName);
        }
        else{
            //Nothing
            toLog("Files exists.");
        }
    }

    private Bitmap cropBitmapToSq(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        Bitmap image = Bitmap.createScaledBitmap(dstBmp, 400, 400, false);
        return image;
    }

    private void saveToInternalStorage(Bitmap bitmapImage, String imageName){
        //create StaffImages
        File mypath = new File(getExternalFilesDir("StaffImages"), imageName+".png");
        Bitmap cropSq = cropBitmapToSq(bitmapImage);
        FileOutputStream fos = null;

        try{
            fos = new FileOutputStream(mypath);
            //use the compress method on the Bitmap object to write image to the OutputStream
            cropSq.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            try{
                fos.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{mypath.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        toLog("Scanned " + path + ":");
                        toLog("-> uri=" + uri);
                    }
                });

//        return directory.getAbsolutePath();
    }

    //Pass to custom view adapter for staff image
    public void loadStaffImgFromStrg(String path){
        String img = "";
        File directory = new File(String.valueOf(getExternalFilesDir(path)));
        String rootpath = directory.getAbsolutePath();
        cva.setRootpath(rootpath);
        toLog("rootpath: "+rootpath);
        //Get name of images
        File[] files = directory.listFiles();
        ArrayList<String> images = new ArrayList<>();
        for(File file : files){
            String imagenm = file.getName();
            images.add(imagenm);
        }
        cva.getListofNames(images);
    }

    private void toLog(String msg){
        Log.e("main", msg);
    }

}
