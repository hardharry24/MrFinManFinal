package services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.BillPopUp;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.DebtPopUp;
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
import java.util.Timer;
import java.util.TimerTask;

import Adapters.Debt_adapter;
import Models.Debts;
import Models.Result;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.methods;
import Utils.notify;
import retrofit2.Call;
import retrofit2.Callback;

public class DebtCheck extends Service  {
    Boolean isDone = false;
    Context ctx;
    int counter =2;
    int userId = 0;
    RequestQueue requestQueue = null;
    List<Debts> debts = new ArrayList<>();
    ArrayList<Debts> debtsNotified = new ArrayList<>();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented!");
    }

    @Override
    public void onCreate() {

            final Handler handler = new Handler();
            Timer timer = new Timer(false);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            populate();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 5000);
        }

    private void populate() {
        try
        {
            debts.clear();
            Call<Debts> getDebts = apIservice.getUserDebts(userId);
            getDebts.enqueue(new Callback<Debts>() {
                @Override
                public void onResponse(Call<Debts> call, retrofit2.Response<Debts> response) {
                    //Log.d("LOGS",""+response);
                    if (response.body().getCode() == 1)
                    {
                        debts = response.body().getDeptlist();

                        for (Debts dbt: debts)
                        {
                            String type = dbt.getPeriod();
                            if (type.equals("Month"))
                            {
                                if (checkDate(dbt.getDueDate()))
                                {
                                    if (dbt.getIsNotify() == 0)
                                        notification(dbt);
                                }
                                /*else if (checkdueDateBefore(dbt.getDueDate()))
                                {
                                    if (dbt.getIsNotifyBefore() ==0)
                                        notification3Before(dbt);
                                }*/
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Debts> call, Throwable t) {

                }
            });


        }
        catch (OutOfMemoryError ex)
        {
            Logs.LOGS(""+ex);
        }
    }

    private boolean checkdueDateBefore(String date) {
        String[] dt = date.split("/");
        String dteToday = methods.day.format(Calendar.getInstance().getTime());
        if (dt[0].toLowerCase().equals(dteToday.toLowerCase()))
            return true;

        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ctx = this;
            userId = Integer.parseInt(getPreference("userID"));
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

    private Boolean checkDate(String date)
    {
        Date dt = Calendar.getInstance().getTime();
        String dateNow = methods.date.format(dt);
        if (dateNow.equals(date))
            return true;

        return false;
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    public void notification(Debts debts)
    {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, DebtPopUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("userId",""+userId);
        intent.putExtra("id",""+debts.getId());

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String fullname = getPreference("lastname")+", "+getPreference("firstname");

        String msg = "Hello "+fullname+"!, "+debts.getName()+" has Due today "+ methods.dateComplete.format(Calendar.getInstance().getTime())+".\nPlease click here for more details!";
        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.debt);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bigIcon, 100, 100, false);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("User Debt Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(msg)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(resizedBitmap)
                .setOngoing(true)
                .setAutoCancel(true);
        int notifyId = debts.getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "3";
            String channelName = "debtNotification";
            NotificationChannel channel = new NotificationChannel(channelId1,channelName,NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.enableVibration(true);

            builder.setChannelId(channelId1);
            if (notificationManager != null)
            {
                notificationManager.createNotificationChannel(channel);
            }
        }else
        {
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        }
        if (notificationManager != null)
        {
            notificationManager.notify(notifyId,builder.build());
        }

        updateIsNotifyDebt(debts);
    }

    public void notification3Before(Debts debts)
    {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, Activity_login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String fullname = getPreference("lastname")+", "+getPreference("firstname");


        String msg = "Hello "+fullname+"!, "+debts.getName()+" has Due today "+ methods.dateComplete.format(Calendar.getInstance().getTime())+".\nPlease click here for more details!";
        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.debt);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bigIcon, 100, 100, false);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("User Debt Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(msg)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(resizedBitmap);
        int notifyId = debts.getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "3";
            String channelName = "debtNotification";
            NotificationChannel channel = new NotificationChannel(channelId1,channelName,NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.enableVibration(true);

            builder.setChannelId(channelId1);
            if (notificationManager != null)
            {
                notificationManager.createNotificationChannel(channel);
            }
        }else
        {
            builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        }
        if (notificationManager != null)
        {
            notificationManager.notify(notifyId,builder.build());
        }

        updateIsNotifyDebtBefore(debts);
    }

    private void updateIsNotifyDebt(final Debts debts) {
        Call<Result> updateDebtisNotify = apIservice.updateDebtisNotify( debts.getId());
        updateDebtisNotify.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if(response.isSuccessful())
                    Logs.LOGS("Message updateDebtisNotify "+response.body().getMessage());

                    //Add to history
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String fDate = df.format(date);

                    Map<String,String> params = new HashMap<>();
                    params.put("histname",debts.getName()+" Debt Due date");
                    params.put("histDetails",debts.getName()+" due today "+methods.date.format(date.getTime())+" from category "+debts.getCategoryDesc());
                    params.put("dateCreated",fDate);
                    params.put("icon",debts.getIcon());
                    params.put("userId",""+ userId);
                    params.put("type","Debt Notify");
                    notify.addtoHistory(ctx,params);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Logs.LOGS("Message Error updateBillisNotify "+t);
            }
        });
    }

    private void updateIsNotifyDebtBefore(final Debts debts) {
        Call<Result> updateDebtisNotifyBefore = apIservice.updateDebtisNotifyBefore( debts.getId());
        updateDebtisNotifyBefore.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if(response.isSuccessful())
                    Logs.LOGS("Message updateDebtisNotify "+response.body().getMessage());


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Logs.LOGS("Message Error updateBillisNotify "+t);
            }
        });



       /* if (checkDate(duedate)) {
            if (b.getIsNotify() == 0)
                notification(b);
        }
        else
        {
            if (b.getIsNotifyBefore() == 0)
                notification3Before(b);
        }*/
    }
}
