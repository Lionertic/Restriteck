package com.restri_tech;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
    private static final int POS_CHANGE = 4;
    private static final int POS_NUMBER = 5;
    private static final int POS_UNINSTALL = 6;

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slidingRootNav;
    public static Display display;
    public static Drawable droid ;
    public static Rect droidTarget;
    public static SpannableString sassyDesc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
        display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        droid = ContextCompat.getDrawable(this, R.drawable.ic_action_number);
        // Tell our droid buddy where we want him to appear
        droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

        sassyDesc = new SpannableString("It allows you to go back, sometimes");
        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "sometimes".length(), sassyDesc.length(), 0);


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
                new SpaceItem(48),
                createItemFor(POS_CHANGE),
                createItemFor(POS_NUMBER),
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
            fm.beginTransaction().replace(R.id.container, iF).commit();

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
            case POS_NUMBER :
                change(false);
            SharedPreferences sd1 = getSharedPreferences("Forgot",0);
            sd1.edit().putBoolean("FirstN",true).commit();
            setTitle("Change");
            Forgot f = new Forgot();
            fm.beginTransaction().replace(R.id.container, f).commit();
        }
        slidingRootNav.closeMenu();
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
            SharedPreferences sd=getSharedPreferences("Worker", Context.MODE_PRIVATE);
            sd.edit().putBoolean("Now",false).commit();
            finish();
        }
        return super.onOptionsItemSelected(item);
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