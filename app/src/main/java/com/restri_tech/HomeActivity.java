package com.restri_tech;

import android.app.ActivityManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;

import com.restri_tech.DB.MyAppDatabase;
import com.restri_tech.Forgot.Forgot;
import com.restri_tech.Fragments.BlockFragment;
import com.restri_tech.Fragments.HomeFragment;
import com.restri_tech.Fragments.InstallFragment;
import com.restri_tech.Fragments.Main;
import com.restri_tech.menu.DrawerAdapter;
import com.restri_tech.menu.DrawerItem;
import com.restri_tech.menu.SimpleItem;
import com.restri_tech.menu.SpaceItem;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {

    public static MyAppDatabase myAppDatabase;
    private static final int POS_HOME = 0;
    private static final int POS_INSTALLED = 1;
    private static final int POS_BLOCKED = 2;
    private static final int POS_CHANGE = 3;
    private static final int POS_UNINSTALL = 5;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_INSTALLED),
                createItemFor(POS_BLOCKED),
                createItemFor(POS_CHANGE),
                new SpaceItem(48),
                createItemFor(POS_UNINSTALL)));
        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);
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
            fm.beginTransaction().replace(R.id.container, hf).commit();
        }
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()> 0) {
            getSupportFragmentManager().popBackStack();
        }else{
            Toast.makeText(getApplicationContext(), "Cant close", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();
        switch (position){
            case POS_HOME :
                change(false);
                setTitle("Home");
                HomeFragment hf = new HomeFragment();
                fm.beginTransaction().replace(R.id.container, hf).commit();
                break;
            case POS_INSTALLED :
                change(false);
                setTitle("Installed Apps");
                InstallFragment iF = new InstallFragment();
                fm.beginTransaction().replace(R.id.container, iF).commit();
                break;
            case POS_BLOCKED :
                change(false);
                setTitle("Blocked Apps");
                BlockFragment bf = new BlockFragment();
                fm.beginTransaction().replace(R.id.container, bf).commit();
                break;
            case POS_CHANGE :
                change(true);
                setTitle("Change Password");
                Main m = new Main();
                fm.beginTransaction().replace(R.id.container, m).commit();
                break;
//        }else if(id == R.id.security){
//            change(false);
//            SharedPreferences sd1 = getSharedPreferences("Forgot",0);
//            sd1.edit().putBoolean("FirstN",true).commit();
//            setTitle("Change");
//            Forgot f = new Forgot();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, f).commit();
//        }
        }
        slidingRootNav.closeMenu();
        //showFragment(selectedScreen);
    }

    private void showFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.ld_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.ld_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }

//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//        if (id == R.id.home) {
//            change(false);
//            setTitle("Home");
//            HomeFragment hf = new HomeFragment();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, hf).commit();
//        } else if (id == R.id.bapps) {
//            change(false);
//            setTitle("Blocked Apps");
//            BlockFragment bf = new BlockFragment();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, bf).commit();
//        } else if (id == R.id.pass) {
//            change(true);
//            setTitle("Change Password");
//            Main m = new Main();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, m).commit();
//        } else if (id == R.id.iapps) {
//            change(false);
//            setTitle("Installed Apps");
//            InstallFragment iF = new InstallFragment();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, iF).commit();
//        }else if(id == R.id.security){
//            change(false);
//            SharedPreferences sd1 = getSharedPreferences("Forgot",0);
//            sd1.edit().putBoolean("FirstN",true).commit();
//            setTitle("Change");
//            Forgot f = new Forgot();
//            FragmentManager fm = getSupportFragmentManager();
//            fm.beginTransaction().replace(R.id.fragment, f).commit();
//        }
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
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