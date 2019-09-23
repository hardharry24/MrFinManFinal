package com.teamcipher.mrfinman.mrfinmanfinal.Biller;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;*/


import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.UserLogin;
import Singleton.UserToken;
import Utils.Logs;
import Utils.message;
import Utils.methods;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_main extends Fragment {
    Context ctx;
    View view;
    String username;
    int userId = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ctx = getActivity().getBaseContext();
        username = getPreference("username");
        LoadUserInfo();
        userId = Integer.parseInt(getPreference("userID"));

        ((Activity_dashboard_biller) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Dashboard" + "</font>"));
        view = inflater.inflate(R.layout.biller_fragment_main, container, false);


        getMyId(getPreference("username"));
        registerToken();
        return view;
    }

    private void registerToken() {
        try
        {
            String token = getPreferenceToken("Token");
            Logs.LOGS_BILLER(token+" === "+userId);
            AndroidNetworking.get(methods.PUSHNOTIF_API_SERVER+"registration.php?userId="+userId+"&Token="+token+"")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                        }
                        public void onError(ANError error) {
                            // message.error("On Token Save Error!\n"+error,ctx);
                        }
                    });
        }
        catch (Exception ex)
        {

        }
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
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private String getPreferenceToken(String key)
    {
        SharedPreferences preferences = getContext().getSharedPreferences("TOKEN",0);
        return preferences.getString(key,null);
    }

    private void getMyId(String username) {
        try
        {
            AndroidNetworking.get(methods.BILLER_API_SERVER+"getBillerId.php?username="+username+"")
                    .setTag("test")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject jobj = response.getJSONObject(0);
                                int code = jobj.getInt("code");
                                if (code == 1)
                                {
                                    int billerid = jobj.getInt("userId");
                                    UserLogin.getInstance().setBillerId(billerid);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        public void onError(ANError error) {
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }

}
