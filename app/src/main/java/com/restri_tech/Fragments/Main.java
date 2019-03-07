package com.restri_tech.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.restri_tech.LogIn.Pass;
import com.restri_tech.LogIn.Pattern;
import com.restri_tech.LogIn.Pin;
import com.restri_tech.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class Main extends Fragment {
    Button pin,pass,patt;
    SharedPreferences sd;
    Switch sw;

    public Main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        sd = getActivity().getSharedPreferences("Home", 0);
        FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
        sw = v.findViewById(R.id.switch1);
        pin = v.findViewById(R.id.pin);
        pass = v.findViewById(R.id.pass);
        patt = v.findViewById(R.id.patt);
        if (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
            sw.setVisibility(View.VISIBLE);
            if(sd.getBoolean("Finger",false)){
                sw.setChecked(sd.getBoolean("Finger",false));
            }
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sd.edit().putBoolean("Finger",isChecked).commit();
                }
            });
        }

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Pin");
                Pin p = new Pin();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.fragment, p).commit();
            }
        });
        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Pass");
                Pass ps = new Pass();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.fragment, ps).commit();
            }
        });
        patt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setTitle("Pattern");
                Pattern pa = new Pattern();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.fragment, pa).commit();
            }
        });

        return v;
    }

}
