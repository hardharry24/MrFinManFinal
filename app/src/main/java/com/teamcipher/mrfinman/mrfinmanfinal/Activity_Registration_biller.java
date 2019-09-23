package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.lamudi.phonefield.PhoneEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.RegistrationSingleton;
import Utils.message;
import Utils.methods;


public class Activity_Registration_biller extends AppCompatActivity {
    Context ctx;
    PhoneEditText txtContactNo;
    EditText txtEmail,txtName,txtAddress;
    Button btnRegister;
    int code = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_biller);

        initialization();
        onclicks();
    }

    private void onclicks() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (validateTxt())
                    {
                        methods.vibrate(ctx);
                        confirmation();
                    }
            }
        });
    }

    public void confirmation()
    {
        try
        {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to save changes?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Map<String,String> params = new HashMap<>();

                            params.put("username",RegistrationSingleton.getInstance().getUsername());
                            params.put("billername",txtName.getText().toString());
                            params.put("email",txtEmail.getText().toString());
                            params.put("address",txtAddress.getText().toString());
                            params.put("contact",txtContactNo.getPhoneNumber().toString().replace("+",""));
                            params.put("code",""+code);
                            saveRegistration(params);


                            String contactNo = txtContactNo.getPhoneNumber().replace("+","");
                            String msg = "Hello "+RegistrationSingleton.getInstance().getFname()+" "+RegistrationSingleton.getInstance().getLname()+"! from "+txtName.getText()+" Company/Store"+ " Thanks for registering with Mr.FinMan. Please use this code for verification "+code+".  \n\n Regards Mr.FinMan.";

                            // Toast.makeText(ctx, ""+contactNo, Toast.LENGTH_SHORT).show();
                            send(contactNo,msg);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what should happen when negative button is clicked
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

    private void send(String ph,String msg)
    {
        AndroidNetworking.get(methods.SMS_API_SERVER+"send.php?pnumber="+ph+"&message="+msg)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Utils.message.success(""+response,ctx);
                        //Toast.makeText(ctx, ""+response, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ANError error) {
                       // Utils.message.error(""+error,ctx);
                    }
                });
    }

    private void saveRegistration(final Map<String,String> params) {
        try
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "registration_biller.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject object = jsonArray.getJSONObject(0);
                        if (object.getString("code").equals("0"))
                            message.error(""+object.getString("message"),ctx);
                        else if (object.getString("code").equals("1"))
                            message.error(""+object.getString("message"),ctx);
                        else if (object.getString("code").equals("3"))
                        {
                            Intent intent = new Intent(ctx,Activity_Confirmation.class);
                            intent.putExtra("type","biller");
                            intent.putExtra("billerId",object.getString("billerId"));
                            startActivity(intent);
                            //Toast.makeText(ctx, "Biller Id "+object.getString("billerId"), Toast.LENGTH_SHORT).show();
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

    private void initialization() {
        try
        {
            code = methods.getCode();
            ctx = Activity_Registration_biller.this;
            btnRegister = findViewById(R.id.btnRegister);
            txtAddress = findViewById(R.id.txt_login_biller_address);
            txtEmail = findViewById(R.id.txt_login_biller_email_add);
            txtContactNo = findViewById(R.id.txt_login_contactNo);
            txtName = findViewById(R.id.txt_login_biller_name);

            txtContactNo.setDefaultCountry("PH");
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {

        }

        return false;
    }
    private Boolean validateTxt()
    {
        try
        {
            if (txtName.getText().toString().equals(""))
            {
                txtName.setError("!");
                return false;
            }
            else if (txtAddress.getText().toString().equals(""))
            {
                txtAddress.setError("!");
                return false;
            }
            else if (txtContactNo.getPhoneNumber().toString().equals(""))
            {
                txtContactNo.setError("!");
                return false;
            }
            if (!txtContactNo.isValid())
            {
                txtContactNo.setError("Not Valid!");
                return false;
            }
            else if (txtEmail.getText().toString().equals(""))
            {
                txtEmail.setError("!");
                return false;
            }
            else if (!(txtEmail.getText().toString().contains("@") || txtEmail.getText().toString().contains(".com")))
            {
                txtEmail.setError("Email must contain '@' and '.com' example: hndela@gmail.com");
                return false;
            }
            else if (txtEmail.getText().toString().equals(""))
            {
                txtEmail.setError("!");
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
