package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_dashboard_biller;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.UserLogin;
import Singleton.UserToken;
import Utils.message;
import Utils.methods;
import background.background;
import es.dmoral.toasty.Toasty;
import services.BillCheck;
import services.DebtCheck;
import services.GoalCheck;

import static android.widget.Toast.LENGTH_SHORT;

public class Activity_login extends AppCompatActivity implements View.OnClickListener {
    Button btnlogin;
    EditText txtUsername,txtPassword;
    Boolean checkLogin=false;
    TextView lblSignup,lblforgot;
    RelativeLayout relativeLayout;
    Context ctx;
    ProgressDialog progressDialog;
    TextInputLayout layoutPass,layoutUser;
    Boolean isFinish = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //clearPreferences();
        oncheckPreference();

        initialization();
        onClick();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            txtUsername.setText(bundle.getString("username"));
        }
    }

    public void  load(final int type,final String msg)
    {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                progressDialog.dismiss();
                if (type == 1) {
                    //User
                    btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                    btnlogin.setText("Sign in");

                    savePreference("username", txtUsername.getText().toString());
                    savePreference("password",txtPassword.getText().toString());
                    UserLogin.getInstance().setUsername(txtUsername.getText().toString());
                    UserLogin.getInstance().setPassword(txtPassword.getText().toString());
                    message.success(""+msg, ctx);
                    Intent intent = new Intent(ctx, Activity_dashboard.class);
                    startActivity(intent);
                    finish();

                } else if (type == 2) {
                    //Biller
                    savePreference("username", txtUsername.getText().toString());
                    savePreference("billerId",""+UserLogin.getInstance().getBillerId());

                    btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                    btnlogin.setText("Sign in");
                    LoadUserInfo();
                    message.success(msg, ctx);
                    startActivity(new Intent(ctx, Activity_dashboard_biller.class));
                    finish();
                } else if (type == 3) {
                    //Admin
                    savePreference("username", txtUsername.getText().toString());
                    btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                    btnlogin.setText("Sign in");
                    message.success(msg, ctx);

                    LoadUserInfo();
                    startActivity(new Intent(ctx, Activity_dashboard_admin.class));
                    finish();
                }
            }
        }.start();
    }


    private void oncheckPreference() {
        try {
            if (!(getPreference("username").toString().equals(""))) {
                if(getPreference("roleId").equals("1"))
                {
                    startActivity(new Intent(this, Activity_dashboard.class));
                    finish();
                }
                else if(getPreference("roleId").equals("2"))
                {
                    startActivity(new Intent(this, Activity_dashboard_biller.class));
                    finish();
                }
                else if(getPreference("roleId").equals("3"))
                {
                    startActivity(new Intent(this, Activity_dashboard_admin.class));
                    finish();
                }
            }
        }
        catch (Exception ex)
        {

        }
    }

    private void onClick() {
        btnlogin.setOnClickListener(this);
        lblSignup.setOnClickListener(this);
        lblforgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.vibrate(ctx);
                startActivity(new Intent(Activity_login.this,Activity_forgot_password.class));
            }
        });
    }

    private void initialization() {
        layoutPass = findViewById(R.id.txtLayout_password);
        layoutUser = findViewById(R.id.txtLayout_username);
        ctx  = Activity_login.this;
        btnlogin = findViewById(R.id.login_btn_login);
        txtUsername = findViewById(R.id.login_txt_username);
        txtPassword = findViewById(R.id.login_txt_password);
        relativeLayout = findViewById(R.id.relative_activity_login);
        lblforgot = findViewById(R.id.lbl_login_forgotPass);
        lblSignup = findViewById(R.id.login_lbl_sign_up);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.login_btn_login:
                methods.vibrate(this);
                validateTxtInput();
                if (checkLogin)
                {
                    btnlogin.setEnabled(false);
                    btnlogin.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_success));
                    btnlogin.setText("Loading....");
                    progressDialog.setTitle("Authenticating");
                    progressDialog.setMessage("Please wait.....");
                    progressDialog.show();
                    login();
                }

                break;
            case R.id.login_lbl_sign_up:
                methods.vibrate(this);
                Intent intent = new Intent(this,ActivityRegistration.class);
                startActivity(intent);
                break;
        }
    }



    private void login() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    if(jsonArray.length() > 0) {
                        JSONObject object = jsonArray.getJSONObject(0);

                        int code = object.getInt("code");

                        switch (code) {
                            case 0:

                                    String params = "";
                                    message.warning("" + object.getString("message"), ctx);
                                    if (object.getInt("type") == 1) {
                                        params = object.getString("params");
                                        String user = object.getString("fullname");
                                        confirmResetCode(user, params, 1);
                                    } else {
                                        params = object.getString("params");
                                        String user = object.getString("fullname");
                                        confirmResetCode(user, params, 2);
                                    }
                                progressDialog.hide();
                                layoutUser.setError(null);
                                layoutPass.setError(null);

                                break;
                            case 1:
                                LoadUserInfo();

                                int type = object.getInt("type");
                                load(type,object.getString("message"));
                                break;
                            case 2:
                                message.alertError(ctx,""+object.getString("message"));
                                progressDialog.hide();
                                btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                                btnlogin.setText("Sign in");
                                btnlogin.setEnabled(true);
                                layoutUser.setError("Incorrect Username");
                                layoutPass.setError("Incorrect Password");
                                break;
                            case 3:
                                message.alertError(ctx,""+object.getString("message"));
                                progressDialog.hide();
                                btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                                btnlogin.setText("Sign in");
                                btnlogin.setEnabled(true);

                                layoutUser.setError(null);
                                layoutPass.setError(null);

                                break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("ERROR LOGIN",response.toString());
                    Log.d("TESTING","Lgin Error "+e);
                    progressDialog.hide();
                    layoutUser.setError(null);
                    layoutPass.setError(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                btnlogin.setText("Sign in");
                Toasty.error(Activity_login.this,"NO INTERNET CONNECTION!",R.drawable.ic_info_outline_black_24dp).show();
                btnlogin.setEnabled(true);
                progressDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",txtUsername.getText().toString());
                params.put("password",txtPassword.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {

        }

        return false;

    }

    private void LoadUserInfo() {
        //userLoginInfo.php
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"userLoginInfo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
               /// Toast.makeText(ctx, "Response : "+response, Toast.LENGTH_SHORT).show();
                try {

                    JSONArray jarray = new JSONArray(response);
                    JSONObject jsonObject = jarray.getJSONObject(0);


                    UserLogin.getInstance().setUser_ID(jsonObject.getInt("userID"));
                    UserLogin.getInstance().setUsername(jsonObject.getString("username"));
                    UserLogin.getInstance().setLname(jsonObject.getString("lastname"));
                    UserLogin.getInstance().setMi(jsonObject.getString("MI"));
                    UserLogin.getInstance().setFname(jsonObject.getString("firstname"));
                    UserLogin.getInstance().setContactNo(jsonObject.getString("contactNo"));
                    UserLogin.getInstance().setEmail(jsonObject.getString("email"));
                    UserLogin.getInstance().setBillerId(jsonObject.getInt("billerId"));
                    UserLogin.getInstance().setRole(jsonObject.getInt("roleId"));

                   // Toast.makeText(ctx, ""+jsonObject.getString("billerId"), Toast.LENGTH_SHORT).show();


                    savePreference("userID",""+UserLogin.getInstance().getUser_ID());
                    savePreference("username",""+UserLogin.getInstance().getUsername());
                    savePreference("lastname",""+UserLogin.getInstance().getLname());
                    savePreference("firstname",""+UserLogin.getInstance().getFname());
                    savePreference("MI",""+UserLogin.getInstance().getMi());
                    savePreference("contactNo",UserLogin.getInstance().getContactNo());
                    savePreference("email",UserLogin.getInstance().getEmail());
                    savePreference("roleId",""+UserLogin.getInstance().getRole());

                    if(UserLogin.getInstance().getBillerId() == 0)
                        savePreference("billerId","NOT");
                    else
                        savePreference("billerId",""+UserLogin.getInstance().getBillerId());

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ctx, ""+e.toString(), LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              message.error("No Internet Connection!",ctx);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",txtUsername.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void confirmResetCode(String user, final String params, final int type)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle("Confirmation")
                //set message
                .setMessage("Confirm "+user+" you? to resend confirmation code.")
                //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ctx,Activity_Confirmation.class);
                        if (type == 1)
                        {
                            intent.putExtra("username",params);
                            intent.putExtra("type","user");
                        }
                        else if (type == 2)
                        {
                            intent.putExtra("billerId",params);
                            intent.putExtra("type","biller");
                        }

                        startActivity(intent);

                    }
                })
                //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnlogin.setEnabled(true);
                        progressDialog.hide();
                        btnlogin.setBackgroundColor(getResources().getColor(R.color.btnLogin));
                        btnlogin.setText("Sign in");
                    }
                })
                .show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.BLACK);
        ba.setTextColor(Color.BLACK);
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


    public void validateTxtInput()
    {
        if(TextUtils.isEmpty(txtUsername.getText().toString().trim()))
        {
            layoutUser.setError("Username cannot be empty!");
            checkLogin = false;
            btnlogin.setEnabled(true);
            return;
        }
        if(TextUtils.isEmpty(txtPassword.getText().toString().trim()))
        {
            layoutPass.setError("Password cannot be empty!");
            checkLogin = false;
            btnlogin.setEnabled(true);
            return;
        }
        else
        {
            btnlogin.setEnabled(true);

            checkLogin = true;
            return;
        }

    }
}
