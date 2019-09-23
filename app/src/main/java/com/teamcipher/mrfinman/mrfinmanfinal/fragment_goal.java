package com.teamcipher.mrfinman.mrfinmanfinal;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.nightonke.boommenu.BoomButtons.BoomButton;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.OnBoomListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import Adapters.Transactions;
import Class.DecimalRemover;
import Models.Category;
import Models.MyBill;
import Models.MyExpense;
import Models.MyGoals;
import Models.Transaction;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;
import at.markushi.ui.CircleButton;

import static Utils.methods.ctx;
import static Utils.methods.dateComplete;
import static Utils.methods.getDateDiff;


public class fragment_goal extends Fragment {
    View view;
    ListView listView;
    UserLogin user = UserLogin.getInstance();
    MyGoalAdapter adapter;
    ArrayList<MyGoals> myGoals = new ArrayList<>();
    Context context;
    FloatingActionButton fabNew;
    FloatingActionMenu fabMenu;
    BoomMenuButton bmMenu;
    RelativeLayout relativeLayout;
    SwipeRefreshLayout swipeRefreshLayout;
    int userId = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_goals, container, false);
        initialization(view);
        onheader("My Goals");
        populate();
        btnSetClicks();


        return view;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.listview_my_goals_list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MyGoals goals = (MyGoals)adapter.getItem(info.position);

        switch(item.getItemId()) {

            case R.id.details:
                dialogue(goals.getName(),goals.getAmount(),goals.getIcon(),goals.getDescription(),goals.getTargetDate());
                return true;
            case R.id.edit:
                edit(info.position);
                return true;
            case R.id.delete:
                delete(goals);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    private void onheader(String title) {
        try
        {
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
        catch (Exception ex)
        {

        }
    }

    private void btnSetClicks() {
        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(context,Activity_my_goals_add.class);
                startActivity(intent);
            }
        });


        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
            }
        });
        fabMenu.setClosedOnTouchOutside(true);

    }





    private void edit(final int index) {
        final MyGoals g = (MyGoals)adapter.getItem(index);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"goal.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    JSONObject obj= jsonArray.getJSONObject(0);
                    Intent intent = new Intent(context,Activity_my_goals_add.class);
                    intent.putExtra("goal_ID",obj.getString("goal_ID"));
                    intent.putExtra("goal_name",obj.getString("goal_name"));
                    intent.putExtra("amount",obj.getString("amount"));
                    intent.putExtra("description",obj.getString("description"));
                    intent.putExtra("targetDate",obj.getString("targetDate"));
                    intent.putExtra("Type","EDIT");
                    intent.putExtra("categoryId",obj.getString("category_id"));
                    startActivity(intent);

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
                params.put("type","retrieve");
                params.put("id",""+g.getId());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }
    private void delete(final MyGoals g, final String type)
    {
        AndroidNetworking.get(methods.server()+"goalDelete_Undo.php?type="+type+"&Id="+g.getId()+"")
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
                                        message.error(""+jObject.getString("message"),context);
                                        break;
                                    case 1:
                                        message.success(""+jObject.getString("message"),context);
                                        populate();
                                        break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    public void onError(ANError error) {
                        message.error(""+error,ctx);
                    }
                });
    }

    public void delete(final MyGoals g)
    {

        final  AlertDialog alertDialog = new AlertDialog.Builder(context)
                //set icon
                .setIcon(android.R.drawable.ic_dialog_info)
                //set title
                .setTitle("Confirmation")
                //set message
                .setMessage("Are you sure you want to delete "+g.getName()+"?")
                //set positive button
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(g,"DELETE");
                        undo(g);


                    }
                })
                //set negative button
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked

                    }
                })
                .show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.WHITE);
        ba.setTextColor(Color.WHITE);
    }

    public void dialogue(String name,String amountvalue,String icon,String descr,String target)
    {
        try
        {
            final Dialog dialog = new Dialog(getActivity(),R.style.DialogTheme);
            dialog.setContentView(R.layout.dialogue_show_goal);
            Button btnDismiss = dialog.findViewById(R.id.goal_dialogue_close);
            TextView amount = dialog.findViewById(R.id.goal_dialogue_amount);
            TextView desc = dialog.findViewById(R.id.goal_dialogue_desc);
            ImageView iconholder = dialog.findViewById(R.id.goal_dialogue_img_icon);
            TextView Name = dialog.findViewById(R.id.goal_dialogue_Name);
            TextView status = dialog.findViewById(R.id.goal_dialogue_status);
            TextView targetdate = dialog.findViewById(R.id.goal_dialogue_targetdate);
            TextView days = dialog.findViewById(R.id.goal_dialogue_days);


            String[] arrdate = target.split("/");

            Date d1 = new Date(arrdate[0]+"/"+arrdate[1]+"/"+arrdate[2]);
            Date dt = Calendar.getInstance().getTime();
            String dtNow = methods.date.format(dt);
            String dtEnd = methods.date.format(d1);

            int remDays = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), dtNow, dtEnd);

            amount.setText("Php "+methods.formatter.format(Double.parseDouble(amountvalue)));
            desc.setText(""+descr);
            Name.setText(""+name);

            targetdate.setText(""+dateComplete.format(d1));
            if (remDays < 0)
            {
                status.setText("Overdue");
                status.setBackgroundColor(getResources().getColor(R.color.red));
                days.setText("");
            }
            else
            {
                status.setText("In progress");
                status.setBackgroundColor(getResources().getColor(R.color.green));
                days.setText(""+remDays);
            }


            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(2)
                    .cornerRadiusDp(5)
                    .oval(false)
                    .build();

            Picasso.get().load(methods.icon_server()+icon) .transform(transformation).into(iconholder);


            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.setCancelable(true);
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }
    private void populate() {
        try
        {
            myGoals.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"goal_list.php?userID="+userId, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Toast.makeText(context, ""+response, Toast.LENGTH_SHORT).show();
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i =0 ; i<jsonArray.length();i++)
                        {
                            JSONObject j = jsonArray.getJSONObject(i);
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

                            myGoals.add(goals);
                        }

                        adapter = new MyGoalAdapter(context,myGoals);
                        listView.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        // Toast.makeText(context, ""+e.toString(), Toast.LENGTH_SHORT).show();

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(context, ""+error, Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }

    private void initialization(View view) {
        try
        {
            userId  = Integer.parseInt(getPreference("userID"));
            relativeLayout = view.findViewById(R.id.rootLayout);
            context = getContext();
            listView = view.findViewById(R.id.listview_my_goals_list);
            fabNew = view.findViewById(R.id.fab_goal_add);
            fabMenu = view.findViewById(R.id.fab_goal_menu);

            registerForContextMenu(listView);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
            onRefresh();
        }
        catch (Exception ex)
        {

        }
    }

    private String getPreference(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }

    private void onRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);

                populate();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ///Toast.makeText(context, "Refresh Completed!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },200);

            }
        });
    }


    public void refresh()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        populate();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 2000);
    }
    private void undo(final MyGoals g)
    {
        final Snackbar snackbar = Snackbar.make(relativeLayout,"Successfuly Deleted!",Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(g,"UNDO");
                snackbar.dismiss();

            }
        });

    }


}



