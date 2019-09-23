package com.teamcipher.mrfinman.mrfinmanfinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import Models.user;
import Singleton.RegistrationSingleton;
import Utils.message;
import Utils.methods;

public class Activity_Confirmation extends AppCompatActivity {
    TextView btnResend;
    EditText txtCode;
    Button btnCheck;
    Context ctx;
    Bundle bundle;
    String type = "",username="",contactNo ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conifirmation);

        initialization();
        onclicks();
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
    private int getCode()
    {
        int code = methods.getCode();
        savePreference("code",""+code);
        if (getPreference("code") == ""+code )
            return methods.getCode();
        else
            return code;
    }

    private void onclicks() {
        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.vibrate(ctx);
                sendCode(getCode(), contactNo,username);
                message.success("Code has been successfully sent!",ctx);
            }
        });
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.vibrate(ctx);

                onconfirm();
            }
        });
    }

    private void onconfirm() {
        if (!(txtCode.getText().toString().equals(""))) {

            Map<String, String> params = new HashMap<>();
            if (type.equals("user")) {
                params.clear();
                params.put("username", bundle.getString("username"));
                params.put("type","user");
                params.put("code", txtCode.getText().toString());

                codeChecking(params);
            } else if (type.equals("biller")) {
                params.clear();
                params.put("type","biller");
                params.put("billerId", bundle.getString("billerId"));

                //oast.makeText(ctx, ""+bundle.getString("billerId"), Toast.LENGTH_SHORT).show();
                params.put("code", txtCode.getText().toString());
                codeChecking(params);
            }
            else if (type.equals("reset")) {
                params.clear();
                params.clear();
                params.put("username", bundle.getString("username"));
                params.put("type","reset");
                params.put("code", txtCode.getText().toString());

                codeChecking(params);
            }
        }
        else
            txtCode.setError("!");
    }

    private void initialization() {
        bundle = getIntent().getExtras();
        ctx = Activity_Confirmation.this;
        btnCheck = findViewById(R.id.btnCheck);
        btnResend = findViewById(R.id.btnResend);
        txtCode = findViewById(R.id.txtCode);
        type = bundle.getString("type");
        username = bundle.getString("username");
        contactNo = bundle.getString("contactNo");

       // Toast.makeText(ctx, ""+type, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            Intent intent = new Intent(this,Activity_login.class);
            startActivity(intent);
            finish();
        }

        return false;
    }

    public void codeChecking(final Map<String,String> params)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "confirmationCheck.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject object = jsonArray.getJSONObject(0);
                    if (object.getString("code").equals("0"))
                    {
                        message.error(""+object.getString("message"),ctx);
                        txtCode.setText("");
                    }
                    else if (object.getString("code").equals("3"))
                        message.error(""+object.getString("message"),ctx);
                    else if (object.getString("code").equals("1"))
                    {
                        String type = object.getString("type");
                        if (type.equals("user") || type.equals("biller")) {
                            message.success("" + object.getString("message"), ctx);
                            Intent intent = new Intent(ctx, Activity_login.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Intent intent = new Intent(ctx, Activity_reset_pword.class);
                            intent.putExtra("username", username);
                            intent.putExtra("fullname",bundle.getString("fullname"));
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TESTING","POP Confirmation Check Activity_confirmation");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //message.error("No Internet Connection!",ctx);
                Log.d("TESTING","POP Confirmation Check Activity_confirmation "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void send(final String pn, String msg)
    {
        AndroidNetworking.get(methods.SMS_API_SERVER+"send.php?pnumber="+pn+"&message="+msg)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Utils.message.success("\n\n"+pn+" "+response,ctx);
                        //Toast.makeText(ctx, ""+response, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("TESTING","POP send confirmation Activity_confirmation "+error);
                    }
                });
    }
    private void sendCode(int code, String contactNo,String username) {
        String msg = "Please use this code "+code+" for resetting password. Regards Mr.FinMan.";

        send(contactNo,msg);


        AndroidNetworking.get(methods.USER_API_SERVER+"updateCode.php?username="+username+"&code="+code+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Utils.message.error(""+error,ctx);
                    }
                });



    }
}
