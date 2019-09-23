package services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.tapadoo.alerter.Alerter;
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
import java.util.Timer;
import java.util.TimerTask;

import Models.Category;
import Models.CategoryAmount;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Utils.Logs;
import Utils.customMethod;
import Utils.methods;

public class RealtimeBudgetRemCheck extends Service {
    Context ctx;
    Intent intent;
    Bundle bundle;
    String userId = "";
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented!");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                     /*   try {*/
                            //Budget Plan
                            categoryAmountList();
                            remaining();

                            if (IsEndMonth())
                            {
                                Log.d("TEST FINAL","YES");
                                for(CategoryAmount cmt: mylist)
                                {
                                    if (cmt.getAmount() > 0)
                                    {
                                        saveToSave(cmt);
                                        Log.d("TEST FINAL"," Category "+cmt.getCategoryName()+" "+cmt.getAmount());
                                    }
                                }
                            }
                            else
                            {
                                Log.d("TEST FINAL","NO");
                            }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 5000);

    }

    private void saveToSave(CategoryAmount cmt) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
        String date = df.format(cal.getTime());
       // Toast.makeText(ctx, ""+date, Toast.LENGTH_SHORT).show();
        AndroidNetworking.get(methods.USER_API_SERVER+"savings.php?todo=RemSavings&userId="+userId+"&amount="+cmt.getAmount()+"&categoryId="+cmt.getCategoryId()+"&dateCreated="+date+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jObject = response.getJSONObject(0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.LOGS("Backgound Error Income");
                        }

                    }
                    public void onError(ANError error) {
                        Logs.LOGS("Backgound Error Income");
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ctx = this;
            bundle = intent.getExtras();
            userId = getPreference("userID");
        }
        catch (Exception ex)
        {
            Log.d("ERROR ",ex.toString());
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public Boolean IsEndMonth()
    {
        Calendar calNow = Calendar.getInstance();
        Calendar calendarEnd = Calendar.getInstance();
        int lastDate = calendarEnd.getActualMaximum(Calendar.DATE);

        calendarEnd.set(Calendar.DATE, lastDate);
        calendarEnd.add(Calendar.DATE,1);

        String endMonth = methods.date.format(calendarEnd.getTime());
        String todayDate =  methods.date.format(calNow.getTime());
        //Log.e("CHECK", todayDate+"  "+endMonth);
        if (todayDate.equals(endMonth))
            return true;

        return false;
    }

    //Remaining Budget Allocation
    public void categoryAmountList()
    {
        RemainingExpenseST.resetInstance();
        categoryAmounts.clear();
        AndroidNetworking.get(methods.server()+"getCategoryAmount.php?username="+getPreference("username"))
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

    private void remaining()
    {
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                mylist.clear();
                //RemainingExpenseST.resetInstance();
                try {
                    for (int i = 0; i < MyCategorySingleton.getInstance().getList().size(); i++) {
                        Category category = MyCategorySingleton.getInstance().getList().get(i);
                        if (!check(category.getId())) {
                            CategoryAmount camount = new CategoryAmount();
                            camount.setCategoryId(category.getId());
                            camount.setCategoryName("" + category.getCategoryName());
                            // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                            camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                            camount.setAmount(methods.amount(category.getPercentage()));

                            mylist.add(camount);
                            //RemainingExpenseST.getInstance().getList().add(camount);
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
                                camount.setCategoryName("" + category.getCategoryName());

                                camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                                camount.setAmount(remainingAmount);

                                camount.setCategoryId(c.getCategoryId());

                                mylist.add(camount);
                            }
                        }

                    }
                }catch (NumberFormatException ex)
                {
                    ex.printStackTrace();
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

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

}
