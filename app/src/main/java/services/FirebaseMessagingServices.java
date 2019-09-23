
package services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.MainActivity;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.notify;

public class FirebaseMessagingServices extends FirebaseMessagingService {
    Context ctx;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        ctx = this;

        int billId = Integer.parseInt(remoteMessage.getData().get("billId").toString());
        showNotification(remoteMessage.getData().get("message"),billId);

    }

    private void showNotification(String message, int notifId) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        String fDate = df.format(date);

        Map<String,String> params = new HashMap<>();
        params.put("histname","Charge Bill ");
        params.put("histDetails",""+message);
        params.put("dateCreated",fDate);
        params.put("icon","bills.png");
        params.put("userId",""+ getPreference("userID"));
        params.put("type","Goal Notify");
        notify.addtoHistory(ctx,params);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //To be change to redirect
        Intent intent = null;
        intent = new Intent(this, Activity_dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("TODO","ToBill");
        intent.putExtra("billId",""+notifId);


        PendingIntent pendingIntent = PendingIntent.getActivity(this,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //pendingIntent.s

        Bitmap bigIcon = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_launcher);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setSummaryText("Charge Bill Notification");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(message)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bigIcon)
                .setAutoCancel(true);

        int notifyId = notifId;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId1 = "1";
            String channelName = "channel1";
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

    }

    private String getPreference(String key) {
        SharedPreferences preferences = this.getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }
}

