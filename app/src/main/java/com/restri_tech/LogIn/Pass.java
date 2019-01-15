package com.restri_tech.LogIn;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.restri_tech.Forgot.Forgot;
import com.restri_tech.HomeActivity;
import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Pass extends Fragment {
    EditText ed1,ed2;
    SharedPreferences sd;
    String pass;
    TextView t;
    Button b1,b2;

    public Pass() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_pass, container, false);

        SharedPreferences sd1 = getActivity().getSharedPreferences("Worker",0);
        if(sd1.getBoolean("Toast",false)){
            Toast.makeText(getContext(),"Your Time is UP",Toast.LENGTH_LONG).show();
            sd1.edit().putBoolean("Toast",false).commit();
        }
        sd=getActivity().getSharedPreferences("Pass",0);
        ed1=v.findViewById(R.id.pass1);
        ed2=v.findViewById(R.id.pass2);
        t=v.findViewById(R.id.text);
        b1=v.findViewById(R.id.LogP);
        b2=v.findViewById(R.id.fg);
        if(sd.getBoolean("First",true)){
            b2.setVisibility(View.GONE);
            ed1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    pass = s.toString();
                    if(pass.equals(""))
                        t.setText("");
                    else if(pass.length()<3)
                        t.setText("Password Too Small");
                    else if (pass.length()<6)
                        t.setText("Weak Password");
                    else
                        t.setText("Strong Password");
                }
            });
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pass.equals(ed2.getText().toString())){
                        sd.edit().putString("Pass",pass).commit();
                        ed1.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        ed2.onEditorAction(EditorInfo.IME_ACTION_DONE);
                        SharedPreferences s = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                        s.edit().putBoolean("c", false).commit();
                        sd.edit().putBoolean("First",false).commit();
                        s = getActivity().getSharedPreferences("Home",0);
                        s.edit().putBoolean("First",false).commit();
                        s.edit().putInt("Type",2).commit();
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
                        ed1.setText("");
                        ed2.setText("");
                        Toast.makeText(getContext(),"Miss Match",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            ed2.setVisibility(View.GONE);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(sd.getString("Pass",null).equals(ed1.getText().toString())){
                        SharedPreferences s = getActivity().getSharedPreferences("check", Context.MODE_PRIVATE);
                        s.edit().putBoolean("c", false).commit();
                        Intent i =new Intent(getContext(), HomeActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK) ;
                        startActivity(i);
                        getActivity().finish();
                    }
                    else{
                        Toast.makeText(getContext(),"Incorrect",Toast.LENGTH_LONG).show();
                    }
                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
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
