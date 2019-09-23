package com.teamcipher.mrfinman.mrfinmanfinal;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Adapters.Transactions;
import Models.Category;
import Models.CategoryAmount;
import Models.Transaction;
import Singleton.ExpenseDateRangeSingleton;
import Singleton.RemainingExpenseST;
import Singleton.TransactionST;
import Singleton.UserLogin;
import Singleton.ViewTypeSingleton;
import Utils.ExternalStorageUtil;
import Utils.message;
import Utils.methods;

public class Activity_transactions extends AppCompatActivity implements View.OnClickListener {
    Context ctx;
    SwipeMenuListView listView;
    UserLogin user = UserLogin.getInstance();
    Transactions adapter;
    int userId = 0;
    String category;
    ArrayList<Transaction> transactions = new ArrayList<>();
    Bundle bundle;
    String type="",dateChoosen="";
    RelativeLayout relativeLayout;
    String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        initialization();

        onheaderBind(category+" Transactions");
        onloadTransactions();
        swipeMenu();
    }

    private void swipeMenu() {
        try
        {
            SwipeMenuCreator creator = new SwipeMenuCreator() {

                @Override
                public void create(SwipeMenu menu) {
                    SwipeMenuItem editItem = new SwipeMenuItem(ctx);
                    editItem.setWidth(90);
                    editItem.setIcon(android.R.drawable.ic_menu_edit);
                    menu.addMenuItem(editItem);

                    SwipeMenuItem deleteItem = new SwipeMenuItem(ctx);
                    deleteItem.setWidth(90);
                    deleteItem.setIcon(android.R.drawable.ic_delete);
                    menu.addMenuItem(deleteItem);
                }
            };
            listView.setMenuCreator(creator);
            listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
            listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                    Transaction tr = (Transaction)adapter.getItem(position);
                    if (today(tr)) {
                        switch (index) {
                            case 0:
                                TransactionST.getInstance().setView(true);

                                if (tr.getType().toUpperCase().equals("EXPENSE")) {
                                    String[] arrdate = tr.getDate().split(" ");
                                    String d = arrdate[0].replace('-', '/');
                                    Date date = new Date(d);
                                    Date time = new Date(d + " " + arrdate[1]);

                                    Intent intent = new Intent(ctx, Activity_expense.class);
                                    intent.putExtra("EDIT","EDIT");
                                    intent.putExtra("amount", "" + tr.getAmount());
                                    intent.putExtra("Id", "" + tr.getId());
                                    intent.putExtra("date", "" + methods.dateYYYYMMDD.format(date));
                                    intent.putExtra("note", "" + tr.getNote());
                                    intent.putExtra("imgReceipt", "" + tr.getImage());
                                    startActivity(intent);
                                } else {
                                    if (tr.getName().equals("Salary")) {
                                        startActivity(new Intent(ctx, Activity_myincome.class));

                                    } else {
                                        startActivity(new Intent(ctx, Activity_income.class));
                                    }
                                }
                                break;
                            case 1:
                                ondelete(tr);
                                break;
                        }
                    }else
                    {
                        message.warning("Not allowed to manage on past transaction!",ctx);
                    }
                    return false;
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    public void downloadImageReciept(String pathImg)
    {
        //Bitmap bitmapCategory = getBitmap();
        String URL = methods.RECEIPT_SERVER+pathImg;

        try{
            if (ExternalStorageUtil.isExternalStorageMounted())
            {
                int writeExternalStorage = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions((Activity_transactions) ctx,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                else
                {
                    String publicDocDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                    Calendar cal = Calendar.getInstance();
                    ///File newFile = new File(publicDocDirPath,"IMG_transac"+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".png");
                    OutputStream out = null;
                    AndroidNetworking.download(URL, publicDocDirPath, "IMG_transac"+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".png")
                            .build()
                            .startDownload(new DownloadListener() {
                                @Override
                                public void onDownloadComplete() {

                                    Toast.makeText(ctx, "DownLoad Complete", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(ANError anError) {

                                }
                            });
                    Bitmap bm = BitmapFactory.decodeFile(URL);
                    notifyUser(bm);
                }
            }
            else
            {
                message.error("Public External is un-available!",ctx);
            }
        }catch (Exception ex)
        {
            Log.e("ERROR",""+ex);
        }
    }
    private void notifyUser(Bitmap bitmap) {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap bigIcon = bitmap;//BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_launcher);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Your Image is Ready!\nClick here for details");
        bigText.setSummaryText("Image");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
                .setContentIntent(pendingIntent)
                .setContentTitle("Mr.FinMan")
                .setContentText("Success!\nClick here for details")
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
    private boolean today(Transaction tr) {
        Calendar calendar = Calendar.getInstance();
        String todaySDt = methods.date.format(calendar.getTime());

        String[] arrdate = tr.getDate().split(" ");
        String d = arrdate[0].replace('-', '/');
        Date date = new Date(d);
        Date dtComp = new Date(d + " " + arrdate[1]);
        Calendar calendarTr = Calendar.getInstance();
        calendarTr.clear();
        calendarTr.setTime(dtComp);
        String trDate = methods.date.format(calendarTr.getTime());

        if (trDate.equals(todaySDt))
            return true;

        return false;
    }

    private void ondelete(final Transaction tr) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Message");
            alertDialogBuilder.setMessage("Are you sure you want to delete this transaction?");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    delete(tr,"DELETE");
                    undo(tr);

                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            alertDialogBuilder.show();
    }

    private void delete(Transaction tr,String type) {
        try
        {
            if (tr.getType().toUpperCase().equals("EXPENSE"))
            {
                AndroidNetworking.get(methods.server()+"expenseDelete_Undo.php?type="+type+"&Id="+tr.getId()+"&tbl=Expense")
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
                                        switch (jObject.getInt("code"))
                                        {
                                            case 0:
                                                ///message.error(""+jObject.getString("message"),ctx);
                                                break;
                                            case 1:
                                                message.success(""+jObject.getString("message"),ctx);
                                                onloadTransactions();
                                                break;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            public void onError(ANError error) {
                                //message.error(""+error,ctx);
                            }
                        });
            }else
            {

            }

        }
        catch (Exception ex)
        {

        }
    }

    private void onloadTransactions() {
            populate_with_category(dateChoosen);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    private void onheaderBind(String title) {
        try
        {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_view_list);
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
            tv.setText(title); // ActionBar title text
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(25);
            tv.setTypeface(typeface, typeface.ITALIC);
            actionBar.setCustomView(tv);
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            startActivity(new Intent(ctx,Activity_dashboard.class));
            finish();
        }
        return false;
    }

    private void populate_with_category(final String date) {
        try
        {
            transactions.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"transaction_list_specific.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i =0 ; i<jsonArray.length();i++)
                        {
                            JSONObject jobject = jsonArray.getJSONObject(i);
                            Transaction tr = new Transaction();
                            tr.setId(jobject.getInt("Id"));
                            tr.setName(""+jobject.getString("Name"));
                            tr.setAmount(Double.parseDouble(jobject.getString("amount")));
                            tr.setIcon(""+jobject.getString("icon"));
                            tr.setType(""+jobject.getString("type"));
                            tr.setDate(""+jobject.getString("dateCreated"));//
                            tr.setNote(""+jobject.getString("note"));
                            tr.setImage(""+jobject.getString("imgReciept"));

                            transactions.add(tr);
                        }

                        adapter = new Transactions(Activity_transactions.this,transactions);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("username",username);
                    params.put("catname",""+category);
                    params.put("date",date);
                    params.put("type",type);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Transaction tr = (Transaction)adapterView.getItemAtPosition(i);
                    showDialogue(tr);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    public void showDialogue(final Transaction trans)
    {
        try
        {
            final Dialog dialog = new Dialog(Activity_transactions.this,R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_show_transaction);

            TextView lblname = dialog.findViewById(R.id.tr_dialogue_name);
            TextView lblamount = dialog.findViewById(R.id.tr_dialogue_amount);
            TextView lbldesc = dialog.findViewById(R.id.tr_dialogue_desc);
            TextView lblDate = dialog.findViewById(R.id.tr_dialogue_date);
            TextView lbltime = dialog.findViewById(R.id.tr_dialogue_time);
            TextView lbltype = dialog.findViewById(R.id.tr_dialogue_type);
            ImageView imgIcon = dialog.findViewById(R.id.tr_dialogue_img);

            Button btnclose = dialog.findViewById(R.id.tr_dialogue_close);


            ImageView ImgView = dialog.findViewById(R.id.tr_dialogue_viewRecept);
            final ImageView imgDl = dialog.findViewById(R.id.tr_dialogue_DlRecept);

            if (trans.getImage().toString() != "no_image.jpg")
            {
                imgDl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        downloadImageReciept(trans.getImage());
                    }
                });
            }
            else
            {
                imgDl.setVisibility(View.GONE);
            }


            String[] arrdate = trans.getDate().split(" ");
            String d = arrdate[0].replace('-','/');
            Date date = new Date(d);
            Date time = new Date(d+" "+arrdate[1]);

            lblname.setText(""+trans.getName());
            lblamount.setText("Php "+methods.formatter.format(trans.getAmount()));
            lblDate.setText(""+methods.dateComplete.format(date));
            lbltime.setText(""+methods.time.format(time));
            lbldesc.setText(""+trans.getNote());//


            String imgRec = trans.getImage();
            if (trans.getType().toUpperCase().equals("INCOME"))
            {
                lbltype.setText(""+trans.getType());
                lbltype.setTextColor(getResources().getColor(R.color.green));
            }
            else
            {
                lbltype.setText(""+trans.getType());
                Picasso.get().load(methods.RECEIPT_SERVER+imgRec) .into(ImgView);
                lbltype.setTextColor(getResources().getColor(R.color.red));
            }

            Picasso.get().load(methods.icon_server()+trans.getIcon()).transform(methods.transformation).into(imgIcon);
            ImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(Activity_transactions.this );
                    dialog.setContentView(R.layout.dialogue_show_image);
                    ImageView imageView = dialog.findViewById(R.id.show_image_file);
                    String imgRec = trans.getImage();


                    Transformation transformation = new RoundedTransformationBuilder()
                            .borderColor(Color.BLACK)
                            .borderWidthDp(3)
                            .cornerRadiusDp(30)
                            .oval(false)
                            .build();

                    if (trans.getType().toUpperCase().equals("EXPENSE"))
                    {
                        Picasso.get().load(methods.RECEIPT_SERVER+imgRec) .transform(transformation).into(imageView);
                        dialog.show();
                    }
                    else
                    {
                        message.info("No receipt in income transactions!",ctx);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
                    }
                }
            });
            btnclose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private void initialization() {
        try
        {
            username = getPreference("username");
            relativeLayout = findViewById(R.id.rootLayout);
            ctx = Activity_transactions.this;
            bundle = getIntent().getExtras();
            category = bundle.getString("category");
            dateChoosen = bundle.getString("date");
            type = bundle.getString("type");

            type = ViewTypeSingleton.getInstance().getTypeview();
            bundle = getIntent().getExtras();
            listView = findViewById(R.id.listview_transaction_list);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Transaction tr = transactions.get(i);

                    Intent intent = new Intent(Activity_transactions.this,DialogTransactionDetails.class);
                    intent.putExtra("Name",tr.getName());
                    intent.putExtra("amount",""+methods.formatter.format(tr.getAmount()));
                    intent.putExtra("icon",tr.getIcon());
                    intent.putExtra("note",tr.getNote());
                    intent.putExtra("type",tr.getType());
                    intent.putExtra("timeCreated",tr.getTime());
                    intent.putExtra("dateCreated",tr.getDate());
                    startActivity(intent);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {

        }
    }

    private void undo(final Transaction tr)
    {
        final Snackbar snackbar = Snackbar.make(relativeLayout,"Successfuly Deleted!",Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(tr,"UNDO");
                snackbar.dismiss();

            }
        });

    }
}
