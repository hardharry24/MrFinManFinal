package com.teamcipher.mrfinman.mrfinmanfinal.Admin;


import android.Manifest;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import Adapters.PrioritiesAdaptor;
import Class.*;


/*import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;*/


import com.github.mikephil.charting.utils.ViewPortHandler;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_dashboard_biller;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Models.Admin.UserNumber;
import Models.Priorities;
import Models.user;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.UserToken;
import Utils.ExternalStorageUtil;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_main_admin extends Fragment {

    BarChart barChart;
    PieChart mychart;
    ArrayList<BarEntry> Bar_popCount = new ArrayList<>();
    ArrayList<String> Bar_popLabel = new ArrayList();
    ArrayList<Models.Priority> priorities = new ArrayList<>();
    ImageView dlCategory,dlUser;
    int activeUser =0,inactiveUser =0,activeBiller = 0, inactiveBiller =0;
    View view;
    Context ctx;
    ImageView dlImgCat,dlImPerc;
    TextView lblTitleCategory;
    TextView lbllegend;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Change header
        ((Activity_dashboard_admin) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Dashboard" + "</font>"));
        view = inflater.inflate(R.layout.admin_fragment_main, container, false);
        ButterKnife.bind(view);
        initialization();
        return view;
    }
    //Sabotable :)
    private void initialization() {
        try
        {
            getItemValues();
            mychart = view.findViewById(R.id.pieChartCategory);
            barChart = view.findViewById(R.id.barchartAdmin);
            dlImgCat = view.findViewById(R.id.dl_image_category);
            dlImgCat.bringToFront();

            dlImPerc = view.findViewById(R.id.dl_image_user);
            dlImPerc.bringToFront();


            ctx = getContext();
            TextView lblTitle = view.findViewById(R.id.lblTitle);
            lblTitleCategory = view.findViewById(R.id.lblTitleCategory);
            lbllegend = view.findViewById(R.id.lbllegend);

            lblTitleCategory.setText("Priorities as of "+methods.monthComplete.format(Calendar.getInstance().getTime()));
            lblTitle.setText("Users as of "+methods.monthComplete.format(Calendar.getInstance().getTime()));

            populateCategories();

            onclicks();
        }
        catch (Exception ex)
        {

        }
    }

    private void onclicks() {
        dlImgCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImageCategory();
            }
        });
        dlImPerc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImageUserChar();
            }
        });
    }

    //Download image for the user account PIE graph
    public void downloadImageCategory()
    {
        try
        {
            mychart.setEntryLabelColor(getResources().getColor(R.color.black));
            Bitmap bitmapCategory = mychart.getChartBitmap();
            try{
                if (ExternalStorageUtil.isExternalStorageMounted())
                {
                    int writeExternalStorage = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity_dashboard_admin) ctx,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    else
                    {
                        String publicDocDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                        Calendar cal = Calendar.getInstance();
                        File newFile = new File(publicDocDirPath,"Category"+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".png");
                        OutputStream out = null;

                        try
                        {
                            out = new FileOutputStream(newFile);
                            bitmapCategory.compress(Bitmap.CompressFormat.PNG,100,out);
                            out.flush();
                            out.close();
                            notifyUser(bitmapCategory);

                        }
                        catch (IOException ex)
                        {
                        }
                    }
                }
                else
                {
                    message.error("Public External is un-available!",ctx);
                }
            }catch (Exception ex)
            {
                //Toast.makeText(ctx, ""+ex.toString(), Toast.LENGTH_SHORT).show();
            }
            int colorBlack = Color.parseColor("#FFFFFF");
            mychart.setEntryLabelColor(colorBlack);
        }
        catch (Exception ex)
        {

        }
    }
    //Download image for the user account bar graph
    //
    public void downloadImageUserChar()
    {
        try
        {
            Bitmap bitmapCategory = barChart.getChartBitmap();
            try{
                if (ExternalStorageUtil.isExternalStorageMounted())
                {
                    int writeExternalStorage = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
                        ActivityCompat.requestPermissions((Activity_dashboard_admin) ctx,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    else
                    {
                        String publicDocDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                        Calendar cal = Calendar.getInstance();
                        File newFile = new File(publicDocDirPath,"Category"+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".png");
                        OutputStream out = null;

                        try
                        {
                            out = new FileOutputStream(newFile);
                            bitmapCategory.compress(Bitmap.CompressFormat.PNG,100,out);
                            out.flush();
                            out.close();
                            notifyUser(bitmapCategory);

                        }catch (IOException ex)
                        {

                        }
                    }
                }
                else
                {
                    message.error("Public External is un-available!",ctx);
                }
            }catch (Exception ex)
            {
                Toast.makeText(ctx, ""+ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {

        }
    }
    //Notification for the user
    private void notifyUser(Bitmap bitmap) {
        try
        {
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
        catch (Exception ex)
        {

        }
    }

    private void populateCategories() {
        try
        {
            priorities.clear();
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"priorities.php?todo=LIST")
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
                                    Models.Priority p = new  Models.Priority ();
                                    p.setpId(jObject.getInt("pId"));
                                    p.setCategoryDesc(""+jObject.getString("categorDesc"));
                                    p.setCategoryId(jObject.getInt("categoryId"));
                                    p.setPercentage(jObject.getDouble("percentage"));
                                    p.setIcon(""+jObject.getString("icon"));
                                    priorities.add(p);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ctx, ""+e, Toast.LENGTH_SHORT).show();
                                }

                            }

                            populatePieChart(priorities);
                        }
                        public void onError(ANError error) {
                            Logs.LOGS_ADMIN("Error POpulating Priorities");
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }

    private void populatePieChart(ArrayList<Models.Priority> p) {

        try
        {
            List<PieEntry> pieEntryList = new ArrayList<>();

            for (int i = 0; i < p.size(); i++)
            {
                Float perc = Float.parseFloat(Double.toString( p.get(i).getPercentage()));
                String lbl = p.get(i).getCategoryDesc();
                pieEntryList.add( new PieEntry(Float.parseFloat( String.format("%.0f",perc)) , lbl));

            }
            int colorBlack = Color.parseColor("#FFFFFF");
            mychart.setEntryLabelColor(colorBlack);
            PieDataSet pieDataSet = new PieDataSet(pieEntryList,"Chart");
            pieDataSet.setSliceSpace(0f);
            pieDataSet.setSelectionShift(10f);
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);


            pieDataSet.setValueFormatter(new PercentFormatter());
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            //pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

            PieData data = new PieData(pieDataSet);
            data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###")));
            data.setValueTextSize(10f);
            data.setValueTextColor(R.color.white);

            mychart.getLegend().setEnabled(false);
            mychart.setEntryLabelTextSize(15f);
            mychart.getDescription().setEnabled(false);
            mychart.setHoleColor(Color.TRANSPARENT);
            mychart.setTransparentCircleRadius(45f);
            mychart.setDrawHoleEnabled(true);
            mychart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
            mychart.animateX(5000);
            mychart.animateY(1000);
            mychart.invalidate();
            mychart.setUsePercentValues(false);

            mychart.getLegend().setEnabled(false);
            mychart.setData(data);

        }
        catch (Exception ex)
        {

        }
    }


    private void getItemValues() {
        try
        {
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"countOfUser.php")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i =0; i<response.length(); i++) {
                                    JSONObject j = response.getJSONObject(i);

                                    if (i == 0)
                                        activeUser = j.getInt("activeUser");
                                    else if (i == 1)
                                        inactiveUser = j.getInt("InactiveUser");
                                    else if (i == 2)
                                        activeBiller = j.getInt("activeBiller");
                                    else if (i == 3)
                                        inactiveBiller = j.getInt("InactiveBiller");

                                }
                                lbllegend.setText("Legend:    Active User : "+activeUser+"    Active Biller : "+activeBiller);


                                Bar_popCount.add(new BarEntry(1f, activeUser, "User"));
                                Bar_popCount.add(new BarEntry(2f, activeBiller,"Biller"));
                                barChart.setDrawBarShadow(false);
                                barChart.setDrawValueAboveBar(true);
                                barChart.getDescription().setEnabled(false);
                                barChart.setPinchZoom(false);

                                barChart.getAxisRight().setEnabled(false);
                                // barChart.getAxisLeft().setValueFormatter( );

                                barChart.setDrawGridBackground(false);
                                barChart.setFocusable(false);



                                BarDataSet bardataset = new BarDataSet(Bar_popCount, "Number of Users");
                                bardataset.setValueFormatter(new IValueFormatter() {
                                    @Override
                                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                        return entry.getData().toString();
                                    }
                                });
                                //barChart.getData().setValueTextSize(15);
                                barChart.animateY(5000);
                                BarData data = new BarData( bardataset);
                                bardataset.setColors(ColorTemplate.MATERIAL_COLORS);
                                barChart.setData(data);
                                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(Bar_popLabel));


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logs.LOGS_ADMIN("Pop Number Of user "+e);
                            }

                        }
                        public void onError(ANError error) {
                            Logs.LOGS_ADMIN("Pop Number Of user "+error);
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

}
