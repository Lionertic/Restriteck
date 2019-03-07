package com.restri_tech;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.restri_tech.DB.MyAppDatabase;
import com.restri_tech.Forgot.Forgot;
import com.restri_tech.Fragments.BlockFragment;
import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.Fragments.InstallFragment;
import com.restri_tech.Fragments.Main;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static MyAppDatabase myAppDatabase;
    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        myAppDatabase = Room.databaseBuilder(getApplicationContext(), MyAppDatabase.class, "userdb").fallbackToDestructiveMigration().allowMainThreadQueries().build();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", 0);
        if (sharedPreferences.getBoolean("home", true)) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("home", false);
            editor.commit();

            setTitle("Installed Apps");
            InstallFragment iF = new InstallFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, iF).commit();

        }
        else {

            setTitle("Home");
            HomeFragment hf = new HomeFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, hf).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getSupportFragmentManager().getBackStackEntryCount()> 0) {
            getSupportFragmentManager().popBackStack();
        }else{
            Toast.makeText(getApplicationContext(), "Cant close", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Help:\n\nInstalled apps:\n\t\tSelect apps you want to restrict and click the icon present at bottom of the screen\n\nBlocked apps:\n\t\tTimer of each app can be edited. swipe to remove the app from the list.\n\nHome: \n\t\tSelect start service to start restricting apps.Select stop service to stop restricting apps");
            alertDialogBuilder.setPositiveButton("ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            //startActivity(new Intent(getBaseContext(),PassCheck.class));
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }else if(id == R.id.exit){
            SharedPreferences sd=getSharedPreferences("Worker",Context.MODE_PRIVATE);
            sd.edit().putBoolean("Now",false).commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home) {
            change(false);
            setTitle("Home");
            HomeFragment hf = new HomeFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, hf).commit();
        } else if (id == R.id.bapps) {
            change(false);
            setTitle("Blocked Apps");
            BlockFragment bf = new BlockFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, bf).commit();
        } else if (id == R.id.pass) {
            change(true);
            setTitle("Change Password");
            Main m = new Main();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, m).commit();
        } else if (id == R.id.iapps) {
            change(false);
            setTitle("Installed Apps");
            InstallFragment iF = new InstallFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, iF).commit();
        }else if(id == R.id.security){
            change(false);
            SharedPreferences sd1 = getSharedPreferences("Forgot",0);
            sd1.edit().putBoolean("FirstN",true).commit();
            setTitle("Change");
            Forgot f = new Forgot();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment, f).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        change(false);
        SharedPreferences sharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("c", true).commit();
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.moveTaskToFront(getTaskId(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean("c", true)) {
            Intent i =new Intent(this, MainActivity.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NEW_TASK) ;
            startActivity(i);
            sharedPreferences.edit().putBoolean("c", false).commit();
        }
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("c", true).commit();
        change(false);
        super.onDestroy();
    }

    private void change(boolean b){
        SharedPreferences sd = getSharedPreferences("Home",0);
        sd.edit().putBoolean("First",b).commit();
        sd = getSharedPreferences("Pin",0);
        sd.edit().putBoolean("First",b).commit();
        sd = getSharedPreferences("Pattern",0);
        sd.edit().putBoolean("First",b).commit();
        sd = getSharedPreferences("Pass",0);
        sd.edit().putBoolean("First",b).commit();
    }


}