package com.anewtech.clientregister.Adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anewtech.clientregister.Model.HostModel;
import com.anewtech.clientregister.Model.StaffDetails;
import com.anewtech.clientregister.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by heriz on 9/1/2018.
 */

public class CustomViewAdapter extends BaseAdapter {

    private static CustomViewAdapter INSTANCE = null;
    private CustomViewAdapter() {}
    public static synchronized CustomViewAdapter getInstance(){
        if(INSTANCE == null){
            INSTANCE = new CustomViewAdapter();
        }
        return(INSTANCE);
    }

    static class ViewHolder {
        TextView staffname;
        ImageView staffImg;

        ViewHolder(View v){
            staffname = v.findViewById(R.id.stafftv);
            staffImg = v.findViewById(R.id.staffimg);
        }
    }

    private Context context;
    private List<StaffDetails> staffnames;
    public List<HostModel> hostdetails;

    private int mSelectedItem;
    private boolean initial;
    private boolean isLoadedLocally = false;
    private PackageManager pm;
    private String rootpath;
    private ArrayList<String> imgNoNamae;

    private ViewHolder holder;
    String img = "";

    public void initialize(Context c, List<StaffDetails> staffnames){
        this.context = c;
        this.staffnames = staffnames;
    }

    public void initialize( List<HostModel> hostdetails){
        this.hostdetails = hostdetails;
    }

    @Override
    public int getCount() {
        try {
            if (isLoadedLocally) {
                return staffnames.size();
            }
            return hostdetails.size();
        }catch (NullPointerException e){
//            getCount();
            Toast.makeText(context
                    ,"Please go back to Welcome tab and sign in again.\nErr: Hosts not loaded, please give it a sec to load."
                    , Toast.LENGTH_SHORT)
                    .show();
            return 1;
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public String getItemName(int i){
        if(isLoadedLocally){
            return staffnames.get(i).name;
        }
        return hostdetails.get(i).name;
    }

    public int getmSelectedItem() {
        return mSelectedItem; // is used for shading(item selected)
    }

    public void setmSelectedItem(int mSelectedItem) {
        this.mSelectedItem = mSelectedItem;
    }

    public boolean isInitial() {
        return initial; // is used for shading(item selected)
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public PackageManager getPm() {
        return pm; // For fragment to use
    }

    public void setPm(PackageManager pm) {
        this.pm = pm; // Get from Activity
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.fragment_staff_details, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        //shade background to show selected item
        if(!(i == getmSelectedItem()) || isInitial()){
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }else{
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray));
        }

        //staffnames from local json
        if(isLoadedLocally) {
            if (staffnames != null) {
                holder.staffname.setText(this.staffnames.get(i).name);
            }


            //imgname from local img
            if (imgNoNamae != null) {
                for (int j = 0; j < imgNoNamae.size(); j++) {
                    // This loop prevents indexOutOfBound error
                    final int index = j;

                    if (j == i) {
                        img = imgNoNamae.get(i);
                        toLog("rootpath: "+rootpath);
                        toLog("img: "+img);
                        File f = new File(rootpath + "/" + img);
                        Picasso.with(context).load(f).into(holder.staffImg);
                    }
                }
            }
        }else {
            //host details from db
            if (hostdetails != null) {
//                for(HostModel host : hostdetails){
//                    String name = host.name;
//                    String photourl = host.imgpath;
//                    holder.staffname.setText(name);
//                    Picasso.with(context).load(photourl).into(holder.staffImg);
//                }
                for(int j=0 ; j<hostdetails.size() ; j++){
                    if(j == i){
                        holder.staffname.setText(hostdetails.get(i).name);
                        Picasso.with(context).load(hostdetails.get(i).imgpath).into(holder.staffImg);
                    }
                }
            }
        }

        return view;
    }

    public void getListofNames(ArrayList<String> names){
        imgNoNamae = names;
    }

    public void setRootpath(String rootpath) {
        this.rootpath = rootpath;
    }

    private void toLog(String msg){
        Log.e("cva", msg);
    }

}
