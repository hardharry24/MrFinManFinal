package com.teamcipher.mrfinman.mrfinmanfinal.Biller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
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

import Adapters.BillerListAdaptor;
import Models.Biller.Biller;
import Models.MyBill;
import Models.billerlist;
import Singleton.BillerSingleton;
import Singleton.UserLogin;
import Utils.Logs;
import Utils.message;
import Utils.methods;

public class manage_tab_1_pending extends Fragment {
    ListView listView;
    Context ctx;
    EditText txtsearch;
    View view;
    int billerId;
    BillerListAdaptor adapter;
    ArrayList<billerlist> billerlists = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.biller_manage_tab_pending, container, false);
        initialize(view);


        return view;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listview_manage_bill_pending) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu, menu);
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //MyBill bill = (MyBill)adapter.getItem(info.position);
        billerlist b = (billerlist) adapter.getItem(info.position);


        switch(item.getItemId()) {

            case R.id.details:
                showDetails(b);
                return true;
            case R.id.edit:
                showEditDetails(b);
                populateListview();
                return true;
            case R.id.delete:
               // billerOption.php?todo=CANCEL&billId=42
                cancelBill(b);
                populateListview();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void cancelBill(final billerlist b) {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to continue cancel this bill?")
                .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AndroidNetworking.get(methods.BILLER_API_SERVER+"billerOption.php?todo=CANCEL&billId="+b.getBillId())
                                .setTag("test")
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            JSONObject jObject = response.getJSONObject(0);
                                            int code = jObject.getInt("code");
                                            if (code == 1)
                                                message.success(""+jObject.getString("message"),ctx);
                                            else if(code == 0)
                                                message.error("Error Occured Please try again",ctx);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d("Backgound Error", "Income  ");
                                        }

                                    }

                                    @Override
                                    public void onError(ANError anError) {

                                    }

                                });
                    }
                })
                .addButton("NO", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED  , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }

    public void initialize(View view) {
        try
        {
            ctx =  (Context) getActivity();
            billerlists = new ArrayList<>();
            billerId = Integer.parseInt(getPreference("billerId"));
            listView = view.findViewById(R.id.listview_manage_bill_pending);
            listView.bringToFront();
            registerForContextMenu(listView);

            txtsearch = view.findViewById(R.id.txtsearch);

            populateListview();
            txtsearchText();
            onItemListViewClick();
        }
        catch (Exception ex)
        {

        }
    }

    private void onItemListViewClick() {
     /*   listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                billerlist b = (billerlist) adapterView.getItemAtPosition(i);
                showDetails(b);
                //Toast.makeText(ctx, "fdsfsdfsdfd", Toast.LENGTH_SHORT).show();
            }
        });*/
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
    private void showEditDetails(final billerlist b) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialogue_biller_update_details);
        dialog.setCancelable(true);
        final EditText lbllname,lblfname,lblemail,lblcontact,lblbill,lbldateCreated,lblduedate,lbldesc,lblpaymentType,lblamount;
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
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.success("Successfuly Updated",ctx);
                //message.success("Successfuly Updated",ctx);
               /* Map<String,String> params = new HashMap<>();
                params.put("billId",""+b.getBillId());
                params.put("billname",""+lblbill.getText().toString());
                params.put("amount",""+lblamount.getText().toString());
                params.put("dueDate",""+lblduedate.getText().toString());
                updateBillDetails(b,params);
                */
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    private void updateBillDetails(final billerlist b, final Map<String,String> parms)
    {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to continue updating this bill?")
                .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.BILLER_API_SERVER+"updateBillUser.php", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();

                                try {
                                    JSONArray jsonA = new JSONArray(response);

                                    try {
                                        JSONObject jObject = jsonA.getJSONObject(0);
                                        int code = jObject.getInt("code");
                                        if (code == 1)
                                            message.success(""+jObject.getString("message"),ctx);
                                        else if(code == 0)
                                            message.error("Error Occured Please try again",ctx);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("Backgound Error", "Income  ");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Logs.LOGS("Error Save Expense Method "+e);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Logs.LOGS("Error Save Expense Method "+error);
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                Calendar calendar = Calendar.getInstance();
                                Date date = calendar.getTime();
                                String fDate = df.format(date);

                                return parms;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                        requestQueue.add(stringRequest);
                    }
                })
                .addButton("NO", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED  , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.show();
    }



    private void txtsearchText() {
        search(txtsearch);
    }


    private void search(final EditText txtsearch) {
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

    private void populateListview() {
        try
        {
            Calendar cal = Calendar.getInstance();
            billerlists.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, methods.BILLER_API_SERVER+"manage_bill.php?type=pending&billerId="+billerId+"&date="+methods.date.format(cal.getTime()), new Response.Listener<String>() {
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
                        //Context context = getActivity().getWindow().getContext();
                        adapter  = new BillerListAdaptor(billerlists,ctx);
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
            Logs.LOGS_BILLER(""+ex);
        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }


}
