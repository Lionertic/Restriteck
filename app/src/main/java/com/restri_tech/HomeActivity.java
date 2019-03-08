package com.restri_tech;

import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.restri_tech.DB.MyAppDatabase;
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