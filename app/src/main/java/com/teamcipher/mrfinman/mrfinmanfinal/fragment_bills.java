package com.teamcipher.mrfinman.mrfinmanfinal;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nightonke.boommenu.BoomMenuButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.MyBillsAdapter;
import Adapters.MyGoalAdapter;
import Models.MyBill;
import Models.MyGoals;
import Models.billerlist;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;

import static Utils.methods.ctx;
import static Utils.methods.dateComplete;
import static Utils.methods.getDateDiff;


public class fragment_bills extends Fragment {
    View view;
    ListView listView;
    SwipeRefreshLayout swipeRefreshLayout;
    UserLogin user = UserLogin.getInstance();
    Context ctx;
    FloatingActionButton fabNew;
    FloatingActionMenu fabMenu;
    ArrayList<MyBill> myBills = new ArrayList<>();
    MyBillsAdapter adapter;
    Bundle bundle = null;
    int userId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fragment_bills, container, false);
        initialization(view);
        onheader("My Bills");


        popListview();

        checkBundle();

        return view;
    }

    private void checkBundle() {
        if (bundle != null)
        {
            int billId = Integer.parseInt(bundle.getString("billId"));
        }
    }

    private void popListview() {
        myBills.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"userBill_list.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i =0 ; i<jsonArray.length();i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        MyBill myBill = new MyBill();

                        myBill.setAmount(Double.parseDouble(""+j.getString("amount")));
                        myBill.setBalance(Double.parseDouble(""+j.getString("balance")));
                        myBill.setDateCreated(""+j.getString("dateCreated"));
                        myBill.setDueDate(""+j.getString("dueDate"));
                        myBill.setDesc(""+j.getString("description"));
                        myBill.setBillname(""+j.getString("billName"));
                        myBill.setId(j.getInt("billId"));
                        if (j.getString("isActive") == "0")
                            myBill.setActive(false);
                        else
                            myBill.setActive(true);

                        myBill.setPaymentType(j.getString("paymentType"));
                        myBill.setBillerId(j.getInt("billerId"));
                        myBill.setComAdress(j.getString("billerAddress"));
                        myBill.setComName(j.getString("billerName"));

                        myBill.setComContactNo(j.getString("billerContactno"));
                        myBill.setBillerfname(j.getString("billerfname"));
                        myBill.setBillerlname(j.getString("billerlname"));
                        myBill.setBillerMIname(j.getString("billerMIname"));
                        myBills.add(myBill);

                    }
                    adapter = new MyBillsAdapter(myBills,ctx);
                    listView.setAdapter(adapter);

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
                params.put("userID",""+userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);

    }

    private void initialization(View view) {
        try
        {
            bundle = this.getArguments();
            userId  = Integer.parseInt(getPreference("userID"));
            ctx = getActivity().getApplicationContext();
            fabMenu = view.findViewById(R.id.fab_bill_menu);
            fabNew = view.findViewById(R.id.fab_bill_add);
            listView = view.findViewById(R.id.listview_my_bills_list);
            registerForContextMenu(listView);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
            onRefresh();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MyBill b = (MyBill) adapterView.getItemAtPosition(i);
                    showDetails(b);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private String getPreference(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }
    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                popListview();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        swipeRefreshLayout.setRefreshing(false);
                    }
                },300);

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listview_my_bills_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu_bill, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MyBill bill = (MyBill)adapter.getItem(info.position);

        switch(item.getItemId()) {

            case R.id.details:
                showDetails(bill);
                return true;
            case R.id.pay:
                showPayDetails(bill);
                return true;
            case R.id.edit:
                message.warning("Only the biller can only edit bills!",ctx);
                return true;
            case R.id.delete:
                // remove stuff here
                message.warning("Only the biller can only delete bills!",ctx);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showPayDetails(MyBill bill) {
        Dialog dia = new Dialog(getActivity());
        dia.setContentView(R.layout.dialogue_bill_pay);
        ImageView imgIcon = dia.findViewById(R.id.imgCategory);
        final EditText txtName,txtamounV,txtDuedate,txtAmountEnter;
        Button btnSave;
        txtamounV = dia.findViewById(R.id.pay_bill_amount);
        txtName = dia.findViewById(R.id.pay_bill_billname);
        txtDuedate = dia.findViewById(R.id.pay_bill_duedate);
        txtAmountEnter = dia.findViewById(R.id.pay_bill_amount_enter);
        btnSave = dia.findViewById(R.id.pay_bill_save);

        Picasso.get().load(methods.icon_server()+"bills.png").transform(methods.transformation).into(imgIcon);
        txtName.setText(""+bill.getBillname());
        txtDuedate.setText(""+bill.getDueDate());
        txtamounV.setText(""+methods.formatter.format( bill.getAmount()));
        txtAmountEnter.setText(""+methods.formatter.format(bill.getAmount()));
        txtAmountEnter.setEnabled(false);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               methods.saveExpense(ctx,userId,1,txtAmountEnter.getText().toString(),"Manual Payment of bills");
            }
        });

        dia.create();
        dia.show();
    }

    private void showDetails(MyBill b) {
        try
        {
            final Dialog dialog = new Dialog(getContext(),R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_bill_details);
            dialog.setCancelable(true);
            EditText lblbill,lbldateCreated,lblduedate,lbldesc,lblpaymentType,lblamount,lblComName,lblComAddress,lblComContact,lblrep,lblbalance;
            Button btnClose;


            lblbill = dialog.findViewById(R.id.lblbillname);
            lblbalance = dialog.findViewById(R.id.lblbillbalance);
            lbldateCreated = dialog.findViewById(R.id.lbldateCreated);
            lblduedate = dialog.findViewById(R.id.lbldateDue);
            lbldesc = dialog.findViewById(R.id.lbldescription);
            lblpaymentType = dialog.findViewById(R.id.lblpaymentType);
            lblamount = dialog.findViewById(R.id.lblbillamount);
            lblComName = dialog.findViewById(R.id.lblcomName);
            lblComAddress = dialog.findViewById(R.id.lblcomAddress);
            lblComContact = dialog.findViewById(R.id.lblcomContactNo);
            lblrep = dialog.findViewById(R.id.lblcomRep);
            btnClose = dialog.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            lblbalance.setText("Php "+methods.formatter.format(b.getBalance()));
            lblbill.setText(""+b.getBillname());
            lbldateCreated.setText(""+b.getDateCreated());
            lblduedate.setText(""+b.getDueDate());
            lbldesc.setText(""+b.getDesc());
            lblpaymentType.setText(""+b.getPaymentType());
            lblamount.setText("Php"+methods.formatter.format(b.getAmount()));
            lblComName.setText(""+b.getComName());
            lblComAddress.setText(""+b.getComAdress());
            lblComContact.setText(""+b.getComContactNo());
            lblrep.setText(""+b.getFullname());

            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }
    private void btnSetClicks() {
        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
            }
        });


        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popListview();
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
            }
        });
        fabMenu.setClosedOnTouchOutside(true);

    }

    private void onheader(String my_goals) {
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
            tv.setText(my_goals); // ActionBar title text
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



