package com.restri_tech.Adapter;

import android.content.Context;
import android.content.Intent;
import java.text.DecimalFormat;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.restri_tech.DB.Package;
import com.restri_tech.HomeActivity;
import com.restri_tech.R;
import com.restri_tech.custom;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter {
    List < ApplicationInfo > apps;
    List <Package> app;
    PackageManager packageManager;
    Context context;


    public void filterList(List < ApplicationInfo > filterdNames) {
        this.apps = filterdNames;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ApplicationInfo ai = apps.get(position);
        initializeViews(ai, holder, position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView t;
        public ImageView im;
        RelativeLayout rv;
        public MyViewHolder(View view) {
            super(view);
            t = view.findViewById(R.id.text1);
            im = view.findViewById(R.id.image);
            rv = view.findViewById(R.id.rw1);
        }

    }

    public MyAdapter(Context context, List < ApplicationInfo > items, PackageManager p) {
        apps = items;
        packageManager = p;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, parent, false);
        return new MyViewHolder(v);
    }

    private void initializeViews(final ApplicationInfo ai, final RecyclerView.ViewHolder holder, final int position) {
        app = HomeActivity.myAppDatabase.myDao().getApps();
        String s = ai.loadLabel(packageManager) + " \nTime Remaining : " + getElapsedTime(app.get(position).getRtime()) + " \nDaily Usage Time : " + getElapsedTime(app.get(position).getFtime()) ;
        ((MyViewHolder) holder).t.setText(s);
        ((MyViewHolder) holder).im.setImageDrawable(ai.loadIcon(packageManager));
        ((MyViewHolder) holder).rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, custom.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("name", ai.processName);
                i.putExtra("position", position);
                context.startActivity(i);
            }
        });
    }

    String getElapsedTime(final long end){
        String str="";
        DecimalFormat df = new DecimalFormat("#.##");
        double difference = end/1000;

        if(difference<0){
            return "0s";
        }
        int minHourDay = 0 ;

        if(difference > 60){
            minHourDay++;
            difference /=60;
            if(difference > 60){
                minHourDay++;
                difference /=60;
                if(difference > 24){
                    minHourDay++;
                    difference /=24;
                }
            }
        }
        switch(minHourDay){
            case 3 :
                str+=(int)(difference)+"d";
                difference*=24;
                difference%=24;
            case 2 :
                str+=(int)(difference)+"h";
                difference*=60;
                difference%=60;
            case 1 :
                str+=(int)(difference)+"m";
                difference*=60;
                difference%=60;
            case 0 :
                str+=df.format(difference)+"s";
        }
        return str.equals("0s") ? "1s" : str ;
    }
}
