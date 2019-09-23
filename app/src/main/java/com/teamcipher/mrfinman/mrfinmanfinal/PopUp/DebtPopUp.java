package com.teamcipher.mrfinman.mrfinmanfinal.PopUp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_expense_goal;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Category;
import Models.CategoryAmount;
import Models.Debts;
import Models.MyGoals;
import Models.Result;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import Utils.notify;
import retrofit2.Call;
import retrofit2.Callback;

public class DebtPopUp extends AppCompatActivity {
    EditText lblAmt,lblName,lblDesc,lblDueDate,lblDateCreated,lblcategory,lblbalance;
    int userId;
    Bundle bundle;
    Context ctx;
    int categoryId,dbtId;
    String username = "";
    Button btnPay,btnCancel;
    Double amount = 0.0,amountRem = 0.0;
    Debts dbt = new Debts();
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();
    List<Debts> debtsList = new ArrayList<>();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_debt);

        this.setFinishOnTouchOutside(false);
       // amount = Double.parseDouble(bundle.getString("amount").replace(",",""));
        listDebt();
        initialize();
        username = getPreference("username");
        categoryAmountList();

        //setValue();
        setHeaderTitle("Debt Reminder");
        btnClicks();
    }

    private void btnClicks() {
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, Activity_expense_goal.class);
                intent.putExtra("debtId",""+dbt.getId());
                intent.putExtra("description",""+dbt.getName()+"\n"+dbt.getDescription());
                intent.putExtra("amount",""+dbt.getEquivalent());
                intent.putExtra("targetDate",""+dbt.getDueDate());
                intent.putExtra("categoryId","10");
                startActivity(intent);
                finish();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,Activity_dashboard.class));
                finish();
            }
        });
    }

    private void updateDebtBalance(double amount,int id) {
        Call<Result> updateBalance = apIservice.updateDebtBalance(id,amount);
        updateBalance.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                //Toast.makeText(ctx, "Result "+response.body().getCode(), Toast.LENGTH_SHORT).show();
                if (response.isSuccessful())
                {
                    Logs.LOGS(response.body().getCode()+" "+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("ERROR",t.toString());
            }
        });
    }

    public static void saveExpense(final Context ctx, final int userId, final int categoryId, final String amnt, final String note )
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_expense.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    // Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    JSONArray jsonA = new JSONArray(response);
                    JSONObject jsonObject = jsonA.getJSONObject(0);

                    if (jsonObject.getInt("code") == 0)
                    {
                        //message.error(jsonObject.getString("message"),ctx);
                        Logs.LOGS("Error Save Expense Method "+jsonObject.getString("message"));

                    }
                    else if (jsonObject.getInt("code") == 1)
                    {
                        message.success(jsonObject.getString("message"),ctx);
                        waitload(ctx);

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
                Map<String,String> params = new HashMap<>();
                params.put("userID",String.valueOf(userId));
                params.put("categoryID",Integer.toString(categoryId));
                params.put("amount",amnt);
                params.put("note",note);
                params.put("dateCreated",fDate);
                params.put("imgReceipt","NONE");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    public static void waitload(final Context ctx)
    {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent intent = new Intent(ctx,Activity_dashboard.class);
                ctx.startActivity(intent);
            }
        }.start();
    }

    private void setHeaderTitle(String s) {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_notifications_white);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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
        tv.setText(s); // ActionBar title text
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setTypeface(typeface, typeface.ITALIC);
        actionBar.setCustomView(tv);
    }

    public void toCenterGravity(EditText[] txts)
    {
        for(EditText t: txts)
            t.setGravity(Gravity.CENTER);
    }

    private void setValue(Debts d) {
        userId = Integer.parseInt(getPreference("userID"));
        lblbalance.setText("Php "+ methods.formatter.format(d.getBalance()));
        lblName.setText(""+d.getName());
        lblcategory.setText(""+d.getCategoryDesc());
        lblAmt.setText("Php "+d.getEquivalent());
        lblDesc.setText(""+d.getDescription());
        lblDueDate.setText(""+d.getDueDate());
        lblDateCreated.setText(""+d.getDate());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Activity_dashboard.class));
        finish();
    }

    private void initialize() {
        ctx = this;
        bundle = getIntent().getExtras();
        lblAmt = findViewById(R.id.lblDebtAmount);
        lblName = findViewById(R.id.lblDebtName);
        lblDesc = findViewById(R.id.lblDebtDesc);
        lblDueDate = findViewById(R.id.lblDebtDueDate);
        btnPay = findViewById(R.id.btnPay);
        btnCancel = findViewById(R.id.btnCancel);
        lblDateCreated = findViewById(R.id.lblDebtDateCreated);
        lblcategory = findViewById(R.id.lblDebtCategory);
        lblbalance = findViewById(R.id.lblDebtBalAmount);

        userId = Integer.parseInt( getPreference("userID"));
        dbtId = Integer.parseInt(bundle.getString("id"));

        //Toast.makeText(this, "debt ID "+dbtId, Toast.LENGTH_SHORT).show();
        dbt = getDebt(dbtId);
    }

    private String getPreference(String key) {
        SharedPreferences preferences = getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }
    public void listDebt()
    {
        int uId = Integer.parseInt(getPreference("userID"));
        Call<Debts> debtsCall = apIservice.getUserDebts(uId);
        debtsCall.enqueue(new Callback<Debts>() {
            @Override
            public void onResponse(Call<Debts> call, retrofit2.Response<Debts> response) {
                int id = response.body().getCode();
                //Toast.makeText(DebtPopUp.this, "CODE "+response.body().getCode(), Toast.LENGTH_SHORT).show();
                switch (id)
                {
                    case 0:
                        Logs.LOGS("Error Occured!");
                        break;
                    case 1:
                        debtsList = response.body().getDeptlist();
                        for(Debts d: debtsList)
                        {
                            if (d.getId() == dbtId)
                            {
                                dbt = d;
                                setValue(d);
                            }
                        }
                        Logs.LOGS("OK Debt List in Debt Pop ups");
                        break;
                }
            }

            @Override
            public void onFailure(Call<Debts> call, Throwable t) {
                Logs.LOGS("Error Occured!");
            }
        });

    }
    private Debts getDebt(int id)
    {
        for(Debts d: debtsList)
        {
            if (d.getId() == id)
                return d;
        }
        return dbt;
    }

    private  ArrayList<CategoryAmount> remainingAmount()
    {
        ArrayList<CategoryAmount> mylist = new ArrayList<>();

        for (int i = 0; i< MyCategorySingleton.getInstance().getList().size(); i++) {
            Category category = MyCategorySingleton.getInstance().getList().get(i);
            if (!check(category.getId())) {
                CategoryAmount camount = new CategoryAmount();
                camount.setCategoryId(category.getId());
                camount.setCategoryName("" + category.getCategoryName());
                // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                camount.setAmount(methods.amount(category.getPercentage()));

                mylist.add(camount);
                RemainingExpenseST.getInstance().getList().add(camount);
            }
            for (CategoryAmount c : categoryAmounts) {
                if (category.getId() == c.getCategoryId()) {
                    Double amt = c.getAmount();
                    String totalExpensePercentage = methods.percentage(amt);
                    String remainingPercentage = "" + (category.getPercentage() - Double.parseDouble(totalExpensePercentage));
                    String remPerc = remainingPercentage + "/" + c.getPercentage();

                    String amountPercSet = "" + methods.amount(category.getPercentage());
                    Double remainingAmount = Double.parseDouble(amountPercSet) - c.getAmount();

                    CategoryAmount camount = new CategoryAmount();
                    camount.setCategoryId(category.getId());
                    camount.setCategoryName("" + category.getCategoryName());

                    camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                    camount.setAmount(remainingAmount);

                    mylist.add(camount);
                    RemainingExpenseST.getInstance().getList().add(camount);
                }
            }
        }
        return mylist;
    }

    public Boolean check(int id)
    {
        for (CategoryAmount c:categoryAmounts) {
            if (id == c.getCategoryId() )
                return true;
        }
        return false;
    }

    private boolean checkRemaining()
    {
        for (CategoryAmount ct: remainingAmount())
        {
            if (10 == ct.getCategoryId())
            {
                if( amount <=  ct.getAmount())
                {
                    return true;
                }
                else
                {
                    amountRem = ct.getAmount();
                }

            }
        }
        return false;
    }


    public void categoryAmountList()
    {
        RemainingExpenseST.resetInstance();
        categoryAmounts.clear();
        AndroidNetworking.get(methods.server()+"getCategoryAmount.php?username="+username)
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

                                RemainingExpenseST.getInstance().add(c);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    public void onError(ANError error) {

                    }
                });
    }
}
