package com.teamcipher.mrfinman.mrfinmanfinal.Admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import Adapters.AdminBillerAdaptor;
import Adapters.BillerListAdaptor;
import Models.Admin.BillerDetails;
import Utils.methods;

public class frag_manage_admin_biller extends Fragment {
    ListView listView;
    ArrayList<BillerDetails> billerDetails = new ArrayList<>();
    ArrayList<BillerDetails> billerDetailsSearch = new ArrayList<>();
    AdminBillerAdaptor adaptor;
    EditText txtSearch;
    Context ctx;
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Activity_dashboard_admin) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Manage Biller Account" + "</font>"));
        view = inflater.inflate(R.layout.admin_fragment_manage_biller, container, false);

        initialize();
        populateList();
        search();
    return view;
    }

    private void search() {
        try
        {
            txtSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    try
                    {
                        if (txtSearch.getText().toString().length() == 0)
                        {
                            populateList();
                        }
                        else
                        {
                            filter(charSequence.toString());
                        }

                    }catch (Exception ex)
                    {
                    }
                }

                private void filter(String s) {
                    billerDetailsSearch.clear();
                    String str = s.toLowerCase();
                    for(BillerDetails b: billerDetails)
                    {
                        if (b.getBillerName().toLowerCase().contains(str) || b.getBillerEmail().toLowerCase().contains(str) ||b.getRepfullname().toLowerCase().contains(str) ||b.getRepemail().toLowerCase().contains(str))
                        {
                            billerDetailsSearch.add(b);
                        }
                    }
                    adaptor = new AdminBillerAdaptor(ctx,billerDetailsSearch);
                    listView.setAdapter(adaptor);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private void populateList() {
        try
        {
            billerDetails.clear();
            billerDetailsSearch.clear();
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"billerList.php?todo=ACTIVE")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for(int i=0;i<response.length();i++)
                            {
                                try {
                                    JSONObject jObj = response.getJSONObject(i);
                                    BillerDetails b = new BillerDetails();
                                    b.setId(i+1);
                                    b.setBillerName(""+jObj.getString("billerName"));
                                    b.setBillerId(jObj.getInt("billerId"));
                                    b.setBillerAddress(""+jObj.getString("billerAddress"));
                                    b.setBillerContactno(""+jObj.getString("billerContactno"));
                                    b.setBillerEmail(""+jObj.getString("billerEmail"));
                                    b.setUserId(jObj.getInt("userId"));
                                    b.setRepfullname(""+jObj.getString("Repfullname"));
                                    b.setRepemail(""+jObj.getString("Repemail"));
                                    b.setRecontactNo(""+jObj.getString("RecontactNo"));
                                    b.setRepusername(""+jObj.getString("Repusername"));

                                    billerDetails.add(b);
                                    billerDetailsSearch.add(b);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                adaptor = new AdminBillerAdaptor(ctx,billerDetails);
                                listView.setAdapter(adaptor);

                            }


                        }
                        @Override
                        public void onError(ANError error) {
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }

    private void initialize() {
        try
        {
            listView = view.findViewById(R.id.listViewBiller);
            txtSearch = view.findViewById(R.id.txtSearch);
            ctx = getContext();
        }
        catch (Exception ex)
        {

        }
    }
}
