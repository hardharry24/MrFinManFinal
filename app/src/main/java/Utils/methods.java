package Utils;



import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import com.github.mikephil.charting.charts.Chart;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Transformation;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_add_category;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_expense;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_income;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.utils.SpotlightListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HttpsURLConnection;

import Adapters.CategoryGridviewAdapter;
import Models.Category;
import Models.User.Saving;
import Singleton.BillerSingleton;
import Singleton.BorrowTotalSingleton;
import Singleton.CategoryTotalSingleton;
import Singleton.ChoosenDateST;
import Singleton.ExpenseDateRangeSingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.SavingSingleton;
import Singleton.TransactionST;
import Singleton.UserLogin;
import Singleton.UserToken;
import Singleton.ViewTypeSingleton;

public class methods
{
    public static Context ctx;

    public static String IP = "192.168.88.1";
    private static String BASE = "http://"+IP+":81/";
    public static String BASE_URL = BASE+"FinMan/";
    public static String result ="";
    public static String USER_API_SERVER = BASE+"FinMan/API_User/";
    public static String BILLER_API_SERVER = BASE+"FinMan/API_biller/";
    public static String PUSHNOTIF_API_SERVER = BASE+"FinMan/PushNotif/";
    public static String ADMIN_API_SERVER = BASE+"FinMan/API_admin/";
    public static String SMS_API_SERVER = BASE+"FinMan/API_sms/";
    public static String RECEIPT_SERVER = BASE+"FinMan/API/upload/receipt/";
    public static String server()
    {
        return BASE+"FinMan/API/";
    }
    public static String delete_server()
    {
        return BASE+"FinMan/Delete_API/";
    }
    public static String icon_server()
    {
        return BASE+"FinMan/icons/";
    }
	

    public static DecimalFormat formatter = new DecimalFormat("#,###.00");
    public static DecimalFormat formatter00 = new DecimalFormat("0.#");
    public static SimpleDateFormat dtComplete = new SimpleDateFormat("yyyy-M-d h:mm:s");
    public static SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat day = new SimpleDateFormat("dd");
    public static SimpleDateFormat dateYYYYMMDD = new SimpleDateFormat("yyyy/MM/dd");
    public static SimpleDateFormat dateComplete = new SimpleDateFormat("dd MMMM, yyyy");
    public static SimpleDateFormat datedayMonths = new SimpleDateFormat("dd MMMM, yyyy");
    public static SimpleDateFormat date_dMMM_yyyy = new SimpleDateFormat("d MMM yyyy");
    public static SimpleDateFormat MM_yyyy = new SimpleDateFormat("MM/yyyy");
    public static SimpleDateFormat date_dMMM = new SimpleDateFormat("d MMM");
    public static SimpleDateFormat date_db = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat month = new SimpleDateFormat("MMMM");
    public static SimpleDateFormat monthDisplay = new SimpleDateFormat("MMM, yyyy");
    public static SimpleDateFormat monthComplete = new SimpleDateFormat("MMMM, yyyy");
    public static SimpleDateFormat year = new SimpleDateFormat("yyyy");
    public static SimpleDateFormat time = new SimpleDateFormat("h:mm a");


    public static Transformation transformation = new RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(2)
            .cornerRadiusDp(5)
            .oval(false)
            .build();
    //d MMM yyyy


    public static void snackbar(String message,View view)
    {
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public static void showMessage(Activity ctx, String title, String body, int icon, int color)
    {
        Alerter.create(ctx).setTitle(title).setBackgroundColorRes(color).setIcon(icon).setText(body).show();
    }

    public static String StrRequest(final Activity act, String url)
    {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    result = response.toString();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Alerter.create(act).setText("NO INTERNET CONNECTION!\n"+error)
                            .setIcon(R.drawable.ic_info_outline_black_24dp)
                            .show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(act);
            requestQueue.add(stringRequest);

        return result;
    }
    public static String StrRequest(final Activity act, String url,Map<String,String> params)
    {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                result = response.toString();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create(act.getParent()).setText("NO INTERNET CONNECTION!\n"+error)
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(act);
        requestQueue.add(stringRequest);

        return result;
    }

    public static String MessageDialog(Activity act,String title, String msg)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                result = "OK";
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                result = "CANCEL";
            }
        });
        AlertDialog dialog = builder.create();
        return result;
    }
    public static String remove(String pattern,String str)
    {
        String result= "";
        for(int i=0;i<str.length();i++)
        {

            if (Character.toString( str.charAt(i)) != pattern)
            {
                result +=""+str.charAt(i);
            }
            else
                break;
        }
        return result;
    }

    public static void notification()
    {
        //NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(ge,)
    }

    public static void resetInstance()
    {
        CategoryTotalSingleton.resetInstance();
        ChoosenDateST.resetInstance();
        UserLogin.resetInstance();
        MyCategorySingleton.resetInstance();
        CategoryTotalSingleton.resetInstance();
        IncomeSingleton.resetInstance();
        ViewTypeSingleton.resetInstance();
        ExpenseDateRangeSingleton.resetInstance();
        RemainingExpenseST.resetInstance();
        ViewTypeSingleton.resetInstance();
        TransactionST.resetInstance();
        ChoosenDateST.resetInstance();
        UserToken.resetInstance();
        SavingSingleton.resetInstance();
        BillerSingleton.resetInstance();
        BorrowTotalSingleton.resetInstance();

    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static String percentage(double item)
    {
        double total = IncomeSingleton.getInstance().getAllIncome();
        Double perc = 0.0;
        perc = ( item / total ) * 100;
        String value = ""+formatter00.format(perc);
        return  value;
    }

    public static double amount(double item)
    {
        double total = IncomeSingleton.getInstance().getAllIncome();
        Double val = 0.0;
        val = (item / 100) * total;
        return val;
    }
  /*  public static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();

        String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encodedImage;
    }*/

    public static String getStringImage(Bitmap image)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeImageStr(String base63)
    {
        byte[] imageAsBytes = Base64.decode(base63.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }

    public static void vibrate(Context context)
    {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
    }
    public static int getCode()
    {
        return  ThreadLocalRandom.current().nextInt(1000,9999);
    }

    public static void sendCode(String phone_number,String msg,final Context context) {
        final String message = "";

        AndroidNetworking.get(methods.SMS_API_SERVER+"send.php?pnumber="+phone_number+"&message="+message)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Utils.message.success(""+response,context);
                    }
                    @Override
                    public void onError(ANError error) {
                        Logs.LOGS("Send Code Error "+error);
                    }
                });
    }


    public static Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 "+IP+"");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void saveExpense(final Context ctx,final int userId,final int categoryId, final String amnt, final String note )
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_expense.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    // Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    JSONArray jsonA = new JSONArray(response);
                    JSONObject jsonObject = jsonA.getJSONObject(0);

                    if (jsonObject.getInt("code") == 0)
                    {
                        //message.error(jsonObject.getString("message"),ctx);
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
                params.put("categoryID",Integer.toString(categoryId));
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
    public static void hideKeyBoard(Context ctx)
    {
        //InputMethodManager inputManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void spot(Context ctx,View target, int color,String header,String text)
    {
    }

    public static int monthsBetweenDates(Date startDate, Date endDate){

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);

        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        int monthsBetween = 0;
        int dateDiff = end.get(Calendar.DAY_OF_MONTH)-start.get(Calendar.DAY_OF_MONTH);

        if(dateDiff<0) {
            int borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH);
            dateDiff = (end.get(Calendar.DAY_OF_MONTH)+borrrow)-start.get(Calendar.DAY_OF_MONTH);
            monthsBetween--;

            if(dateDiff>0) {
                monthsBetween++;
            }
        }
        else {
            monthsBetween++;
        }
        monthsBetween += end.get(Calendar.MONTH)-start.get(Calendar.MONTH);
        monthsBetween  += (end.get(Calendar.YEAR)-start.get(Calendar.YEAR))*12;
        return monthsBetween;
    }

    public static ArrayList<String> getDays()
    {
        ArrayList<String> list = new ArrayList<>();
        for(int i = 1; i< 31 ; i++ )
            list.add(String.valueOf(i));
        return list;
    }

    public static int getNumberMonth()
    {
        Calendar calendarEnd = Calendar.getInstance();
        int lastDate = calendarEnd.getActualMaximum(Calendar.DATE);

        return lastDate;

    }




}