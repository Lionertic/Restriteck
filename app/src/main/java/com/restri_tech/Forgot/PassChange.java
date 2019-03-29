package com.restri_tech.Forgot;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PassChange extends Fragment {
    View view;
    String a, b;
    EditText ed1, ed2;

    public PassChange() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        view = inflater.inflate(R.layout.fragment_pass_change, container, false);

        Button button =  view.findViewById(R.id.ch);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed1 = view.findViewById(R.id.ed1);
                ed2 = view.findViewById(R.id.ed2);
                a = ed1.getText().toString().trim();
                b = ed2.getText().toString().trim();
                if (a.equals("") || b.equals("")) {
                    Toast.makeText(getContext(), "Empty fields detected", Toast.LENGTH_LONG).show();


                } else if (!(a.equals(b))) {
                    Toast.makeText(getContext(), "Password mismatch", Toast.LENGTH_LONG).show();

                } else {
                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("password", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("pass", a);
                    editor.commit();
                    getActivity().setTitle("Home");
                    HomeFragment hf = new HomeFragment();
                    FragmentManager fm = getFragmentManager();
                    fm.beginTransaction().replace(R.id.fragment, hf).commit();
                }
            }
        });
        return view;
    }

}