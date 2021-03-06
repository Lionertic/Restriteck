package com.restri_tech.Fragments;


import android.app.ActivityManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.work.WorkManager;
import android.app.admin.DevicePolicyManager;
import android.widget.Toast;

import com.restri_tech.BackCheck;
import com.restri_tech.PolicyManager.PolicyManager;
import com.restri_tech.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    Button start;
    private PolicyManager policyManager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        policyManager = new PolicyManager(getContext());

//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", 0);
//
//        if (sharedPreferences.getBoolean("home2", true)) {
//
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
//            alertDialogBuilder.setMessage("You are just one step away from completing the setup.\n \tSelect start service to start");
//            alertDialogBuilder.setPositiveButton("ok",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            //startActivity(new Intent(getBaseContext(),PassCheck.class));
//                        }
//                    });
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
//            sharedPreferences.edit().putBoolean("home2", false).commit();
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        start = (Button) v.findViewById(R.id.start);

        if(isLocationServiceRunning()){
            start.setText("STOP SERVICE");
        }
        else{
            start.setText("START SERVICE");
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isLocationServiceRunning()) {
                    getActivity().stopService(new Intent(getActivity(),BackCheck.class));
                    Toast.makeText(getContext(),"Stopped",Toast.LENGTH_LONG).show();
                    start.setText("START SERVICE");
                }
                else {
                    startLocationService();
                    Toast.makeText(getContext(),"Started",Toast.LENGTH_LONG).show();
                    start.setText("STOP SERVICE");
                }
            }
        });
        return v;
    }
    private void startLocationService(){

        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(getActivity(), BackCheck.class);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                getActivity().startForegroundService(serviceIntent);
            }else{
                getActivity().startService(serviceIntent);
            }
        }
    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.restri_tech.BackCheck".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}