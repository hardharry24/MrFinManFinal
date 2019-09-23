package com.teamcipher.mrfinman.mrfinmanfinal.Biller;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import Adapters.BillerListAdaptor;
import Models.billerlist;
import Utils.methods;

public class manage_tab_3_overdue extends Fragment {
    ListView listView;
    Context ctx;
    EditText txtsearch;
    View view;
    int billerId;
    BillerListAdaptor adapter;
    ArrayList<billerlist> billerlists= new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biller_manage_tab_overdue, container, false);
        initialize(view);
        return view;
    }

    public void initialize(View view) {
        try
        {
            ctx = getContext();
            billerId = Integer.parseInt(getPreference("billerId"));
            listView = view.findViewById(R.id.listview_manage_bill_overdue);
            txtsearch = view.findViewById(R.id.txtsearch);

            populateListview();
            txtsearchText();
            onItemListViewClick();
        }
        catch (Exception ex)
        {

        }
    }
    private void txtsearchText() {
        search(txtsearch);
    }


    private void search(final  EditText txtsearch) {
        try
        {
            final ArrayList<billerlist> billerlist_s = new ArrayList<>();
            txtsearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (TextUtils.isEmpty(txtsearch.getText()))
                    {
                        adapter = new BillerListAdaptor(billerlists,ctx);
                        listView.setAdapter(adapter);
                    }
                    else
                    {
                        billerlist_s.clear();
                        String text = txtsearch.getText().toString().toLowerCase();
                        for (billerlist u : billerlists)
                        {
                            if (u.getFname().toLowerCase().toLowerCase().contains(text) || u.getLname().toLowerCase().contains(text)
                                    || u.getEmail().toLowerCase().contains(text) || u.getUsername().toLowerCase().contains(text)|| u.getBillname().toLowerCase().contains(text))
                            {
                                billerlist_s.add(u);
                            }
                        }
                        adapter = new BillerListAdaptor(billerlist_s,ctx);
                        listView.setAdapter(adapter);
                    }
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
    private void showDetails(billerlist b) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_biller_details);
        dialog.setCancelable(true);
        EditText lbllname,lblfname,lblemail,lblcontact,lblbill,lbldateCreated,lblduedate,lbldesc,lblpaymentType,lblamount;
        Button btnClose;

        lbllname = dialog.findViewById(R.id.lbllname);
        lblfname = dialog.findViewById(R.id.lblfname);
        lblemail = dialog.findViewById(R.id.lblemail);
        lblcontact = dialog.findViewById(R.id.lblcontactNo);
        lblbill = dialog.findViewById(R.id.lblbillname);
        lbldateCreated = dialog.findViewById(R.id.lbldateCreated);
        lblduedate = dialog.findViewById(R.id.lbldateDue);
        lbldesc = dialog.findViewById(R.id.lbldescription);
        lblpaymentType = dialog.findViewById(R.id.lblpaymentType);
        lblamount = dialog.findViewById(R.id.lblbillamount);
        btnClose = dialog.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        lbllname.setText(""+b.getLname());
        lblfname.setText(""+b.getFname());

        String val = b.getContactno();

        if (b.getEmail().toString() !="")
            lblemail.setText(""+b.getEmail());
        else
            lblemail.setText("No Email");
        if (b.getContactno().toString() !="")
            lblcontact.setText(""+b.getContactno());
        else
            lblcontact.setText("No Contact Number");

        lblbill.setText(""+b.getBillname());
        lbldateCreated.setText(""+b.getDateCreated());
        lblduedate.setText(""+b.getDueDate());
        lbldesc.setText(""+b.getDescription());
        lblpaymentType.setText(""+b.getPaymentType());
        lblamount.setText("Php"+ methods.formatter.format(b.getAmount()));
        dialog.show();

    }

    private void onItemListViewClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                billerlist b = (billerlist) adapterView.getItemAtPosition(i);
                showDetails(b);
                //Toast.makeText(ctx, "fdsfsdfsdfd", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void populateListview() {
        try
        {
            Calendar cal = Calendar.getInstance();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, methods.BILLER_API_SERVER+"manage_bill.php?type=overdue&billerId="+billerId+"&date="+methods.date.format(cal.getTime()), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jarray = null;
                        jarray = new JSONArray(response);
                        for (int i =0;i<jarray.length(); i++)
                        {
                            JSONObject jobject = jarray.getJSONObject(i);
                            billerlist b = new billerlist();
                            b.setUserid(jobject.getInt("userId"));
                            b.setLname(""+jobject.getString("lname"));
                            b.setFname(""+jobject.getString("fname"));
                            b.setEmail(""+jobject.getString("email"));
                            b.setUsername(""+jobject.getString("username"));
                            b.setBillId(jobject.getInt("billId"));
                            b.setContactno(jobject.getString("contactNo"));
                            b.setBillname(""+jobject.getString("billName"));
                            b.setDateCreated(""+jobject.getString("dateCreated"));
                            b.setAmount(jobject.getDouble("amount"));
                            b.setDueDate(""+jobject.getString("dueDate"));
                            b.setDescription(""+jobject.getString("description"));
                            b.setPaymentType(""+jobject.getString("paymentType"));
                            billerlists.add(b);
                        }
                        adapter = new BillerListAdaptor(billerlists,ctx);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
}
