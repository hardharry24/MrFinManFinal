package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;
import es.dmoral.toasty.Toasty;

public class Activity_myincome extends AppCompatActivity {
    TextView lblDateToday;
    EditText txtmonthly,txtanually,txtamount,txtdesc;
    Spinner myspinner;
    Button btnSave;
    String rsponse = "",amount ="", username = "";
    int userId = 0;
    Context ctx;
    FloatingActionButton fabEdit;
    FloatingActionMenu fabMenu;
    Double final_amount = 0.00;
    public UserLogin user = UserLogin.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myincome);

        initialization();
        getIncome();
        onheaderActionBar("My Income");

    }
    private void onheaderActionBar(String title) {
        try
        {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);

            ActionBar actionBar = getSupportActionBar();
            TextView tv = new TextView(getApplicationContext());
            Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            Intent intent = new Intent(this,Activity_dashboard.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            //
        }

        return false;

    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {
        try
        {
            userId  = Integer.parseInt(getPreference("userID"));
            username = getPreference("username");
            fabMenu = findViewById(R.id.fab_bill_menu);
            fabEdit = findViewById(R.id.fab_bill_edit);
            lblDateToday = findViewById(R.id.my_income_date_now);
            txtmonthly = findViewById(R.id.txt_myincome_monthly);
            txtanually = findViewById(R.id.txt_myincome_annually);
            myspinner = findViewById(R.id.spiner_income_type);
            txtamount = findViewById(R.id.txt_myincome_amount);
            txtdesc = findViewById(R.id.txt_myincome_note);
            btnSave = findViewById(R.id.btn_myincome_save);
            ctx = Activity_myincome.this;
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onsave();
                }
            });
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fabMenu.close(true);

                    fabMenu.setVisibility(View.INVISIBLE);
                    btnSave.setVisibility(View.VISIBLE);
                    btnSave.setBackgroundColor(getResources().getColor(R.color.fbutton_color_belize_hole));
                    btnSave.setText("UPDATE");

                    txtdesc.setEnabled(true);
                    txtamount.setEnabled(true);
                    myspinner.setEnabled(true);

                }
            });


            fabMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fabMenu.isOpened()) {
                        fabMenu.close(true);
                    }
                }
            });
            fabMenu.setClosedOnTouchOutside(true);

            txtamount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (txtamount.getText().toString().equals(""))
                    {
                        txtmonthly.setText("");
                        txtanually.setText("");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            popDdate();
            fabMenu.bringToFront();

            myspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    amountFormat();
                    typeSpinnerChange();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    amountFormat();
                }
            });

            fabMenu.setIconAnimated(false);
            fabMenu.getMenuIconView().setImageResource(R.drawable.icmenu);

            txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!b)
                        typeSpinnerChange();


                }
            });

            txtamount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    typeSpinnerChange();
                }
            });
            myspinner.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if(!b)
                        typeSpinnerChange();
                    else
                        amountFormat();
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
    private void amountFormat()
    {
        try
        {
            if (txtamount.getText().toString().equals(""))
                txtamount.setError(null);
            else if (txtamount.getText().toString() != "")
            {
                amount = txtamount.getText().toString().replace(",","");
                if (Double.parseDouble(amount) > 10)
                {
                    txtamount.setText(""+methods.formatter.format(Double.parseDouble(txtamount.getText().toString().replace(",",""))));
                }
                else
                {
                    txtamount.setError("Amount must be greater than Php 10.00!");
                }
            }
        }
        catch (Exception ex)
        {

        }
    }
    public void typeSpinnerChange()
    {
        try
        {
            if (myspinner.getSelectedItem().toString().equals("Weekly"))
            {
                try
                {
                    if (!checkAmount())
                    {
                        Double txtValue = Double.parseDouble(txtamount.getText().toString().replace(",",""));
                        Double monthly = txtValue * 4.35;
                        Double annually = monthly * 12;

                        final_amount = monthly;

                        txtmonthly.setText("Php "+methods.formatter.format(monthly));
                        txtanually.setText("Php "+methods.formatter.format(annually));
                    }
                    else
                        methods.showMessage(Activity_myincome.this,"Error Message!","Oops amount not valid!",R.drawable.ic_close,R.color.red);
                }catch (Exception ex)
                {
                    methods.showMessage(Activity_myincome.this,"Error Message!",""+ex,R.drawable.ic_close,R.color.red);
                }
            }
            else if (myspinner.getSelectedItem().toString().equals("Semi-Monthly"))
            {
                try
                {
                    if (!checkAmount())
                    {
                        Double txtValue = Double.parseDouble(txtamount.getText().toString().replace(",",""));
                        Double monthly = txtValue * 2;
                        Double annually = monthly * 12;

                        final_amount = monthly;

                        txtmonthly.setText("Php "+methods.formatter.format(monthly));
                        txtanually.setText("Php "+methods.formatter.format(annually));
                    }
                    else
                        methods.showMessage(Activity_myincome.this,"Error Message!","Oops amount not valid!",R.drawable.ic_close,R.color.red);
                }catch (Exception ex)
                {
                    //methods.showMessage(Activity_myincome.this,"Error Message!",""+ex,R.drawable.ic_close,R.color.red);
                }
            }
            else if (myspinner.getSelectedItem().toString().equals("Monthly"))
            {
                try
                {
                    if (!checkAmount())
                    {
                        Double txtValue = Double.parseDouble(txtamount.getText().toString().replace(",",""));
                        Double monthly = txtValue * 1;
                        Double annually = monthly * 12;

                        final_amount = monthly;

                        txtmonthly.setText("Php "+methods.formatter.format(monthly));
                        txtanually.setText("Php "+methods.formatter.format(annually));
                    }
                    else
                        methods.showMessage(Activity_myincome.this,"Error Message!","Oops amount not valid!",R.drawable.ic_close,R.color.red);
                }catch (Exception ex)
                {
                    //methods.showMessage(Activity_myincome.this,"Error Message!",""+ex,R.drawable.ic_close,R.color.red);
                }
            }
            else
            {
                txtmonthly.setText("");
                txtanually.setText("");
            }
        }
        catch (Exception ex)
        {

        }
    }
    private void onsave() {
        try
        {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Message");
            if (btnSave.getText().toString().equals("SAVE"))
                alertDialogBuilder.setMessage("Confirm to save income?");
            else
                alertDialogBuilder.setMessage("Confirm to update income?");

            alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (btnSave.getText().toString().equals("SAVE"))
                    {
                        if (!(txtamount.getText().toString().equals("")))
                        {
                            if (!(Double.parseDouble(txtamount.getText().toString().replace(",","")) < 100))
                            {
                                if ((myspinner.getSelectedItemId() == 0))
                                {
                                    message.warning("Type not valid!",ctx);
                                }
                                else
                                {
                                    Toasty.info(ctx,"You've been added a default priority!",Toast.LENGTH_LONG).show();
                                    saveIncome();
                                }
                            }
                            else
                            {
                                message.warning("Amount must be greater than Php 100.00",ctx);
                            }
                        }
                        else
                        {
                            txtamount.setError("Not valid amount!");
                        }
                    }
                    else
                    {
                        if (!(txtamount.getText().toString().equals("")))
                        {
                            if (!(Double.parseDouble(txtamount.getText().toString().replace(",","")) < 100))
                            {
                                if ((myspinner.getSelectedItemId() == 0))
                                {
                                    message.warning("Invalid Income Type!",ctx);
                                }
                                else
                                {
                                    updateIncome();
                                }
                            }
                            else
                            {
                                message.warning("Amount must be greater than Php 100.00",ctx);
                            }
                        }
                        else
                        {
                            txtamount.setError("Not valid amount!");
                        }
                    }



                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq.setTextColor(Color.WHITE);
            ba.setTextColor(Color.WHITE);
        }
        catch (Exception ex)
        {

        }
    }

    public void waitNext()
    {
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(),Activity_dashboard.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }
    private void saveIncome() {
        final String amountDetails = "amount-"+amount+"|monthly-"+txtmonthly.getText().toString()+"|annually-"+txtanually.getText().toString()+"";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_income.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(ctx, "Respnse "+response, Toast.LENGTH_SHORT).show();
                if (response.toString().trim().equals("0"))
                {
                    methods.showMessage(Activity_myincome.this,"Error","Expense Already exist!",R.drawable.ic_close,R.color.red);
                }
                else if (response.toString().trim().equals("1"))
                {
                    methods.showMessage(Activity_myincome.this,"Message","Successfuly Save",R.drawable.ic_done,R.color.green);
                    startActivity(new Intent(ctx,Activity_editbudgetplan.class));
                    finish();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create(Activity_myincome.this).setText("NO INTERNET CONNECTION!")
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Date date;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = Calendar.getInstance().getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("type","insert");
                params.put("userID",Integer.toString(userId));
                params.put("categoryID",Integer.toString(28));
                params.put("payment",myspinner.getSelectedItem().toString());
                params.put("dateCreated",fDate);
                params.put("amount",""+final_amount);
                params.put("noteDesc",txtdesc.getText().toString());
                params.put("amountDetails",amountDetails);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateIncome() {
        final String amountDetails = "amount-"+amount+"|monthly-"+txtmonthly.getText().toString()+"|annually-"+txtanually.getText().toString()+"";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_income.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.toString().trim().equals("0"))
                {
                    methods.showMessage(Activity_myincome.this,"Error","Expense Already exist!",R.drawable.ic_close,R.color.red);
                }
                if (response.toString().trim().equals("1"))
                {
                    methods.showMessage(Activity_myincome.this,"Message","Successfuly Save",R.drawable.ic_done,R.color.green);
                    startActivity(new Intent(ctx,Activity_dashboard.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create(Activity_myincome.this).setText("NO INTERNET CONNECTION!")
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Date date;
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = Calendar.getInstance().getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("type","update");
                params.put("userID",Integer.toString(userId));
                params.put("categoryID",Integer.toString(28));
                params.put("payment",myspinner.getSelectedItem().toString());
                params.put("amount",""+final_amount);
                params.put("noteDesc",txtdesc.getText().toString());
                params.put("dateCreated",fDate);
                params.put("amountDetails",amountDetails);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void popDdate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMMM, yyyy");
        String fDate = df.format(date);
        lblDateToday.setText(fDate);
    }

    private boolean checkAmount() {
        if (txtamount.getText().toString().equals(""))
            return true;
        return false;
    }

    public String getIncome()
    {
        AndroidNetworking.get(methods.server()+"getIncome.php?username="+username)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        String res = "";
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            res = jsonObject.getString("message");
                            int code = jsonObject.getInt("code");
                            if (code != 0) {
                                btnSave.setVisibility(View.INVISIBLE);
                                txtamount.setText("" + methods.formatter.format(jsonObject.getDouble("amount")));
                                IncomeSingleton.getInstance().setAllIncome(Double.parseDouble(jsonObject.getString("amount")));
                                txtdesc.setText("" + jsonObject.getString("description"));
                                String type = jsonObject.getString("periodId");
                                setSelectedSpinner(type);
                                txtdesc.setEnabled(false);
                                txtamount.setEnabled(false);
                                myspinner.setEnabled(false);

                                fabMenu.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                fabMenu.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                    }
                });
        return rsponse;
    }

    private void setSelectedSpinner(String type) {
        if (type!="")
        {
            if ( type.equals("Weekly"))
                myspinner.setSelection(1);
            else if (type.equals("Semi-Monthly"))
                myspinner.setSelection(2);
            else if (type.equals("Monthly"))
                myspinner.setSelection(3);
        }
        else
        {
            myspinner.setSelection(0);
        }
    }

}
