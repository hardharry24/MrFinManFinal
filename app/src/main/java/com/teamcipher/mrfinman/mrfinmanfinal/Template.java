package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.lamudi.phonefield.PhoneEditText;

import org.angmarch.views.NiceSpinner;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import Utils.message;
import Utils.methods;
import es.dmoral.toasty.Toasty;

public class Template extends AppCompatActivity {
    TextView lbldate;
    PhoneEditText phoneEditText;
    Calendar calendar,currentCalendar,finalCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        AndroidNetworking.initialize(getApplicationContext());

        phoneEditText =  findViewById(R.id.edit_text);
        phoneEditText.setDefaultCountry("PH");

        Button btn = findViewById(R.id.btnCheck);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;


                if (phoneEditText.isValid()) {
                    phoneEditText.setError(null);
                } else {
                    phoneEditText.setError("Not valid phone number!");
                    valid = false;
                }

                if (valid) {
                    Toast.makeText(Template.this,"Valid", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Template.this, "Valid", Toast.LENGTH_SHORT).show();
                }

                // Return the phone number as follows
                String phoneNumber = phoneEditText.getPhoneNumber();

                message.success(""+phoneNumber,Template.this);
            }
        });
    }


    private void onheaderActionBar(String title) {
        setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_goal_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            Intent intent = new Intent(this,Activity_dashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
