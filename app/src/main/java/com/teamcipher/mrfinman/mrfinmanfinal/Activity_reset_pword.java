package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Models.user;
import Utils.message;
import Utils.methods;

public class Activity_reset_pword extends AppCompatActivity {
    EditText txtPword,txtrePword;
    Button btnConfirm;
    TextInputLayout pwordLayout,repwordLayout;
    TextView lblInfo;
    Bundle bundle;
    Context ctx;
    String username = "";
    int code =0;
    TextView lblfullDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pword);

        initialize();
        onclicks();

    }
    private void onSave()
    {
        try
        {
            final String uname = username;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.USER_API_SERVER+"resetPword.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject j = jsonArray.getJSONObject(0);
                        int code = j.getInt("code");
                        String msg = j.getString("message");
                        switch (code)
                        {
                            case 0:
                                message.error(""+msg,ctx);
                                break;
                            case 1:
                                message.success(msg,ctx);
                                Intent intent = new Intent(ctx, Activity_login.class);
                                intent.putExtra("username",j.getString("username"));
                                startActivity(intent);
                                finish();
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("password",txtPword.getText().toString());
                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }
    private void onclicks() {
       btnConfirm.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               try
               {
                   if (validate())
                   {
                       onReset();
                   }
               }
               catch (Exception ex)
               {

               }
           }
       });
    }

    public void onReset()
    {
        try
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Message")
                    .setMessage("Confirm Changes?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try
                            {
                                onSave();
                            }
                            catch (Exception ex)
                            {

                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq.setTextColor(Color.BLACK);
            ba.setTextColor(Color.BLACK);
        }
        catch (Exception ex)
        {

        }
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private void savePreference(String key,String value)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    private void clearPreferences() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private boolean validate() {
        try
        {
            if ((txtPword.getText().toString().equals("")))
            {
                pwordLayout.setError("Password must be greater than 8 characters!");
                message.error("Empty Password!",ctx);
                return false;
            }
            else if ((txtrePword.getText().toString().equals("")))
            {
                repwordLayout.setError("Password must be greater than 8 characters!");
                message.error("Empty Password!",ctx);
                return false;
            }
            if ((txtPword.getText().toString().length() < 7 ))
            {
                pwordLayout.setError("Password must be greater than 8 characters!");
                repwordLayout.setError("Password must be greater than 8 characters!");
                message.error("Password must be greater than 8 characters! "+txtPword.getText().toString().length(),ctx);
                return false;
            }
            else if (!(txtPword.getText().toString().equals(txtrePword.getText().toString())))
            {
                pwordLayout.setError("Password not match!");
                repwordLayout.setError("Password not match!");
                message.error("Password not match!",ctx);
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private void initialize() {
        try
        {
            bundle = getIntent().getExtras();
            ctx = Activity_reset_pword.this;
            txtPword = findViewById(R.id.txtPassword);
            txtrePword = findViewById(R.id.txtRePassword);
            btnConfirm = findViewById(R.id.btnCheck);
            pwordLayout = findViewById(R.id.pword);
            repwordLayout = findViewById(R.id.repword);
            lblInfo = findViewById(R.id.txtInfo);
            username = bundle.getString("username");

            lblInfo.setText(""+bundle.getString("fullname")+"\n"+username);
        }
        catch (Exception ex)
        {

        }

    }
    private int getCode()
    {
        int code = methods.getCode();
        savePreference("code",""+code);
        if (getPreference("code") == ""+code )
            return methods.getCode();
        else
            return code;
    }


}
