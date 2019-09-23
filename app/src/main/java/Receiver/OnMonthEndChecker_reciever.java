package Receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.teamcipher.mrfinman.mrfinmanfinal.Testing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Models.CategoryAmount;
import Singleton.RemainingExpenseST;
import Utils.methods;

public class OnMonthEndChecker_reciever extends BroadcastReceiver {
    Context ctx;
    Bundle bundle;
    @Override
    public void onReceive(Context context, Intent intent) {
        bundle = intent.getExtras();
        ctx = context;
        if (IsEndMonth())
        {
            Log.d("SET","IS END MONTH");
            for (CategoryAmount remAmount: RemainingExpenseST.getInstance().getList()) {
                Map<String,String> params = new HashMap<>();
                params.put("userId",""+bundle.getString("userId"));
                params.put("categoryId",""+remAmount.getCategoryId());
                params.put("amount",""+remAmount.getPercentage());
                params.put("remAmount",""+remAmount.getAmount());
                params.put("date",""+methods.date_db.format(Calendar.getInstance().getTime()));

                saveRemainingBudget(params);
            }
        }
        else
            Log.d("SET","NOT");
    }

    public Boolean IsEndMonth()
    {
        String todayDate = methods.date.format(Calendar.getInstance().getTime());
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        //calendar.setTime(today);

        calendar.set(2019,0,31);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        String endMonth = methods.date.format(lastDayOfMonth);

        if (todayDate.equals(endMonth))
            return true;

        return false;
    }
    private void saveRemainingBudget(final Map<String,String> params)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"onMonthEnd.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create((Activity)ctx).setText("ERROR IN CATEGORY\n"+error)
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

}
