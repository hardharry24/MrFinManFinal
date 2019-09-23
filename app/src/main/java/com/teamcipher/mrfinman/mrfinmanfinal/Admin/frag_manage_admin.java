package com.teamcipher.mrfinman.mrfinmanfinal.Admin;


import android.Manifest;
import android.app.Activity;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
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
import com.androidnetworking.model.Progress;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.R;
import com.teamcipher.mrfinman.mrfinmanfinal.Testing;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Adapters.UserListAdaptor;
import Models.Admin.UserNumber;
import Models.Debts;
import Models.MyGoals;
import Models.user;
import Utils.ExternalStorageUtil;
import Utils.message;
import Utils.methods;
import Utils.customMethod;
import es.dmoral.toasty.Toasty;
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
public class frag_manage_admin extends Fragment {
    ArrayList<user> userArrayList = new ArrayList<>();
    ArrayList<user> userDeletedArrayList = new ArrayList<>();
    ListView listView;
    Context ctx;
    View view;
    EditText lblfname, lbllname, lblmi, lblcontact, lblemail, lblusername,lblpassword;
    Button btnClose;
    Spinner spinnerRole;
    UserListAdaptor adaptor;
    user globalUser;
    Spinner spinnerFilter;
    EditText searchView;
    TextView lblFilter;
    Dialog dialog;
    int i;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<user> userArrayList_search = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Change Heder
        ((Activity_dashboard_admin) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Manage User Account" + "</font>"));
        view = inflater.inflate(R.layout.admin_fragment_manage, container, false);

        initialization();
        populate();
        listViewClick();
        onclicks();
        searchTxt();
        setHasOptionsMenu(true);



        return view;
    }
    private void initialization() {
        try
        {
            ctx = getContext();
            listView = view.findViewById(R.id.listViewUsers);
            registerForContextMenu(listView);
            lblFilter = view.findViewById(R.id.lblfilter);
            searchView = view.findViewById(R.id.txtSearch);
            lblFilter.setText("All");
        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.admin_setting_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Setting icon (Elipsis icon) items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.admin_setting_generate:
                waitDonwLoad("Active User");
                break;
            case R.id.admin_setting_new_user:
                NewDetailsUser();
                break;
            case R.id.admin_setting_deleted_user:
                showDialogueDeleted();
                break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    //Display or dialogue of the delted item
    private void showDialogueDeleted() {
        try
        {
            dialog = new Dialog(ctx,R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_admin_deleted_user);
            dialog.setCancelable(true);

            ListView listView = dialog.findViewById(R.id.listView_deleted_acc);
            ImageView btnGenerateReport = dialog.findViewById(R.id.btnGenerateRep);

            populate(listView);
            btnGenerateReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userDeletedArrayList.size() != 0)
                        waitDonwLoad("DELETED USER");
                    else
                    {
                        Toast.makeText(ctx, "No User to Generate!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            dialog.create();
            dialog.show();

        }
        catch (Exception ex)
        {

        }
    }
    //Populated Deleted User
    public void populateDeletedUser(final ListView listView)
    {
        try
        {
            userDeletedArrayList.clear();
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"userList.php?todo=DELETED")
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
                                    user u = new user();
                                    u.setUserId(jObject.getInt("userId"));
                                    u.setLname(""+jObject.getString("lname"));
                                    u.setFname(""+jObject.getString("fname"));
                                    u.setMi(""+jObject.getString("mi"));
                                    u.setUsername(""+jObject.getString("username"));
                                    u.setContactNo(""+jObject.getString("contactNo"));
                                    u.setEmail(""+jObject.getString("email"));
                                    if(jObject.getInt("isActive") == 1)
                                        u.setActive(true);
                                    else
                                        u.setActive(false);

                                    if(jObject.getInt("isLock") == 0)
                                        u.setLock(false);
                                    else
                                        u.setLock(true);

                                    u.setRoleId(jObject.getInt("roleId"));
                                    u.setId(i+1);


                                    userDeletedArrayList.add(u);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                   // Toast.makeText(ctx, ""+e, Toast.LENGTH_SHORT).show();
                                }

                            }
                            if (userDeletedArrayList.size() ==0)
                                Toast.makeText(ctx, "No User Deleted!", Toast.LENGTH_SHORT).show();


                            adaptor = new UserListAdaptor(ctx,userDeletedArrayList);
                            listView.setAdapter(adaptor);
                        }
                        public void onError(ANError error) {
                            //message.error(""+error,ctx);
                        }
                    });

        }
        catch (Exception ex)
        {

        }
    }

    private void populate(final ListView mylistView) {
        try
        {
            populateDeletedUser(mylistView);

            mylistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    final user u = (user)adapterView.getItemAtPosition(i);
                    AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Confirmation")
                            .setMessage("Restore?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onRestoreUser();
                                }

                                private void onRestoreUser() {
                                    AndroidNetworking.get(methods.ADMIN_API_SERVER+"userList.php?todo=RESTORE&userId="+u.getUserId())
                                            .setTag("test")
                                            .setPriority(Priority.LOW)
                                            .build()
                                            .getAsJSONArray(new JSONArrayRequestListener() {
                                                @Override
                                                public void onResponse(JSONArray response) {
                                                    try {
                                                        JSONObject jObj = response.getJSONObject(0);
                                                        int code = jObj.getInt("code");
                                                        String msg = jObj.getString("message");
                                                        if (code == 1) {
                                                            message.success(msg, ctx);
                                                            populateDeletedUser(mylistView);
                                                        }
                                                        else if (code == 0)
                                                            message.error(msg,ctx);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                public void onError(ANError error) {
                                                   // message.error(""+error,ctx);
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    bq.setTextColor(Color.BLACK);
                    ba.setTextColor(Color.BLACK);
                    return true;
                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    private void waitDonwLoad(String sheetName) {
            final ProgressDialog dialog = new ProgressDialog(ctx);
            dialog.setTitle("Generating Report");
            dialog.setMessage("Please wait...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            generateReportUser(sheetName);

            final Handler handler = new Handler();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(i=0;i<=100;i++)
                    {
                        try
                        {
                            Thread.sleep(100);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                dialog.setProgress(i);
                            }
                        });
                        if (i == 100)
                        {
                            //Intent intent = new Intent(ctx, )
                            dialog.dismiss();
                            //Toast.makeText(ctx, "Successfuly save to donwloads!", Toast.LENGTH_SHORT).show();
                            notifyUser();
                            //startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        }

                    }
                }
            }).start();
    }

    private void generateReportUser(String sheetName) {

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
                    File newFile = new File(publicDocDirPath,"GenReport_FinMan_User_"+methods.date.format(cal.getTime()).toString().replace("/","")+cal.getTime().getSeconds()+".xls");


                    try {
                        WritableWorkbook workbook = Workbook.createWorkbook(newFile);
                        WritableSheet sheet = workbook.createSheet(sheetName,0);

                        String[] header = {"USER ID","LASTNAME","FIRSTNAME","MI","EMAIL","CONTACT NUMBER","USERNAME","ROLE ID"};


                        for(int i=0; i<header.length;i++)
                        {
                            //WritableFont TableFormat = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE)
                            WritableFont wf = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD,false, UnderlineStyle.NO_UNDERLINE, Colour.WHITE);
                            CellFormat cf = new WritableCellFormat(wf);
                            Label label = new Label(i,0,header[i],cf);
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

                        for(int j=0;j<userArrayList_search.size();j++)
                        {
                            user u = userArrayList_search.get(j);
                            String[] info = {""+u.getUserId(),""+u.getLname(),""+u.getFname(),""+u.getMi(),""+u.getEmail(),""+u.getContactNo(),""+u.getUsername(),""+u.getRoleId()};

                            for (int i=0; i<header.length; i++)
                            {
                                Label label = new Label(i,j+1,info[i]);
                                sheet.addCell(label);
                                CellView cell = sheet.getColumnView(i);
                                cell.setAutosize(true);
                                sheet.setColumnView(i, cell);
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
                    //notifyUser();
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
        bigText.bigText("Success!\nClick here for details");
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

    private void DetailsUser(user u) {
        dialog = new Dialog(ctx,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_admin_user_details);
        dialog.setCancelable(true);

        EditText lblfname,lbllname,lblmi,lblcontact,lblemail,lblusername;
        Button btnClose;
        lbllname = dialog.findViewById(R.id.lbl_user_Lname);
        lblfname = dialog.findViewById(R.id.lbl_user_Fname);
        lblmi = dialog.findViewById(R.id.lbl_user_MIname);
        lblcontact = dialog.findViewById(R.id.lbl_user_contactNo);
        lblemail = dialog.findViewById(R.id.lbl_user_email);
        lblusername = dialog.findViewById(R.id.lbl_user_username);
        btnClose = dialog.findViewById(R.id.btnClose);

        lbllname.setText(""+u.getLname());
        lblfname.setText(""+u.getFname());


        lblmi.setText(""+u.getMi());
        lblemail.setText(""+u.getEmail());
        lblusername.setText(""+u.getUsername());
        lblcontact.setText(""+u.getContactNo());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.create();
        dialog.show();
    }
    public void loadListviewSearh(ArrayList<user> userlist)
    {
        adaptor = new UserListAdaptor(ctx,userlist);
        listView.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
    }

    private void searchTxt() {
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                try
                {
                    if (searchView.getText().toString().length() == 0)
                    {
                        populate();
                    }
                    else
                    {
                        filter(charSequence.toString());
                    }

                }catch (Exception ex)
                {
                    //Toast.makeText(ctx, "Message \n"+ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void onclicks() {
        lblFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilter();
            }
        });
    }

    private void filter(String str)
    {
        String text = str.toLowerCase();
        ArrayList<user> userListResult = new ArrayList<>();

        for (user u : userArrayList_search)
        {
            if (u.getFname().toLowerCase().toLowerCase().contains(text) || u.getLname().toLowerCase().contains(text) || u.getEmail().toLowerCase().contains(text) || u.getUsername().toLowerCase().contains(text)
                    && u.getRoleId() == customMethod.getRole(lblFilter.getText().toString()))
            {   
                userListResult.add(u);
            }
        }
        adaptor = new UserListAdaptor(ctx,userListResult);
        listView.setAdapter(adaptor);
    }

    private void showFilter()
    {
        final Dialog dialog = new Dialog(ctx,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_show_filter_admin_manage);
        spinnerFilter = dialog.findViewById(R.id.spinner_filter_by);

        dialog.setCancelable(true);
        Button btnOk = dialog.findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spinnerFilter.getSelectedItemId() != 0)
                {
                    lblFilter.setText(""+spinnerFilter.getSelectedItem());
                    filter(spinnerFilter.getSelectedItemPosition());
                    dialog.dismiss();
                }
                else
                {
                    adaptor = new UserListAdaptor(ctx,userArrayList);
                    listView.setAdapter(adaptor);
                    dialog.dismiss();
                }
            }
        });
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLUE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        dialog.create();
        dialog.show();
    }

    private void filter(int selectedItem) {
        userArrayList_search.clear();
        if (lblFilter.getText().toString().equals("All")){
            userArrayList_search = userArrayList;
        }
        else {
            for (user u : userArrayList) {
                if (u.getRoleId() == selectedItem)
                    userArrayList_search.add(u);
            }
        }
        adaptor = new UserListAdaptor(ctx,userArrayList_search);
        listView.setAdapter(adaptor);
    }

    private void listViewClick() {
        try
        {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    user u = (user)adapterView.getItemAtPosition(i);
                    DetailsUser(u);
                }
            });
        }
        catch (Exception ex)
        {

        }

    }

    private void populate() {
        try
        {
            userArrayList.clear();
            userArrayList_search.clear();
            AndroidNetworking.get(methods.server()+"user.php")
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
                                    user u = new user();
                                    u.setUserId(jObject.getInt("userId"));
                                    u.setLname(""+jObject.getString("lname"));
                                    u.setFname(""+jObject.getString("fname"));
                                    u.setMi(""+jObject.getString("mi"));
                                    u.setUsername(""+jObject.getString("username"));
                                    u.setContactNo(""+jObject.getString("contactNo"));
                                    u.setEmail(""+jObject.getString("email"));
                                    if(jObject.getInt("isActive") == 1)
                                        u.setActive(true);
                                    else
                                        u.setActive(false);

                                    if(jObject.getInt("isLock") == 0)
                                        u.setLock(false);
                                    else
                                        u.setLock(true);

                                    u.setRoleId(jObject.getInt("roleId"));
                                    u.setId(i+1);


                                    if (u.getActive() == true){
                                        userArrayList.add(u);
                                        userArrayList_search.add(u);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(ctx, ""+e, Toast.LENGTH_SHORT).show();
                                }

                            }

                            adaptor = new UserListAdaptor(ctx,userArrayList);
                            listView.setAdapter(adaptor);
                        }
                        public void onError(ANError error) {
                            message.error(""+error,ctx);
                        }
                    });

        }
        catch (Exception ex)
        {

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listViewUsers) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu_admin, menu);
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        user u = (user)adaptor.getItem(info.position);
        globalUser = u;

        switch(item.getItemId()) {

            case R.id.lock:
               //dialogue(goals.getName(),goals.getAmount(),goals.getIcon(),goals.getDescription(),goals.getTargetDate());
                ConfirmationLock(u);
                return true;
            case R.id.edit:
                showDetailsUser(globalUser);
                populate();
                return true;
            case R.id.delete:
                delete(u);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void ConfirmationLock(final user u)
    {
        try
        {
            String status = "";
            String todo="";
            if (u.getLock() == true) {
                status = "De-activate";
                todo = "Activate";
            }
            else
            {
                status = "Active";
                todo = "De-activate";
            }
            String msg = u.getLname()+", "+u.getFname()+" is currently "+status+"\nAre you sure to "+status+"";
            AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Confirmation")
                    .setMessage(u.getLname()+", "+u.getFname()+" is currently "+status+" confirm "+todo+"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if (u.getLock() == true)
                                lockUnlock(u,"UNLOCK");
                            else
                                lockUnlock(u,"LOCK");

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq.setTextColor(Color.BLACK);
            ba.setTextColor(Color.BLACK);
        }
        catch (Exception ex)
        {

        }
    }

    private void lockUnlock(user u,String todo) {
        try
        {
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"user_lock_unlock.php?userId="+u.getUserId()+"&todo="+todo+"")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {

                                JSONObject jsonObject = response.getJSONObject(0);
                                int code = jsonObject.getInt("code");
                                switch (code)
                                {
                                    case 0:
                                        message.error(""+jsonObject.getString("message"),ctx);
                                        break;
                                    case 1:
                                        message.success(""+jsonObject.getString("message"),ctx);
                                        populate();
                                        break;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        public void onError(ANError error) {
                            message.error(""+error,ctx);
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }

    private void delete(final user u, final String type)
    {
        try
        {
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"userUndoDelete.php?userId="+u.getUserId()+"&type="+type+"")
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
                                            message.error(""+jObject.getString("message"),ctx);
                                            break;
                                        case 1:
                                            message.success(""+jObject.getString("message"),ctx);
                                            populate();
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
        }
        catch (Exception ex)
        {

        }
    }

    public void delete(final user u)
    {
        try
        {
            final AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete user "+u.getLname()+", "+u.getFname()+"?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            delete(u,"DELETE");
                            undo(u);


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            bq.setTextColor(Color.WHITE);
            ba.setTextColor(Color.WHITE);
        }
        catch (Exception ex)
        {

        }
    }

    private void undo(final user u)
    {
        RelativeLayout rel = view.findViewById(R.id.relative);

        final Snackbar snackbar = Snackbar.make(rel,"Successfuly Deleted!",Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(u,"UNDO");
                snackbar.dismiss();

            }
        });

    }

    private void showDetailsUser(final user u) {
        try
        {
            final Dialog dialog;
            dialog = new Dialog(ctx,R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_admin_user_update);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);


            lbllname = dialog.findViewById(R.id.lbl_user_Lname);
            lblfname = dialog.findViewById(R.id.lbl_user_Fname);
            lblmi = dialog.findViewById(R.id.lbl_user_MIname);
            lblcontact = dialog.findViewById(R.id.lbl_user_contactNo);
            lblemail = dialog.findViewById(R.id.lbl_user_email);
            lblusername = dialog.findViewById(R.id.lbl_user_username);
            btnClose = dialog.findViewById(R.id.btnClose);
            spinnerRole = dialog.findViewById(R.id.spinnerRole);

            lbllname.setText(""+u.getLname());
            lblfname.setText(""+u.getFname());


            lblmi.setText(""+u.getMi());
            lblemail.setText(""+u.getEmail());
            lblusername.setText(""+u.getUsername());

            if (u.getRoleId() == 1)
                spinnerRole.setSelection(1);
            else if (u.getRoleId() == 2)
                spinnerRole.setSelection(2);
            else if (u.getRoleId() == 3)
                spinnerRole.setSelection(3);
            else
                spinnerRole.setSelection(0);


            btnClose.setText("Update");
            lblcontact.setText(""+u.getContactNo());
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to save changes?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Map<String,String> params = new HashMap<>();
                                    params.put("userId",""+u.getUserId());
                                    params.put("fname",lblfname.getText().toString().toUpperCase());
                                    params.put("lname",lbllname.getText().toString().toUpperCase());
                                    params.put("mi",lblmi.getText().toString().toUpperCase());
                                    params.put("username",lblusername.getText().toString());
                                    params.put("email",lblemail.getText().toString());
                                    params.put("contact",lblcontact.getText().toString());

                                    if (spinnerRole.getSelectedItemId() == 1)
                                        params.put("roleId","1");
                                    if (spinnerRole.getSelectedItemId() == 2)
                                        params.put("roleId","2");
                                    if (spinnerRole.getSelectedItemId() == 3)
                                        params.put("roleId","3");
                                    else
                                        params.put("roleId","1");

                                    onupdate(params);

                                    dialog.dismiss();
                                    populate();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    bq.setTextColor(Color.WHITE);
                    ba.setTextColor(Color.WHITE);
                }
            });

            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    private void NewDetailsUser() {
        try
        {
            final Dialog dialog;
            dialog = new Dialog(ctx,R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_admin_user_new);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            lbllname = dialog.findViewById(R.id.lbl_user_Lname);
            lblfname = dialog.findViewById(R.id.lbl_user_Fname);
            lblmi = dialog.findViewById(R.id.lbl_user_MIname);
            lblcontact = dialog.findViewById(R.id.lbl_user_contactNo);
            lblemail = dialog.findViewById(R.id.lbl_user_email);
            lblusername = dialog.findViewById(R.id.lbl_user_username);
            btnClose = dialog.findViewById(R.id.btnClose);
            spinnerRole = dialog.findViewById(R.id.spinnerRole);
            lblpassword = dialog.findViewById(R.id.lbl_user_password);




            btnClose.setText("SAVE");
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to save new user?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Map<String,String> params = new HashMap<>();
                                    params.put("fname",lblfname.getText().toString().toUpperCase());
                                    params.put("lname",lbllname.getText().toString().toUpperCase());
                                    params.put("mi",lblmi.getText().toString().toUpperCase());
                                    params.put("username",lblusername.getText().toString());
                                    params.put("email",lblemail.getText().toString());
                                    params.put("contact",lblcontact.getText().toString());
                                    params.put("password",lblpassword.getText().toString());

                                    if (spinnerRole.getSelectedItemId() == 1)
                                        params.put("roleId","1");
                                    if (spinnerRole.getSelectedItemId() == 2)
                                        params.put("roleId","2");
                                    if (spinnerRole.getSelectedItemId() == 3)
                                        params.put("roleId","3");
                                    else
                                        params.put("roleId","1");


                                    if (validateUserInput())
                                    {
                                        onsave(params);
                                        populate();
                                        dialog.dismiss();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .show();
                    Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                    bq.setTextColor(Color.WHITE);
                    ba.setTextColor(Color.WHITE);
                }
            });

            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    private void onupdate(final Map<String,String> params )
    {
        try
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.ADMIN_API_SERVER + "updateUser.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("message");

                        switch (code)
                        {
                            case 1:
                                message.success(msg,ctx);
                                populate();
                                break;
                            case 2:
                                message.success(msg,ctx);
                                showDetailsUser(globalUser);

                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    message.error(error.toString(),ctx);
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
        catch (Exception ex)
        {

        }
    }
    public void fragmentRedirection(Fragment ctx)
    {
        (getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin, ctx)
                .addToBackStack(null)
                .commit();
    }
    private void onsave(final Map<String,String> params )
    {
        try
        {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.ADMIN_API_SERVER + "insertUser.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("message");

                        switch (code)
                        {
                            case 1:
                                message.success(msg,ctx);
                                populate();
                                fragmentRedirection(new frag_manage_admin());
                                break;
                            case 2:
                                message.success(msg,ctx);
                                // showDetailsUser(globalUser);
                                break;
                        }

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
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }

    private Boolean validateUserInput()
    {
        if (lbllname.getText().toString().equals(""))
        {
            lbllname.setError("!");
            return false;
        }
        else if (lblfname.getText().toString().equals(""))
        {
            lbllname.setError("!");
            return false;
        }
        else if (lblcontact.getText().toString().equals(""))
        {
            lbllname.setError("!");
            return false;
        }
        else if (lblemail.getText().toString().equals(""))
        {
            lblemail.setError("!");
            return false;
        }
        else if (!(lblemail.getText().toString().contains("@") || lblemail.getText().toString().contains(".com")))
        {
            lblemail.setError("!");
            return false;
        }
        else if (lblusername.getText().toString().equals(""))
        {
            lblusername.setError("!");
            return false;
        }
        else if (lblpassword.getText().toString().equals(""))
        {
            lblpassword.setError("!");
            return false;
        }
        return true;
    }

}
