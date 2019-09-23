package com.teamcipher.mrfinman.mrfinmanfinal.Biller;

import android.app.AlertDialog;
import android.content.Context;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.teamcipher.mrfinman.mrfinmanfinal.fragment_history;
import com.teamcipher.mrfinman.mrfinmanfinal.fragment_main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Models.Biller.Biller;
import Models.biller;
import Singleton.BillerSingleton;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_dashboard_biller extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DrawerLayout drawer;
    FrameLayout frameLayout;
    NavigationView navigationView;
    String username;
    TextView lblUserlogin;
    biller billerAllInfo = new biller();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TypefaceProvider.registerDefaultIconSets();
        setContentView(R.layout.activity_dashboard_biller);
        //getUserInfo();

        initialization();
        onheaderBind("Dashboard");
        UserLogin.getInstance().setBillerId(Integer.parseInt(getPreference("billerId")));

        username = getPreference("username");
        getBillerInfo();
        populateBillerInfo();
    }
    private void savePreference(String key,String value)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    private void getBillerInfo() {
        AndroidNetworking.get(methods.BILLER_API_SERVER+"getBillerInfo.php?username="+username)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jObject = response.getJSONObject(0);
                            Biller b = new Biller();
                            b.setBillerId(jObject.getInt("billerId"));
                            b.setBillerName(jObject.getString("billerName"));
                            b.setBillerEmail(jObject.getString("billerEmail"));
                            b.setBillerContactno(jObject.getString("billerContactno"));

                            BillerSingleton.getInstance().setBillerInfo(b);
                            savePreference("billerName",jObject.getString("billerName"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Backgound Error","Income  ");
                        }

                    }
                    public void onError(ANError error) {
                        Log.d("Backgound Error","Income  ");
                    }
                });
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
        }
        return false;
    }


    private void onheaderBind(String title) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_gray);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#808080\">" +title+ "</font>")));

        NavigationView navigationView = findViewById(R.id.nav_view_biller);

        lblUserlogin = navigationView.getHeaderView(0).findViewById(R.id.lblUserlogin);
        lblUserlogin.setText("");
        lblUserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(),Activity_biller_profile.class));
            }
        });
    }

    private void populateBillerInfo()
    {
        final Call<biller> billerInfo = apIservice.BillerInfo(username);
        billerInfo.enqueue(new Callback<biller>() {
            @Override
            public void onResponse(Call<biller> call, Response<biller> response) {
                billerAllInfo.setId(response.body().getId());
                billerAllInfo.setLname(response.body().getLname());
                billerAllInfo.setFname(response.body().getFname());
                billerAllInfo.setMi(response.body().getMi());
                billerAllInfo.setName(response.body().getName());
                billerAllInfo.setUserId(response.body().getUserId());
                billerAllInfo.setEmail(response.body().getEmail());
                billerAllInfo.setContact(response.body().getContact());
                billerAllInfo.setRepContact(response.body().getRepContact());
                billerAllInfo.setRepEmail(response.body().getRepEmail());
                billerAllInfo.setRepUsername(response.body().getRepUsername());
                billerAllInfo.setRepPassword(response.body().getRepPassword());
                billerAllInfo.setAddress(response.body().getAddress());

                lblUserlogin.setText(billerAllInfo.getFullname()+"\n"+billerAllInfo.getName());
            }

            @Override
            public void onFailure(Call<biller> call, Throwable t) {
                Logs.LOGS_BILLER("Error in populateBillerInfo "+t);
            }
        });
    }

    public void fragmentRedirection(Fragment ctx)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_biller, ctx)
                .addToBackStack(null)
                .commit();
    }

    private void initialization() {
        try
        {
            fragmentRedirection(new frag_main());
            drawer =  findViewById(R.id.drawer_layout_biller);
            drawer.bringToFront();
            frameLayout = findViewById(R.id.fragment_container_biller);


            navigationView = findViewById(R.id.nav_view_biller);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.nav_charge_biller:
                            fragmentRedirection(new frag_chargebill());
                            break;
                        case R.id.nav_manage_bill:
                            Intent intent = new Intent(getApplicationContext(),Activity_biller_manage.class);
                            startActivity(intent);
                            break;
                        case R.id.nav_history_biller:
                            fragmentRedirection(new frag_history());
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
            fragmentRedirection(new frag_main());
        }
        catch (Exception ex)
        {

        }

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
    //

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
                            Intent intent  = new Intent(Activity_dashboard_biller.this, Activity_login.class);
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
