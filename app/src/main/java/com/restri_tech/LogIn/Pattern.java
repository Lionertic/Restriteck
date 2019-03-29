package com.restri_tech.LogIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.restri_tech.Forgot.Forgot;
import com.restri_tech.HomeActivity;
import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.R;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Pattern extends Fragment {
    PatternLockView patternLockView;
    SharedPreferences sd;
    String pat;
    Button b;

    public Pattern() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pattern, container, false);

        SharedPreferences sd1 = getActivity().getSharedPreferences("Worker",0);
        if(sd1.getBoolean("Toast",false)){
            Toast.makeText(getContext(),"Your Time is UP",Toast.LENGTH_LONG).show();
            sd1.edit().putBoolean("Toast",false).commit();
        }
        b=v.findViewById(R.id.sub1);
        sd=getActivity().getSharedPreferences("Pattern",0);
        patternLockView = v.findViewById(R.id.patternView);
        if(sd.getBoolean("First",true)){
            b.setVisibility(View.GONE);
            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List progressPattern) {

                }

                @Override
                public void onComplete(List pattern) {

                    if (PatternLockUtils.patternToString(patternLockView, pattern).length() != 1) {
                        if(sd.getBoolean("First",true)){
                            patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                            pat = PatternLockUtils.patternToString(patternLockView, pattern);
                            Toast.makeText(getContext(),"Confirm Again",Toast.LENGTH_LONG).show();
                            sd.edit().putBoolean("First",false).commit();
                        }
                        else{
                            if(pat.equals(PatternLockUtils.patternToString(patternLockView, pattern))){
                                patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                                sd.edit().putString("Pattern",pat).commit();
                                sd.edit().putBoolean("First",false).commit();
                                SharedPreferences s = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                                s.edit().putBoolean("c", false).commit();
                                s = getActivity().getSharedPreferences("Home",0);
                                s.edit().putBoolean("First",false).commit();
                                s.edit().putInt("Type",3).commit();
                                s=getActivity().getSharedPreferences("Forgot",0);
                                if(s.getBoolean("First",true)) {
                                    getActivity().setTitle("Forgot");
                                    Forgot f = new Forgot();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    fm.beginTransaction().replace(R.id.fragment, f).commit();
                                }
                                else {
                                    getActivity().setTitle("Home");
                                    HomeFragment hf = new HomeFragment();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    fm.beginTransaction().replace(R.id.fragment, hf).commit();
                                }
                            }
                            else{
                                patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                                Toast.makeText(getContext(),"Miss Match",Toast.LENGTH_LONG).show();
                                sd.edit().putBoolean("First",true).commit();
                            }
                        }
                    }
                    else {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    }
                }

                @Override
                public void onCleared() {

                }
            });
        }
        else{
            patternLockView.addPatternLockListener(new PatternLockViewListener() {
                @Override
                public void onStarted() {

                }

                @Override
                public void onProgress(List progressPattern) {

                }

                @Override
                public void onComplete(List pattern) {

                    if (PatternLockUtils.patternToString(patternLockView, pattern).equalsIgnoreCase(sd.getString("Pattern",null))) {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                        SharedPreferences s = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                        s.edit().putBoolean("c", false).commit();
                        Intent i =new Intent(getContext(), HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                        startActivity(i);
                        getActivity().finish();

                    }
                    else {
                        patternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                        Toast.makeText(getContext(),"Wrong",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCleared() {

                }
            });
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sd1 = getActivity().getSharedPreferences("Forgot",0);
                    sd1.edit().putBoolean("Finish",false).commit();
                    if(isNetworkConnected()) {
                        getActivity().setTitle("Forgot");
                        Forgot f = new Forgot();
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().replace(R.id.fragment, f).commit();
                    }
                    else {
                        Toast.makeText(getContext(),"Connect To Internet",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        return v;
    }
        private boolean isNetworkConnected() {
            ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

            return cm.getActiveNetworkInfo() != null;
        }
}
