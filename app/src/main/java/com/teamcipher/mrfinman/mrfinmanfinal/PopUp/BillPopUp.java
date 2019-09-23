package com.teamcipher.mrfinman.mrfinmanfinal.PopUp;

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
import android.util.Log;
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
import java.util.List;
import java.util.Map;

import Models.Category;
import Models.CategoryAmount;
import Models.Debts;
import Models.MyBill;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import Utils.notify;

public class BillPopUp extends AppCompatActivity {
    Context ctx;
    EditText lblAmt,lblName,lblDesc,lblDueDate, lblDateCreated;
    int userId;
    Double amount = 0.0,amountRem = 0.0;
    Bundle bundle;
    Button btnPay,btnPayed;
    String username = "", type= "";
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();
    List<Debts> debtsList = new ArrayList<>();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_bill);

        initialize();
        categoryAmountList();
        setValue();
        setHeaderTitle("Bill Reminder");
        onclicks();
    }

    private void onclicks() {
        btnPayed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ctx, Activity_login.class));
                finish();
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("Onetime")) {
                    Toast.makeText(ctx, "ONE TIME", Toast.LENGTH_SHORT).show();
                    final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to continue?")
                            .addButton("CONTINUE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (checkRemaining()) {

                                        String description = "Expense from your bills, Name " + lblName.getText().toString() + " with target date " + lblDueDate.getText().toString();
                                        saveExpense(ctx, userId, "" + amount, description);
                                        Double remBalance = amount;
                                        //updateDebtBalance(remBalance,dbt.getId());
                                        //Add to history
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = calendar.getTime();
                                        String fDate = df.format(date);

                                        Map<String, String> params = new HashMap<>();
                                        params.put("histname", lblName.getText().toString() + " Bill ");
                                        params.put("histDetails", description);
                                        params.put("dateCreated", fDate);
                                        params.put("icon", "bills.png");
                                        params.put("userId", "" + userId);
                                        params.put("type", "Bill Payment");
                                        notify.addtoHistory(ctx, params);

                                        dialogInterface.dismiss();
                                    } else {
                                        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setTitle("Warning")
                                                .setMessage("Your'e remaining allocation for the bill is only Php " + methods.formatter.format(amountRem) + " would you like to continue this transaction?\n" +
                                                        "Note: \nOnce it will continue, the remaining balance will be paid next due date.")
                                                .addButton("CONTINUE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (checkRemaining()) {

                                                            String description = "Expense from your Bill, Name " + lblName.getText().toString() + " with target date " + lblDueDate.getText().toString();
                                                            saveExpense(ctx, userId, "" + amountRem, description);

                                                            //Add to history
                                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                                            Calendar calendar = Calendar.getInstance();
                                                            Date date = calendar.getTime();
                                                            String fDate = df.format(date);

                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("histname", lblName.getText().toString() + " Bill ");
                                                            params.put("histDetails", description);
                                                            params.put("dateCreated", fDate);
                                                            params.put("icon", "bills.png");
                                                            params.put("userId", "" + userId);
                                                            params.put("type", "Debt Payment");
                                                            notify.addtoHistory(ctx, params);
                                                        } else {

                                                        }
                                                    }
                                                })
                                                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                        builder.show();
                                    }
                                }
                            })
                            .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }
                else
                {
                    Toast.makeText(ctx, "ELSE Monthly", Toast.LENGTH_SHORT).show();
                    final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                            .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to continue?")
                            .addButton("CONTINUE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (checkRemaining()) {
                                        Toast.makeText(ctx, "Remaining true", Toast.LENGTH_SHORT).show();

                                        String description = "Expense from your bills, Name " + lblName.getText().toString() + " with target date " + lblDueDate.getText().toString();
                                        saveExpense(ctx, userId, "" + amount, description);
                                        Double remBalance = amount;
                                        //updateDebtBalance(remBalance,dbt.getId());
                                        //Add to history
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = calendar.getTime();
                                        String fDate = df.format(date);

                                        Map<String, String> params = new HashMap<>();
                                        params.put("histname", lblName.getText().toString() + " Bill ");
                                        params.put("histDetails", description);
                                        params.put("dateCreated", fDate);
                                        params.put("icon", "bills.png");
                                        params.put("userId", "" + userId);
                                        params.put("type", "Bill Payment");
                                        notify.addtoHistory(ctx, params);

                                        dialogInterface.dismiss();
                                    } else {

                                        Toast.makeText(ctx, "Remaining false", Toast.LENGTH_SHORT).show();

                                        final CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ctx)
                                                .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
                                                .setTitle("Warning")
                                                .setMessage("Your'e remaining allocation for the bill is only Php " + methods.formatter.format(amountRem) + " would you like to continue this transaction?\n" +
                                                        "Note: \nOnce it will continue, the remaining balance will be paid next due date.")
                                                .addButton("CONTINUE", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        if (checkRemaining()) {
                                                            Toast.makeText(ctx, "Remaining chu chu", Toast.LENGTH_SHORT).show();

                                                            String description = "Expense from your Bill, Name " + lblName.getText().toString() + " with target date " + lblDueDate.getText().toString();
                                                            saveExpense(ctx, userId, "" + amountRem, description);

                                                            //Add to history
                                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                                            Calendar calendar = Calendar.getInstance();
                                                            Date date = calendar.getTime();
                                                            String fDate = df.format(date);

                                                            Map<String, String> params = new HashMap<>();
                                                            params.put("histname", lblName.getText().toString() + " Bill ");
                                                            params.put("histDetails", description);
                                                            params.put("dateCreated", fDate);
                                                            params.put("icon", "bills.png");
                                                            params.put("userId", "" + userId);
                                                            params.put("type", "Debt Payment");
                                                            notify.addtoHistory(ctx, params);
                                                        } else {

                                                        }
                                                    }
                                                })
                                                .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                        builder.show();
                                    }
                                }
                            })
                            .addButton("CANCEL", -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.show();
                }

            }
        });

    }

    public static void saveExpense(final Context ctx, final int userId, final String amnt, final String note )
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_expense.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonA = new JSONArray(response);
                    JSONObject jsonObject = jsonA.getJSONObject(0);

                    if (jsonObject.getInt("code") == 0)
                    {
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
                params.put("categoryID","1");
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Activity_dashboard.class));
        finish();
    }

    private void setHeaderTitle(String str) {
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
        tv.setText(str); // ActionBar title text
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setTypeface(typeface, typeface.ITALIC);
        actionBar.setCustomView(tv);
    }

    private void setValue() {
        userId = bundle.getInt("userId");
        lblName.setText(bundle.getString("billName"));
        lblDesc.setText(bundle.getString("description"));
        lblAmt.setText("Php "+ methods.formatter.format(Double.parseDouble(bundle.getString("amount"))));
        lblDueDate.setText(methods.dateComplete.format(Calendar.getInstance().getTime()));
        lblDateCreated.setText(""+bundle.getString("dateCreated"));

        amount = Double.parseDouble(bundle.getString("amount"));
        //dateCreated
    }

    private void initialize() {
        ctx = this;
        userId = Integer.parseInt(getPreference("userID"));
        username = getPreference("username");

        bundle = getIntent().getExtras();
        lblAmt = findViewById(R.id.lblBillAmount);
        lblName = findViewById(R.id.lblBillName);
        lblDesc = findViewById(R.id.lblBillDesc);
        lblDueDate = findViewById(R.id.lblBillDueDate);
        btnPay = findViewById(R.id.btnPay);
        btnPayed = findViewById(R.id.btnAlreadyPayed);
        lblDateCreated = findViewById(R.id.lblBillDateCreated);
        type = bundle.getString("type");
    }

    private boolean checkRemaining()
    {
        for (CategoryAmount ct: remainingAmount())
        {
            Log.d("BILL TEST ","");
            if (1 == ct.getCategoryId())
            {
                Log.d("BILL TEST ","True");
                if( amount <=  ct.getAmount())
                {
                    Log.d("BILL TEST Amt check ","True");
                    return true;
                }
                else {
                    Log.d("BILL TEST Amt check ", "False");
                    amountRem = ct.getAmount();
                }

            }
            else
            {
                Log.d("BILL TEST ","False");
            }

        }
        return false;
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private ArrayList<CategoryAmount> remainingAmount()
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
