package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.muddzdev.styleabletoast.StyleableToast;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Adapters.MyGoalAdapter;
import Adapters.UserDetailsAdaptor;
import Models.Category;
import Models.CategoryAmount;
import Models.MyGoals;
import Models.Transaction;
import Models.user;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;
import Utils.notify;

public class Activity_my_goals_add extends AppCompatActivity implements View.OnClickListener {

    Button btnSave;
    EditText txtamount,txtDate,txtnote,txtname;
    GridView gridView;
    UserLogin user = UserLogin.getInstance();
    int nPrevSelGridItem = -1;
    SimpleDateFormat df = new SimpleDateFormat("dd MMMM, yyyy");
    ArrayList<Category> categories = new ArrayList<>();
    Boolean hasDebt = false;
    Calendar dateChoosen;
    int selectedID = -1,userId = 0;
    String categoryNameSelected = "",datepick,amount = "",dueDate="";
    Bundle bundle;
    Calendar calendar = Calendar.getInstance();
    Context ctx;
    String dbtName = "",dbtDueDate ="",error_message = "",username="";
    final SimpleDateFormat dt = new SimpleDateFormat("MM/dd/yyyy");
    Double dbtAmount = 0.0,amtRemaining = 0.0;
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_goal);


        bundle = getIntent().getExtras();
        initialization();
        populate();
        onclicks();

        checkIntent();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt__new_goal_target_date:
                showCalendar();
                break;
            case R.id.btn_new_goal_save:
                try
                {
                    if (validate())
                    {
                        if (btnSave.getText().toString().equals("SAVE"))
                        {
                            Calendar cal = Calendar.getInstance();
                            String today = methods.MM_yyyy.format(cal.getTime());
                            String entered = methods.MM_yyyy.format(dateChoosen.getTime());
                            if (today.equals(entered))
                            {
                                if (checkExpenseCurrent(selectedID))
                                {
                                    methods.vibrate(this);
                                    saveGoal("SAVE");
                                }
                                else
                                {
                                    Double amtNeed = Double.parseDouble(txtamount.getText().toString().replace(",","")) - amtRemaining;
                                    message.alertWarning(ctx,"Oops! You are out of budget for this expense category,\nYou only have Php "+methods.formatter.format(amtRemaining)+".\n\nYou need to save Php "+methods.formatter.format(amtNeed)+" for the month of "+methods.month.format(dateChoosen.getTime()));
                                }
                            }
                            else
                            {
                                if (checkExpenseNextMonth(selectedID))
                                {
                                    methods.vibrate(this);
                                    saveGoal("SAVE");
                                }
                                else
                                {
                                    Double amtNeed = Double.parseDouble(txtamount.getText().toString().replace(",","")) - amtRemaining;
                                    message.alertWarning(ctx,"Oops! You are out of budget for this expense category,\nYou only have Php "+methods.formatter.format(amtRemaining)+".\n\nYou need to save Php "+methods.formatter.format(amtNeed)+" for the month of "+methods.month.format(dateChoosen.getTime()));
                                }
                            }
                        }
                        else
                        {
                            methods.vibrate(this);
                            saveGoal("UPDATE");
                        }
                    }
                }
                catch (Exception ex)
                {

                }
                break;
        }
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void checkIntent() {
        try
        {
            if (bundle != null) {
                if (bundle.getString("Type").equals("EDIT")) {
                    onheaderActionBar("Edit Goal");
                    txtname.setText("" + bundle.getString("goal_name"));
                    txtamount.setText("" + bundle.getString("amount"));
                    txtnote.setText("" + bundle.getString("description"));
                    selectedID =  Integer.parseInt(bundle.getString("categoryId"));
                    gridView.setSelection(selectedID);
                    String targetDt = bundle.getString("targetDate");
                    String[] arrdate = targetDt.split("/");
                    Date d1 = new Date(arrdate[0]+"/"+arrdate[1]+"/"+arrdate[2]);
                    calendar.setTime(d1);
                    txtDate.setText(df.format(calendar.getTime()));
                    datepick = dt.format(calendar.getTime());

                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setDisplayUseLogoEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    getSupportActionBar().setDisplayShowCustomEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    ActionBar actionBar = getSupportActionBar();
                    TextView tv = new TextView(getApplicationContext());
                    Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                            RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                    tv.setLayoutParams(lp);
                    tv.setText("Edit Goal"); // ActionBar title text
                    tv.setTextColor(Color.WHITE);
                    tv.setTextSize(25);
                    tv.setTypeface(typeface, typeface.ITALIC);
                    actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_edit);
                    actionBar.setCustomView(tv);



                    btnSave.setText("UPDATE");
                    btnSave.setBackgroundColor(getResources().getColor(R.color.bootstrap_brand_success));
                }
            }
            else
            {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayUseLogoEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayShowCustomEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                ActionBar actionBar = getSupportActionBar();
                TextView tv = new TextView(getApplicationContext());
                Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                        RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
                tv.setLayoutParams(lp);
                tv.setText("New Goal"); // ActionBar title text
                tv.setTextColor(Color.WHITE);
                tv.setTextSize(25);
                tv.setTypeface(typeface, typeface.ITALIC);
                actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_add);
                actionBar.setCustomView(tv);

            }

        }
        catch (Exception ex)
        {
            Log.e("Error",""+ex.toString());
        }


    }

    private void onheaderActionBar(String title) {
        setTitle(title);
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


    private void populate() {
        try
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"getUserCategory.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i =0 ; i<jsonArray.length();i++)
                        {
                            JSONObject j = jsonArray.getJSONObject(i);
                            Category c1 = new Category();
                            c1.setCategoryName(j.getString("Name"));
                            c1.setIcon(j.getString("Icon"));
                            c1.setId(j.getInt("catID"));


                            if (c1.getId() == 1 || c1.getId() == 10 || c1.getId() == 31 || c1.getId() == 32)
                            {}
                            else
                                categories.add(c1);
                        }
                        CategoryGridviewAdapter adapter = new CategoryGridviewAdapter(ctx,categories);
                        gridView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TESTING"," Expense  "+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
        catch (Exception ex)
        {

        }
    }

    private void initialization() {
        try
        {
            userId  = Integer.parseInt(getPreference("userID"));
            username = getPreference("username");
            ctx = Activity_my_goals_add.this;
            btnSave = findViewById(R.id.btn_new_goal_save);
            txtamount = findViewById(R.id.txt_my_goal_amount);
            txtDate = findViewById(R.id.txt__new_goal_target_date);
            gridView = findViewById(R.id.grid_add_goal_view_category);
            txtnote = findViewById(R.id.txt_my_goal_add_note);
            txtname = findViewById(R.id.txt_my_goal_add_name);
            categoryAmountList();
            remaining();

            gridviewListener();
            txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    amount = txtamount.getText().toString().replace(",","");
                    if (txtamount.getText().toString().equals(""))
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
            });

            txtamount.setError(null);
        }
        catch (Exception ex)
        {

        }
    }

    private void gridviewListener()
    {
        try
        {
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                View viewPrev;
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                    try {
                        checkHasDebtMonth(dueDate);
                        if (nPrevSelGridItem != -1) {
                            viewPrev = gridView.getChildAt(nPrevSelGridItem);
                            viewPrev.setBackgroundColor(Color.TRANSPARENT);
                        }
                        nPrevSelGridItem = position;
                        if (nPrevSelGridItem == position) {
                            //View viewPrev = (View) gridview.getChildAt(nPrevSelGridItem);
                            categoryNameSelected = categories.get(position).getCategoryName();
                            selectedID = categories.get(position).getId();
                            view.setBackgroundColor(getResources().getColor(R.color.light_grey));
                            adapterView.showContextMenuForChild(view);
                            Alerter.create(Activity_my_goals_add.this).setTitle("Mr.FinMan").setBackgroundColorRes(R.color.cardcolor).setIcon(R.drawable.ic_done).setText(categoryNameSelected+"").show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private Boolean checkExpenseCurrent(int categoryId) {
        try
        {
            String amnt = txtamount.getText().toString().replace(",","");
            for (CategoryAmount cmt : mylist)
            {
                Log.d("TESTING",categoryId+"  category Id: "+cmt.getCategoryId());
                if (cmt.getCategoryId() == categoryId)
                {
                    Double amount =Double.parseDouble(amnt);
                    Double alloAmt = cmt.getAmount();
                    String amtOver = cmt.getRemPercentage();
                    if( amount <= alloAmt)
                    {
                        return true;
                    }
                    else
                    {
                        error_message = "You only have Php "+methods.formatter.format(alloAmt)+" budget for this category in this current month!\n\n";
                        amtRemaining = alloAmt;
                        return false;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            return false;
        }
        return false;
    }

    private Boolean checkExpenseNextMonth(int categoryId) {
        try
        {
            String amnt = txtamount.getText().toString().replace(",","");
            for (CategoryAmount cmt : mylist)
            {
                Log.d("TESTING",categoryId+"  category Id: "+cmt.getCategoryId());
                if (cmt.getCategoryId() == categoryId)
                {
                    Double amount =Double.parseDouble(amnt);
                    Double alloAmt = cmt.getAmount();
                    String amtOver =  cmt.getRemPercentage().replace(",","");
                    Double amountOver = Double.parseDouble(amtOver);
                   // Toast.makeText(this, "Budget "+amtOver+"  "+cmt.getRemPercentage(), Toast.LENGTH_SHORT).show();
                    if(  amount <= amountOver)
                    {
                        return true;
                    }
                    else
                    {
                        error_message = "You only have Php "+methods.formatter.format(alloAmt)+" budget for this category in this current month!\n\n";
                        amtRemaining = alloAmt;
                        return false;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            return false;
        }
        return false;
    }


    private boolean validate() {
        if ( selectedID != -1)
        {
            if (Double.parseDouble(amount) > 10)
            {
                if (datepick != null)
                {
                    if (!(TextUtils.isEmpty(txtname.getText().toString())))
                    {
                        return true;
                    }
                    else
                    {
                        txtname.setError("Goal name must not be empty!");
                        return false;
                    }
                }
                else
                {
                    methods.showMessage(this,"Error","Target date must not be null",R.drawable.ic_close,R.color.red);
                    return false;
                }
            }
            else
            {
                methods.showMessage(this,"Error","Amount must be greater than Php 10.00!",R.drawable.ic_close,R.color.red);
                return false;
            }
        }
        else
        {
            methods.showMessage(this,"Error","Category must be selected!",R.drawable.ic_close,R.color.red);
            return false;
        }
    }

    public void categoryAmountList()
    {
        RemainingExpenseST.resetInstance();

        categoryAmounts.clear();
        AndroidNetworking.get(methods.server()+"getCategoryAmount.php?userId="+user.getUser_ID())
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i =0 ;i<response.length(); i++)
                        {
                            try {
                                JSONObject jObject = response.getJSONObject(i);
                                CategoryAmount c = new CategoryAmount();
                                c.setAmount(jObject.getDouble("amount"));
                                c.setCategoryId(jObject.getInt("categoryId"));
                                c.setPercentage(jObject.getDouble("percentage"));
                                categoryAmounts.add(c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    public void onError(ANError error) {
                        Log.d("TESTING"," Expense  "+error);
                    }
                });
    }

    private void remaining()
    {
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                mylist.clear();
                for (int i = 0; i< MyCategorySingleton.getInstance().getList().size(); i++)
                {
                    Category category = MyCategorySingleton.getInstance().getList().get(i);
                    if (!check(category.getId()))
                    {
                        CategoryAmount camount = new CategoryAmount();
                        camount.setCategoryId( category.getId());
                        camount.setCategoryName(""+category.getCategoryName());
                        // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                        camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                        camount.setAmount(methods.amount(category.getPercentage()));

                        mylist.add(camount);
                        //RemainingExpenseST.getInstance().getList().add(camount);
                    }
                    for (CategoryAmount c:categoryAmounts) {
                        if (category.getId() == c.getCategoryId() )
                        {
                            Double amt = c.getAmount();
                            String totalExpensePercentage = methods.percentage(amt);
                            String remainingPercentage = ""+(category.getPercentage() - Double.parseDouble(totalExpensePercentage));
                            String remPerc = remainingPercentage+"/"+c.getPercentage();

                            String amountPercSet = ""+methods.amount(category.getPercentage());
                            Double remainingAmount = Double.parseDouble(amountPercSet) - c.getAmount();



                            CategoryAmount camount = new CategoryAmount();
                            camount.setCategoryName(""+category.getCategoryName());

                            camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                            camount.setAmount(remainingAmount);

                            camount.setCategoryId( c.getCategoryId());

                            mylist.add(camount);
                        }
                    }

                }
            }
        }.start();
    }
    public Boolean check(int id)
    {
        for (CategoryAmount c:RemainingExpenseST.getInstance().getList()) {
            if (id == c.getCategoryId() )
                return true;
        }
        return false;
    }


    private void saveGoal(String type) {
        try {
            if (type == "SAVE") {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "save_goal.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().trim().equals("1")) {
                            methods.showMessage((Activity) ctx, "Mr.FinMan", "Success!", R.drawable.ic_done, R.color.green);
                            waitNext();
                        }
                        else if (response.toString().trim().equals("0"))
                        {
                            message.error(response.toString(),ctx);
                        }
                        else {
                            methods.showMessage((Activity) ctx, "Mr.FinMan", "Success!", R.drawable.ic_done, R.color.green);
                            waitNext();
                        }
                    }

                    private void waitNext() {
                            new CountDownTimer(2000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    onBackPressed();
                                }
                            }.start();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TESTING","Save goal "+error);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                        final Date date = Calendar.getInstance().getTime();
                        String fDate = df.format(date);

                        Map<String, String> params = new HashMap<>();
                        params.put("userID", "" + userId);//
                        params.put("categoryID", Integer.toString(selectedID));
                        params.put("goalname", txtname.getText().toString());
                        params.put("amount", "" + amount);
                        params.put("note", txtnote.getText().toString());
                        params.put("targetDate", datepick);
                        params.put("dateCreated", fDate);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
            else if (type == "UPDATE")
            {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "goal.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.toString().trim().equals("1")) {
                           // notification("You have added your goal " + txtname.getText().toString() + " target date " + txtDate.getText().toString());
                            methods.showMessage((Activity) ctx, "Mr.FinMan", "Success!", R.drawable.ic_done, R.color.green);
                            onBackPressed();
                            //finish();
                        }
                        else if (response.toString().trim().equals("0"))
                        {
                            methods.showMessage((Activity) ctx, "Mr.FinMan", "" + response, R.drawable.ic_close, R.color.red);
                        }
                         else {
                            Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                        final Date date = Calendar.getInstance().getTime();
                        String fDate = df.format(date);


                        Map<String, String> params = new HashMap<>();
                        params.put("type","update");
                        params.put("id",bundle.getString("goal_ID"));
                        params.put("userID", "" + userId);//
                        params.put("categoryID", Integer.toString(selectedID));
                        params.put("goalname", txtname.getText().toString());
                        params.put("amount", "" + amount);
                        params.put("note", txtnote.getText().toString());
                        params.put("targetDate", datepick);
                        params.put("dateCreated", fDate);
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(stringRequest);
            }
        }
        catch (Exception ex)
        {

        }

    }


    private void showCalendar() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


         DatePickerDialog.OnDateSetListener  mdatelistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //month = month +1;
                calendar.set(year, month, day);
                Calendar calendarToday = Calendar.getInstance();
                if (calendar.getTime().after(calendarToday.getTime()))
                {
                    txtDate.setText(df.format(calendar.getTime()));
                    datepick = dt.format(calendar.getTime());
                    dateChoosen = calendar;
                    SimpleDateFormat dtformat = new SimpleDateFormat("M/yyyy");
                    dueDate = dtformat.format(calendar.getTime());
                }
                else
                {
                    message.alertError(ctx,"Target date must be later than today!");
                    //message.error("Target date must be in the future,\nMinimum of 1 week from today's date,\nYou target date must be on or after "+methods.dateComplete.format(calendarweek.getTime()),ctx);
                    showCalendar();
                }
            }
        };
        DatePickerDialog dialog = new DatePickerDialog((Activity) ctx, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,mdatelistener,year,month,day);
        dialog.show();

    }



    private void checkHasDebtMonth(String date)
    {
        AndroidNetworking.get(methods.USER_API_SERVER+"hasDebt.php?username="+user.getUsername()+"&date="+date+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                            try {
                                JSONObject jObject = response.getJSONObject(0);
                                switch (jObject.getInt("code"))
                                {
                                    case 0:
                                        hasDebt = false;
                                        break;
                                    case 1:
                                        hasDebt = true;
                                        dbtAmount = jObject.getDouble("dbtAmount");
                                      //  dbtName = jObject.getString("dbtName");
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                               // Toast.makeText(ctx, ""+e, Toast.LENGTH_SHORT).show();
                            }
                    }
                    public void onError(ANError error) {
                        //message.error(""+error,ctx);
                    }
                });
    }
}
