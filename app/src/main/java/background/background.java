package background;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
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
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Models.Category;
import Models.CategoryAmount;
import Models.User.history;
import Singleton.HistorySingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.SavingSingleton;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import retrofit2.Call;
import retrofit2.Callback;
import services.BillCheck;
import services.DebtCheck;
import services.GoalCheck;

import static Utils.methods.isOnline;

public class background extends Service {
    String username = "";
    Context ctx;
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        ctx = this;
        //username = getPreference("username");
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (isOnline()) {
                            generateIncome();
                            //remainingBaseAmount();
                            loadSavings();
                            //loadUserInfo();
                        }
                        else
                        {
                            /*AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext())
                                    .setIcon(getResources().getDrawable(R.drawable.ic_info_outline_black_24dp))
                                    .setTitle("Message")
                                    .setMessage("No Internet Connection!\n\nMr.FinMan will close!")
                                    .setCancelable(false)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            System.exit(0);
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                            Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                            Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                            bq.setTextColor(Color.BLACK);
                            ba.setTextColor(Color.BLACK);*/

                          //  Toast.makeText(ctx, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                            //stopService(new Intent(ctx, .class));
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

    }

    private void loadUserInfo() {
        Call<UserLogin> userDetails = apIservice.getUserDetails(getPreference("username"));
        userDetails.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, retrofit2.Response<UserLogin> response) {
                //Logs.LOGS(""+response);
                if (response.isSuccessful())
                {
                    UserLogin.getInstance().setUsername(""+response.body().getUsername());
                    UserLogin.getInstance().setPassword(""+response.body().getPassword());
                    UserLogin.getInstance().setLname(""+response.body().getLname());
                    UserLogin.getInstance().setFname(""+response.body().getFname());
                    UserLogin.getInstance().setMi(""+response.body().getMi());
                    UserLogin.getInstance().setEmail(""+response.body().getEmail());
                    UserLogin.getInstance().setContactNo(""+response.body().getContactNo());
                    UserLogin.getInstance().setUserId(response.body().getUserId());
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Logs.LOGS("Background Error User"+t);
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

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


    public void generateIncome()
    {

    }
    public void remainingBaseAmount()
    {
        int userId = Integer.parseInt(getPreference("userID"));
        RemainingExpenseST.resetInstance();
        AndroidNetworking.get(methods.server()+"getCategoryAmount.php?userId="+userId)
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
                                RemainingExpenseST.getInstance().add(c);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    public void onError(ANError error) {
                        Logs.LOGS("Backgound Error Remaining");
                    }
                });
    }
    private void loadSavings() {
        AndroidNetworking.get(methods.USER_API_SERVER+"getSavings.php?username="+username)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(0);
                            int code = jsonObject.getInt("code");
                            switch (code)
                            {
                                case 0:
                                    //message.error(""+jsonObject.getString("message"),ctx);
                                    break;
                                case 1:
                                    SavingSingleton.getInstance().setAllSavings(Double.parseDouble(jsonObject.getString("totalSavings")) + Double.parseDouble(jsonObject.getString("SavingsThisMonth")));
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    public void onError(ANError error) {
                        Logs.LOGS("Backgound Error Savings");
                    }
                });

    }
    private void populateHistory() {
        HistorySingleton.resetInstance();
        AndroidNetworking.get(methods.USER_API_SERVER+"history.php?username=nickie")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i=0; i<response.length();i++) {
                                JSONObject jObject = response.getJSONObject(i);
                                history hist = new history();
                                hist.setId(jObject.getInt("id"));
                                hist.setActionName(jObject.getString("name"));
                                hist.setDetails(jObject.getString("date"));
                                hist.setIcon(jObject.getString("icon"));
                                HistorySingleton.getInstance().getList().add(hist);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                    public void onError(ANError error) {
                        Log.d("Backgound","Income  ");
                    }
                });
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }



}
