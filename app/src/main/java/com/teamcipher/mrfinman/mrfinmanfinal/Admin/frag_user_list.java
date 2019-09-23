package com.teamcipher.mrfinman.mrfinmanfinal.Admin;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.UserDetailsAdaptor;
import Adapters.UserListAdaptor;
import Models.user;
import Utils.message;
import Utils.methods;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_user_list extends Fragment {
    ArrayList<user> userArrayList = new ArrayList<>();
    ListView listView;
    Context ctx;
    View view;
    Dialog dialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Activity_dashboard_admin) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "User Account" + "</font>"));
        view = inflater.inflate(R.layout.admin_fragment_user_list, container, false);

        initialization();
        populate();
        onclicks();
        return view;
    }

    private void onclicks() {
        try
        {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    user u = (user)adapterView.getItemAtPosition(i);
                    showDetailsUser(u);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private void showDetailsUser(user u) {
        try
        {
            dialog = new Dialog(ctx);
            dialog.setContentView(R.layout.dialogue_admin_user_details);
            dialog.setCancelable(true);

            EditText lblfname,lbllname,lblmi,lblcontact,lblemail,lblusername;
            Button btnClose;
            lbllname = dialog.findViewById(R.id.lbl_user_Lname);
            lblfname = dialog.findViewById(R.id.lbl_user_Fname);
            lblmi = dialog.findViewById(R.id.lbl_user_MIname);
            lblcontact = dialog.findViewById(R.id.lbl_user_contactNo);
            lblemail = dialog.findViewById(R.id.lbl_user_email);
            lblusername = dialog.findViewById(R.id.lbl_user_username);
            btnClose = dialog.findViewById(R.id.btnClose);

            lbllname.setText(""+u.getLname());
            lblfname.setText(""+u.getFname());


            lblmi.setText(""+u.getMi());
            lblemail.setText(""+u.getEmail());
            lblusername.setText(""+u.getUsername());
            lblcontact.setText(""+u.getContactNo());
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    private void populate() {
        try
        {
            AndroidNetworking.get(methods.server()+"user.php")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i =0 ;i<response.length(); i++)
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
                                    u.setId(i+1);
                                    userArrayList.add(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ctx, ""+e, Toast.LENGTH_SHORT).show();
                                }

                            }

                            UserDetailsAdaptor adaptor = new UserDetailsAdaptor(ctx,userArrayList);
                            listView.setAdapter(adaptor);
                        }
                        public void onError(ANError error) {
                            message.error(""+error,ctx);
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }

    private void initialization() {
        try
        {
            ctx = getContext();
            listView = view.findViewById(R.id.listViewUsers);

        }
        catch (Exception ex)
        {

        }

    }

}
