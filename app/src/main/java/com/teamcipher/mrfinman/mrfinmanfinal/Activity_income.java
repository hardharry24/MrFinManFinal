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
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
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
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;

public class Activity_income extends AppCompatActivity implements View.OnClickListener {
    GridView gridView;
    TextView date_now;
    Date date;
    SimpleDateFormat df;
    Calendar calendar;
    int nPrevSelGridItem = -1;
    String categoryiconSelected = "", username = "";
    String categoryNameSelected = "",amount= "";
    int selectedID = -1,userId = 0;
    ArrayList<Category> categories = new ArrayList<>();
    CategoryGridviewAdapter adapter;
    public UserLogin user = UserLogin.getInstance();
    Context ctx;
    Button btnSave;
    EditText txtAmount,txtNote;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        onheaderActionBar("Additional Income");

        initialization();
        populateCategories();
        popDdate();
        gridViewListener();


        btnSave.setOnClickListener(this);

    }
    private void onheaderActionBar(String title) {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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

    private void gridViewListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View viewPrev;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                validateAmt();
                try {
                    if (nPrevSelGridItem != -1) {
                        viewPrev = gridView.getChildAt(nPrevSelGridItem);
                        viewPrev.setBackgroundColor(Color.TRANSPARENT);
                    }
                    nPrevSelGridItem = position;
                    if (nPrevSelGridItem == position) {
                        //View viewPrev = (View) gridview.getChildAt(nPrevSelGridItem);
                        categoryNameSelected = categories.get(position).getCategoryName();
                        categoryiconSelected = categories.get(position).getIcon().toString();
                        selectedID = categories.get(position).getId();
                        view.setBackgroundColor(getResources().getColor(R.color.light_grey));

                        adapterView.showContextMenuForChild(view);
                        if (categories.get(position).getId() != 0)
                            Alerter.create(Activity_income.this).setTitle("Mr.FinMan").setBackgroundColorRes(R.color.gray).setIcon(R.drawable.ic_done).setText(categoryNameSelected+"").show();
                        else {
                            selectedID = -1;
                            message.info("Oops!\nYou must be a premium user!", getBaseContext());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {
        username = getPreference("username");
        userId  = Integer.parseInt(getPreference("userID"));
        ctx = Activity_income.this;
        gridView = findViewById(R.id.grid_income_view_category);
        date_now = findViewById(R.id.income_date_now);
        btnSave = findViewById(R.id.btn_income_save);
        txtAmount = findViewById(R.id.txt_income_amount);
        txtNote = findViewById(R.id.txt_income_note);

        txtAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                validateAmt();
            }
        });

        txtAmount.setError(null);

    }
    private void validateAmt()
    {
        try
        {
            String amt = txtAmount.getText().toString().replace(",","");
            if (TextUtils.isEmpty(amt))
                txtAmount.setError(null);
            else if (!(TextUtils.isEmpty(amt)))
            {
                amount = amt;
                if (Double.parseDouble(amount) > 10)
                {
                    txtAmount.setText(""+methods.formatter.format(Double.parseDouble(txtAmount.getText().toString())));
                }
                else
                {
                    txtAmount.setError("Amount must be greater than Php 10.00!");
                }
            }
        }
        catch (Exception ex)
        {
        }
    }
    private void popDdate() {
        date = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("MMMM, yyyy");
        String fDate = df.format(date);
        date_now.setText(fDate);
    }
    private void populateCategories() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"list_Income_Category.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i =0 ; i<jsonArray.length();i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        Category c1 = new Category();
                        c1.setCategoryName(j.getString("Desc"));
                        c1.setIcon(j.getString("Icon"));
                        c1.setId(j.getInt("ID"));
                        if (c1.getId() == 27)
                        {}
                        else
                            categories.add(c1);
                    }
                    adapter = new CategoryGridviewAdapter(Activity_income.this,categories);
                    gridView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING","Add income"+error);
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

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_income_save:
                if (checkTxt())
                    onsave();
                break;
        }
    }

    private void saveIncome() {
        final String amountDetails = "amount-"+amount+"|type-Additional";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_income.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //message.success(response,Activity_income.this);
                if (response.toString().trim().equals("0"))
                {
                    methods.showMessage(Activity_income.this,"Error","Expense Already exist!",R.drawable.ic_close,R.color.red);
                }
                if (response.toString().trim().equals("1"))
                {
                    methods.showMessage(Activity_income.this,"Message","Success!",R.drawable.ic_done,R.color.green);
                    waitNext();
                }
                else
                    message.error(""+response,Activity_income.this);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create(Activity_income.this).setText("NO INTERNET CONNECTION!")
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = Calendar.getInstance().getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("type","insert");
                params.put("userID",Integer.toString(userId));
                params.put("categoryID",Integer.toString(selectedID));
                params.put("payment","Additional");
                params.put("dateCreated",fDate);
                params.put("amount",amount);
                params.put("noteDesc",txtNote.getText().toString());
                params.put("amountDetails",amountDetails);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void onsave() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to save an Additional Income?\n\nNote:\n This income will only be added for the current month.");
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (selectedID !=  -1)
                    saveIncome();
                else
                {
                    selectedID = -1;
                    message.error("Please Select Category!",ctx);
                }
                // Toast.makeText(Activity_myincome.this, "WELCOME TO THE", Toast.LENGTH_SHORT).show();
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

    public Boolean checkTxt()
    {
        if (txtAmount.getText().toString().equals(""))
        {
            txtAmount.setError("No Valid Amount!");
            return false;
        }
        else if (txtNote.getText().toString().equals(""))
        {
            txtNote.setError("Empty Description!");
            return false;
        }
        else
        {
            txtAmount.setError(null);
            txtNote.setError(null);
        }
        return  true;
    }
}
