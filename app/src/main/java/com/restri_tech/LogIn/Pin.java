package com.restri_tech.LogIn;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.restri_tech.Forgot.Forgot;
import com.restri_tech.HomeActivity;
import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class Pin extends Fragment {
    EditText ed1,ed2;
    String pin;
    SharedPreferences sd;
    Button b;

    public Pin() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pin, container, false);

        SharedPreferences sd1 = getActivity().getSharedPreferences("Worker",0);
        if(sd1.getBoolean("Toast",false)){
            Toast.makeText(getContext(),"Your Time is UP",Toast.LENGTH_LONG).show();
            sd1.edit().putBoolean("Toast",false).commit();
        }

        ed1 = v.findViewById(R.id.pin1);
        ed2 = v.findViewById(R.id.pin2);
        b=v.findViewById(R.id.sub2);
        sd = getActivity().getSharedPreferences("Pin",0);
        if (sd.getBoolean("First",true)){
            b.setVisibility(View.GONE);
            ed1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().length()==4){
                        pin = s.toString();
                        ed1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                    }
                }
            });
            ed2.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().length()==4){
                        ed2.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        if(pin.equals(s.toString())){
                            sd.edit().putString("Pin",pin).commit();
                            SharedPreferences sd1 = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                            sd1.edit().putBoolean("c", false).commit();
                            sd.edit().putBoolean("First",false).commit();
                            sd1 = getActivity().getSharedPreferences("Home",0);
                            sd1.edit().putBoolean("First",false).commit();
                            sd1.edit().putInt("Type",1).commit();
                            sd1=getActivity().getSharedPreferences("Forgot",0);
                            if(sd1.getBoolean("First",true)) {
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
                        else
                        {
                            ed1.setText("");
                            ed2.setText("");
                            Toast.makeText(getContext(),"Mismatch",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
        else
        {
            ed2.setVisibility(View.GONE);
            ed1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().length()==4){
                        ed1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        if(s.toString().equals(sd.getString("Pin",null))){
                            SharedPreferences sd1 = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                            sd1.edit().putBoolean("c", false).commit();
                            Intent i =new Intent(getContext(), HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(i);
                            getActivity().finish();
                        }
                        else
                        {
                            ed1.setText("");
                            Toast.makeText(getContext(),"Mismatch",Toast.LENGTH_LONG).show();
                        }
                    }
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
