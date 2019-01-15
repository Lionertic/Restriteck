package com.restri_tech.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.restri_tech.Adapter.app;
import com.restri_tech.DB.Package;
import com.restri_tech.HomeActivity;
import com.restri_tech.Adapter.MyArrayAdapter;
import com.restri_tech.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 */
public class InstallFragment extends Fragment {
    RecyclerView myRecyclerView;
    MyArrayAdapter myArrayAdapter;
    PackageManager packageManager = null;
    List <com.restri_tech.Adapter.app> app;
    List <Package> apps;
    EditText ed;
    FloatingActionButton fab;

    public InstallFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = new ArrayList < > ();

        MyTask myTask = new MyTask();
        myTask.execute();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", 0);
        if (sharedPreferences.getBoolean("iapps", true)) {


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
            alertDialogBuilder.setMessage("1.All the apps installed in your device will be listed here. \n\n2.Select the apps you want to restrict \n\n3.Click the icon at the bottom of the screen");
            alertDialogBuilder.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //startActivity(new Intent(getBaseContext(),PassCheck.class));

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            sharedPreferences.edit().putBoolean("iapps", false).commit();

        }


        View v = inflater.inflate(R.layout.fragment_install, container, false);

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List < String > resultList = myArrayAdapter.getSelectedItem();
                for (int i = 0; i < resultList.size(); i++) {
                    Package p = new Package();
                    p.setName(resultList.get(i));
                    p.setFtime(TimeUnit.MINUTES.toNanos(0));
                    p.setRtime(TimeUnit.MINUTES.toNanos(0));
                    HomeActivity.myAppDatabase.myDao().addApps(p);
                }

                getActivity().setTitle("Blocked Apps");
                BlockFragment bf = new BlockFragment();
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.fragment, bf).commit();

            }
        });

        Context context = v.getContext();
        myRecyclerView = v.findViewById(R.id.list);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        myArrayAdapter = new MyArrayAdapter(packageManager, app);
        myRecyclerView.setAdapter(myArrayAdapter);
        ed = v.findViewById(R.id.editTextSearch);
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return v;
    }

    private class MyTask extends AsyncTask < String, Void, List < app >> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(List < app > result) {
            super.onPostExecute(result);
            for (int i = 0; i < result.size(); i++)
                for (int k = i; k < result.size(); k++)
                    if (result.get(i).aiGet().loadLabel(packageManager).toString().compareTo(result.get(k).aiGet().loadLabel(packageManager).toString()) > 0) {
                        app a = result.get(i);
                        result.set(i, result.get(k));
                        result.set(k, a);
                    }
            app.addAll(result);
            myArrayAdapter.notifyDataSetChanged();
            ed.setVisibility(View.VISIBLE);
            fab.setVisibility(View.VISIBLE);
            dialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected List < app > doInBackground(String...params) {
            List < app > result;

            try {
                packageManager = getContext().getPackageManager();
                List < ApplicationInfo > appList;
                result = new ArrayList < > ();
                appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
                apps = HomeActivity.myAppDatabase.myDao().getApps();

                for (ApplicationInfo ai: appList) {
                    int flag = 0;
                    for (Package p: apps)
                        if (p.getName().equals(ai.processName)) {
                            flag = 1;
                            break;
                        }
                    if (flag == 0) {
                        app a = new app();
                        a.aiSet(ai);
                        result.add(a);
                    }
                }
                return result;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }
    private List < ApplicationInfo > checkForLaunchIntent(List < ApplicationInfo > list) {
        ArrayList < ApplicationInfo > applist = new ArrayList < > ();
        for (ApplicationInfo info: list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName))
                    applist.add(info);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return applist;
    }
    private void filter(String text) {
        //new array list that will hold the filtered data
        List < app > filterdNames = new ArrayList < > ();

        //looping through existing elements
        for (app s: app) {
            //if the existing elements contains the search input
            if (s.aiGet().processName.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        myArrayAdapter.filterList(filterdNames);
    }

}