package com.teamcipher.mrfinman.mrfinmanfinal;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Adapters.HistoryAdaptor;
import Adapters.HistoryUserAdaptor;
import Adapters.UserListAdaptor;
import Models.User.history;
import Models.user;
import Singleton.HistorySingleton;
import Singleton.IncomeSingleton;
import Singleton.UserLogin;
import Utils.customMethod;
import Utils.message;
import Utils.methods;


public class fragment_history extends Fragment {
    View view;
    Dialog dialog;
    RelativeLayout relativeLayout;
    int userId = UserLogin.getInstance().getUser_ID();
    Button btnGenerate;
    ListView listView;
    Context ctx;
    EditText searchView;
    String username;
    HistoryUserAdaptor adaptor;

    ArrayList<history> histories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_history, container, false);
        initialization(view);
        onheader("History");
        populate();
        listViewAction();
        searchBoxAction();
        return view;
    }

    private void searchBoxAction() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try
                {
                    if (searchView.getText().toString().length() == 0)
                    {
                        populate();
                    }
                    else
                    {
                        filter(charSequence.toString());
                    }

                }catch (Exception ex)
                {

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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
        adaptor = new HistoryUserAdaptor(ctx,historyArrayList);
        listView.setAdapter(adaptor);
    }

    private void listViewAction() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                history hist = (history)adapterView.getItemAtPosition(i);
                showDetails(hist);
            }
        });
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
                                adaptor = new HistoryUserAdaptor(ctx, histories);
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


    private void initialization(View view) {
        ctx = getContext();
        listView = view.findViewById(R.id.listViewHistory);
        searchView = view.findViewById(R.id.txtsearch);
        username = getPreference("username");
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private void onheader(String title) {
        try
        {
            ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((Activity_dashboard)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
            ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);

            ActionBar actionBar = ((Activity_dashboard)getActivity()).getSupportActionBar();
            TextView tv = new TextView(getContext());
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.dancingfont);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
            tv.setLayoutParams(lp);
            tv.setText(title); // ActionBar title text
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(25);
            tv.setTypeface(typeface, typeface.ITALIC);
            actionBar.setCustomView(tv);
        }
        catch (Exception ex)
        {

        }
    }



}



