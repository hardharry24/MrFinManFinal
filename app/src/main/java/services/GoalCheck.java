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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.GoalPopUp;
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

import Models.MyGoals;
import Models.Result;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.methods;
import Utils.notify;
import retrofit2.Call;
import retrofit2.Callback;

public class GoalCheck extends Service  {
    Context ctx;
    int userId = 0;
    RequestQueue requestQueue = null;
    ArrayList<MyGoals> goalList = new ArrayList<>();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet Implemented!");
    }

    @Override
    public void onCreate() {
        ctx = this;
            final Handler handler = new Handler();
            Timer timer = new Timer(false);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadGoal();
                        }


                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 3000);
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
           // loadGoal();
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
        String[] duedate = date.split("/");
        String finalDuedate = duedate[1]+"/"+duedate[0]+"/"+duedate[2];
        Log.d("TEST",finalDuedate+" "+dateNow);
        if (dateNow.equals(finalDuedate))
            return true;

        return false;
    }

    private void loadGoal()
    {
        try {
            goalList.clear();
            AndroidNetworking.get(methods.server()+"goal_list.php?userID="+userId)
                    .setTag("test")
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {

                                for (int i =0 ; i<response.length();i++)
                                {
                                    JSONObject j = response.getJSONObject(i);

                                    MyGoals goals = new MyGoals();
                                    goals.setName(""+j.getString("goal_name"));
                                    goals.setAmount(""+ j.getString("amount"));
                                    goals.setCategory(""+j.getString("category_Desc"));
                                    goals.setCategoryID(j.getInt("category_id"));
                                    goals.setId(j.getInt("goal_ID"));
                                    goals.setName(""+j.getString("goal_name"));
                                    goals.setDateCreated(""+j.getString("dateCreated"));
                                    goals.setDescription(""+j.getString("description"));
                                    goals.setTargetDate(""+j.getString("targetDate"));
                                    goals.setIcon(""+j.getString("icon"));
                                    goals.setIsNotify(Integer.parseInt(j.getString("isNotify")));

                                    goalList.add(goals);
                                    String duedate = goals.getTargetDate();
                                    if (checkDate(duedate)) {
                                        if (goals.getIsNotify() == 0)
                                        {
                                            notification(goals);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logs.LOGS("Error Populating Goals "+e);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {

                        }
                    });
        }
        catch (Exception ex)
        {
            Logs.LOGS(""+ex);
        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    public void notification(MyGoals goal)
    {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(ctx, GoalPopUp.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        intent.putExtra("userId",""+userId);
        intent.putExtra("goalName",""+goal.getName());
        intent.putExtra("amount",""+goal.getAmount());
        intent.putExtra("dueDate",""+goal.getTargetDate());
        intent.putExtra("goalId",""+goal.getId());
        intent.putExtra("description",""+goal.getDescription());
        intent.putExtra("dateCreated",""+goal.getDateCreated());
        intent.putExtra("category",""+goal.getCategory());
        intent.putExtra("categoryId",""+goal.getCategoryID());
        intent.putExtra("icon",""+goal.getIcon());

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String fullname = getPreference("lastname")+", "+getPreference("firstname");


        String msg = "Hello "+fullname+"!, You have goal today "+ methods.dateComplete.format(Calendar.getInstance().getTime())+".\nPlease click here for more details!";

        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.mipmap.goal);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(msg);
        bigText.setSummaryText("User Goal Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(msg)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bigIcon)
                .setOngoing(true)
                .setAutoCancel(true);

        int notifyId = goal.getId();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "2";
            String channelName = "Goal";
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

        updateGoalisNotify(goal);
    }

    private void updateGoalisNotify(final MyGoals goal) {
        Call<Result> updateIsnotify = apIservice.updateGoalisNotify( goal.getId());
        updateIsnotify.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if(response.isSuccessful())
                    Log.d("TESTING",response.body().getMessage());

                    //Add to history
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                    Calendar calendar = Calendar.getInstance();
                    Date date = calendar.getTime();
                    String fDate = df.format(date);

                    Map<String,String> params = new HashMap<>();
                    params.put("histname",goal.getName()+" Goal Due");
                    params.put("histDetails",goal.getName()+" due today "+methods.date.format(date.getTime())+" from category "+goal.getCategory());
                    params.put("dateCreated",fDate);
                    params.put("icon",goal.getIcon());
                    params.put("userId",""+ userId);
                    params.put("type","Goal Notify");
                    notify.addtoHistory(ctx,params);
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("TESTING",t.toString());
            }
        });
    }

}
