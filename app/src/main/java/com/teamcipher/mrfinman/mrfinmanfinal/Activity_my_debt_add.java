package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
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
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Models.Category;
import Models.Debts;
import Singleton.IncomeSingleton;
import Singleton.UserLogin;
import Utils.customMethod;
import Utils.message;
import Utils.methods;

public class Activity_my_debt_add extends AppCompatActivity implements View.OnClickListener {

    Button btnSave;
    EditText txtamount,txtDate,txtnote,txtname,txtNo,txtAmountP;
    UserLogin user = UserLogin.getInstance();
    int nPrevSelGridItem = -1, userId;
    SimpleDateFormat df = new SimpleDateFormat("dd MMMM, yyyy");
    ArrayList<Category> categories = new ArrayList<>();
    Spinner debtTypeSpinner;
    Context ctx;
    Double amount_partial = 0.0, amount_per_month = 0.0;
    TextView lbldetails;
    final SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy");

    int selectedID = -1,debtId = -1;
    String categoryNameSelected = "",datepick,amount = "";
    Bundle bundle;
    Calendar calendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_debt);
        bundle = getIntent().getExtras();

        initialization();
        onclicks();
        checkIntent();
        noOnchange();
    }

    private void noOnchange() {
        txtDate.setEnabled(false);
        txtNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                OverViewDetails();
            }
        });
    }

    private void OverViewDetails()
    {
        try
        {
            if (!(Double.parseDouble(txtAmountP.getText().toString()) > Double.parseDouble(txtamount.getText().toString())))
            {
                calendar = Calendar.getInstance();
                int nNo = Integer.parseInt(txtNo.getText().toString());
                txtNo.setText(""+nNo);
                double amt = Double.parseDouble(txtamount.getText().toString().replace(",","")) / nNo;
                amount_per_month = amt;

                String dueDt = "";
                calendar.add(Calendar.MONTH,nNo);
                datepick = methods.date.format(calendar.getTime());
                String str = "";
                if (!(txtAmountP.getText().toString().equals("0") || txtAmountP.getText().toString().equals("")))
                {

                    if (Double.parseDouble(txtAmountP.getText().toString()) < 0)
                    {
                        str = "By default "+methods.dateComplete.format(calendar.getTime())+" is your due date.\nYou will pay your debt in "+txtNo.getText().toString()+" Month(s) amounting Php "+methods.formatter.format(amt)+" per month.";
                    }
                    else
                    {
                        Double amount =( Double.parseDouble(txtamount.getText().toString()) - Double.parseDouble(txtAmountP.getText().toString())) / Double.parseDouble(txtNo.getText().toString());
                        str = "By default "+methods.dateComplete.format(calendar.getTime())+" is your due date.You have paid a partial payment a total of Php "+methods.formatter.format(Double.parseDouble(txtAmountP.getText().toString()))+" \nYou will pay your debt in "+txtNo.getText().toString()+" Month(s) amounting Php "+methods.formatter.format(amount)+" per month.";
                    }

                }
                else
                {
                    str = "By default "+methods.dateComplete.format(calendar.getTime())+" is your due date.\nYou will pay your debt in "+txtNo.getText().toString()+" Month(s) amount Php "+methods.formatter.format(amt)+" a month.";
                }
                lbldetails.setText(str);
            }
            else
            {
                message.alertWarning(ctx,"Partial Payment is higher than the debt amount!");
            }

            txtDate.setText(""+methods.dateComplete.format(calendar.getTime()));
        }
        catch (Exception ex)
        {
            txtDate.getText().clear();
            lbldetails.setText("");
        }
    }

    private void checkIntent() {
        try
        {
            if (bundle != null)
            {
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                ActionBar actionBar = getSupportActionBar();
                TextView tv = new TextView(getApplicationContext());
                Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                tv.setLayoutParams(lp);
                tv.setText("Edit Debt"); // ActionBar title text
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(25);
                tv.setTypeface(typeface, typeface.ITALIC);
                actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_edit);
                actionBar.setCustomView(tv);

                datepick = bundle.getString("dueDate");

                int id = bundle.getInt("CategoryId");

                txtname.setText(""+bundle.getString("Name"));
                txtamount.setText(""+bundle.getString("amount"));
                txtnote.setText(""+bundle.getString("Desc"));

                debtId = bundle.getInt("Id");

                txtNo.setText(""+bundle.getInt("no"));
                String targetDt = bundle.getString("dueDate");
                String[] arrdate = targetDt.split("/");
                Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);
                calendar.setTime(d1);
                txtDate.setText(df.format(calendar.getTime()));
                datepick = dt.format(calendar.getTime());

                btnSave.setText("UPDATE");
                btnSave.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_success));

                OverViewDetails();
            }
            else
            {
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ActionBar actionBar = getSupportActionBar();
                TextView tv = new TextView(getApplicationContext());
                Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                tv.setLayoutParams(lp);
                tv.setText("New Debt"); // ActionBar title text
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(25);
                tv.setTypeface(typeface, typeface.ITALIC);
                actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
                actionBar.setCustomView(tv);

            }
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

        }
        return super.onOptionsItemSelected(item);
    }
    private void onclicks() {
        txtDate.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    private String getPreference(String key) {
        SharedPreferences preferences = getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }
    private void initialization() {
        try
        {
            ctx = Activity_my_debt_add.this;
            btnSave = findViewById(R.id.btn_new_debt_save);
            txtamount = findViewById(R.id.txt_my_debt_amount);
            txtDate = findViewById(R.id.txt__new_debt_target_date);
            txtNo = findViewById(R.id.debt_no);
            txtnote = findViewById(R.id.txt_my_goal_debt_note);
            txtname = findViewById(R.id.txt_my_goal_debt_name);
            lbldetails = findViewById(R.id.dbt_details);

            userId = Integer.parseInt(getPreference("userID"));
            txtAmountP = findViewById(R.id.txt_add_p_amount);
            txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try
                    {
                        amount = txtamount.getText().toString().replace(",","");
                        if (amount.equals(""))
                            txtamount.setError(null);
                        else if (amount != "")
                        {
                            if (Double.parseDouble(amount) > 10)
                            {
                                txtamount.setText(""+methods.formatter.format(Double.parseDouble(amount)));
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
            });
            txtNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    OverViewDetails();
                }
            });

            txtamount.setError(null);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt__new_debt_target_date:
                showCalendar();
                break;
            case R.id.btn_new_debt_save:
                try
                {
                    if (checkInput()) {
                       // Toast.makeText(ctx, "CHECK INPUT TRUe", Toast.LENGTH_SHORT).show();
                        methods.vibrate(ctx);
                        if (btnSave.getText().toString().equals("SAVE")) {
                            String definition = txtnote.getText().toString();
                            if (!(txtAmountP.getText().toString().replace(",","").trim().equals("0")))
                            {
                                definition += "\nPartial payment " + methods.formatter.format(Double.parseDouble(txtAmountP.getText().toString())) + "";
                            }
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                            final Date date = Calendar.getInstance().getTime();
                            String fDate = df.format(date);
                            Double amtBalance = (Double.parseDouble(txtamount.getText().toString().replace(",","")) - Double.parseDouble(txtAmountP.getText().toString().replace(",",""))) / Double.parseDouble(txtNo.getText().toString()) ;

                            Map<String, String> params = new HashMap<>();
                            params.put("categoryId", "10");
                            params.put("dateCreated", "" + fDate);
                            params.put("amount", "" + amount);
                            params.put("debtName", "" + txtname.getText());
                            params.put("period", "Month/s");
                            params.put("equi", "" + amount_per_month);
                            params.put("balance", "" + amtBalance);
                            params.put("noDays", txtNo.getText().toString());
                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.MONTH, 1);
                            params.put("dueDate", methods.date.format(cal.getTime()));
                            params.put("description", definition);
                            params.put("userId", "" + userId);
                            params.put("type", "INSERT");

                            Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
                            if (Double.parseDouble(amount) > IncomeSingleton.getInstance().getAllIncome())
                            {
                                String str = "Oops! Your Debt must not exceed from your income!";

                                Alerter.create((Activity) getApplicationContext())
                                        .setTitle("Message")
                                        .setText(str)
                                        .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                                        .setDuration(5000)
                                        .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Alerter.hide();
                                            }
                                        }).show();
                                txtamount.setError("");
                            }
                            else
                            {
                                saveDebt(params);
                            }
                        } else {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");

                            final Date date = Calendar.getInstance().getTime();
                            String fDate = df.format(date);

                            Map<String, String> params = new HashMap<>();
                            params.put("categoryId", "10");
                            params.put("dateCreated", "" + fDate);
                            params.put("amount", "" + amount);
                            params.put("debtName", "" + txtname.getText().toString());
                            params.put("period", "Monthly");
                            params.put("noDays", "" + txtNo.getText());
                            params.put("equi", "" + txtNo.getText());
                            params.put("balance", "" + txtNo.getText());

                            params.put("dueDate", datepick);
                            params.put("description", txtnote.getText().toString());
                            params.put("userId", "" + userId);
                            params.put("debtId", "" + debtId);
                            params.put("type", "UPDATE");

                            saveDebt(params);
                        }
                    }
                    else
                    {
                        //Toast.makeText(ctx, "CHECK INPUT FALSE", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception ex)
                {
                    //Toast.makeText(ctx, "Error "+ex.toString(), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Boolean checkInput() {
        try
        {
            if (TextUtils.isEmpty(txtamount.getText().toString()))
            {
                message.error("Empty amount!",ctx);
                message.alertError(ctx,"Empty amount!");
                return false;
            }
            if (datepick == null || datepick.toString().equals("")) {
                message.error("Target date must not be null!",ctx);
                message.alertError(ctx,"Target date must not be null!");
                return false;
            }
            if (txtname.getText().toString() == "")
            {
                txtname.setError("Debt name must not be empty!");
                message.alertError(ctx,"Debt name must not be empty!");
                return false;
            }
            Double amt = Double.parseDouble(txtamount.getText().toString().replace(",",""));
            if (amt <= 10)
            {
                txtamount.setError("Amount must be greater than Php 10.00!");
                message.alertError(ctx,"Amount must be greater than Php 10.00!");
                return false;
            }
            //if (IncomeSingleton.getInstance().getIncomeMonthly() > )
            if (TextUtils.isEmpty(txtNo.getText().toString()))
            {
                txtNo.setError("Empty field!");
                return false;
            }
            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private void saveDebt(final Map<String,String> params) {
        try {
            if (bundle == null) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "debt.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("TESTING",response.toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            switch (jsonObject.getInt("code"))
                            {
                                case 0:
                                    message.error(jsonObject.getString("message"),ctx);
                                    break;
                                case 1:
                                    message.success(jsonObject.getString("message"),ctx);
                                    onBackPressed();

                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.error(error.toString(),ctx);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
            else
            {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "debt.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            switch (jsonObject.getInt("code"))
                            {
                                case 0:
                                    message.error(jsonObject.getString("message"),ctx);
                                    break;
                                case 1:
                                    message.success(jsonObject.getString("message"),ctx);
                                    onBackPressed();

                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ctx, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.error(error.toString(),ctx);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
        catch (Exception ex)
        {
            methods.showMessage(Activity_my_debt_add.this, "Mr.FinMan", "" + ex, R.drawable.ic_close, R.color.red);
        }
    }

    private void showCalendar() {
        try
        {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");

            DatePickerDialog.OnDateSetListener  mdatelistener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    //month = month +1;
                    calendar.set(year, month, day);
                    Calendar calendar_current = Calendar.getInstance();
                    calendar_current.add(Calendar.DATE,7);

                    if (calendar.after(calendar_current))
                    {
                        txtDate.setText(df.format(calendar.getTime()));
                        datepick = dt.format(calendar.getTime());

                    }
                    else
                    {
                        message.warning("Due date must be in the future,\nMinimum of 1 week from today's date,",ctx);
                        showCalendar();
                    }
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(Activity_my_debt_add.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,mdatelistener,year,month,day);
            dialog.show();

        }catch (Exception ex)
        {

        }


    }



}
