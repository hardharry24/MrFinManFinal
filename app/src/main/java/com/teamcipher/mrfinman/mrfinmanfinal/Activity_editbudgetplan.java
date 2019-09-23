package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.beardedhen.androidbootstrap.AwesomeTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.Category_edit_bp_adapter;
import Adapters.MyPrioritiesAdaptor;
import Models.Category;
import Singleton.CategoryTotalSingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;

public class Activity_editbudgetplan extends AppCompatActivity implements View.OnClickListener {
    SwipeMenuListView listView;
    Category_edit_bp_adapter adapter;
    Button btnNewCat, btnSave;
    ImageView btnBack;
    ProgressDialog progressDialog;
    public UserLogin user = UserLogin.getInstance();
    TextView lblTotal;
    ArrayList<Category> categories = new ArrayList<>();
    AwesomeTextView btnview;
    Context ctx;
    String username = "";
    int userId = 0;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editbudgetplan);

        initialization();
        populateCat();
        methodOnclick();
        onload("Edit Budget Plan");
        onloadItems();
        swipeMenu();
        initialize();


    }

    private void initialize() {
        loadIncome();
        loadALlincome();
    }

    public void loadALlincome()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getAllincome.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != "")
                {
                    IncomeSingleton.getInstance().setAllIncome(Double.parseDouble(response.toString()));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //message.error("NO INTERNET CONNECTION!",ctx);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",""+UserLogin.getInstance().getUsername());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void loadIncome()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"getSumIncome.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.toString().trim().equals("00"))
                {
                    IncomeSingleton.getInstance().setTotal(Double.parseDouble(response));
                }
                if (response.toString() == "00")
                {
                    IncomeSingleton.getInstance().setTotal(0);
                }
                else
                    IncomeSingleton.getInstance().setTotal(Double.parseDouble(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               //message.error("NO INTERNET CONNECTION!",ctx);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void swipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(Activity_editbudgetplan.this);
                deleteItem.setWidth(90);
                deleteItem.setIcon(R.drawable.ic_delete_forever_black_24dp);
                deleteItem.setBackground(R.drawable.user_backgroud_login);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        try {
                            categoryDeleteConfirmation(position);
                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(Activity_editbudgetplan.this, ex.toString(), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    private void onload(String title) {
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
    public void onloadItems()
    {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if (MyCategorySingleton.getInstance().getList().size() == 0 && categories.size() == 0)
                    promptAddCategory();
            }
        }.start();
    }
    private void promptAddCategory() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("Oops you have no categories!\nContinue to add category");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplication(), Activity_add_category.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialogBuilder.show();
    }

    private void categoryDeleteConfirmation(final int index) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("Are you sure you want to remove this category?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                final Category c = MyCategorySingleton.getInstance().getList().get(index);
                MyCategorySingleton.getInstance().getList().remove(index);
                adapter.notifyDataSetChanged();


                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "deleteCategory.php?userId="+userId+"&categoryId="+c.getId()+"", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.getString("code").equals("1"))
                                message.success(jsonObject.getString("message"),ctx);
                            else if (jsonObject.getString("code").equals("0"))
                                message.error(jsonObject.getString("message"),ctx);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        message.error(""+error,ctx);
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                requestQueue.add(stringRequest);


                //deleteCategory.php?userId=31&categoryId=2
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onsave();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void methodOnclick() {

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Intent intent;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.btn_edit_bottom_add:
                        intent = new Intent(getApplicationContext(), Activity_add_category.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.btn_edit_bottom_save:
                        methods.vibrate(ctx);
                        onsave();
                        break;
                }
                return true;
            }
        });

        loadBalance();
    }

    private void initialization() {
        username = getPreference("username");
        userId  = Integer.parseInt(getPreference("userID"));
        ctx = Activity_editbudgetplan.this;
        listView = findViewById(R.id.edit_budget_listview);
        listView.setItemsCanFocus(true);
        bottomNavigationView = findViewById(R.id.edit_budget_bottom_nav);

        lblTotal = findViewById(R.id.lbl_bp_total);

    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editplan_menu, menu);
        return true;
    }

    private void populateCat() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        String s = "";
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);

                            Category ct = new Category();
                            ct.setId(j.getInt("catID"));
                            ct.setIcon(j.getString("Icon"));
                            ct.setCategoryName(j.getString("Name"));//Percentage
                            ct.setPercentage(j.getDouble("Percentage"));
                            ct.setPriority(false);
                            categories.add(ct);

                            if (!contain(ct.getCategoryName())) {
                                {
                                    //s += ct.getCategoryName()+"  ";
                                    MyCategorySingleton.getInstance().getList().add(ct);
                                    //Toast.makeText(ctx, ""+ct.getCategoryName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                       // Toast.makeText(ctx, "M Size "+MyCategorySingleton.getInstance().getList().size(), Toast.LENGTH_SHORT).show();
                        adapter = new Category_edit_bp_adapter(Activity_editbudgetplan.this, MyCategorySingleton.getInstance().getList());
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    adapter = new Category_edit_bp_adapter(Activity_editbudgetplan.this, MyCategorySingleton.getInstance().getList());
                    listView.setAdapter(adapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(Activity_editbudgetplan.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public boolean contain(String name) {
        for (Category category : MyCategorySingleton.getInstance().getList()) {
            if (category.getCategoryName().equals(name))
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
    }

    private void onsave() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to save changes?");
        alertDialogBuilder.setIcon(R.drawable.ic_info_white);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if  (MyCategorySingleton.getInstance().totalVal()==100)
                {
                    if (saveCategory()) {
                       // methods.showMessage(Activity_editbudgetplan.this, "Message", "Category updated!", R.drawable.ic_done, R.color.green);
                        message.success("Successfully Updated!",ctx);
                        waitNext(new Activity_dashboard());
                    }
                }
                else
                {
                    methods.showMessage(Activity_editbudgetplan.this,"Warning","",R.drawable.ic_close,R.color.orange);
                    message.warning("Total percentage must be equal to 100%",ctx);
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.WHITE);
    }

    private void onsaveViewPlan() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to save changes?");
        alertDialogBuilder.setIcon(R.drawable.ic_info_white);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if  (MyCategorySingleton.getInstance().totalVal()==100)
                {
                    if (saveCategory()) {
                        // methods.showMessage(Activity_editbudgetplan.this, "Message", "Category updated!", R.drawable.ic_done, R.color.green);
                        message.success("Successfully Updated!",ctx);
                        waitNext(new Activity_my_budget_plan());
                    }
                }
                else
                {
                    methods.showMessage(Activity_editbudgetplan.this,"Warning","",R.drawable.ic_close,R.color.orange);
                    message.warning("Total percentage must be a total of 100%",ctx);
                }
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.WHITE);
    }

    private Boolean saveCategory() {
        for (int i = 0; i < Category_edit_bp_adapter.categories.size(); i++) {
            Category cat = Category_edit_bp_adapter.categories.get(i);
            {
                String catId = Integer.toString(Category_edit_bp_adapter.categories.get(i).getId());
                int userid = userId;
                Double percentage = Category_edit_bp_adapter.categories.get(i).getPercentage();
                if (cat.getPriority())
                    addToMyCategoy(catId, userid, percentage,1);
                else
                    addToMyCategoy(catId, userid, percentage,0);

            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onsave();
        }
        if (id == R.id.edit_eye_b_plan)
        {
            onsaveViewPlan();
        }
        else if (id == R.id.edit_setting_b_plan)
        {
            showSetting();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSetting() {
        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_edit_bplan_show_setting);
        dialog.setCancelable(false);
        Button btnSet,btnView;
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        btnSet = dialog.findViewById(R.id.btnSetPrio);
        btnView = dialog.findViewById(R.id.btnView);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEdit();
            }

            private void showEdit() {
                final Dialog dialog1 = new Dialog(ctx);
                dialog1.setContentView(R.layout.dialogue_edit_b_plan_show_prio);

                TextView lblTitle = dialog1.findViewById(R.id.title);
                ListView listView = dialog1.findViewById(R.id.listView_edit_b_plan_show_prio);

                lblTitle.setText("Edit Priorities");

                MyPrioritiesAdaptor adaptor = new MyPrioritiesAdaptor(ctx,MyCategorySingleton.getInstance().getList(),"EDIT");
                listView.setAdapter(adaptor);

                Button btn = dialog1.findViewById(R.id.btnOk);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    for (Category cmt : MyCategorySingleton.getInstance().getList()) {
                        if (cmt.getPriority())
                            addToMyCategoy(String.valueOf(cmt.getId()), userId, cmt.getPercentage(),1);
                        else
                            addToMyCategoy(String.valueOf(cmt.getId()), userId, cmt.getPercentage(),0);
                    }
                        Toast.makeText(Activity_editbudgetplan.this, "Successfuly Set Priorities!", Toast.LENGTH_SHORT).show();
                        dialog1.dismiss();
                    }
                });
                dialog1.create();
                dialog1.show();
            }
        });
        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showView();

            }

            private void showView() {
                final Dialog dialog1 = new Dialog(ctx);
                dialog1.setContentView(R.layout.dialogue_edit_b_plan_show_prio);

                TextView lblTitle = dialog1.findViewById(R.id.title);
                ListView listView = dialog1.findViewById(R.id.listView_edit_b_plan_show_prio);

                lblTitle.setText("View Priorities");
                Button btn = dialog1.findViewById(R.id.btnOk);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog1.dismiss();
                    }
                });

                ArrayList<Category> cmt = new ArrayList<>();
                for(Category ct: MyCategorySingleton.getInstance().getList())
                {
                    if (ct.getPriority())
                        cmt.add(ct);
                }
                MyPrioritiesAdaptor adaptor = new MyPrioritiesAdaptor(ctx,cmt,"VIEW");
                listView.setAdapter(adaptor);
                dialog1.create();
                dialog1.show();
            }
        });

        dialog.create();
        dialog.show();
    }

    public void waitNext(final Activity act)
    {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(),act.getClass());
                startActivity(intent);
                finish();
            }
        }.start();
    }
    public void loadBalance()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TESTING",""+methods.formatter00.format(MyCategorySingleton.getInstance().totalVal()));
                        TextView lbltotal = findViewById(R.id.lbl_edit_bp_total);
                        lbltotal.setText(methods.formatter00.format(CategoryTotalSingleton.getInstance().totalVal()) +" / 100%");
                        lblTotal.setText(methods.formatter00.format(CategoryTotalSingleton.getInstance().totalVal())+"%");
                        if (CategoryTotalSingleton.getInstance().totalVal()==100)
                        {
                            lbltotal.setTextColor(getResources().getColor(R.color.green));
                            lblTotal.setTextColor(getResources().getColor(R.color.green));

                        }
                        else if (CategoryTotalSingleton.getInstance().totalVal()> 100)
                        {
                            lbltotal.setTextColor(getResources().getColor(R.color.red));
                            lblTotal.setTextColor(getResources().getColor(R.color.red));

                        }
                        else
                        {
                            lbltotal.setTextColor(getResources().getColor(R.color.orange));
                            lblTotal.setTextColor(getResources().getColor(R.color.orange));

                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 2000);
    }


    private void addToMyCategoy( final String catID, final int userid, final double percentage,final int isPriority)
    {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "NewExpenseUserCategory.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TESTING"," Check Edit bplan "+error);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("catID", catID);
                    params.put("user_ID", Integer.toString(userid));
                    params.put("percentage", Double.toString(percentage));
                    params.put("isPriority",String.valueOf(isPriority));
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
    }





}
