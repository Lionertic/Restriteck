package com.restri_tech.Fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.restri_tech.DB.Package;
import com.restri_tech.HomeActivity;
import com.restri_tech.Adapter.BlockAdapter;
import com.restri_tech.R;

import java.util.ArrayList;
import java.util.List;


public class BlockFragment extends Fragment {

    // TODO: Customize parameters

    Button b;
    RecyclerView rv;
    List <Package> apps;
    PackageManager packageManager;
    EditText ed;

    public static BlockAdapter adapter;
    public static List < ApplicationInfo > appList, app;

    public BlockFragment() {}

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

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("MyPrefs", 0);
//        if (sharedPreferences.getBoolean("bapps", true)) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
//            alertDialogBuilder.setMessage("1.All the apps Restricted in your device will be listed here.\n\n2.Select the apps to add timer \n\n3.Click the set button .\n\n4.Click the icon at the bottom when you are done ");
//            alertDialogBuilder.setPositiveButton("ok",
//                    new DialogInterface.OnClickListener() {
//                        @SuppressLint("RestrictedApi")
//                        @Override
//                        public void onClick(DialogInterface arg0, int arg1) {
//                            FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
//                            fab.setVisibility(View.VISIBLE);
//                            fab.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    getActivity().setTitle("Home");
//                                    HomeFragment hf = new HomeFragment();
//                                    FragmentManager fm = getActivity().getSupportFragmentManager();
//                                    fm.beginTransaction().replace(R.id.fragment, hf).commit();
//                                }
//                            });
//                        }
//                    });
//
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.show();
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean("bapps", false);
//            editor.commit();
//        }

        View view = inflater.inflate(R.layout.fragment_block, container, false);

        // Set the adapter
        Context context = view.getContext();
        rv = view.findViewById(R.id.outputList);

        rv.setLayoutManager(new LinearLayoutManager(context));


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            public static final float ALPHA_FULL = 1.0f;

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;

                    Paint p = new Paint();
                    Bitmap icon;

                    if (dX < 0) {

                        //color : left side (swiping towards right)
                        p.setARGB(255, 244, 75, 66);

                        c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                                (float) itemView.getRight(), (float) itemView.getBottom(), p);

                        //icon : left side (swiping towards right)
                        icon = BitmapFactory.decodeResource(getActivity().getApplication().getResources(), R.drawable.ic_delete);
                        c.drawBitmap(icon,
                                (float) itemView.getRight() - convertDpToPx(16) - icon.getWidth(),
                                (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                                p);
                    }

                    // Fade out the view when it is swiped out of the parent
                    final float alpha = ALPHA_FULL - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);

                } else {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }

            private int convertDpToPx(int dp) {
                return Math.round(dp * (getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
            }
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition(); //swiped position

                if (direction == ItemTouchHelper.LEFT) { //swipe left


                    String p = app.get(position).processName;
                    Package pa = new Package();
                    pa.setName(p);
                    HomeActivity.myAppDatabase.myDao().delete(pa);
                    app.remove(position);
                    adapter.notifyItemRemoved(position);
                }

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(rv);
        adapter = new BlockAdapter(getContext(), app, packageManager);
        rv.setAdapter(adapter);

        ed = view.findViewById(R.id.editTextSearch);
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
        return view;
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


    private class MyTask extends AsyncTask < String, Void, List < ApplicationInfo >> {

        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(List < ApplicationInfo > result) {
            super.onPostExecute(result);
            app.addAll(result);
            adapter.notifyDataSetChanged();
            if(result.size() != 0 )
            ed.setVisibility(View.VISIBLE);
            dialog.dismiss();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected List < ApplicationInfo > doInBackground(String...params) {
            List < ApplicationInfo > result;

            try {
                packageManager = getContext().getPackageManager();
                apps = HomeActivity.myAppDatabase.myDao().getApps();
                appList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
                result = new ArrayList < > ();

                for (Package p: apps)
                    for (ApplicationInfo ai: appList)
                        if (p.getName().equals(ai.processName)) {
                            result.add(ai);
                            break;
                        }
                return result;
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }

    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        List < ApplicationInfo > filterdNames = new ArrayList < > ();

        //looping through existing elements
        for (ApplicationInfo s: app) {
            //if the existing elements contains the search input
            if (s.processName.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }
        //calling a method of the adapter class and passing the filtered list
        adapter.filterList(filterdNames);
    }

}