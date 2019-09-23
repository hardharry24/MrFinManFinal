package com.teamcipher.mrfinman.mrfinmanfinal;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapLabel;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Singleton.RegistrationSingleton;
import Utils.methods;

public class ActivityRegistration extends AppCompatActivity {
    boolean check =false;
    TextView lblReturnLogin;
    Button btnRegNext;
    Context ctx;
    EditText txtfname,txtlname,txtmi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initialize();
    }


    private void initialize() {
        try
        {
            ctx = ActivityRegistration.this;
            // lblReturnLogin = findViewById(R.id.txt);
            txtfname = findViewById(R.id.txt_login_firstname);
            txtlname = findViewById(R.id.txt_login_lastname);
            txtmi = findViewById(R.id.txt_login_MI);
            btnRegNext = findViewById(R.id.btnRegNext);

            btnRegNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateTxtInput())
                    {
                        methods.vibrate(ctx);

                        RegistrationSingleton reg = RegistrationSingleton.getInstance();
                        reg.setLname(""+txtlname.getText().toString().toUpperCase());
                        reg.setFname(""+txtfname.getText().toString().toUpperCase());
                        reg.setMi(""+txtmi.getText().toString().toUpperCase());


                        Intent intent = new Intent(ctx,ActivityRegistration2.class);
                        startActivity(intent);
                    }
                }
            });
        }
        catch (Exception ex)
        {

        }

    }

    private Boolean validateTxtInput() {
        try
        {
            if (txtlname.getText().toString().trim().equals(""))
            {
                txtlname.setError("Last name must not empty!");
                return false;
            }
            else if (txtfname.getText().toString().trim().equals(""))
            {
                txtfname.setError("First name must not empty!");
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }
}
