package Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.Button;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.BillPopUp;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Map;

import Singleton.BorrowTotalSingleton;
import Singleton.UserLogin;

public class notify {
    static boolean isconfirm =false;
    public static void notification(Context context,String summaryText,String message )
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(context, null);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap bigIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(message);
        bigText.setSummaryText("User Bill Reminder");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText(message)
                .setStyle(bigText)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(bigIcon)
                .setAutoCancel(true);
        int notifyId = 001;

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
    public static void addtoHistory(final Context ctx, Map<String,String> params)
    {

        AndroidNetworking.get(methods.server()+"history.php?histname="+params.get("histname")+"&histDetails="+params.get("histDetails")+"&dateCreated="+params.get("dateCreated")+"&icon="+params.get("icon")+"&userId="+params.get("userId")+"&type="+params.get("type")+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Utils.message.success("\n\n"+pn+" "+response,ctx);
                        //Toast.makeText(ctx, ""+response, Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Logs.LOGS("Add History "+error);
                    }
                });
    }

    public static Boolean alert(Context ctx,String header,String body)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(""+header)
                .setMessage(""+body)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isconfirm = true;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isconfirm = false;
                    }
                })
                .show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.WHITE);
        ba.setTextColor(Color.WHITE);
        return isconfirm;
    }
}
