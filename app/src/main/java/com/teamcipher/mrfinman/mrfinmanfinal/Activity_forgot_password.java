package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.UserAdaptor;
import Models.user;
import Singleton.RegistrationSingleton;
import Utils.message;
import Utils.methods;

public class Activity_forgot_password extends AppCompatActivity {
    Button btnfind;
    EditText txtEmail;
    Context ctx;

    ArrayList<user> userArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initialization();
        onclicks();
    }

    private void onclicks() {



        btnfind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(txtEmail.getText().toString().equals(""))) {

                    find(txtEmail.getText().toString());

                    final ProgressDialog progressDialog = new ProgressDialog(ctx);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Please wait....");
                    progressDialog.setTitle("Searching");
                    progressDialog.show();

                    new CountDownTimer(2000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            Toast.makeText(ctx, userArrayList.size() + " result(s) found!", Toast.LENGTH_SHORT).show();
                            progressDialog.hide();
                            Dialog dialog = new Dialog(ctx);
                            dialog.setContentView(R.layout.dialogue_show_user_list);
                            dialog.setCancelable(true);
                            TextView lbltitle = dialog.findViewById(R.id.title);
                            lbltitle.setText("Select you:");

                            ListView listView = dialog.findViewById(R.id.dialogue_listview_user_list);

                            UserAdaptor adaptor = new UserAdaptor(ctx, userArrayList);
                            listView.setAdapter(adaptor);
                            adaptor.notifyDataSetChanged();

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    user u = (user) adapterView.getItemAtPosition(i);
                                    //Toast.makeText(Activity_forgot_password.this, ""+u.getLname(), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(ctx, Activity_Confirmation.class);
                                    intent.putExtra("fullname",u.getLname()+", "+u.getFname());
                                    intent.putExtra("username", ""+u.getUsername());
                                    intent.putExtra("userId", "" + u.getUserId());
                                    intent.putExtra("type", "reset");
                                    intent.putExtra("contactNo", ""+u.getContactNo());
                                    //int code = methods.getCode();
                                    sendCode(getCode(), u);
                                    message.success("A code sent to your mobile number!",ctx);
                                    startActivity(intent);

                                }
                            });
                            dialog.create();
                            dialog.show();
                        }
                    }.start();


                }}



                private void find(String str) {
                    userArrayList.clear();
                    StringRequest stringRequest = new StringRequest(methods.server() + "getSearch.php?item=" + str, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i=0; i<jsonArray.length();i++)
                                {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    user u = new user();
                                    u.setLname(object.getString("lastname"));
                                    u.setFname(object.getString("firstname"));
                                    u.setMi(object.getString("MI"));
                                    u.setUsername(object.getString("username"));
                                    u.setContactNo(object.getString("contactNo"));
                                    userArrayList.add(u);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("TESTING"," Forgot password  "+error);
                        }
                    });
                    RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                    requestQueue.add(stringRequest);
                }



        });
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
                        Log.d("TESTING"," Forgot password  send code"+error);
                    }
                });
    }
    private void sendCode(int code,user u) {
        String msg = "Please use this code "+code+" for resetting password. Regards Mr.FinMan.";

        send(u.getContactNo(),msg);


        AndroidNetworking.get(methods.USER_API_SERVER+"updateCode.php?username="+u.getUsername()+"&code="+code+"")
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
                        Log.d("TESTING"," Forgot password  update code"+error);
                    }
                });



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
    public user getUser(String username)
    {
        for (user u:userArrayList) {
            if (u.getUsername() == username)
                return u;
        }
        return null;
    }
    private void initialization() {
        ctx = Activity_forgot_password.this;
        btnfind = findViewById(R.id.btn_sign_find);
        txtEmail = findViewById(R.id.txt_forgot_user);

    }
}
