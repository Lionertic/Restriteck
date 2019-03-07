package com.restri_tech;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.restri_tech.Fragments.BlockFragment;
import com.restri_tech.DB.Package;

import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

public class custom extends AppCompatActivity {
    List <Package> apps;
    PackageManager packageManager = null;
    ApplicationInfo a;
    Package s;
    EditText ed;
    Button b;
    String k;
    int po;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        packageManager = getPackageManager();
        SharedPreferences sd = getSharedPreferences("check", Context.MODE_PRIVATE);
        sd.edit().putBoolean("c", false).commit();

        TextView t =(TextView) findViewById(R.id.t);
        ImageView im = (ImageView)findViewById(R.id.im);
        ed = (EditText) findViewById(R.id.time);
        b = (Button) findViewById(R.id.b);
        Intent i = getIntent();
        k = i.getStringExtra("name");
        po = i.getIntExtra("position", 0);
        apps = HomeActivity.myAppDatabase.myDao().getApps();
        List < ApplicationInfo > appList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo ai: appList) {
            if (ai.processName.equals(k)) {
                a = ai;
                break;
            }
        }
        for (Package p: apps)
            if (p.getName().equals(k)) {
                s = p;
                im.setImageDrawable(a.loadIcon(packageManager));
                t.setText("\t\t\t"+a.loadLabel(packageManager)+"\nEnter in minutes");
            }
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ed.getText().toString().trim().equals("")) {
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                } else {
                    Long t = Long.parseLong(ed.getText().toString().trim());
                    s.setFtime(TimeUnit.MINUTES.toMillis(t));
                    s.setRtime(TimeUnit.MINUTES.toMillis(t));
                    HomeActivity.myAppDatabase.myDao().setTime(s);
                    BlockFragment.app.set(po, a);
                    BlockFragment.adapter.notifyDataSetChanged();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
}