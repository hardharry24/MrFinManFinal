package com.teamcipher.mrfinman.mrfinmanfinal;


import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.util.IOUtils;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nightonke.boommenu.BoomMenuButton;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.MyGoalAdapter;
import Adapters.RemAdaptor;
import Adapters.SavingAdaptor;
import Models.Category;
import Models.CategoryAmount;
import Models.MyGoals;
import Models.User.Saving;
import Models.user;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.UserLogin;
import Utils.ExternalStorageUtil;
import Utils.message;
import Utils.methods;
import Class.*;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static Utils.methods.ctx;
import static Utils.methods.dateComplete;
import static Utils.methods.getDateDiff;


public class fragment_report extends Fragment {
    View view;
    Dialog dialog;
    RelativeLayout relativeLayout;
    int userId = 0;
    Button btnGenerate;
    Context ctx;
    ListView listViewRemBp,listViewSavings;
    String username = "";
    ProgressDialog pr;
    PieChart chartBudgetPlan;
    TextView lblTotalIncome,lblAdditionalheader,lblAdditionalAmount;
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();
    ArrayList<Saving> savings = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_report, container, false);
        initialization(view);
        onheader("Reports");
        onclick();

        //Budget Plan
        categoryAmountList();
        remaining();

        loadSavings();
        loadingReportData();

        return view;
    }

    private void onloadDetails() {
        //Budget Plan
        RemAdaptor Readaptor = new RemAdaptor(ctx,mylist);
        listViewRemBp.setAdapter(Readaptor);

        //Income
        lblTotalIncome.setText("Php "+methods.formatter.format(IncomeSingleton.getInstance().getAllIncome()));

        //Budget Plan
        setGraph();

    }

    public void loadingReportData()
    {
        pr = new ProgressDialog(ctx);
        pr.setTitle("Message");
        pr.setMessage("Please wait\nLoading data......");
        pr.show();
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                pr.hide();
                onloadDetails();
            }
        }.start();
    }


    private void onclick() {
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogue();
            }

            private void showDialogue() {
                dialog = new Dialog(ctx);
                dialog.setContentView(R.layout.dialog_user_generate_report);
                final EditText txtFilename = dialog.findViewById(R.id.txtFilename);
                Button btnSave = dialog.findViewById(R.id.btnSave);

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!(txtFilename.getText().toString().equals("")))
                        {
                            String filename = txtFilename.getText().toString().replace(" ","");
                            generateReport(filename);
                            dialog.dismiss();
                        }
                        else
                        {
                            txtFilename.setError("Filename must not be empty!");
                        }

                    }
                });

                dialog.create();
                dialog.show();
            }
        });
    }

    private void generateReport(String filename) {
        try{
            if (ExternalStorageUtil.isExternalStorageMounted())
            {
                int writeExternalStorage = ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (writeExternalStorage != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions((Activity_dashboard) ctx,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                else
                {
                    String publicDocDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
                    Calendar cal = Calendar.getInstance();
                    String name = filename+""+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".xls";
                    File newFile = new File(publicDocDirPath,name);


                    try {
                        WritableWorkbook workbook = Workbook.createWorkbook(newFile);



                        WritableSheet sheet = workbook.createSheet("Remaining Allocation",0);

                        String[] headerA = {"CATEGORY","BUDGET","BALANCE"};


                        for(int i=0; i<headerA.length;i++)
                        {
                            //WritableFont TableFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE)
                            WritableFont wf = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
                            CellFormat cf = new WritableCellFormat(wf);
                            Label label = new Label(i,0,headerA[i],cf);
                            sheet.addCell(label);

                            CellView cell = sheet.getColumnView(i);
                            cell.setAutosize(true);
                            sheet.setColumnView(i, cell);

                            WritableCell c = sheet.getWritableCell(i,0);

                            WritableCellFormat newFormat = new WritableCellFormat();
                            newFormat.setFont(wf);
                            newFormat.setBackground(Colour.GREEN);


                            c.setCellFormat(newFormat);
                        }

                        for(int j=0;j<mylist.size();j++)
                        {
                            CategoryAmount a = mylist.get(j);
                            String[] info = {""+a.getCategoryName(),"Php "+a.getRemPercentage(),"Php "+methods.formatter.format(a.getAmount())};

                            for (int i=0; i<headerA.length; i++)
                            {
                                Label label = new Label(i,j+1,info[i]);
                                sheet.addCell(label);
                                CellView cell = sheet.getColumnView(i);
                                cell.setAutosize(true);
                                sheet.setColumnView(i, cell);
                            }

                        }

                        //Monthly Income
                        WritableSheet sheet2 = workbook.createSheet("Monthly Income",1);
                        String[] headerIncome = {"Amount"};
                        for(int i=0; i<headerIncome.length;i++)
                        {
                            //WritableFont TableFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE)
                            WritableFont wf = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
                            CellFormat cf = new WritableCellFormat(wf);
                            Label label = new Label(i,0,headerIncome[i],cf);
                            sheet2.addCell(label);

                            CellView cell = sheet2.getColumnView(i);
                            cell.setAutosize(true);
                            sheet2.setColumnView(i, cell);

                            WritableCell c = sheet2.getWritableCell(i,0);

                            WritableCellFormat newFormat = new WritableCellFormat();
                            newFormat.setFont(wf);
                            newFormat.setBackground(Colour.GREEN);


                            c.setCellFormat(newFormat);
                        }

                        Label label = new Label(0,0,"Php "+methods.formatter.format(IncomeSingleton.getInstance().getAllIncome()));
                        sheet2.addCell(label);
                        CellView cell = sheet2.getColumnView(0);
                        cell.setAutosize(true);
                        sheet2.setColumnView(0, cell);


                        //My Budget Plan
                        WritableSheet sheet3 = workbook.createSheet("My Budget Plan",2);
                        Bitmap bplan = chartBudgetPlan.getChartBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bplan.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        sheet3.addImage(new WritableImage(0,0,8,20,byteArray));

                        //Savings
                        WritableSheet sheet4 = workbook.createSheet("Savings",3);

                        String[] headerS = {"ID","CATEGORY","DATE","AMOUNT"};

                        for(int i=0; i<headerS.length;i++)
                        {
                            //WritableFont TableFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE)
                            WritableFont wf = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
                            CellFormat cf = new WritableCellFormat(wf);
                            Label label1 = new Label(i,0,headerS[i],cf);
                            sheet4.addCell(label1);

                            CellView cell1 = sheet4.getColumnView(i);
                            cell1.setAutosize(true);
                            sheet4.setColumnView(i, cell1);

                            WritableCell c = sheet4.getWritableCell(i,0);

                            WritableCellFormat newFormat = new WritableCellFormat();
                            newFormat.setFont(wf);
                            newFormat.setBackground(Colour.GREEN);
                            c.setCellFormat(newFormat);
                        }

                        for(int j=0;j<savings.size();j++)
                        {
                            Saving a = savings.get(j);
                            String[] info = {""+(j+1),""+a.getCategory(),""+a.getDateCreated(),"Php "+methods.formatter.format(a.getAmount())};

                            for (int i=0; i<info.length; i++)
                            {
                                Label label1 = new Label(i,j+1,info[i]);
                                sheet4.addCell(label1);
                                CellView cell1 = sheet4.getColumnView(i);
                                cell1.setAutosize(true);
                                sheet4.setColumnView(i, cell1);
                            }
                        }

                        workbook.write();
                        workbook.close();

                        Log.e("FILE","SUCCESS "+publicDocDirPath);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("FILE","FAIL "+e);

                    } catch (WriteException e) {
                        e.printStackTrace();
                        Log.e("FILE","FAIL "+e);
                    }
                    notifyUser();
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

    private void notifyUser() {
        NotificationManager notificationManager = (NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap bigIcon = BitmapFactory.decodeResource(ctx.getResources(),R.drawable.ic_launcher);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Your'e file is ready!\nClick here for details");
        bigText.setSummaryText("Generate Report");

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

    private void initialization(View view) {
        ctx = getContext();
        btnGenerate = view.findViewById(R.id.btnGenerateReport);
        username = getPreference("username");
        userId = Integer.parseInt(getPreference("userID"));
        listViewRemBp = view.findViewById(R.id.report_listview_remaining_b_a);
        listViewSavings = view.findViewById(R.id.report_listview_saving);
        lblTotalIncome = view.findViewById(R.id.report_lblMonthlyIncomeAmount);
        chartBudgetPlan = view.findViewById(R.id.report_pieBudgetPlan);

    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void onheader(String title) {

        ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((Activity_dashboard)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
        ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((Activity_dashboard)getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);

        ActionBar actionBar = ((Activity_dashboard)getActivity()).getSupportActionBar();
        TextView tv = new TextView(getContext());
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.dancingfont);
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

    //Remaining Budget Allocation
    public void categoryAmountList()
    {
        RemainingExpenseST.resetInstance();
        categoryAmounts.clear();
        AndroidNetworking.get(methods.server()+"getCategoryAmount.php?username="+username)
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
                                categoryAmounts.add(c);

                                RemainingExpenseST.getInstance().add(c);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    public void onError(ANError error) {

                    }
                });
    }

    private void remaining()
    {
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                mylist.clear();
                //RemainingExpenseST.resetInstance();
                try {
                    for (int i = 0; i < MyCategorySingleton.getInstance().getList().size(); i++) {
                        Category category = MyCategorySingleton.getInstance().getList().get(i);
                        if (!check(category.getId())) {
                            CategoryAmount camount = new CategoryAmount();
                            camount.setCategoryId(category.getId());
                            camount.setCategoryName("" + category.getCategoryName());
                            // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                            camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                            camount.setAmount(methods.amount(category.getPercentage()));

                            mylist.add(camount);
                            //RemainingExpenseST.getInstance().getList().add(camount);
                        }
                        for (CategoryAmount c : categoryAmounts) {
                            if (category.getId() == c.getCategoryId()) {
                                Double amt = c.getAmount();
                                String totalExpensePercentage = methods.percentage(amt);
                                String remainingPercentage = "" + (category.getPercentage() - Double.parseDouble(totalExpensePercentage));
                                String remPerc = remainingPercentage + "/" + c.getPercentage();

                                String amountPercSet = "" + methods.amount(category.getPercentage());
                                Double remainingAmount = Double.parseDouble(amountPercSet) - c.getAmount();


                                CategoryAmount camount = new CategoryAmount();
                                camount.setCategoryName("" + category.getCategoryName());

                                camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                                camount.setAmount(remainingAmount);

                                camount.setCategoryId(c.getCategoryId());

                                mylist.add(camount);
                            }
                        }

                    }
                }catch (NumberFormatException ex)
                {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    public Boolean check(int id)
    {
        for (CategoryAmount c:RemainingExpenseST.getInstance().getList()) {
            if (id == c.getCategoryId() )
                return true;
        }
        return false;
    }

    //********End

    //Budget Plan Graph
    private void setGraph() {

        List<PieEntry> pieEntryList = new ArrayList<>();

        for (int i = 0; i < MyCategorySingleton.getInstance().getList().size(); i++)
        {
            Float perc = Float.parseFloat(Double.toString(MyCategorySingleton.getInstance().getList().get(i).getPercentage()));
            String lbl = MyCategorySingleton.getInstance().getList().get(i).getCategoryName();
            pieEntryList.add( new PieEntry(Float.parseFloat( String.format("%.0f",perc)) , lbl));

        }
        int colorBlack = getResources().getColor(R.color.black);
        chartBudgetPlan.setEntryLabelColor(colorBlack);
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
        data.setValueTextColor(R.color.black);

        chartBudgetPlan.getLegend().setEnabled(false);
        chartBudgetPlan.setEntryLabelTextSize(15f);
        chartBudgetPlan.getDescription().setEnabled(false);
        chartBudgetPlan.setHoleColor(Color.WHITE);
        chartBudgetPlan.setTransparentCircleRadius(45f);
        chartBudgetPlan.setDrawHoleEnabled(true);
        chartBudgetPlan.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
        chartBudgetPlan.animateX(5000);
        chartBudgetPlan.animateY(1000);
        chartBudgetPlan.invalidate();
        chartBudgetPlan.setUsePercentValues(false);

        chartBudgetPlan.getLegend().setEnabled(false);
        chartBudgetPlan.setData(data);
        chartBudgetPlan.setCenterText("100%");
        chartBudgetPlan.setCenterTextColor(getResources().getColor(R.color.green));
        chartBudgetPlan.setCenterTextSize(15f);

    }

    //********End chart plan

    //Savings
    public void loadSavings()
    {
        savings.clear();
        AndroidNetworking.get(methods.USER_API_SERVER+"savingReport.php?username="+username)
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
                                Saving save = new Saving();
                                save.setSavingId(jObject.getInt("savingId"));
                                save.setAmount(jObject.getDouble("amount"));
                                save.setDateCreated(""+jObject.getString("date"));
                                save.setCategory(""+jObject.getString("categoryDesc"));
                                savings.add(save);
                            }
                            catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        SavingAdaptor adaptor = new SavingAdaptor(ctx,savings);
                        listViewSavings.setAdapter(adaptor);

                    }
                    public void onError(ANError error) {
                        //message.error(""+error,Testing.this);
                    }
                });
    }



}



