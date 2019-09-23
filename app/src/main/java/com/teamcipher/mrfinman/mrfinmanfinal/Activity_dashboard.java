package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.crowdfire.cfalertdialog.CFAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.SavingAdaptor;
import Models.Category;
import Models.Priorities;
import Models.User.Saving;
import Singleton.ExpenseDateRangeSingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.PrioritiesSingleton;
import Singleton.UserLogin;
import Singleton.ViewTypeSingleton;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.customMethod;
import Utils.message;
import Utils.methods;
import Utils.notify;
import background.background;
import retrofit2.Call;
import retrofit2.Callback;
import services.BillCheck;
import services.DebtCheck;
import services.GoalCheck;
import services.RealtimeBudgetRemCheck;

public class Activity_dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    DrawerLayout drawer;
    ImageButton menuLeft,menuRight;
    FrameLayout frameLayout;
    NavigationView navigationView1;
    NavigationView navigationView2;
    View viewNotification;
    String username;
    Dialog dialog;
    Context ctx;
    Bundle bundle;
    TextView lblUserlogin;
    ArrayList<Priorities> priorities = new ArrayList<>();
    UserLogin userInfo = new UserLogin();
    ArrayList<Saving> savings = new ArrayList<>();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    int badgeCount = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        loadPriorities();
        TypefaceProvider.registerDefaultIconSets();
        initialization();

        checkBundle();
    }

    private void checkBundle() {
        Intent intent = getIntent();
        String todo = intent.getStringExtra("TODO");
        Log.d("BUNDLE","NAA = "+todo);
        //TODO","ToBill
        if (bundle != null) {
            if (bundle.getString("TODO").toString().equals("ToBill")) {
                loadUserInfo();
                onheaderBind();
                onLoadCheckUser();
                populateCat();

                startServices();
                fragment_bills fr = new fragment_bills();
                fr.setArguments(bundle);
                fragmentRedirection(fr);
            }
        }
        else
        {
            loadUserInfo();
            onheaderBind();
            onLoadCheckUser();

            populateCat();
            fragmentRedirection(new fragment_main());
            startServices();
        }

    }

    private void startServices() {
        startService(new Intent(ctx, background.class));
        startService(new Intent(ctx, GoalCheck.class));
        startService(new Intent(ctx, BillCheck.class));
        startService(new Intent(ctx, DebtCheck.class));
        startService(new Intent(ctx, RealtimeBudgetRemCheck.class));
    }

    private void stopServices() {
        stopService(new Intent(ctx, background.class));
        stopService(new Intent(ctx, GoalCheck.class));
        stopService(new Intent(ctx, BillCheck.class));
        stopService(new Intent(ctx, DebtCheck.class));
        stopService(new Intent(ctx, RealtimeBudgetRemCheck.class));
    }



    private void populateCat() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);

                            Category ct = new Category();
                            ct.setId(j.getInt("catID"));
                            ct.setIcon(j.getString("Icon"));
                            ct.setCategoryName(j.getString("Name"));//Percentage
                            ct.setPercentage(j.getDouble("Percentage"));
                            if (j.getInt("isPriority") == 1)
                                ct.setPriority(true);
                            else
                                ct.setPriority(false);

                            if (!contain(ct.getCategoryName())) {
                                MyCategorySingleton.getInstance().getList().add(ct);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctx, "" + error, Toast.LENGTH_SHORT).show();
                Log.d("TESTING","POP CAT Dashboard");
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


    private void addToMyCategoy( final String catID, final int userid, final double percentage)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "NewExpenseUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // message.error(response,ctx);
                if (response.toString().equals("1")) {
                    //Toast.makeText(ctx, ""+response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING","Add to category Dashboard");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("catID", catID);
                params.put("user_ID", Integer.toString(userid));
                params.put("percentage", Double.toString(percentage));
                params.put("isPriority","0");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void onLoadCheckUser() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, methods.server() + "checkUserIncome.php?username="+username, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.trim().equals("0"))
                {
                    putCategory();
                    dialogWelcome();
                    savePreference("hasIncome","0");
                }
                else
                {
                    savePreference("hasIncome","1");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING","POP check username Dashboard");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void putCategory() {
        if (priorities.size() != 0) {
            for (Priorities pr : priorities) {
                addToMyCategoy(Integer.toString(pr.getCategoryId()), UserLogin.getInstance().getUser_ID(), pr.getPercentage());
            }
        }
        else
        {
            //Log.d()
        }
    }

    private void initializeDate() {
        AndroidNetworking.get(methods.server() + "getMinMax.php?userId="+UserLogin.getInstance().getUser_ID())
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {


                            JSONObject jsonObject = response.getJSONObject(0);
                            String res = jsonObject.getString("code");
                            if (res.equals("1"))
                            {
                                Date startDate = new Date(""+jsonObject.getString("minDate"));
                                Date endDate = new Date(""+jsonObject.getString("maxDate"));

                                ExpenseDateRangeSingleton.getInstance().setStartDate(startDate);
                                ExpenseDateRangeSingleton.getInstance().setEndDate(endDate);
                            }
                            else
                            {
                                Date startDate = new Date("10/1/2017 04:26:53");
                                Date endDate = new Date("10/1/2019 04:26:53");
                                ExpenseDateRangeSingleton.getInstance().setStartDate(startDate);
                                ExpenseDateRangeSingleton.getInstance().setEndDate(endDate);
                            }

                        } catch (JSONException e) {
                            Log.d("TESTING","POP Date Dashboard");
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        Log.d("TESTING","POP Date Dashboard");
                    }
                });
    }

    private void onheaderBind() {
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
        tv.setText("Dashboard"); // ActionBar title text
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setTypeface(typeface, typeface.ITALIC);
        actionBar.setHomeAsUpIndicator(R.drawable.icmenu);
        actionBar.setCustomView(tv);


        NavigationView navigationView = findViewById(R.id.nav_view);

        lblUserlogin = navigationView.getHeaderView(0).findViewById(R.id.lblUserlogin);
        lblUserlogin.setText(""+userInfo.getFullname());

        lblUserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,Activity_profile.class));
                finish();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    public void fragmentRedirection(Fragment ctx)
    {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ctx)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
        }
        return false;
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {

        bundle = getIntent().getExtras();
        initializeDate();
        ctx = Activity_dashboard.this;
        drawer =  findViewById(R.id.drawer_layout);
        frameLayout = findViewById(R.id.fragmentContainer);
        frameLayout.bringToFront();


        navigationView1 = findViewById(R.id.nav_view);
        navigationView2 = findViewById(R.id.nav_view2);
        navigationView1.setNavigationItemSelectedListener(this);
        navigationView2.setNavigationItemSelectedListener(this);

        navigationView1.bringToFront();
        navigationView2.bringToFront();

        username = getPreference("username");

        savePreference("DebtIsSet","false");
        loadSavings();
        }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        lblUserlogin.setText(""+userInfo.getFullname());
        int id = item.getItemId();
        if (id == 16908332)
        {
            methods.vibrate(this);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
            drawer.closeDrawer(GravityCompat.END);
            return true;
        }
        if (id == R.id.dash_settings) {
            methods.vibrate(this);
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.nav_home:
                fragmentRedirection(new fragment_main());
                break;
            case R.id.nav_setIncome:
                redirectIntent(new Activity_myincome());
                break;
            case R.id.nav_setBudgetPlan:
                redirectIntent(new Activity_my_budget_plan());
                break;
            /*case R.id.nav_setBudget:
                showDialoge();
                break;*/
            case R.id.nav_bills:
                fragmentRedirection(new fragment_bills());
                break;
            case R.id.nav_debts:
                fragmentRedirection(new fragment_debt());
                break;
            case R.id.nav_goals:
                fragmentRedirection(new fragment_goal());
                break;
            case R.id.nav_history:
                fragmentRedirection(new fragment_history());
                break;
            case R.id.nav_reports:
                fragmentRedirection(new fragment_report());
                break;
            case R.id.nav_view_type:
                showchoices();
                break;//nav_saving
            case R.id.nav_saving:
                showMySavings();
                break;//
            case R.id.nav_reset:
                showReset();
                break;//
            case R.id.nav_logout:
                methods.vibrate(this);
                logout();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        drawer.closeDrawer(GravityCompat.END);
        return false;
    }

    private void showReset() {
        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                .setTitle("Message")
                .setMessage("Note:\nAll data will be deleted advise save report.\n\nWould you like to continue reset?")
                .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       showPassword();
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

    private void showPassword() {
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialogue_reset_data);
        final EditText txtPass = dialog.findViewById(R.id.txtPassword);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String retPass = getPreference("password");
                if (txtPass.getText().toString().equals(retPass))
                {
                    message.success("success  ",ctx);
                    onReset();
                    startActivity(new Intent(ctx,Activity_dashboard.class));
                    finish();
                }
                else
                {
                    message.error("Incorrect Password! ",ctx);
                    txtPass.getText().clear();
                }


            }
        });
        dialog.create();
        dialog.show();
    }
    private void onReset() {
        AndroidNetworking.get(methods.USER_API_SERVER+"reset_data.php?userId="+getPreference("userID"))
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                    }
                    public void onError(ANError error) {
                    }
                });
    }

    private void showMySavings() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogue_my_savings);
        dialog.setCancelable(true);

        final ListView listView = dialog.findViewById(R.id.mySavings);
        savings.clear();

        AndroidNetworking.get(methods.USER_API_SERVER+"savingReport.php?username="+username)
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
                                Saving save = new Saving();
                                save.setSavingId(jObject.getInt("savingId"));
                                save.setAmount(jObject.getDouble("amount"));
                                save.setDateCreated(""+jObject.getString("date"));
                                save.setCategory(""+jObject.getString("categoryDesc"));
                                savings.add(save);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        SavingAdaptor adaptor = new SavingAdaptor(ctx,savings);
                        listView.setAdapter(adaptor);
                    }
                    public void onError(ANError error) {
                    }
                });

        dialog.create();
        dialog.show();
    }

    //Savings
    public void loadSavings()
    {

    }
    private void showDialoge() {
        dialog = new Dialog(this,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_set_budget);
        dialog.setCancelable(false);

        final EditText txt_amount = dialog.findViewById(R.id.txtamount);
        TextView lblmessage = dialog.findViewById(R.id.lblMessage);

        lblmessage.setText("How much do you want to spend?");

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        final Button btnOK = dialog.findViewById(R.id.btnOK);
        final Spinner spinner = dialog.findViewById(R.id.chooseType);
        final TextInputLayout layout = dialog.findViewById(R.id.amountLayout);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);

        ToggleButton toggleBtn = dialog.findViewById(R.id.togglebutton);
        toggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                if (on) {
                    Toast.makeText(ctx, "OFF", Toast.LENGTH_SHORT).show();
                    spinner.setEnabled(false);
                    txt_amount.setEnabled(false);
                    btnOK.setEnabled(false);

                    spinner.setSelection(0);
                    txt_amount.setText("0");
                    customMethod.savePreference(Activity_dashboard.this, username, "budgetType", "");
                    customMethod.savePreference(Activity_dashboard.this, username, "budgetAmount", "");

                } else {
                    Toast.makeText(ctx, "ON", Toast.LENGTH_SHORT).show();
                    spinner.setEnabled(true);
                    txt_amount.setEnabled(true);
                    btnOK.setEnabled(true);
                }
            }
        });

        String type = customMethod.getPreference(Activity_dashboard.this, username, "budgetType");
        if (!TextUtils.isEmpty(type))
        {
            if (type.equals("Daily"))
                spinner.setSelection(1);
            if (type.equals("Weekly"))
                spinner.setSelection(2);
            if (type.equals("Monthly"))
                spinner.setSelection(3);
            txt_amount.setText(""+customMethod.getPreference(Activity_dashboard.this, username, "budgetAmount"));
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String amt = txt_amount.getText().toString();
                if(!(TextUtils.isEmpty(amt)))
                {
                    if (Double.parseDouble(amt) >= 100)
                    {
                        if (spinner.getSelectedItemPosition() != 0) {
                            if (Double.parseDouble(amt) <= IncomeSingleton.getInstance().getAllIncome()) {

                                AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                        .setIcon(android.R.drawable.ic_dialog_info)
                                        .setTitle("Confirmation")
                                        .setMessage("Are you sure to save changes?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(ctx, "Successfuly Set Budget!", Toast.LENGTH_SHORT).show();
                                                customMethod.savePreference(Activity_dashboard.this, username, "budgetType", spinner.getSelectedItem().toString());
                                                customMethod.savePreference(Activity_dashboard.this, username, "budgetAmount", amt);
                                                dialog.dismiss();
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
                                bq.setTextColor(Color.WHITE);
                                ba.setTextColor(Color.WHITE);
                            }
                            else
                            {
                                message.warning("Amount Budget is greater than your income!",ctx);
                                txt_amount.setError("");
                            }
                        }else
                        {
                            message.error("Not Valid Budget Type!",ctx);
                        }
                    }
                    else
                    {
                        txt_amount.setError("The amount of budget is greater than  Php 100.00");
                    }
                }
                else
                    layout.setError("Not valid amount!");

            }
        });
        dialog.create();
        dialog.show();

    }

    private void showchoices() {
            final Context context = this;
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);


            final Dialog dialog = new Dialog(context,R.style.MyDialogTheme);
            dialog.setContentView(R.layout.dialogue_dash_date_picker);

            final Spinner spinner = dialog.findViewById(R.id.typeViewSpinner);
            Button btnDismiss = dialog.findViewById(R.id.btnOK);
            String viewType = ViewTypeSingleton.getInstance().getTypeview();
            if (viewType.equals(""))
                spinner.setSelection(0);
            else if (viewType.equals("Day"))
                spinner.setSelection(1);
            else if (viewType.equals("Week"))
                spinner.setSelection(2);
            else if (viewType.equals("Month"))
                spinner.setSelection(3);
            else if (viewType.equals("Year"))
                spinner.setSelection(4);
            else if (viewType.equals("All"))
                spinner.setSelection(5);

            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String choosen = spinner.getSelectedItem().toString();
                    ViewTypeSingleton.getInstance().setTypeview(choosen);
                    message.success(""+choosen,context);
                    fragmentRedirection(new fragment_main());
                }
            });
            dialog.create();
            dialog.show();
    }

    public void logout()
    {
        try
        {
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to logout?")
                    .addButton("YES", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED   , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            stopServices();
                            methods.resetInstance();
                            clearPreferences();
                            Intent intent  = new Intent(Activity_dashboard.this, Activity_login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

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
        catch (Exception ex)
        {

        }
    }

    private void clearPreferences() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void redirectIntent(Context ctx) {
        Intent intent = new Intent(Activity_dashboard.this, ctx.getClass());
        startActivity(intent);
    }
    private void dialogWelcome()
    {
        try
        {
            Dialog dialog = new Dialog(ctx);
            dialog.setContentView(R.layout.dialogue_user_welcome);
            dialog.setCancelable(false);

            Button btnOk = dialog.findViewById(R.id.btnOK);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ctx,Activity_myincome.class));
                }
            });
            dialog.create();
            dialog.show();

        }
        catch (Exception ex)
        {

        }

    }

    private void savePreference(String key,String value)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    private void loadUserInfo() {
        try
        {
            Call<UserLogin> userDetails = apIservice.getUserDetails(username);
            userDetails.enqueue(new Callback<UserLogin>() {
                @Override
                public void onResponse(Call<UserLogin> call, retrofit2.Response<UserLogin> response) {
                    Logs.LOGS(""+response);
                    if (response.isSuccessful())
                    {
                        userInfo.setLname(response.body().getLname());
                        userInfo.setFname(response.body().getFname());
                        userInfo.setMi(response.body().getMi());
                        userInfo.setEmail(response.body().getEmail());
                        userInfo.setContactNo(response.body().getContactNo());
                        userInfo.setUserId(response.body().getUser_ID());
                        userInfo.setUsername(response.body().getUsername());
                        userInfo.setPassword(response.body().getPassword());
                    }
                }

                @Override
                public void onFailure(Call<UserLogin> call, Throwable t) {
                    Logs.LOGS("Background Error "+t);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private void loadPriorities() {
        try
        {
            priorities.clear();
            AndroidNetworking.get(methods.server() + "priorities.php")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i=0; i<response.length();i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    Priorities pr = new Priorities();
                                    pr.setpId(jsonObject.getInt("pId"));
                                    pr.setCategoryId(jsonObject.getInt("categoryId"));
                                    pr.setPercentage(jsonObject.getDouble("percentage"));
                                    priorities.add(pr);
                                }

                            } catch (JSONException e) {
                                Log.d("TESTING","POP PRIORITIeS Dashboard");
                            }
                        }
                        @Override
                        public void onError(ANError error) {
                            Log.d("TESTING","POP PRIORITIeS Dashboard");
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }



}
