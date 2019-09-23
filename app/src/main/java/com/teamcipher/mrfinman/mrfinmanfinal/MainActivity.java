package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_biller_manage;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_dashboard_biller;


/*
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
*/


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import Models.Priorities;
import Models.user;
import Singleton.PrioritiesSingleton;
import Singleton.UserListSingleton;
import Utils.methods;

import static Utils.methods.isOnline;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AndroidNetworking.initialize(getApplicationContext());


        Intent intent = new Intent(getApplicationContext(), Activity_login.class);
        startActivity(intent);
        finish();

       // waitload();
      /*  if (isOnline())
            Log.d("ONLINE","TRUE");
        else
            Log.d("ONLINE","FALSE");*/
      /*  for(Priorities p: PrioritiesSingleton.getInstance().getList())
            Toast.makeText(this, p.getCategoryId()+" "+p.getPercentage(), Toast.LENGTH_SHORT).show();*/
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }



    public void waitload()
    {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if (isOnline()) {
                   // getAllUser();
                    //savePreference("isOnline","1");

                    Intent intent = new Intent(getApplicationContext(), Activity_login.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    /*AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
                            .setIcon(getResources().getDrawable(R.drawable.ic_info_outline_black_24dp))
                            .setTitle("Message")
                            .setMessage("No Internet Connection!\n\nMr.FinMan will close!")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    moveTaskToBack(true);
                                    finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    bq.setTextColor(Color.BLACK);
                    ba.setTextColor(Color.BLACK);*/
                    Toast.makeText(MainActivity.this, "No Internet Connection\nApplication close!", Toast.LENGTH_SHORT).show();
                    moveTaskToBack(true);
                    finish();
                    
                }
            }
        }.start();
    }

    private void savePreference(String key, String value) {
        SharedPreferences preferences = getSharedPreferences("credentials", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private void getAllUser() {
        AndroidNetworking.get(methods.server()+"user.php")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0;i<response.length();i++)
                        {
                            try {
                                JSONObject jObject = response.getJSONObject(i);
                                user u = new user();
                                u.setUserId(jObject.getInt("userId"));
                                u.setLname(""+jObject.getString("lname"));
                                u.setFname(""+jObject.getString("fname"));
                                u.setMi(""+jObject.getString("mi"));
                                u.setUsername(""+jObject.getString("username"));
                                u.setContactNo(""+jObject.getString("contactNo"));
                                u.setEmail(""+jObject.getString("email"));
                                if(jObject.getInt("isActive") == 1)
                                    u.setActive(true);
                                else
                                    u.setActive(false);

                                u.setRoleId(jObject.getInt("roleId"));

                                UserListSingleton.getInstance().addtoList(u);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        //Utils.message.error(""+error,MainActivity.this);
                    }
                });
    }




}
