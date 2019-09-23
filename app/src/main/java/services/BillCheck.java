package services;

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
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.BillPopUp;
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

import Models.MyBill;
import Models.Result;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.methods;
import Utils.notify;
import retrofit2.Call;
import retrofit2.Callback;

public class BillCheck extends Service  {
    Boolean isDone = false;
    Context ctx;
    int counter =2;
    int userId = 0;
    RequestQueue requestQueue = null;
    List<MyBill> myBills = new ArrayList<>();
    //ArrayList<MyBill> billsNotified = new ArrayList<>();
    ArrayList<String> notifyBillIds = new ArrayList<>();
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
                            loadBills();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 5000);
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

    private void loadBills()
    {
        try {
            Call<MyBill> popBill = apIservice.mybills(userId);
            popBill.enqueue(new Callback<MyBill>() {
                @Override
                public void onResponse(Call<MyBill> call, retrofit2.Response<MyBill> response) {
                    if (response.isSuccessful())
                    {
                        if (response.body().getCode() == 1)
                        {
                            myBills = response.body().getGetlistBills();

                            for(final MyBill bill: myBills)
                            {
                                if (bill.getPaymentType().equals("Onetime"))
                                {
                                    if (checkDate(bill.getDueDate()))
                                    {
                                        if (bill.getIsNotify() == 0)
                                            notification(bill);
                                    }
                                    if (checkdate3Days(bill.getDueDate()))
                                    {
                                        if (bill.getIsNotifyBefore() ==0)
                                            notification3daysBefore(bill);
                                    }
                                }
                                else if (bill.getPaymentType().equals("Monthly"))
                                {
                                    //Getting and splitting the duedate from db
                                    String dtToday = methods.day.format(Calendar.getInstance().getTime());

                                    String dtDuedate = bill.getDueDate();

                                    Log.d("TYPE",dtToday+" == "+dtDuedate+"  ");

                                   if (dtToday.equals(dtDuedate))
                                   {
                                       final Handler handler = new Handler();
                                       handler.postDelayed(new Runnable() {
                                           @Override
                                           public void run() {

                                               notification(bill);
                                           }
                                       }, 10000); //86400000
                                   }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<MyBill> call, Throwable t) {
                    Logs.LOGS("Background Pop Bills"+t);
                }
            });

                                /*if (checkDate(bill.getDueDate()))
                                    {
                                        if (bill.getIsNotify() == 0)
                                            notification(bill);
                                    }*/

        }
        catch (Exception ex)
        {
            Logs.LOGS(""+ex);
        }
    }

   /* private boolean check3DateNotified(String id) {
        for (String billId:notifyBillIds) {
            if (billId.equals(id))
                return true;
        }
        return false;
    }*/

    private Boolean checkdate3Days(String date) {
        Calendar cal3day = Calendar.getInstance();
        cal3day.add(Calendar.DATE,-3);
        String date3dayB4 = methods.date.format(cal3day.getTime());
        if (date3dayB4.equals(date))
            return true;
        return false;
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    public void notification(MyBill bill)
    {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, BillPopUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("userId",""+userId);
        intent.putExtra("billName",""+bill.getBillname());
        intent.putExtra("comName",""+bill.getComName());
        intent.putExtra("amount",""+bill.getAmount());
        intent.putExtra("dueDate",""+bill.getDueDate());
        intent.putExtra("billId",""+bill.getId());
        intent.putExtra("type",""+bill.getPaymentType());
        intent.putExtra("description",""+bill.getDesc());
        intent.putExtra("dateCreated",""+bill.getDateCreated());

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String fullname = getPreference("lastname")+", "+getPreference("firstname");



        String msg = "Hello "+fullname+"!, "+bill.getBillname()+" has Due today "+ methods.dateComplete.format(Calendar.getInstance().getTime())+"\n" +
                "Please click here for more details!";
        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.bill);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bigIcon, 100, 100, false);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("User Bill Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(msg)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(resizedBitmap)
                .setOngoing(true)
                .setAutoCancel(true);


        int notifyId = bill.getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "1";
            String channelName = "billnotification";
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
        updateIsNotifyBill(bill);
    }

    public void notification3daysBefore(MyBill bill)
    {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, Activity_login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String fullname = getPreference("lastname")+", "+getPreference("firstname");



        String msg = "Hello "+fullname+"!, 3 days from now your bill "+bill.getBillname()+" will due" +
                "\nPlease click here for more details!";
        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.bill);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                bigIcon, 100, 100, false);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("User Bill Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(msg)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(resizedBitmap);


        int notifyId = bill.getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "1";
            String channelName = "billnotification";
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
        updateIsNotifyBillBefore(bill);
    }

    private void updateIsNotifyBill(final MyBill bill) {
        Call<Result> updateBillisNotify = apIservice.updateBillisNotify( bill.getId());
        updateBillisNotify.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if(response.isSuccessful())
                    Logs.LOGS("Message updateBillisNotify "+response.body().getMessage());

                //Add to history
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                Calendar calendar = Calendar.getInstance();
                Date date = calendar.getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("histname",bill.getBillname()+" Due");
                params.put("histDetails",bill.getBillname()+" due today "+methods.date.format(date.getTime()));
                params.put("dateCreated",fDate);
                params.put("icon",bill.getIcon());
                params.put("userId",""+ userId);
                params.put("type","Bill Notify");
                notify.addtoHistory(ctx,params);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Logs.LOGS("Message Error updateBillisNotify "+t);
            }
        });
    }

    private void updateIsNotifyBillBefore(final MyBill bill) {
        Call<Result> updateBillisNotifyBefore = apIservice.updateBillisNotifyBefore( bill.getId());
        updateBillisNotifyBefore.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if(response.isSuccessful())
                    Logs.LOGS("Message updateBillisNotify "+response.body().getMessage());

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Logs.LOGS("Message Error updateBillisNotify "+t);
            }
        });
    }
}
