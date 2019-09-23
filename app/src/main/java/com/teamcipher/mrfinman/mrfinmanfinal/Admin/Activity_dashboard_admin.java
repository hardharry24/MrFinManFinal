package com.teamcipher.mrfinman.mrfinmanfinal.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_dashboard_biller;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.frag_main;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.teamcipher.mrfinman.mrfinmanfinal.fragment_main;

import java.util.Calendar;
import java.util.Date;

import Singleton.UserLogin;
import Utils.methods;

public class Activity_dashboard_admin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DrawerLayout drawer;
    FrameLayout frameLayout;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_dashboard_admin);


        initialization();
        onheaderBind("Dashboard");
        headerSetOnclicks();
    }
    //Header set to admin name and setting onclicks on it
    private void headerSetOnclicks() {
        NavigationView navigationView = findViewById(R.id.nav_view_admin);
        TextView lblUserlogin = navigationView.getHeaderView(0).findViewById(R.id.lblUserloginAdmin);
        ImageView imgView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        lblUserlogin.setText(getPreference("lastname")+", "+getPreference("firstname")+"\nAdmin");
        lblUserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentRedirection(new frag_main_admin());
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentRedirection(new frag_main_admin());
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    //Shared Preference
    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }


    //Change the header of the application
    private void onheaderBind(String title) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_gray);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#808080\">" +title+ "</font>")));
    }
    //Disable back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
        }
        return false;
    }
    //Code to redirect to other fragment
    public void fragmentRedirection(Fragment ctx)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin,ctx)
                .addToBackStack(null)
                .commit();
    }
    //Initialization of the widgets / Tools
    private void initialization() {

        drawer =  findViewById(R.id.drawer_layout_admin);
        drawer.bringToFront();
        frameLayout = findViewById(R.id.fragment_container_admin);

        navigationView = findViewById(R.id.nav_view_admin);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_manage_users:
                        fragmentRedirection(new frag_manage_admin());
                        break;
                    case R.id.nav_manage_priorities:
                        fragmentRedirection(new frag_manage_priorities());
                        break;
                    case R.id.nav_manage_billers:
                        fragmentRedirection(new frag_manage_admin_biller());
                        break;
                    case R.id.nav_logout:
                        logout();
                        clearPreferences();
                        break;

                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        fragmentRedirection(new frag_main_admin());

    }
    private void clearPreferences() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }

    }
    //Onback press code
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container_biller);
        if (f.getId() == 0x7f0a0385) {
            Log.i("BACK PRESSED", "BACK PRESSED");
        }else{
            super.onBackPressed();
        }

        if(getSupportFragmentManager().getBackStackEntryCount()>0)
        {
            fragmentRedirection(new frag_main());
        }
    }

    //Navigation drawer
    //16908332 is the actual ID of the navigation icon / menu icon
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == 16908332)
        {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);

            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_logout:
                logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }
    //Method to log out
    public void logout()
    {
        try
        {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to logout?")
                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            methods.resetInstance();
                            clearPreferences();
                            Intent intent  = new Intent(Activity_dashboard_admin.this, Activity_login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addButton("NO", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED  , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }
        catch (Exception ex)
        {

        }
    }






}
