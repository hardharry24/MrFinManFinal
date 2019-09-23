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
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
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
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_my_bills_add;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.teamcipher.mrfinman.mrfinmanfinal.fragment_main;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.UserAdaptor;
import Models.Biller.Biller;
import Models.Category;
import Models.user;
import Singleton.BillerSingleton;
import Singleton.IncomeSingleton;
import Singleton.UserLogin;
import Singleton.UserToken;
import Utils.customMethod;
import Utils.message;
import Utils.methods;
import Utils.notify;
import es.dmoral.toasty.Toasty;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_chargebill extends Fragment {
    Button btnSave;
    Context ctx;
    Dialog dialog;
    ListView listView;
    EditText txtuser;
    AppCompatImageView btnEditUser;
    View view;
    Spinner billType;
    EditText txtamount, txtDate, txtnote, txtname;
    GridView gridView;
    String dayPick = "";
    UserLogin userLogin;
    SimpleDateFormat df = new SimpleDateFormat("dd MMMM, yyyy");
    String datepick = "", amount = "";
    TextView lblbilluser;
    Button btnShowbiller;
    String username,contactNo ="",billerName ="";
    int billerid;
    int userbillId;
    CardView cardDueDate;

    Calendar calendar = Calendar.getInstance();
    Biller billerInfo;
    ArrayList<user> userlist = new ArrayList<>();
    ArrayList<user> myuserlist = new ArrayList<>();
    UserAdaptor adaptor = null;
    ImageView btnChooseDt;
    Biller b = new Biller();
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        initialization(inflater, container);
        ((Activity_dashboard_biller) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Charge Bill" + "</font>"));


        onLoadType();
        return view;
    }

    private void onLoadType() {
        if (billType.getSelectedItemPosition() != 0)
        {
            if (billType.getSelectedItemPosition() == 1)
                cardDueDate.setVisibility(View.VISIBLE);
            else
                cardDueDate.setVisibility(View.GONE);
        }
        else
            cardDueDate.setVisibility(View.GONE);
    }

    private void initialization(LayoutInflater inflater, ViewGroup container) {
        try
        {
            view = inflater.inflate(R.layout.biller_frag_charge_bill, container, false);
            ctx = getContext();
            btnEditUser = view.findViewById(R.id.btneditUser);
            txtuser = view.findViewById(R.id.txt_frag_charge_user);
            txtuser.setEnabled(false);

            btnSave = view.findViewById(R.id.btn_new_bills_save);
            txtamount = view.findViewById(R.id.txt_my_bill_amount);
            txtDate = view.findViewById(R.id.txt_new_bill_due_date);
            txtDate.setEnabled(false);
            txtname = view.findViewById(R.id.txt_new_bill_name);
            txtnote = view.findViewById(R.id.txt_new_bill_desc);
            lblbilluser = view.findViewById(R.id.txt_frag_charge_user);
            billerid = UserLogin.getInstance().getBillerId();
            btnChooseDt = view.findViewById(R.id.lblSelectDt);
            billType = view.findViewById(R.id.spinnerBillType);
            cardDueDate = view.findViewById(R.id.due_date);

            btnEditUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogue();
                }
            });
            txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (txtamount.getText().toString().equals(""))
                        txtamount.setError(null);
                    else if (txtamount.getText().toString() != "") {
                        amount = txtamount.getText().toString();
                        if (Double.parseDouble(txtamount.getText().toString()) > 10) {
                            txtamount.setText("" + methods.formatter.format(Double.parseDouble(txtamount.getText().toString())));
                        } else {
                            txtamount.setError("Amount must be greater than Php 10.00!");
                        }
                    }
                }
            });
            billType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    onLoadType();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            btnChooseDt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCalendar();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double amountPayment = 0.0;
                    billerName = getPreference("billerName");
                    if (validate())
                    {
                        if (billType.getSelectedItemPosition() == 2)
                        {
                            Dialog dialog = new Dialog(ctx);
                            dialog.setCancelable(true);
                            dialog.setContentView(R.layout.dialogue_display_choose_day);
                            final Spinner spinnerDay = dialog.findViewById(R.id.spinnerDay);
                            Button btnOk = dialog.findViewById(R.id.btnOK);
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx,android.R.layout.simple_list_item_1,methods.getDays());
                            spinnerDay.setAdapter(adapter);
                            btnOk.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dayPick = spinnerDay.getSelectedItem().toString();

                                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                            .setTitle("Message")
                                            .setMessage("Are you sure you want to continue?")
                                            .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    savebill();
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
                            });
                            dialog.create();
                            dialog.show();
                        }
                        else
                        {
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                    .setTitle("Message")
                                    .setMessage("Are you sure you want to continue?")
                                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            savebill();
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
                    }
                }
            });
            lblbilluser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogue();
                }
            });
            billerInfo = BillerSingleton.getInstance().getBillerInfo();
            username = getPreference("username");
            populateMyUserList();
            populateUser();
        }
        catch (Exception ex)
        {

        }
    }

    private boolean isContain(int id,ArrayList<user> userlist)
    {
        for (user us: userlist)
            if(us.getId() == id)
            {
                return true;
            }
                
        return false;
    }



    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private boolean validate() {
        try
        {
            if (TextUtils.isEmpty(txtamount.getText().toString()))
            {
                txtamount.setError("Empty field!");
                return false;
            }
            else if (TextUtils.isEmpty(datepick) && billType.getSelectedItemPosition() == 1)
            {
                txtDate.setError("Empty field!");
                return false;
            }
            else if (TextUtils.isEmpty(txtname.getText().toString())) {
                txtname.setError("Empty field!");
                return false;
            }
            else if (Double.parseDouble(txtamount.getText().toString().replace(",",""))  <= 0)
            {
                txtname.setError("Minimum amount is 10.00!");
                return false;
            }
            else if(billType.getSelectedItemPosition() == 0)
            {
                message.alertWarning(ctx,"Type not valid!");
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {

        }
        return false;
    }

    private void showCalendar() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
        DatePickerDialog.OnDateSetListener mdatelistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                if (billType.getSelectedItemPosition() != 0)
                {
                    if (billType.getSelectedItemPosition() == 1)
                    {
                        calendar.set(year, month, day);
                        txtDate.setText(df.format(calendar.getTime()));
                        datepick = dt.format(calendar.getTime());
                        Calendar caFuture = Calendar.getInstance();
                        caFuture.add(Calendar.DATE, 5);
                        if (calendar.getTime().before(caFuture.getTime()))
                        {
                            txtDate.setText("");
                            datepick = "";
                            //showCalendar();
                            message.alertWarning(ctx,"Due date must at least 5 days from now!");
                            message.warning("Due date must at least 5 days from now!",ctx);
                        }
                    }
                    else if (billType.getSelectedItemPosition() == 2)
                    {
                        calendar.set(year, month, day);
                        txtDate.setText(df.format(calendar.getTime()));
                        datepick = dt.format(calendar.getTime());
                        Calendar caFuture = Calendar.getInstance();
                        caFuture.add(Calendar.MONTH, 2);
                        if (calendar.getTime().before(caFuture.getTime()))
                        {
                            txtDate.setText("");
                            datepick = "";
                            message.alertWarning(ctx,"Due date must at least 2 months from now!");
                            message.warning("Due date must at least 2 months from now!",ctx);
                        }
                    }
                }
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(ctx, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, mdatelistener, year, month, day);
        dialog.show();
    }

    private void savebill() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "bills.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jObj = jsonArray.getJSONObject(0);
                        int code = jObj.getInt("code");
                        switch (code)
                        {
                            case 1:
                                String str = "You have been charge Php "+txtamount.getText().toString()+" for bill name '"+txtname.getText()+"' from "+billerName+". \n\nFor more details please contact "+UserLogin.getInstance().getContactNo()+".\n\nThank you";
                                int billId = jObj.getInt("billId");
                                sendNotificationUser(str,userbillId, billId);
                                Toasty.success(ctx, "Successfuly charge bill to "+lblbilluser.getText().toString()+" !", Toast.LENGTH_SHORT).show();
                                Toasty.info(ctx, ""+lblbilluser.getText().toString()+" will be notified for this bill!", Toast.LENGTH_SHORT).show();


                                //Add to history
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                Calendar calendar = Calendar.getInstance();
                                Date date = calendar.getTime();
                                String fDate = df.format(date);

                                Map<String, String> params = new HashMap<>();
                                params.put("histname", "Charge Bill ");
                                params.put("histDetails", "You have charge Php "+txtamount.getText().toString()+" for bill name '"+txtname.getText()+" to "+txtname.getText());
                                params.put("dateCreated", fDate);
                                params.put("","");
                                params.put("icon", "bills.png");
                                params.put("userId", "" + getPreference("userID"));
                                params.put("type", "Bill Payment");
                                notify.addtoHistory(ctx, params);

                                startActivity(new Intent(ctx,Activity_biller_manage.class));
                                getActivity().finish();

                                break;
                            case 3:
                                message.alertError(ctx,"Oops Bill name Already Exist!");
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("HELLO",""+e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Alerter.create(getActivity()).setText("NO INTERNET CONNECTION!\n" + error)
                            .setIcon(R.drawable.ic_info_outline_black_24dp)
                            .show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");

                    final Date date = Calendar.getInstance().getTime();
                    String fDate = df.format(date);

                    Map<String, String> params = new HashMap<>();
                    params.put("type", "insert");
                    params.put("userId", "" + userbillId);
                    params.put("billname", "" + txtname.getText().toString());
                    params.put("amount", "" + amount);
                    params.put("billerId", "" + billerid);
                    params.put("note", "" + txtnote.getText().toString());

                    if (billType.getSelectedItemPosition() == 2)
                        params.put("targetDate", datepick);
                    else
                        params.put("targetDate", dayPick);

                    params.put("targetDate", datepick);
                    params.put("paymenttype", ""+billType.getSelectedItem());
                    params.put("dateCreated", fDate);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            requestQueue.add(stringRequest);

        } catch (Exception ex) {
            Log.d("HELLO",""+ex);
        }

    }

    private void showDialogue()
    {
        dialog = new Dialog(getContext(),R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_biller_choose_user_list);
        swipeRefreshLayout = dialog.findViewById(R.id.swipe_refresh);
        listView = dialog.findViewById(R.id.dialogue_listview_user_list);
        ImageView btnAddUser = dialog.findViewById(R.id.imgBtnAdd);
        final EditText txtSearch = dialog.findViewById(R.id.txtsearch);

        onsearch(txtSearch,myuserlist,listView);
        UserAdaptor adapter = new UserAdaptor(ctx, myuserlist);
        registerForContextMenu(listView);
        listView.setOnCreateContextMenuListener(this);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                populateMyUserList();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },200);
            }
        });

        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddUser();
            }
        });
        dialog.show();
    }

    private void showAddUser()
    {
        dialog = new Dialog(getContext(),R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_biller_add_to_user_list);
        listView = dialog.findViewById(R.id.dialogue_listview_add_user_list);
        final EditText txtSearch = dialog.findViewById(R.id.txtsearch);

       // Toast.makeText(ctx, ""+userlist.size(), Toast.LENGTH_SHORT).show();
   /*     ArrayList<user> u = new ArrayList<>();
        for (user s : userlist)
        {
            if (!isContain(s.getId(),myuserlist))
                u.add(s);
        }*/

        UserAdaptor adaptor = new UserAdaptor(ctx,userlist);
        listView.setAdapter(adaptor);

        onsearch(txtSearch,userlist,listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                final user s = (user) adapterView.getItemAtPosition(i);
                AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Message")
                        .setMessage("Would you like to add "+s.getLname()+", "+s.getFname()+" in your customer list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                              onAddUser(s.getUserId());
                            }

                            private void onAddUser(int userId) {

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                Date date = calendar.getTime();
                                String fDate = df.format(date);

                                AndroidNetworking.get(methods.BILLER_API_SERVER+"user_biller.php?type=insert&userId="+userId+"&billerId="+billerid+"&dateCreated="+fDate+"")
                                        .setTag("test")
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsJSONArray(new JSONArrayRequestListener() {
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                                                try {
                                                    JSONObject jObject = response.getJSONObject(0);
                                                    switch (jObject.getInt("code"))
                                                    {
                                                        case 0:
                                                            message.error(jObject.getString("message"),ctx);
                                                            break;
                                                        case 1:
                                                            message.success(jObject.getString("message"),ctx);
                                                            dialog.hide();

                                                            //populateMyUserList();
                                                            //adaptor.notifyDataSetChanged();
                                                            break;
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Log.d("Backgound Error","Income  ");
                                                }

                                            }
                                            public void onError(ANError error) {
                                                Log.d("Backgound Error","Income  ");
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
                Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                bq.setTextColor(Color.BLACK);
                ba.setTextColor(Color.BLACK);
            }
        });


        dialog.create();
        dialog.show();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        if (v.getId() == R.id.dialogue_listview_user_list   ){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;

            String[] menuitems = {"Charge","Remove"};

            for (int i=0;i<menuitems.length;i++){
                menu.add(Menu.NONE,i,i,menuitems[i]);
            }
            final long selectid = info.id;
            final user userSelected = getUser((int)selectid);

            for (int i=0;i<menu.size();i++){
                menu.getItem(i).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int menuindex = item.getItemId();
                        switch (menuindex) {
                            case 0:
                                String msg = userSelected.getLname() + ", " + userSelected.getFname() + " " + userSelected.getMi() + ".";
                                txtuser.setText(msg);
                                userbillId = userSelected.getUserId();
                                dialog.dismiss();
                                message.success(""+msg+" Selected!",ctx);
                                break;
                            case 1:
                                ondelete(userSelected.getUserId());
                                break;

                            default:
                                Toast.makeText(getView().getContext(), "invalid option!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void ondelete(final int userId) {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        AndroidNetworking.get(methods.BILLER_API_SERVER + "user_biller.php?type=delete&userId=" + userId + "&billerId=" + billerid + "")
                                .setTag("test")
                                .setPriority(Priority.LOW)
                                .build()
                                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        try {
                                            JSONObject jObject = response.getJSONObject(0);
                                            switch (jObject.getInt("code")) {
                                                case 0:
                                                    message.error(jObject.getString("message"), ctx);
                                                    break;
                                                case 1:
                                                    message.success(jObject.getString("message"), ctx);
                                                    populateMyUserList();
                                                    break;
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Log.d("Backgound Error", "Income  ");
                                        }
                                    }
                                    public void onError(ANError error) {
                                        Log.d("Backgound Error", "Income  ");
                                    }
                                });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.BLACK);
        ba.setTextColor(Color.BLACK);
    }

    private user getUser(int id) {
        for (user s : myuserlist)
        {
            if (s.getUserId() == id)
                return s;
        }
        return null;
    }

    public void populateUser() {
        userlist.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "listUser.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        user s = new user();
                        s.setUserId(jsonObject.getInt("ID"));
                        s.setFname(jsonObject.getString("firstname"));
                        s.setLname(jsonObject.getString("lastname"));
                        s.setMi(jsonObject.getString("MI"));
                        s.setEmail(jsonObject.getString("email"));
                        s.setUsername(jsonObject.getString("username"));
                        s.setContactNo(jsonObject.getString("contactNo"));
                        //if (!isContain(s.getId()))
                        userlist.add(s);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create((Activity) getContext()).setText("NO INTERNET CONNECTION!\n")
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void populateMyUserList() {
        myuserlist.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.BILLER_API_SERVER + "user_biller.php?type=my_list&billerId="+billerid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        user s = new user();
                        s.setUserId(jsonObject.getInt("ID"));
                        s.setFname(jsonObject.getString("firstname"));
                        s.setLname(jsonObject.getString("lastname"));
                        s.setMi(jsonObject.getString("MI"));
                        s.setEmail(jsonObject.getString("email"));
                        s.setUsername(jsonObject.getString("username"));
                        s.setContactNo(jsonObject.getString("contactNo"));
                        myuserlist.add(s);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TESTING",""+e.toString());
                }
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create((Activity) getContext()).setText("NO INTERNET CONNECTION!\n" + error)
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void onsearch(final EditText txtSearch, final ArrayList<user> users, final ListView listView) {
        txtSearch.addTextChangedListener(new TextWatcher() {
            ArrayList<user> userSearch = new ArrayList<>();

            UserAdaptor adapter = null;//new UserAdaptor(ctx, users);
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(txtSearch.getText()))
                {
                    adapter = new UserAdaptor(ctx, users);
                    listView.setAdapter(adapter);
                }
                else
                {
                    userSearch.clear();
                    String text = txtSearch.getText().toString().toLowerCase();
                    for (user u : users)
                    {
                        if (u.getFname().toLowerCase().toLowerCase().contains(text) || u.getLname().toLowerCase().contains(text) || u.getEmail().toLowerCase().contains(text) || u.getUsername().toLowerCase().contains(text))
                        {
                            userSearch.add(u);
                        }
                    }
                    adapter = new UserAdaptor(ctx,userSearch);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendNotificationUser(String msg,int userId, int billId)
    {
        AndroidNetworking.get(methods.PUSHNOTIF_API_SERVER+"charge_bill.php?userId="+userId+"&message="+msg+"&billId="+billId+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                         //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    }
                    public void onError(ANError error) {
                         //message.error("On Token Save Error!\n"+error,ctx);
                    }
                });
    }

    public void fragmentRedirection(Fragment ctx)
    {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_biller, ctx)
                .addToBackStack(null)
                .commit();

    }
}
