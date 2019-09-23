package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.lamudi.phonefield.PhoneEditText;
import com.tapadoo.alerter.Alerter;

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

import static android.widget.Toast.LENGTH_SHORT;

public class ActivityRegistration2 extends AppCompatActivity {
    boolean check =false;
    TextView lblReturnLogin;
    Button btnRegister;
    Context ctx;
    RadioGroup groupbiller;
    String userType ="";
    int typeId= -1, code;
    PhoneEditText txtContactNo;
    EditText txtEmail,txtuname,txtpassword,txtrepassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        initialize();
    }


    private void initialize() {
        try
        {
            ctx = ActivityRegistration2.this;
            txtEmail = findViewById(R.id.txt_login_email);

            txtContactNo = findViewById(R.id.txt_login_contactNo);
            txtContactNo.setDefaultCountry("PH");

            txtuname = findViewById(R.id.txt_login_username);
            txtrepassword = findViewById(R.id.txt_login_re_password);
            groupbiller = findViewById(R.id.txt_login_userType);
            txtpassword = findViewById(R.id.txt_login_password);
            btnRegister = findViewById(R.id.btnRegister);
            code = methods.getCode();

            groupbiller.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i)
                    {
                        case R.id.biller:
                            userType = "biller";
                            typeId = 2;
                            break;
                        case R.id.user:
                            userType = "user";
                            typeId = 1;
                            break;
                        default:
                            userType ="None";
                            break;
                    }
                }
            });

            btnRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    methods.vibrate(ctx);
                    if (validateTxtInput())
                    {
                        if (!(txtEmail.getText().toString().contains("@") && txtEmail.getText().toString().contains(".com")))
                        {
                            txtEmail.setError("!");
                            message.warning("Email must contain '@' and '.com' ex: hnadela@gmail.com",ctx);
                            return;
                        }
                        else
                        {
                            if (!(txtpassword.getText().toString().length() > 8))
                            {
                                message.warning("Password must be greater than 8 characters!",ctx);
                                txtpassword.setError("Password must be greater than 8 characters!");
                                return;
                            }
                            else
                            {
                                if (!(userType.equals("")))
                                {
                                    RegistrationSingleton reg = RegistrationSingleton.getInstance();
                                    reg.setEmail(""+txtEmail.getText().toString());
                                    reg.setContactNo(""+txtContactNo.getPhoneNumber().toString().replace("+",""));
                                    reg.setUsername(""+txtuname.getText().toString());
                                    reg.setPassword(""+txtpassword.getText().toString());
                                    reg.setRoledId(""+getRole());
                                    reg.setBiller(getIsBiller());
                                    int code = getCode();

                                    final Map<String,String> params = new HashMap<>();
                                    params.put("lname",reg.getLname());
                                    params.put("fname",reg.getFname());
                                    params.put("mi",reg.getMi());
                                    params.put("email",reg.getEmail());
                                    params.put("contact",reg.getContactNo());
                                    params.put("username",reg.getUsername());
                                    params.put("password",reg.getPassword());
                                    params.put("userType",""+typeId);
                                    params.put("codeConfirmation",""+code);

                                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                                    alertDialogBuilder.setTitle("Save Changes");
                                    alertDialogBuilder.setMessage("Do you want to SAVE or Cancel your changes?. ");
                                    alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
                                    alertDialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            register(params);
                                            // Toast.makeText(ctx, "Phone number: "+RegistrationSingleton.getInstance().getContactNo(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                    Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                    Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                    bq.setTextColor(Color.BLACK);
                                    ba.setTextColor(Color.BLACK);
                                }
                                else
                                {
                                    message.error("Please select user type!",ctx);
                                }
                            }
                        }
                    }
                }
            });

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
    private int getCode()
    {
        int code = methods.getCode();
        savePreference("code",""+code);
        int CodeFpref = Integer.parseInt(getPreference("code"));
        if (CodeFpref == code ) {
            code = methods.getCode();
            return code;
        }
        else
            return code;
    }

    private void register(final Map<String,String> params) {
        try
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "registration.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //   Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.getString("code").equals("0"))
                            message.error(""+object.getString("message"),ctx);
                        else if (object.getString("code").equals("1"))
                            message.error(""+object.getString("message"),ctx);
                        else if (object.getString("code").equals("2"))
                            message.error(""+object.getString("message"),ctx);
                        else if (object.getString("code").equals("3")) {
                            if (typeId == 1) {
                                String contactNo = RegistrationSingleton.getInstance().getContactNo();
                                String msg = "Hello "+RegistrationSingleton.getInstance().getFname()+" "+RegistrationSingleton.getInstance().getLname()+"! Thanks for registering with Mr.FinMan. Please use this code for verification "+params.get("codeConfirmation")+".  \n\n Regards Mr.FinMan.";

                                // Toast.makeText(ctx, ""+contactNo, Toast.LENGTH_SHORT).show();
                                send(contactNo,msg);

                                Intent intent = new Intent(ctx,Activity_Confirmation.class);
                                intent.putExtra("type","user");
                                intent.putExtra("username",RegistrationSingleton.getInstance().getUsername());
                                startActivity(intent);

                            } else if (typeId == 2)
                            {
                                Intent intent = new Intent(ctx,Activity_Registration_biller.class);
                                intent.putExtra("username",RegistrationSingleton.getInstance().getUsername());
                                startActivity(intent);
                                finish();
                            }
                        }
                        else if (object.getString("code").equals("4"))
                            message.error(""+object.getString("message"),ctx);
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
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }


    private Boolean getIsBiller() {
        if (userType.equals("biller"))
            return true;
        return false;
    }

    private int getRole() {
        if (userType.equals("biller"))
            return 2;
        else if (userType.equals("user"))
            return 1;
        return 0;
    }

    private Boolean validateTxtInput() {
        try
        {
            if (txtEmail.getText().toString().trim().equals(""))
            {
                txtEmail.setError("Email must not be empty!");
                return false;
            }
            else if (txtContactNo.getPhoneNumber().toString().trim().equals(""))
            {
                txtContactNo.setError("Contact number must not be empty!");
                return false;
            }
            else if (!(txtContactNo.isValid())) {
                txtContactNo.setError("Not valid phone number!");
                return false;
            }
            else if (txtuname.getText().toString().trim().equals(""))
            {
                txtuname.setError("Username must not be empty!");
                return false;
            }
            else if (txtpassword.getText().toString().trim().equals(""))
            {
                txtpassword.setError("Password must not be empty!");
                return false;
            }
            else if (txtrepassword.getText().toString().trim().equals(""))
            {
                txtrepassword.setError("Re-Password must not be empty!");
                return false;
            }
            else if (!(txtpassword.getText().toString().equals(txtrepassword.getText().toString())))
            {
                message.warning("Oops! Password do not match!",ctx);
                txtpassword.setError("");
                txtrepassword.setError("");
                return false;
            }
            txtContactNo.setError(null);
            return true;

        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private void send(String ph,String msg)
    {
        AndroidNetworking.get(methods.SMS_API_SERVER+"send.php?pnumber="+ph+"&message="+msg)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                    @Override
                    public void onError(ANError error) {

                    }
                });
    }


}
