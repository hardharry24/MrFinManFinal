package com.teamcipher.mrfinman.mrfinmanfinal.Biller;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.HistoryAdaptor;
import Adapters.UserAdaptor;
import Models.Biller.Biller;
import Models.User.history;
import Models.user;
import Singleton.BillerSingleton;
import Singleton.HistorySingleton;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_history extends Fragment {
    ListView listView;
    Context ctx;
    EditText searchView;
    EditText txtSearch;
    ImageView btnSearhClose;
    HistoryAdaptor adaptor;
    String username;
    View view;

    ArrayList<history> histories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        ((Activity_dashboard_biller) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "History" + "</font>"));
        view = inflater.inflate(R.layout.biller_fragment_history, container, false);
        initialization();
        populate();
        return view;
    }

    private void initialization() {
        try
        {
            ctx = getContext();
            username = getPreference("username");
            txtSearch = view.findViewById(R.id.txtsearch);
            listView  = view.findViewById(R.id.listViewHistory);

            searchTxt();
        }
        catch (Exception ex)
        {

        }

    }

    private void searchTxt() {
        try
        {
            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    filter(txtSearch.getText().toString().toLowerCase());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    history h = (history)adapterView.getItemAtPosition(i);
                    showDetails(h);
                }
            });
        }
        catch (Exception ex)
        {

        }

    }

    private void showDetails(history hist) {
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialogue_history_details);
        dialog.setCancelable(true);
        EditText lblAction,lbldetails,lbldate;
        ImageView lblImage;

        lblAction = dialog.findViewById(R.id.lblAction);
        lbldetails = dialog.findViewById(R.id.lblDetails);
        lbldate = dialog.findViewById(R.id.lblDate);

        lblImage = dialog.findViewById(R.id.lblIcon);

        lblAction.setText(""+hist.getActionName());
        lbldetails.setText(""+hist.getDetails());
        lbldate.setText(""+hist.getDate());
        Picasso.get().load(methods.icon_server()+hist.getIcon()).into(lblImage);

        dialog.create();
        dialog.show();
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private void filter(String str) {
        String text = str.toLowerCase();
        ArrayList<history> historyArrayList = new ArrayList<>();

        for (history u : histories)
        {
            if (u.getActionName().toLowerCase().toLowerCase().contains(text) || u.getDetails().toLowerCase().contains(text) || u.getDate().toLowerCase().contains(text))
            {
                historyArrayList.add(u);
            }
        }
        adaptor = new HistoryAdaptor(ctx,historyArrayList);
        listView.setAdapter(adaptor);
    }
    private void populate() {
        try
        {
            histories.clear();
            HistorySingleton.resetInstance();
            AndroidNetworking.get(methods.USER_API_SERVER+"history.php?username="+username)
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                            try {
                                for (int i=0; i<response.length();i++) {
                                    JSONObject jObject = response.getJSONObject(i);
                                    history hist = new history();
                                    hist.setId(jObject.getInt("id"));
                                    hist.setActionName(jObject.getString("name"));
                                    hist.setDetails(jObject.getString("details"));
                                    hist.setDate(jObject.getString("date"));
                                    hist.setIcon(jObject.getString("icon"));
                                    histories.add(hist);

                                }
                                adaptor = new HistoryAdaptor(ctx, histories);
                                listView.setAdapter(adaptor);

                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                        public void onError(ANError error) {
                            Log.d("Backgound","Income  ");
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }


}
