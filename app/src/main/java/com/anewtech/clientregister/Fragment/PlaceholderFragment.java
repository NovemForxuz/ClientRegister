package com.anewtech.clientregister.Fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.anewtech.clientregister.Adapter.CustomViewAdapter;
import com.anewtech.clientregister.Model.ClientInfoModel;
import com.anewtech.clientregister.Model.VisitorModel;
import com.anewtech.clientregister.R;
import com.anewtech.clientregister.Service.Api;
import com.anewtech.clientregister.Service.Post;
import com.anewtech.clientregister.SignInActivity;
import com.anewtech.clientregister.SignOutActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.app.Activity.RESULT_OK;
import static junit.framework.Assert.assertTrue;

/**
 * A placeholder fragment containing a simple view.
 */

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private CustomViewAdapter cva = CustomViewAdapter.getInstance();
    public static ClientInfoModel cim = ClientInfoModel.getInstance();

    private EditText name, email, phoneNo, companyName;
    public View rootView = null;
    private ImageView clientImg;

    private boolean isPhotoTaken;
    private String filename;
    private String filepath = "StaffPhoto";
    private File myExternalFile;

    public PlaceholderFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments().getInt(ARG_SECTION_NUMBER)==1){
            rootView = inflater.inflate(R.layout.fragment_welcome, container, false);
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
            rootView = inflater.inflate(R.layout.fragment_info_1, container, false);
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER)==3){
            rootView = inflater.inflate(R.layout.fragment_info_2, container, false);
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER)==4){
            rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER)==5){
            rootView = inflater.inflate(R.layout.fragment_tc, container, false);
        }
        else if(getArguments().getInt(ARG_SECTION_NUMBER)==6){
            rootView = inflater.inflate(R.layout.fragment_confirm, container, false);
        }
        else {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        }
        initialiseUI(rootView);

        return rootView;
    }
    private void initialiseUI(@NonNull final View mainView){

        //Tab 1...
        final TextView tab1title = mainView.findViewById(R.id.tab1_title);
        if(tab1title != null){
            if(cim.isSignedIn().get(1) && !cim.isSignedIn().get(0)){
                tab1title.setTextSize(30f);
                tab1title.setText("Mr/Ms "+cim.getName()+" from "+cim.getCompanyName()+"\n is currently signed in.");
            }else{
                tab1title.setTextSize(50f);
                tab1title.setText("Welcome!");
            }
        }

        Button signIn = mainView.getRootView().findViewById(R.id.sign_in_btn);
        if(signIn != null){
            signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("xxx", "Sign in test...");
//                    getCurrentTime();
                    cim.setSignedIn(0,true);
                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if (tab != null) {
                        tab.getTabAt(1).select();
                    }
                }
            });
            if(cim.isSignedIn().get(1) && !cim.isSignedIn().get(0)){
                signIn.setVisibility(View.GONE);
            }else{
                signIn.setVisibility(View.VISIBLE);
            }
        }

        Button signOut = mainView.findViewById(R.id.sign_out_btn);
        if(signOut != null){
            signOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cim.setSignedIn(1,false);

                    Intent intent = new Intent(getContext(), SignOutActivity.class);
                    intent.putExtra("name", cim.getName());
                    intent.putExtra("email", cim.getEmail());
                    intent.putExtra("phoneno", cim.getPhoneNo());
                    intent.putExtra("company", cim.getCompanyName());
                    startActivity(intent);

                }
            });
            if(!cim.isSignedIn().get(1)){
                signOut.setClickable(false);
            }else{
                signOut.setClickable(true);
            }
        }

        android.widget.Space tab1space = mainView.findViewById(R.id.tab1space);
        if(tab1space != null){
            if(cim.isSignedIn().get(1)){
                tab1space.setVisibility(View.GONE);
            }else{
                tab1space.setVisibility(View.VISIBLE);
            }
        }

        //Tab 2...
        name = mainView.getRootView().findViewById(R.id.name_edtxt);
        email = mainView.getRootView().findViewById(R.id.email_edtxt);
        phoneNo = mainView.getRootView().findViewById(R.id.phoneno_edtxt);
        companyName = mainView.getRootView().findViewById(R.id.companyname_edtxt);

        Button nextInfoBtn = mainView.getRootView().findViewById(R.id.info_next_btn);
        if(nextInfoBtn != null){
            nextInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewPager mpager = mainView.getRootView().findViewById(R.id.container);
                    int currentitem = mpager.getCurrentItem();
                    ViewGroup container = (ViewGroup) mainView.getRootView();


                    cim.setName(name.getText().toString());
                    cim.setEmail(email.getText().toString());

                    String num = phoneNo.getText().toString();
                    if(!num.contains("+65")){
                        String sgNo = "+65" + num;
                        cim.setPhoneNo(sgNo);
                    }else{
                        cim.setPhoneNo(num);
                    }

                    cim.setCompanyName(companyName.getText().toString());
                    toLog("infomodel - name: "+cim.getName());
                    toLog("infomodel - email: "+cim.getEmail());
                    toLog("infomodel - phone no.:"+cim.getPhoneNo());
                    toLog("infomodel - company name:"+cim.getCompanyName());

                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if (tab != null) {
                        tab.getTabAt(2).select();
                    }
                }
            });
        }

        //Tab3...
        final ListView staffList = mainView.getRootView().findViewById(R.id.staff_list);

        if(staffList != null){
            if(cva != null){
                staffList.setAdapter(cva);
                toLog("Adapter set!");
            }
            staffList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String staff = cva.getItemName(i);
                    cim.setStaffSeeking(staff);
                    cim.setHostId(cva.getHostId());
                    cva.setmSelectedItem(i);
                    cva.setInitial(false);
                    cva.notifyDataSetChanged();
                    toLog("onItemClicked: "+staff);
                }
            });
        }

        Button nextStaffBtn = mainView.getRootView().findViewById(R.id.info_nexttab_btn);
        if(nextStaffBtn != null){
            nextStaffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if(tab != null){
                        tab.getTabAt(3).select();
                        toLog("infomodel - email: "+cim.getEmail());
                    }
                }
            });
        }

        //Tab4...
        final Button nextPhotoBtn = mainView.findViewById(R.id.photo_next_btn);
        Button takePhoto = mainView.findViewById(R.id.photo_btn);
        if(takePhoto != null){
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        dispatchTakePictureIntent();
                        nextPhotoBtn.setVisibility(View.VISIBLE);
                    }catch (Exception e){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                        toLog(e.getMessage());
                    }

                }
            });
        }

        if(nextPhotoBtn != null){
            nextPhotoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if(tab != null){
                        tab.getTabAt(4).select();
                    }
                }
            });
        }

        clientImg = mainView.findViewById(R.id.clientImg);
        if(clientImg != null){
            if(isPhotoTaken){
                clientImg.setImageBitmap(cim.getPhotoId());
            }
            if(cim.getPhotoId() != null){
                clientImg.setImageBitmap(Bitmap.createScaledBitmap(cim.getPhotoId(), 960, 960, false));
            }

        }

        //Tab5...
        Button agreeBtn = mainView.findViewById(R.id.tc_agree_btn);
        if(agreeBtn != null){
            agreeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if(tab != null){
                        tab.getTabAt(5).select();
//                        toLog("infomodel - company name:"+cim.getCompanyName()); //Able to get data after implementing Singleton design pattern for cim
                    }
                }
            });
        }

        //...and set UI for Tab 6.
        ImageView confirmImg = mainView.findViewById(R.id.confirmImg);
        if(confirmImg != null){
            if(cim.getPhotoId() != null){
//                confirmImg.setImageBitmap(Bitmap.createScaledBitmap(cim.getPhotoId(), 960, 960, false));
                confirmImg.setImageDrawable(cropBitmapToRound(cim.getPhotoId()));
            }
        }

        TextView confirmtv = mainView.findViewById(R.id.confirmtv);
        if(confirmtv != null){
            String confirmMsg = "My name is "+cim.getName()+". I am from "+cim.getCompanyName()+".\n I am looking for "+cim.getStaffSeeking()+".";
            confirmtv.setText(confirmMsg);
        }

        Button confirmBtn = mainView.findViewById(R.id.confirmBtn);
        if(confirmBtn != null){
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cim.setSignedIn(1,true);
                    cim.setSignedIn(0,false);

                    TabLayout tab = mainView.getRootView().findViewById(R.id.tabs);
                    if(tab != null){
//                        tab.getTabAt(0).select();
                        Intent intent = new Intent(getContext(), SignInActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private RoundedBitmapDrawable cropBitmapToRound(Bitmap bm){
//        Bitmap imageBitmap=BitmapFactory.decodeResource(getResources(),  R.drawable.theenigma_pp); //get bitmap from resources drawable
        Bitmap image = Bitmap.createScaledBitmap(bm, 960, 960, false);
        RoundedBitmapDrawable roundedBitmapDrawable= RoundedBitmapDrawableFactory.create(getResources(), image);

//setting radius
        roundedBitmapDrawable.setCornerRadius(470.0f);
        roundedBitmapDrawable.setAntiAlias(true);
        return roundedBitmapDrawable;
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

        Bitmap image = Bitmap.createScaledBitmap(dstBmp, 960, 960, false);
        return image;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Bitmap croppedBitmap = cropBitmapToSq(imageBitmap);
            cim.setPhotoId(croppedBitmap);
            clientImg.setImageBitmap(croppedBitmap); //it is setting img but still reset when rotate
            isPhotoTaken = true;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(cva.getPm()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void toLog(String msg){
        Log.e("fragment", msg);
    }

}
