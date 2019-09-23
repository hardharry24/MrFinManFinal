package com.teamcipher.mrfinman.mrfinmanfinal.Biller;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_expense;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Adapters.BillerListAdaptor;
import Adapters.Pager_biller_manage_bill;
import Adapters.UserAdaptor;
import Models.billerlist;
import Models.user;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_managebill extends Fragment {
    ListView listView;
    TabLayout tabLayout;
    View view;
    Context ctx;
    Toolbar toolbar;
    ArrayList<billerlist> billerlists= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        initialization(inflater,container);

        onclick();
        return view;
    }

    private void onclick() {


    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization(LayoutInflater inflater, ViewGroup container) {
        try
        {
            ((Activity_dashboard_biller) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Manage Bill" + "</font>"));
            view = inflater.inflate(R.layout.biller_fragment_manage_bill, container, false);
            //listView = view.findViewById(R.id.listview_manage_bill);
            ctx = getContext();


            toolbar = view.findViewById(R.id.toolbar);
            ((Activity_dashboard_biller) getActivity()).setSupportActionBar(toolbar);

            tabLayout = view.findViewById(R.id.tab_layout);
            tabLayout.addTab(tabLayout.newTab().setText("Pending"));
            tabLayout.addTab(tabLayout.newTab().setText("Paid"));
            tabLayout.addTab(tabLayout.newTab().setText("Overdue"));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


                final ViewPager viewPager =  view.findViewById(R.id.pager);
                final Pager_biller_manage_bill adapter = new Pager_biller_manage_bill(getFragmentManager(), tabLayout.getTabCount());
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        viewPager.setCurrentItem(tab.getPosition());
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
        }
        catch (Exception ex)
        {

        }
    }
}
