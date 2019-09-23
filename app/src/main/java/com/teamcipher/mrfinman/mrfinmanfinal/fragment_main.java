package com.teamcipher.mrfinman.mrfinmanfinal;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;


import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


import com.takusemba.spotlight.CustomTarget;
import com.takusemba.spotlight.OnSpotlightEndedListener;
import com.takusemba.spotlight.OnSpotlightStartedListener;
import com.takusemba.spotlight.SimpleTarget;
import com.takusemba.spotlight.Spotlight;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.PopUp.DebtPopUp;
import com.wooplr.spotlight.SpotlightView;
import com.wooplr.spotlight.shape.Circle;
import com.wooplr.spotlight.target.Target;
import com.wooplr.spotlight.utils.SpotlightListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.Transactions;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import background.background;
import Class.DecimalRemover;
import Models.Category;
import Models.CategoryAmount;
import Models.Debts;
import Models.MyBill;
import Models.MyExpense;
import Models.MyGoals;
import Models.Transaction;
import retrofit2.Call;
import retrofit2.Callback;
import services.BillCheck;
import services.DebtCheck;
import services.GoalCheck;
import services.RealtimeBudgetRemCheck;
import Singleton.ChoosenDateST;
import Singleton.ExpenseDateRangeSingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.SavingSingleton;
import Singleton.TransactionST;
import Singleton.UserLogin;
import Singleton.ViewTypeSingleton;
import Utils.customMethod;
import Utils.message;
import Utils.methods;
import at.markushi.ui.CircleButton;


public class fragment_main extends Fragment implements View.OnClickListener, OnChartValueSelectedListener {
    Context ctx;
    DatePickerDialog.OnDateSetListener mdatelistener;
    Date date, month;
    SimpleDateFormat df;
    Calendar calendar;
    CircleButton btnExpense, btnIncome;
    AwesomeTextView btnNextDate, btnPrevDate,lbl_guide,lbl_refresh;
    TextView lbldate, btnSave, btnshow, legend;
    PieChart chart;
    View view;
    Double totalIncome = 0.0,total_Income = 0.0, totalExpense = 0.0;
    Boolean hasSave = false;
    Date startDate, endDate;
    String datePick = "", type = "";
    public UserLogin user;
    ArrayList<Category> categories = new ArrayList<>();
    ArrayList<MyExpense> myExpensesDay = new ArrayList<>();
    ArrayList<MyExpense> myExpensesWeek = new ArrayList<>();
    ArrayList<MyExpense> myExpensesMonth = new ArrayList<>();
    ArrayList<MyExpense> myExpensesYear = new ArrayList<>();
    ArrayList<Transaction> transactions = new ArrayList<>();
    ArrayList<MyBill> myBills = new ArrayList<>();
    ArrayList<Debts> debts = new ArrayList<>();

    List<CategoryAmount> categoryAmounts = new ArrayList<>();
    List<PieEntry> pieEntryList = new ArrayList<>();
    PieDataSet pieDataSet;
    ListView listView;
    View BottomSheet;
    BottomSheetBehavior behavior;
    NestedScrollView nestedScrollView;
    String dtFrom = "", dtTo = "",username = "";
    SwipeRefreshLayout swipeRefreshLayout;
    Date dtNow;
    CardView cardViewSavings, btnGuide,btnRefresh;
    Double savingAmount = 0.00, savingAmountthisMonth = 0.0;
    SimpleDateFormat dateformaterStart = new SimpleDateFormat("dd");
    SimpleDateFormat dateformaterEnd = new SimpleDateFormat("dd MMMM,yyyy");
    TextView lblTotalSavings, lbllbltitleDateSavings, lblAmountSavingDate, lblTotalExpense;
    ArrayList<CategoryAmount> RemainingBudgetList = new ArrayList<>();
    UserLogin userInfo = new UserLogin();
    TextView lblTotal_Income;
    APIservice apIservice = APIclient.getClient().create(APIservice.class);

    int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        totalIncome = IncomeSingleton.getInstance().getIncomeMonthly();

        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

        TypefaceProvider.registerDefaultIconSets();
        view = inflater.inflate(R.layout.fragment_fragment_main, container, false);

        initialization();
        loadingDialogue();
        onLoadItems();
        loadSavings();


        btnNextDate.setOnClickListener(this);
        btnPrevDate.setOnClickListener(this);

        btnIncome.setOnClickListener(this);
        btnExpense.setOnClickListener(this);
        lbldate.setOnClickListener(this);
        btnshow.setOnClickListener(this);
        lbl_guide.setOnClickListener(this);
        lbl_refresh.setOnClickListener(this);
        registerToken();
        return view;
    }

    private String getPreferenceToken(String key) {
        SharedPreferences preferences = ctx.getSharedPreferences("TOKEN", 0);
        return preferences.getString(key, null);
    }

    private void registerToken() {
        String token = getPreferenceToken("Token");
        AndroidNetworking.get(methods.PUSHNOTIF_API_SERVER + "registration.php?userId=" + userId + "&Token=" + token + "")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                    }
                    public void onError(ANError error) {
                    }
                });
    }

    private void initialization() {
        username = getPreference("username");
        loadUserInfo();
        calendar= Calendar.getInstance();
        dtNow = Calendar.getInstance().getTime();
        userId = Integer.parseInt(getPreference("userID"));
        ctx = getContext();
        onheader("Dashboard");

        btnshow = view.findViewById(R.id.btn_lbl_show_transaction);
        listView = view.findViewById(R.id.listview_dash_transactions);
        TypefaceProvider.registerDefaultIconSets();
        btnExpense = view.findViewById(R.id.fab_expense);
        btnIncome = view.findViewById(R.id.fab_income);

        btnNextDate = view.findViewById(R.id.lbl_main_date_forward);
        btnPrevDate = view.findViewById(R.id.lbl_main_date_back);
        lbldate = view.findViewById(R.id.lbl_month);

        bottomSheetMethods();
        loadBalance();

        startDate = ExpenseDateRangeSingleton.getInstance().getStartDate();
        endDate = ExpenseDateRangeSingleton.getInstance().getEndDate();

        btnNextDate.bringToFront();
        btnPrevDate.bringToFront();

        btnExpense.bringToFront();
        btnIncome.bringToFront();

        lblTotalSavings = view.findViewById(R.id.lbl_Total_savings);//titleSavingMonth
        lbllbltitleDateSavings = view.findViewById(R.id.lblSavingTitle);
        lblAmountSavingDate = view.findViewById(R.id.lblSavingMonth);
        cardViewSavings = view.findViewById(R.id.CardSavings);
        cardViewSavings.setOnClickListener(this);
        btnGuide = view.findViewById(R.id.btnGuide);
        btnGuide.bringToFront();
        lbl_guide = view.findViewById(R.id.lbl_guide);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        btnRefresh.bringToFront();
        lbl_refresh = view.findViewById(R.id.lbl_Refresh);
        lblTotalExpense = view.findViewById(R.id.lbl_total_expense);
        lblTotal_Income = view.findViewById(R.id.lblTotalIncome);


        lblTotal_Income.bringToFront();
        savePreference("isSet", "1");
        waitLoadRemaining();

        savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
    }

    private void oncheckSavings() {
        new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (!hasSave) {
                    showSetSavingDialogue();
                }
            }
        }.start();

    }

    private void loadUserInfo() {
        Call<UserLogin> userDetails = apIservice.getUserDetails(username);
        userDetails.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, retrofit2.Response<UserLogin> response) {
                Logs.LOGS(""+response);
                if (response.isSuccessful())
                {
                    userInfo.setLname(response.body().getLname());
                    userInfo.setFname(response.body().getFname());
                    userInfo.setMi(response.body().getMi());
                    userInfo.setEmail(response.body().getEmail());
                    userInfo.setContactNo(response.body().getContactNo());
                    userInfo.setUserId(response.body().getUser_ID());
                    userInfo.setUsername(response.body().getUsername());
                    userInfo.setPassword(response.body().getPassword());
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Logs.LOGS("Background Error "+t);
            }
        });
    }

    private void showSetSavingDialogue() {
        String hasIncome = getPreference("hasIncome");
        if (hasIncome.equals("1")) {
            AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Message")
                    .setMessage("Hello " + userInfo.fullname() + " Good Day" + "!\n\nWould you like to set your savings for this month?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setSavingDialogue();
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
    }

    private void checkHasSave() {
        AndroidNetworking.get(methods.USER_API_SERVER + "checkHasSave.php?username=" + username)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(0);
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 0:
                                    savingAmount = Double.parseDouble(jsonObject.getString("amount"));
                                    setSavingDialogue();
                                    break;
                                case 1:
                                    AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                            .setIcon(android.R.drawable.ic_dialog_info)
                                            .setTitle("Message")
                                            .setMessage("You have already set a saving for the month of " + methods.month.format(Calendar.getInstance().getTime()) + ".\nWould you like to update your savings?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    setUpdateSavingsDialogue(savingAmount);
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

                                    savingAmount = Double.parseDouble(jsonObject.getString("amount"));
                                    savingAmountthisMonth = Double.parseDouble(jsonObject.getString("SavingsThisMonth"));
                                    lblAmountSavingDate.setText("Php " + methods.formatter.format(savingAmount));
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    public void onError(ANError error) {
                    }
                });
    }

    private void loadSavings() {
        AndroidNetworking.get(methods.USER_API_SERVER + "getSavings.php?username=" + username)
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(0);
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 0:
                                    //message.error("" + jsonObject.getString("message"), ctx);
                                    break;
                                case 1:
                                    SavingSingleton.getInstance().setAllSavings(Double.parseDouble(jsonObject.getString("totalSavings")) + Double.parseDouble(jsonObject.getString("SavingsThisMonth")));

                                    lblTotalSavings.setText("Php " + methods.formatter.format(Double.parseDouble(jsonObject.getString("totalSavings"))));
                                    lbllbltitleDateSavings.setText("Savings for " + methods.monthDisplay.format(Calendar.getInstance().getTime()));
                                    lblAmountSavingDate.setText("Php " + methods.formatter.format(Double.parseDouble(jsonObject.getString("SavingsThisMonth"))));
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    public void onError(ANError error) {
                    }
                });
    }

    private void onLoadItems() {
        try {
            calendar = Calendar.getInstance();
            ChoosenDateST.getInstance().setDate(calendar);
            type = ViewTypeSingleton.getInstance().getTypeview();
            if (type.equals("Day")) {
                date = calendar.getTime();
                lbldate.setText("" + methods.dateComplete.format(date));
                getExpenseDaily(methods.date.format(date));
                loadListview(methods.date.format(date));
            } else if (type.equals("Month")) {
                month = calendar.getTime();
                lbldate.setText("" + methods.monthDisplay.format(month));
                getExpenseMonthly(methods.month.format(month.getTime()));
                loadListview(methods.month.format(month));
            } else if (type.equals("Week")) {
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                SimpleDateFormat dateformaterStart = new SimpleDateFormat("dd");
                SimpleDateFormat dateformaterEnd = new SimpleDateFormat("dd MMMM,yyyy");

                String value = "";
                value = dateformaterStart.format(calendar.getTime());
                String startDt = methods.date_db.format(calendar.getTime());
                dtFrom = startDt;
                calendar.add(Calendar.DAY_OF_WEEK, 6);

                value += "-" + dateformaterEnd.format(calendar.getTime());
                String endDt = methods.date_db.format(calendar.getTime());
                dtTo = endDt;
                calendar.add(Calendar.DAY_OF_WEEK, 1);
                getExpenseWeekly(startDt, endDt);


                loadListview(startDt + " " + endDt);

                lbldate.setText("" + value);

            } else if (type.equals("Year")) {
                calendar = Calendar.getInstance();
                lbldate.setText("" + methods.year.format(calendar.getTime()));
                getExpenseYearly(methods.year.format(calendar.getTime()));
                loadListview(methods.year.format(calendar.getTime()));
            } else if (type.equals("All")) {
                calendar = Calendar.getInstance();
                String value = methods.date_dMMM.format(startDate.getTime()) + "-" + methods.date_dMMM.format(endDate.getTime()) + "," + methods.year.format(calendar.getTime());
                lbldate.setText(value);
                getExpenseYearly(methods.year.format(calendar.getTime()));
                loadListview(methods.year.format(calendar.getTime()));
            } else {
                date = calendar.getTime();
                lbldate.setText("" + methods.dateComplete.format(date));
                getExpenseDaily(methods.date.format(date));
                loadListview(methods.date.format(date));
            }
        } catch (Exception ex) {
        }
    }

    private void showSavings() {
      checkHasSave();
    }

    private void SavingMethod(String todo, int userId, String dateCreated, Double amount) {
        AndroidNetworking.get(methods.USER_API_SERVER + "savings.php?todo=" + todo + "&userId=" + userId + "&dateCreated=" + dateCreated + "&amount=" + amount + "&categoryId=27")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            JSONObject jsonObject = response.getJSONObject(0);
                            int code = jsonObject.getInt("code");
                            switch (code) {
                                case 0:
                                    break;
                                case 1:
                                    message.success("" + jsonObject.getString("message"), ctx);
                                    fragmentRedirection(new fragment_main());
                                    break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    public void onError(ANError error) {
                    }
                });
    }

    private void setSavingDialogue() {
        final Dialog dialog;
        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialogue_set_savings);
        dialog.setCancelable(false);

        final EditText txt_amount = dialog.findViewById(R.id.txtamount);
        TextView lblmessage = dialog.findViewById(R.id.lblMessage);
        TextView lblTitle = dialog.findViewById(R.id.saving_title);

        //bill_title
        lblTitle.setText("* * *  Set Savings  * * *");
        lblmessage.setText("How much do you want to save for the month of " + methods.month.format(Calendar.getInstance().getTime()) + "?");

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOK = dialog.findViewById(R.id.btnOK);
        final TextInputLayout layout = dialog.findViewById(R.id.amountLayout);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(txt_amount.getText().toString())) {
                    if (Double.parseDouble(txt_amount.getText().toString()) < IncomeSingleton.getInstance().getIncomeMonthly())
                    {
                        if (Double.parseDouble(txt_amount.getText().toString()) >= 50) {
                            SavingMethod("addSavings", userId, methods.dtComplete.format(Calendar.getInstance().getTime()), Double.parseDouble(txt_amount.getText().toString()));
                            dialog.dismiss();
                        } else {
                            layout.setError("Saving amount must be greater than  Php 50.00");
                        }
                    }
                    else
                    {
                        message.alertWarning(ctx,"Amount to save is greater than total Income!");
                        message.warning("Amount to save is greater than total Income!",ctx);
                    }
                } else
                    layout.setError("Not valid amount!");

            }
        });
        dialog.create();
        dialog.show();


    }

    private void setUpdateSavingsDialogue(double amount) {
        final Dialog dialog;
        dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialogue_set_savings);
        dialog.setCancelable(false);


        final EditText txt_amount = dialog.findViewById(R.id.txtamount);
        TextView lblmessage = dialog.findViewById(R.id.lblMessage);
        TextView lblTitle = dialog.findViewById(R.id.saving_title);

        //bill_title
        lblTitle.setText("* * *  Update My Savings  * * *");
        lblmessage.setText("How much do you want to save for the month of " + methods.month.format(Calendar.getInstance().getTime()) + "?");

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setText("UPDATE");

        final TextInputLayout layout = dialog.findViewById(R.id.amountLayout);

        txt_amount.setText("" + methods.formatter00.format(amount));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(txt_amount.getText().toString().equals(""))) {
                    if (Double.parseDouble(txt_amount.getText().toString()) >= 50) {
                        SavingMethod("update", userId, methods.dtComplete.format(Calendar.getInstance().getTime()), Double.parseDouble(txt_amount.getText().toString()));
                        dialog.dismiss();
                    } else {
                        layout.setError("Saving amount must be greater than  php 50.");
                    }
                } else
                    layout.setError("Not valid amount!");

            }
        });
        dialog.create();
        dialog.show();
    }

    private void loadListview(final String dateChoosen) {
        try {
            transactions.clear();
            totalExpense = 0.0;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "transaction_list.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.length() > 0) {
                        try {
                            JSONArray jo = new JSONArray(response);
                            for (int i = 0; i < jo.length(); i++) {
                                JSONObject jobject = jo.getJSONObject(i);
                                Transaction tr = new Transaction();
                                tr.setName("" + jobject.getString("Name"));
                                tr.setAmount(Double.parseDouble(jobject.getString("amount")));
                                tr.setIcon("" + jobject.getString("icon"));
                                tr.setType("" + jobject.getString("type"));
                                tr.setDate("");//+jobject.getString("dateCreated")
                                tr.setNote("" + jobject.getString("note"));
                                tr.setId(i + 1);
                                //tr.setImage(""+jobject.getString("imgReciept"));
                                if (tr.getType().equals("Expense"))
                                    totalExpense += Double.parseDouble(jobject.getString("amount"));


                                transactions.add(tr);
                            }
                            lblTotalExpense.setText("Php "+methods.formatter.format(totalExpense));

                            Transactions adaptor = new Transactions(getContext(), transactions);
                            listView.setAdapter(adaptor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("username", "" + username);
                    params.put("type", "" + type);
                    params.put("date", "" + dateChoosen);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent;
                    Transaction tr = (Transaction) adapterView.getItemAtPosition(i);
                    String categoryname = tr.getName();
                    intent = new Intent(getActivity(), Activity_transactions.class);
                    if (type.equals("Day")) {
                        String dateChos = methods.date.format(calendar.getTime());
                        intent.putExtra("category", tr.getName());
                        intent.putExtra("date", dateChos);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    } else if (type.equals("Month")) {
                        String dateChos = methods.month.format(calendar.getTime());
                        intent.putExtra("category", tr.getName());
                        intent.putExtra("date", dateChos);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    } else if (type.equals("Year")) {
                        String dateChos = methods.year.format(calendar.getTime());
                        intent.putExtra("category", tr.getName());
                        intent.putExtra("date", dateChos);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    } else if (type.equals("Week")) {
                        String dateChos = dtFrom + " " + dtTo;
                        intent.putExtra("category", tr.getName());
                        intent.putExtra("date", dateChos);
                        intent.putExtra("type", type);
                        startActivity(intent);
                    }


                }
            });
        } catch (Exception ex) {
        }

    }

    private void bottomSheetMethods() {
        BottomSheet = view.findViewById(R.id.design_bottom_sheet_testing);
        behavior = BottomSheetBehavior.from(BottomSheet);
        behavior.setHideable(false);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        behavior.setHideable(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        behavior.setHideable(false);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        behavior.setHideable(false);
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
            }
        });
    }

    private void getExpenseDaily(final String dateChoosen) {
        try {
            myExpensesDay.clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getExpense_daily.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    if (response.length() > 0) {
                        try {
                            JSONArray jo = new JSONArray(response);
                            for (int i = 0; i < jo.length(); i++)
                            {
                                JSONObject jobject = jo.getJSONObject(i);
                                MyExpense myExpense = new MyExpense();
                                myExpense.setName(jobject.getString("Name"));
                                myExpense.setValue(Float.parseFloat(jobject.getString("Percentage")));
                                myExpense.setDateCreated("" + jobject.getString("dateCreated"));
                                myExpensesDay.add(myExpense);
                            }
                            if (myExpensesDay.size() >= 0)
                                setGraph(myExpensesDay);
                            else
                                {
                                myExpensesDay.clear();
                                setGraph(myExpensesDay);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("username", "" + username);
                    params.put("date", "" + dateChoosen);

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        } catch (Exception ex) {
        }
    }

    private void getExpenseWeekly(final String dateFrom, final String dateTo) {
        myExpensesWeek.clear();
        AndroidNetworking.get(methods.server() + "getExpense_weekly.php?username=" + username + "&dateFrom=" + dateFrom + "&dateTo=" + dateTo + "")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jobject = response.getJSONObject(i);
                                MyExpense myExpense = new MyExpense();
                                myExpense.setName(jobject.getString("Name"));
                                myExpense.setValue(Float.parseFloat(jobject.getString("Percentage")));
                                myExpense.setDateCreated("" + jobject.getString("dateCreated"));
                                myExpensesWeek.add(myExpense);
                            }
                            if (myExpensesWeek.size() >= 0)
                                setGraph(myExpensesWeek);
                            else {
                                myExpensesWeek.clear();
                                setGraph(myExpensesWeek);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                    }
                });
    }

    private void getExpenseMonthly(final String dateChoosen) {
        myExpensesMonth.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getExpense_monthly.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length() > 0) {
                    try {
                        JSONArray jo = new JSONArray(response);
                        for (int i = 0; i < jo.length(); i++) {
                            JSONObject jobject = jo.getJSONObject(i);
                            MyExpense myExpense = new MyExpense();
                            myExpense.setName(jobject.getString("Name"));
                            myExpense.setValue(Float.parseFloat(jobject.getString("Percentage")));
                            myExpense.setDateCreated("" + jobject.getString("dateCreated"));
                            myExpensesMonth.add(myExpense);
                        }
                        if (myExpensesMonth.size() >= 0)
                            setGraph(myExpensesMonth);
                        else {
                            myExpensesMonth.clear();
                            setGraph(myExpensesMonth);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("username", "" +username);
                params.put("date", "" + dateChoosen);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void getExpenseYearly(final String dateChoosen) {
        myExpensesYear.clear();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getExpense_yearly.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.length() > 0) {
                    try {
                        JSONArray jo = new JSONArray(response);
                        for (int i = 0; i < jo.length(); i++) {
                            JSONObject jobject = jo.getJSONObject(i);
                            MyExpense myExpense = new MyExpense();
                            myExpense.setName(jobject.getString("Name"));
                            myExpense.setValue(Float.parseFloat(jobject.getString("Percentage")));
                            myExpense.setDateCreated("" + jobject.getString("dateCreated"));
                            myExpensesYear.add(myExpense);
                        }
                        if (myExpensesYear.size() >= 0)
                            setGraph(myExpensesYear);
                        else {
                            myExpensesYear.clear();
                            setGraph(myExpensesYear);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("username", "" + username);
                params.put("date", "" + dateChoosen);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void setGraph(ArrayList<MyExpense> myExpenseArrayList) {
        try {
            pieEntryList.clear();
            chart = view.findViewById(R.id.myChart);
            pieEntryList = new ArrayList<>();

            for (int i = 0; i < myExpenseArrayList.size(); i++) {
                Float perc = myExpenseArrayList.get(i).getValue();
                String lbl = myExpenseArrayList.get(i).getName();
                //pieEntryList.add(new PieEntry(Float.parseFloat(String.format("%.0f", perc)), lbl));
                pieEntryList.add(new PieEntry(perc, lbl));
            }
            int colorBlack = Color.parseColor("#FFFFFF");
            chart.setEntryLabelColor(colorBlack);
            pieDataSet = new PieDataSet(pieEntryList, "Chart");
            pieDataSet.setSliceSpace(0f);
            pieDataSet.setSelectionShift(10f);
            pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

            pieDataSet.setValueFormatter(new PercentFormatter());
            pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
            PieData data = new PieData(pieDataSet);
            data.setValueFormatter(new DecimalRemover(new DecimalFormat("###,###,###.##")));
            data.setValueTextSize(10f);
            data.setValueTextColor(R.color.white);

            chart.getLegend().setEnabled(false);
            chart.setEntryLabelTextSize(15f);
            chart.getDescription().setEnabled(false);
            chart.setTransparentCircleRadius(55f);
            chart.setDrawHoleEnabled(true);
            chart.animateX(4000);
            chart.animateY(8000);
            chart.setHoleColor(Color.TRANSPARENT);
            chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);
            chart.setData(data);
            chart.invalidate();
            chart.setUsePercentValues(false);
            chart.getLegend().setEnabled(false);
            chart.setCenterText("" /*"Total Income\nPhp " + methods.formatter.format(totalIncome)*/);
            chart.setCenterTextColor(getResources().getColor(R.color.green));
            chart.setCenterTextSize(15f);

            chart.setOnChartValueSelectedListener(this);
        } catch (Exception ex) {
            //Toast.makeText(ctx, ""+ex, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Intent intent;
        PieEntry pe = (PieEntry) e;

        if (type.equals("Day")) {
            String date = methods.date.format(calendar.getTime());
            intent = new Intent(getActivity(), Activity_transactions.class);
            intent.putExtra("category", pe.getLabel());
            intent.putExtra("date", date);
            intent.putExtra("type", type);
            startActivity(intent);
            getActivity().getFragmentManager().popBackStack();


        } else if (type.equals("Week")) {
            String dateChos = dtFrom + " " + dtTo;
            intent = new Intent(getActivity(), Activity_transactions.class);
            intent.putExtra("category", pe.getLabel());
            intent.putExtra("date", dateChos);
            intent.putExtra("type", type);
            startActivity(intent);
            getActivity().getFragmentManager().popBackStack();

        } else if (type.equals("Month")) {
            String date = methods.month.format(calendar.getTime());
            intent = new Intent(getActivity(), Activity_transactions.class);
            intent.putExtra("category", pe.getLabel());
            intent.putExtra("date", date);
            intent.putExtra("type", type);
            startActivity(intent);
        } else if (type.equals("Year")) {
            String date = methods.year.format(calendar.getTime());
            intent = new Intent(getActivity(), Activity_transactions.class);
            intent.putExtra("category", pe.getLabel());
            intent.putExtra("date", date);
            intent.putExtra("type", type);
            startActivity(intent);
        } else if (type.equals("All")) {
            String date = methods.year.format(calendar.getTime());
            intent = new Intent(getActivity(), Activity_transactions.class);
            intent.putExtra("category", pe.getLabel());
            intent.putExtra("date", date);
            intent.putExtra("type", type);
            startActivity(intent);
        }
    }

    private void onheader(String title) {
        try
        {
            ((Activity_dashboard) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((Activity_dashboard) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
            ((Activity_dashboard) getActivity()).getSupportActionBar().setDisplayUseLogoEnabled(true);
            ((Activity_dashboard) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((Activity_dashboard) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);

            ActionBar actionBar = ((Activity_dashboard) getActivity()).getSupportActionBar();
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

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {//
            case R.id.CardSavings:
                showSavings();
                break;
            case R.id.lbl_main_date_forward:
                nextDate();
                loadIncome();
                break;
            case R.id.lbl_main_date_back:
                prevDate();
                loadIncome();
                break;
            case R.id.fab_expense:
                methods.vibrate(ctx);
                TransactionST.resetInstance();
                Intent intent = new Intent(ctx, Activity_expense.class);
                startActivity(intent);
                break;
            case R.id.fab_income:
                methods.vibrate(ctx);
                TransactionST.resetInstance();
                Activity_income activity_income = new Activity_income();
                startIntent(activity_income);
                break;
            case R.id.lbl_month:
                showchoices();
                methods.vibrate(ctx);
                break;
            case R.id.lbl_guide:
                onTutorial();
                methods.vibrate(ctx);
                break;
            case R.id.lbl_Refresh:
                fragmentRedirection(new fragment_main());
                methods.vibrate(ctx);
                break;
            case R.id.btn_lbl_show_transaction:
                methods.vibrate(ctx);
                if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    showBottomSheet();
                else
                    hideBottomSheet();
                break;
        }
    }

    public void showBottomSheet() {
        behavior.setHideable(false);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void hideBottomSheet() {
        behavior.setHideable(true);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void startIntent(Context ctx) {
        Intent intent = new Intent(getContext(), ctx.getClass());
        startActivity(intent);

    }

    private void showchoices() {
        try
        {
            final Context context = getContext();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);
            final Dialog dialog = new Dialog(context,R.style.MyDialogTheme);
            dialog.setContentView(R.layout.dialogue_dash_date_picker);


            final Spinner spinner = dialog.findViewById(R.id.typeViewSpinner);
            Button btnDismiss = dialog.findViewById(R.id.btnOK);
            String viewType = ViewTypeSingleton.getInstance().getTypeview();
            if (viewType.equals(""))
                spinner.setSelection(0);
            else if (viewType.equals("Day"))
                spinner.setSelection(1);
            else if (viewType.equals("Week"))
                spinner.setSelection(2);
            else if (viewType.equals("Month"))
                spinner.setSelection(3);
            else if (viewType.equals("Year"))
                spinner.setSelection(4);
            else if (viewType.equals("All"))
                spinner.setSelection(5);

            btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String choosen = spinner.getSelectedItem().toString();
                    ViewTypeSingleton.getInstance().setTypeview(choosen);
                    fragmentRedirection(new fragment_main());
                }
            });
            dialog.create();
            dialog.show();
        }
        catch (Exception ex)
        {

        }
    }

    private void prevDate() {

        methods.vibrate(ctx);

        if (type.equals("Day")) {
            calendar.add(Calendar.DATE, -1);
            lbldate.setText("" + methods.dateComplete.format(calendar.getTime()));
            getExpenseDaily(methods.date.format(calendar.getTime()));
            loadListview(methods.date.format(calendar.getTime()));

            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        } else if (type.equals("Month")) {
            calendar.add(Calendar.MONTH, -1);
            lbldate.setText("" + methods.monthDisplay.format(calendar.getTime()));
            getExpenseMonthly(methods.month.format(calendar.getTime()));
            loadListview(methods.month.format(calendar.getTime()));

            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        } else if (type.equals("Week")) {
            SimpleDateFormat dateformaterStart = new SimpleDateFormat("dd");
            SimpleDateFormat dateformaterEnd = new SimpleDateFormat("dd MMMM,yyyy");

            String vaStr = "", vaEnd = "";

            calendar.add(Calendar.DAY_OF_WEEK, -8);
            String endDt = methods.date_db.format(calendar.getTime());
            dtTo = endDt;
            vaEnd = dateformaterEnd.format(calendar.getTime());


            calendar.add(Calendar.DAY_OF_WEEK, -6);
            vaStr = "" + dateformaterStart.format(calendar.getTime());
            String startDt = methods.date_db.format(calendar.getTime());
            dtFrom = startDt;
            getExpenseWeekly(startDt, endDt);
            loadListview(startDt + " " + endDt);

            lbldate.setText(vaStr + "-" + vaEnd);

            ///message.warning(startDt + " " + endDt, ctx);

            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        } else if (type.equals("Year")) {
            calendar.add(Calendar.YEAR, -1);
            lbldate.setText("" + methods.year.format(calendar.getTime()));
            getExpenseYearly(methods.year.format(calendar.getTime()));
            loadListview(methods.year.format(calendar.getTime()));
        } else if (type.equals("All")) {
            //message.warning("Cannot Preview!",ctx);
        } else {
            calendar.add(Calendar.DATE, -1);
            lbldate.setText("" + methods.dateComplete.format(calendar.getTime()));
            getExpenseDaily(methods.date.format(calendar.getTime()));
            loadListview(methods.date.format(calendar.getTime()));
            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        }
        ChoosenDateST.getInstance().setDate(calendar);
    }

    private void nextDate() {
        methods.vibrate(ctx);
        if (type.equals("Day")) {
            if (calendar.getTime().before(dtNow)) {
                calendar.add(Calendar.DATE, 1);
                lbldate.setText(methods.dateComplete.format(calendar.getTime()));
                getExpenseDaily(methods.date.format(calendar.getTime()));
                loadListview(methods.date.format(calendar.getTime()));

                savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
            } else {
                message.warning("Oops! Cannot navigate to the future Date!", ctx);
            }
        }
        else if (type.equals("Month")) {
            if (calendar.getTime().before(dtNow)) {

                calendar.add(Calendar.MONTH, 1);
                lbldate.setText("" + methods.monthDisplay.format(calendar.getTime()));
                getExpenseMonthly(methods.month.format(calendar.getTime()));
                loadListview(methods.month.format(calendar.getTime()));

                savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
            } else {
                calendar.add(Calendar.MONTH, -1);
                lbldate.setText("" + methods.monthDisplay.format(calendar.getTime()));
                getExpenseMonthly(methods.month.format(calendar.getTime()));
                loadListview(methods.month.format(calendar.getTime()));
                message.warning("Oops! Cannot navigate to the future Date!", ctx);

            }

        } else if (type.equals("Week")) {
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

            SimpleDateFormat dateformaterStart = new SimpleDateFormat("dd");
            SimpleDateFormat dateformaterEnd = new SimpleDateFormat("dd MMMM,yyyy");

            String value = "";
            value = dateformaterStart.format(calendar.getTime());
            String startDt = methods.date_db.format(calendar.getTime());
            dtFrom = startDt;
            calendar.add(Calendar.DAY_OF_WEEK, 6);
            String endDt = methods.date_db.format(calendar.getTime());
            dtTo = endDt;
            value += "-" + dateformaterEnd.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_WEEK, 1);
            getExpenseWeekly(startDt, endDt);
            loadListview(startDt + " " + endDt);
            lbldate.setText("" + value);

            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        } else if (type.equals("Year")) {

            if (calendar.getTime().before(dtNow)) {
                calendar.add(Calendar.YEAR, 1);
                lbldate.setText("" + methods.year.format(calendar.getTime()));
                getExpenseYearly(methods.year.format(calendar.getTime()));
                loadListview(methods.year.format(calendar.getTime()));
            } else {
                calendar.add(Calendar.YEAR, -1);
                lbldate.setText("" + methods.year.format(calendar.getTime()));
                getExpenseYearly(methods.year.format(calendar.getTime()));
                loadListview(methods.year.format(calendar.getTime()));
                message.warning("Oops! Cannot navigate to the future Date!", ctx);
            }

        } else if (type.equals("All")) {
            //message.warning("Cannot Preview!",ctx);
        } else {
            calendar.add(Calendar.DATE, 1);
            lbldate.setText(methods.dateComplete.format(calendar.getTime()));
            getExpenseDaily(methods.date.format(calendar.getTime()));
            loadListview(methods.date.format(calendar.getTime()));
            type = "Day";

            savePreference("DATE",methods.MM_yyyy.format(calendar.getTime()));
        }
        ChoosenDateST.getInstance().setDate(calendar);
    }

    private void populateCat() {
        try
        {
            Context ctx = getContext();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "getUserCategory.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject j = jsonArray.getJSONObject(i);

                            Category ct = new Category();
                            ct.setId(j.getInt("catID"));
                            ct.setIcon(j.getString("Icon"));
                            ct.setCategoryName(j.getString("Name"));//Percentage
                            ct.setPercentage(j.getDouble("Percentage"));
                            categories.add(ct);

                            if (!contain(ct.getCategoryName())) {
                                MyCategorySingleton.getInstance().getList().add(ct);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }

    public boolean contain(String name) {
        for (Category category : MyCategorySingleton.getInstance().getList()) {
            if (category.getCategoryName().equals(name))
                return true;
        }
        return false;
    }

    public void loadingDialogue() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading.....");
        progressDialog.show();
        populateCat();


        progressDialog.hide();

    }

    public void loadBalance() {
        try
        {
            final Handler handler = new Handler();
            Timer timer = new Timer(false);
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadIncome();
                            btnshow.setText("Balance Php " + methods.formatter.format(IncomeSingleton.getInstance().getTotal()));
                            categoryAmountList();
                            loadRemainingBudget();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 2000);
        }
        catch (Exception ex)
        {

        }

    }

    public void fragmentRedirection(Fragment ctx) {
        (getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, ctx)
                .addToBackStack(null)
                .commit();
    }

    public void loadIncome() {
        try
        {
            AndroidNetworking.get(methods.USER_API_SERVER+"getIncome.php?username="+getPreference("username")+"")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject jObject = response.getJSONObject(0);
                                switch (jObject.getInt("code"))
                                {
                                    case 0:
                                        // message.error(jObject.getString("message"),ctx);
                                        break;
                                    case 1:
                                        IncomeSingleton.getInstance().setAllIncome(Double.parseDouble(jObject.getString("sum")));
                                        break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logs.LOGS("Backgound Error Income");
                            }

                        }
                        public void onError(ANError error) {
                            Logs.LOGS("Backgound Error Income");
                        }
                    });

            AndroidNetworking.get(methods.USER_API_SERVER+"getIncomeExpense.php?username="+getPreference("username")+"")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject jObject = response.getJSONObject(0);
                                switch (jObject.getInt("code"))
                                {
                                    case 0:
                                        //message.error(jObject.getString("message"),ctx);
                                        break;
                                    case 1:
                                        IncomeSingleton.getInstance().setTotal(Double.parseDouble(jObject.getString("sum")));
                                        break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logs.LOGS("Backgound Error Income");
                            }

                        }
                        public void onError(ANError error) {
                            Logs.LOGS("Backgound Error Income");
                        }
                    });

            AndroidNetworking.get(methods.USER_API_SERVER+"getIncomeMonthly.php?username="+getPreference("username")+"&date="+getPreference("DATE"))
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONObject jObject = response.getJSONObject(0);
                                switch (jObject.getInt("code"))
                                {
                                    case 0:
                                        //message.error(jObject.getString("message"),ctx);
                                        break;
                                    case 1:
                                        IncomeSingleton.getInstance().setIncomeMonthly(Double.parseDouble(jObject.getString("sum")));
                                        lblTotal_Income.setText("Total Income\nPhp "+methods.formatter.format(Double.parseDouble(jObject.getString("sum"))));
                                        break;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Logs.LOGS("Backgound Error Income");
                            }

                        }
                        public void onError(ANError error) {
                            Logs.LOGS("Backgound Error Income");
                        }
                    });
        }
        catch (Exception ex)
        {

        }
    }



    private String getPreference(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }


    private void savePreference(String key, String value) {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void savePreference(String title, String key, String value) {
        SharedPreferences preferences = getActivity().getSharedPreferences(title, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void clearPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }


    public void categoryAmountList() {
        try
        {
            RemainingExpenseST.resetInstance();
            categoryAmounts.clear();
            AndroidNetworking.get(methods.server() + "getCategoryAmount.php?username=" + username)
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
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
        catch (Exception ex)
        {

        }
    }

    private void loadRemainingBudget() {
        try {
            RemainingExpenseST.resetInstance();

            for (int i = 0; i < MyCategorySingleton.getInstance().getList().size(); i++) {
                Category category = MyCategorySingleton.getInstance().getList().get(i);
                if (!check(category.getId())) {
                    CategoryAmount camount = new CategoryAmount();
                    camount.setCategoryId(category.getId());
                    camount.setCategoryName("" + category.getCategoryName());
                    camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                    camount.setAmount(methods.amount(category.getPercentage()));

                    RemainingBudgetList.add(camount);
                }

                for (CategoryAmount c : categoryAmounts) {
                    if (category.getId() == c.getCategoryId()) {
                        Log.d("TESTING", "NAA");
                        Double amt = c.getAmount();
                        String totalExpensePercentage = methods.percentage(amt);
                        String remainingPercentage = "" + (category.getPercentage() - Double.parseDouble(totalExpensePercentage));
                        String remPerc = remainingPercentage + "/" + c.getPercentage();

                        String amountPercSet = "" + methods.amount(category.getPercentage());
                        Double remainingAmount = Double.parseDouble(amountPercSet) - c.getAmount();

                        CategoryAmount camount = new CategoryAmount();
                        camount.setCategoryId(category.getId());
                        camount.setCategoryName("" + category.getCategoryName());

                        camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                        camount.setAmount(remainingAmount);

                        RemainingBudgetList.add(camount);
                        RemainingExpenseST.getInstance().getList().add(camount);
                    }
                }
            }
        }
        catch (Exception ex)
        {

        }
    }

    public void waitLoadRemaining() {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                loadRemainingBudget();

            }
        }.start();
    }

    public Boolean check(int id) {
        for (CategoryAmount c : categoryAmounts) {
            if (id == c.getCategoryId())
                return true;
        }
        return false;
    }
    private void onTutorial() {
        new SpotlightView.Builder((Activity) ctx)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(getResources().getColor(R.color.white))
                .headingTvSize(32)
                .headingTvText("Add Income Button")
                .subHeadingTvColor(getResources().getColor(R.color.white))
                .subHeadingTvSize(16)
                .subHeadingTvText("Tap this button to add an additional income.")
                .maskColor(Color.parseColor("#dc000000"))
                .target(btnIncome)
                .lineAnimDuration(400)
                .lineAndArcColor(getResources().getColor(R.color.green))
                .dismissOnTouch(true)
                .setListener(new SpotlightListener() {
                    @Override
                    public void onUserClicked(String s) {
                        new SpotlightView.Builder((Activity) ctx)
                                .introAnimationDuration(400)
                                .enableRevealAnimation(true)
                                .performClick(true)
                                .fadeinTextDuration(400)
                                .headingTvColor(getResources().getColor(R.color.white))
                                .headingTvSize(32)
                                .headingTvText("Add Expense Button")
                                .subHeadingTvColor(getResources().getColor(R.color.white))
                                .subHeadingTvSize(16)
                                .subHeadingTvText("Tap this button to add new expense.")
                                .maskColor(Color.parseColor("#dc000000"))
                                .target(btnExpense)
                                .lineAnimDuration(400)
                                .lineAndArcColor(getResources().getColor(R.color.red))
                                .dismissOnTouch(true)
                                .setListener(new SpotlightListener() {
                                    @Override
                                    public void onUserClicked(String s) {
                                        new SpotlightView.Builder((Activity) ctx)
                                                .introAnimationDuration(400)
                                                .enableRevealAnimation(true)
                                                .performClick(true)
                                                .fadeinTextDuration(400)
                                                .headingTvColor(getResources().getColor(R.color.white))
                                                .headingTvSize(32)
                                                .headingTvText("Savings Panel")
                                                .subHeadingTvColor(getResources().getColor(R.color.white))
                                                .subHeadingTvSize(16)
                                                .subHeadingTvText("Tap this panel to set and view savings.")
                                                .maskColor(Color.parseColor("#dc000000"))
                                                .target(cardViewSavings)
                                                .lineAnimDuration(400)
                                                .lineAndArcColor(getResources().getColor(R.color.green))
                                                .dismissOnTouch(true)
                                                .setListener(new SpotlightListener() {
                                                    @Override
                                                    public void onUserClicked(String s) {
                                                        new SpotlightView.Builder((Activity) ctx)
                                                                .introAnimationDuration(400)
                                                                .enableRevealAnimation(true)
                                                                .performClick(true)
                                                                .fadeinTextDuration(400)
                                                                .headingTvColor(getResources().getColor(R.color.white))
                                                                .headingTvSize(32)
                                                                .headingTvText("Expense chart")
                                                                .subHeadingTvColor(getResources().getColor(R.color.white))
                                                                .subHeadingTvSize(16)
                                                                .subHeadingTvText("This is the graphical view of your expenses.")
                                                                .maskColor(Color.parseColor("#dc000000"))
                                                                .target(chart)
                                                                .lineAnimDuration(400)
                                                                .lineAndArcColor(getResources().getColor(R.color.green))
                                                                .dismissOnTouch(true)
                                                                .setListener(new SpotlightListener() {
                                                                    @Override
                                                                    public void onUserClicked(String s) {
                                                                        new SpotlightView.Builder((Activity) ctx)
                                                                                .introAnimationDuration(400)
                                                                                .enableRevealAnimation(true)
                                                                                .performClick(true)
                                                                                .fadeinTextDuration(400)
                                                                                .headingTvColor(getResources().getColor(R.color.white))
                                                                                .headingTvSize(32)
                                                                                .headingTvText("Date")
                                                                                .subHeadingTvColor(getResources().getColor(R.color.white))
                                                                                .subHeadingTvSize(16)
                                                                                .subHeadingTvText("Tap this area to change date.")
                                                                                .maskColor(Color.parseColor("#dc000000"))
                                                                                .target(lbldate)
                                                                                .lineAnimDuration(400)
                                                                                .lineAndArcColor(getResources().getColor(R.color.green))
                                                                                .dismissOnTouch(true)
                                                                                .setListener(new SpotlightListener() {
                                                                                    @Override
                                                                                    public void onUserClicked(String s) {

                                                                                    }
                                                                                })
                                                                                .dismissOnBackPress(true)
                                                                                .enableDismissAfterShown(true)
                                                                                .usageId("E5" + Calendar.getInstance().getTime()) //UNIQUE ID
                                                                                .show();
                                                                    }
                                                                })
                                                                .dismissOnBackPress(true)
                                                                .enableDismissAfterShown(true)
                                                                .usageId("D4" + Calendar.getInstance().getTime()) //UNIQUE ID
                                                                .show();
                                                    }
                                                })
                                                .dismissOnBackPress(true)
                                                .enableDismissAfterShown(true)
                                                .usageId("C3" + Calendar.getInstance().getTime()) //UNIQUE ID
                                                .show();
                                    }
                                })
                                .dismissOnBackPress(true)
                                .enableDismissAfterShown(true)
                                .usageId("B2" + Calendar.getInstance().getTime()) //UNIQUE ID
                                .show();
                    }
                })
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("A1" + Calendar.getInstance().getTime()) //UNIQUE ID
                .show();
    }
}