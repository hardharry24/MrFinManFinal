package com.teamcipher.mrfinman.mrfinmanfinal.PopUp;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import Models.Result;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.methods;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_expense;
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
import java.util.Map;

import Models.Category;
import Models.CategoryAmount;
import Models.MyGoals;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Utils.message;
import Utils.methods;
import Utils.notify;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static Utils.methods.transformation;

public class GoalPopUp extends AppCompatActivity {
    EditText lblAmt,lblName,lblDesc,lblDueDate,lblDateCreated,lblcategory;
    int userId;
    Bundle bundle;
    int categoryId;
    double amount = 0.0;
    Button btnAchieved,btnUnAchieved;
    ImageView imgIcon;
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();
    MyGoals goal = new MyGoals();
    Context ctx;
    String username = "";
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_goal);
        ctx = this;
        ButterKnife.bind(this);
        initialize();
        categoryAmountList();
        setValue();

        setHeaderTitle("Goal Reminder");
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

    private void setValue() {
        userId = Integer.parseInt(bundle.getString("userId"));
        goal.setId(Integer.parseInt(bundle.getString("goalId")));
        goal.setCategoryID(Integer.parseInt(bundle.getString("categoryId")));
        goal.setCategory(bundle.getString("category"));
        goal.setDateCreated(bundle.getString("dateCreated"));
        goal.setDescription(bundle.getString("description"));
        goal.setTargetDate(bundle.getString("dueDate"));
        goal.setAmount(bundle.getString("amount"));
        goal.setName(bundle.getString("goalName"));
        goal.setIcon(bundle.getString("icon"));

        amount = Double.parseDouble(bundle.getString("amount").replace(",",""));
        categoryId = goal.getCategoryID();

        lblName.setText(""+goal.getName());
        lblcategory.setText(""+goal.getCategory());
        lblAmt.setText("Php "+goal.getAmount());
        lblDesc.setText(""+goal.getDescription());
        lblDueDate.setText(""+goal.getTargetDate());
        lblDateCreated.setText(""+goal.getDateCreated());

        Picasso.get().load(methods.icon_server()+bundle.getString("icon")) .transform(transformation).into(imgIcon);
    }

    @Override
    public void onBackPressed() {

    }

    private void initialize() {
        bundle = getIntent().getExtras();
        lblAmt = findViewById(R.id.lblGoalAmount);
        lblName = findViewById(R.id.lblGoalName);
        lblDesc = findViewById(R.id.lblGoalDesc);
        lblDueDate = findViewById(R.id.lblGoalDueDate);
        btnAchieved = findViewById(R.id.btnAchieved);
        btnUnAchieved = findViewById(R.id.btnGoalUnachieved);
        lblDateCreated = findViewById(R.id.lblGoalDateCreated);
        lblcategory = findViewById(R.id.lblGoalCategory);
        username = getPreference("username");
        imgIcon = findViewById(R.id.GoalIcon);
    }


    @OnClick(R.id.btnAchieved)
    public void BtnAchieveOnclick(View view)
    {
        Intent intent = new Intent(this, Activity_expense_goal.class);
        intent.putExtra("goalId",""+goal.getId());
        intent.putExtra("description",goal.getName()+"\n"+goal.getDescription());
        intent.putExtra("amount",""+goal.getAmount());
        intent.putExtra("targetDate",""+goal.getTargetDate());
        intent.putExtra("categoryId",""+goal.getCategoryID());
        startActivity(intent);
        finish();

        /*if (checkRemaining())
        {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
            alertDialogBuilder.setTitle("Confirm");
            alertDialogBuilder.setMessage("Are you sure you want to continue?");
            alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String description = "Expense from your goal name "+goal.getName()+" with target date "+goal.getTargetDate();

                    //Save to Expense
                    methods.saveExpense(ctx,userId,categoryId,String.valueOf(amount),description);

                    //Add to history
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String fDate = df.format(date);

                    Map<String,String> params = new HashMap<>();
                    params.put("histname","Goal Achieved '"+goal.getName()+"'");
                    params.put("histDetails",description);
                    params.put("dateCreated",fDate);
                    params.put("icon",goal.getIcon());
                    params.put("userId",""+ userId);
                    params.put("type","Goal Achieved");
                    notify.addtoHistory(ctx,params);

                    //Update goal to achieved

                    Call<Result> updateToGoalAchieved = apIservice.updateGoalStatus(goal.getId());
                    updateToGoalAchieved.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            if (response.isSuccessful())
                            {
                                if (response.body().getCode() == 0)
                                    Logs.LOGS("Goal updateToGoalAchieved  ERROR");
                                if (response.body().getCode() == 1)
                                    Logs.LOGS("Goal updateToGoalAchieved Success");
                            }
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            Logs.LOGS("Goal updateToGoalAchieved "+t);
                        }
                    });


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
        else
        {
            message.error("Budget Remaining for "+goal.getCategory()+" is not enough!",ctx);
        }*/
    }



    @OnClick(R.id.btnGoalUnachieved)
    public void btnUnAchievedOnclick(View view)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("You are about to cancel/remove this goal!");
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                alertDialogBuilder.setTitle("Message");
                alertDialogBuilder.setMessage("Would you like to extend/update target date of this goal on next month?\n\nPress Ok to reschedule next month else Cancel to remove the goal permanently!");
                alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String[] arrdate = goal.getTargetDate().split("/");
                        Date date = new Date(arrdate[0]+"/"+arrdate[1]+"/"+arrdate[2]);

                        final Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.setTime(date);
                        calendar.add(Calendar.MONTH,1);
                        SimpleDateFormat dtFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Call<Result> updateMonth = apIservice.updateGoal(goal.getId(),"DUEDATE",dtFormat.format(calendar.getTime()));
                        updateMonth.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                if (response.isSuccessful())
                                {
                                    if (response.body().getCode() == 0) {
                                        Logs.LOGS("Goal updateToGoalAchieved  Success");

                                    }
                                    if (response.body().getCode() == 1)
                                    {
                                        //Add to history
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = calendar.getTime();
                                        String fDate = df.format(date);

                                        Map<String,String> params = new HashMap<>();
                                        params.put("histname",goal.getName()+" Goal Re-schedule Due date");
                                        params.put("histDetails","Successfuly Re-schedule next month "+methods.month.format(calendar.getTime()));
                                        params.put("dateCreated",fDate);
                                        params.put("icon",goal.getIcon());
                                        params.put("userId",""+ userId);
                                        params.put("type","Goal Reschedule");
                                        notify.addtoHistory(ctx,params);


                                        message.success("Successfuly Re-schedule next month "+methods.month.format(calendar.getTime()),ctx);
                                        startActivity(new Intent(ctx,Activity_dashboard.class));
                                        finish();
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Logs.LOGS("Goal updateToGoalAchieved ERROR " +t);
                            }
                        });

                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        Call<Result> updateMonth = apIservice.updateGoal(goal.getId(),"DELETE","Hello");
                        updateMonth.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                if (response.isSuccessful())
                                {
                                    if (response.body().getCode() == 1) {
                                        //Add to history
                                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                        Calendar calendar = Calendar.getInstance();
                                        Date date = calendar.getTime();
                                        String fDate = df.format(date);

                                        Map<String,String> params = new HashMap<>();
                                        params.put("histname",goal.getName()+" Goal Deleted");
                                        params.put("histDetails","Successfuly remove goal category "+goal.getCategory()+" name "+goal.getName());
                                        params.put("dateCreated",fDate);
                                        params.put("icon",goal.getIcon());
                                        params.put("userId",""+ userId);
                                        params.put("type","Goal Deleted");
                                        notify.addtoHistory(ctx,params);



                                        Logs.LOGS("Goal updateToGoalAchieved  Success");
                                        message.success("Successfuly Deleted!",ctx);
                                        startActivity(new Intent(ctx,Activity_dashboard.class));
                                        finish();
                                    }
                                    if (response.body().getCode() == 0)
                                        Logs.LOGS("Goal updateToGoalAchieved ERROR");
                                }
                            }
                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Logs.LOGS("Goal updateToGoalAchieved ERROR " +t);
                            }
                        });
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
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.WHITE);
    }

    private boolean checkRemaining()
    {
        for (CategoryAmount ct: remainingAmount())
        {
            Log.d("GOAL TEST ","");
            if (categoryId == ct.getCategoryId())
            {
                Log.d("GOAL TEST ","True");
                if( amount <=  ct.getAmount())
                {
                    Log.d("GOAL TEST Amt check ","True");
                    return true;
                }
                else
                    Log.d("GOAL TEST Amt check ","False");

            }
            else
            {
                Log.d("GOAL TEST ","False");
            }

        }
        return false;
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private  ArrayList<CategoryAmount> remainingAmount()
    {
        ArrayList<CategoryAmount> mylist = new ArrayList<>();

        for (int i =0; i<MyCategorySingleton.getInstance().getList().size();i++) {
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

