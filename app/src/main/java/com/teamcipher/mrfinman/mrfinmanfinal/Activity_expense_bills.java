package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.Activity;
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
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
import com.mvc.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.BorrowCatAdaptor;
import Adapters.CategoryGridviewAdapter;
import Adapters.RemAdaptor;
import Models.Category;
import Models.CategoryAmount;
import Models.Result;
import Singleton.BorrowTotalSingleton;
import Singleton.ChoosenDateST;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.SavingSingleton;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.customMethod;
import Utils.message;
import Utils.methods;
import Utils.notify;
import retrofit2.Call;
import retrofit2.Callback;

public class Activity_expense_bills extends AppCompatActivity implements View.OnClickListener {
    DatePickerDialog.OnDateSetListener mdatelistener;
    ProgressDialog progressDialog;
    ImageView imgReciept;
    GridView gridView;
    TextView date_now;
    Double incomeTotal = 0.0;
    Date date;
    Context ctx;
    SimpleDateFormat df;
    Calendar calendar;
    ImageView backspace;
    EditText txtamount,txtDesc;
    Button btnsave;
    int nPrevSelGridItem = -1;
    Double inExpense = 0.0,budgetAmount = 0.0;
    String budgetType = "";
    Double amtBorrow = 0.0,amountLack = 0.0;

    String categoryiconSelected = "", categoryNameSelected = "",username = "",amount ="",datePick = "";
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    int selectedID = -1;
    ArrayList<Category> categories = new ArrayList<>();
    CategoryGridviewAdapter adapter;
    Button txtImgreceipt;
    Bitmap btmapImageResult = null;
    String dtChoosen ="";
    int goalId = 0;
    Double amtRemaining = 0.0;
    int userId = 0;
    public UserLogin user = UserLogin.getInstance();
    Bundle bundle;
    TextView lbl_budget_type,lbl_budget_amount_alocated,lbl_budget_rem;
    String update_amount = "",update_category="",update_user="",update_id="",error_message= "";
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        loadcheckCategory();

        initialization();

        backspace.setOnClickListener(this);
        btnsave.setOnClickListener(this);

        txtImageFunctions();
        datePickDateExpense();
        onEdit();
        populateCategories();
        popDdate();
        gridViewListener();
        txtAmountFocusFunction();
        categoryAmountList();
        remaining();


        getBudgetExpense();
    }

    private void datePickDateExpense() {
        date_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar();
            }
        });
    }

    private void txtAmountFocusFunction() {
        txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                try{
                    if (!b)
                    {
                        String strAmount = txtamount.getText().toString().replace(",","");
                        if (strAmount.equals(""))
                            txtamount.setError(null);
                        else if (strAmount != "")
                        {
                            amount = strAmount;
                            if (Double.parseDouble(amount) > 10)
                            {
                                txtamount.setText(""+methods.formatter.format(Double.parseDouble(strAmount)));
                            }
                            else
                            {
                                txtamount.setError("Amount must be greater than Php 10.00!");
                            }
                        }
                    }
                }catch (Exception ex)
                {
                    message.error(""+ex,ctx);
                }
            }
        });
    }

    private void txtImageFunctions() {
        txtImgreceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strAmount = txtamount.getText().toString().replace(",","");
                if (strAmount.equals(""))
                    txtamount.setError(null);
                else if (strAmount != "")
                {
                    amount = strAmount;
                    txtamount.setError("Amount must be greater than Php 10.00!");
                }

                methods.vibrate(ctx);
                ImagePicker.pickImage(Activity_expense_bills.this, "Select image receipt:");
            }
        });
        imgReciept.bringToFront();
        imgReciept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(Activity_expense_bills.this);
                dialog.setContentView(R.layout.dialogue_show_image);
                ImageView imageView = dialog.findViewById(R.id.show_image_file);
                if (btnsave.getText().toString().equals("UPDATE"))
                {
                    Picasso.get().load(methods.RECEIPT_SERVER+""+bundle.getString("imgReceipt")).into(imgReciept);
                }
                else
                    imageView.setImageBitmap(btmapImageResult);
                dialog.show();
            }
        });

    }

    private void onEdit() {
        if (bundle != null )
        {
            //txtImgreceipt.setEnabled(false);
            Date dt = new Date(""+bundle.getString("targetDate"));
            calendar.setTime(dt);
            df = new SimpleDateFormat("dd MMMM, yyyy");
            String fDate = df.format(calendar.getTime());
            date_now.setText(fDate);
            txtamount.setText(""+bundle.getString("amount"));
            txtDesc.setText(""+bundle.getString("description"));
            selectedID = Integer.parseInt(bundle.getString("categoryId"));
            txtamount.setEnabled(false);
            goalId = Integer.parseInt(""+bundle.getString("goalId"));

          // gridView.setSelection(0);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        btmapImageResult = ImagePicker.getImageFromResult(this, requestCode, resultCode, data);
        imgReciept.setImageBitmap(btmapImageResult);
    }

    private void popDdate() {
        date = calendar.getTime();
        df = new SimpleDateFormat("dd MMMM, yyyy");
        String fDate = df.format(date);
        date_now.setText(fDate);
    }
    private void showCalendar() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        mdatelistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //month = month +1;
                calendar.set(year,month,day);
                Calendar calendar_current = Calendar.getInstance();
                calendar_current.add(Calendar.DATE,-1);

                datePick =  methods.date.format(calendar.getTime());
                calendar.set(year,month,day);
                Calendar current = Calendar.getInstance();
                if (calendar.before(current))
                {
                    date_now.setText(df.format(calendar.getTime()));
                }
                else
                {
                    message.warning("Future date not available!",ctx);
                    calendar = Calendar.getInstance();
                    date_now.setText(df.format(calendar.getTime()));
                }

            }
        };
        DatePickerDialog dialog = new DatePickerDialog(Activity_expense_bills.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,mdatelistener,year,month,day);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_expense_menu, menu);
        return true;
    }


    private void populateCategories() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"getUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i =0 ; i<jsonArray.length();i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        Category c1 = new Category();
                        c1.setCategoryName(j.getString("Name"));
                        c1.setIcon(j.getString("Icon"));
                        c1.setId(j.getInt("catID"));


                        if (c1.getId() == selectedID )
                            categories.add(c1);

                    }
                    adapter = new CategoryGridviewAdapter(Activity_expense_bills.this,categories);
                    gridView.setAdapter(adapter);

                    //gridView.setSelected(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING"," Expense  "+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateGoal(int id)
    {
        Call<Result> updateToGoalAchieved = apIservice.updateGoalStatus(id);
        updateToGoalAchieved.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                if (response.isSuccessful())
                {
                    if (response.body().getCode() == 0)
                        Logs.LOGS("Goal updateToGoalAchieved  ERROR");
                    if (response.body().getCode() == 1)
                        Logs.LOGS("Goal updateToGoalAchieved Success");
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }



    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {
        try
        {

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowCustomEnabled(true);

            ActionBar actionBar = getSupportActionBar();
            TextView tv = new TextView(getApplicationContext());
            Typeface typeface = ResourcesCompat.getFont(this, R.font.dancingfont);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
            tv.setLayoutParams(lp);
            tv.setText("New Expense"); // ActionBar title text
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(25);
            tv.setTypeface(typeface, typeface.ITALIC);
            actionBar.setCustomView(tv);



        }
        catch (Exception ex)
        {

        }
        userId  = Integer.parseInt(getPreference("userID"));
        ImagePicker.setMinQuality(600, 600);
        ctx = Activity_expense_bills.this;

        imgReciept = findViewById(R.id.img_expense_receipt);
        txtImgreceipt = findViewById(R.id.txt_expense_camera);
        gridView = findViewById(R.id.grid_view_category);
        date_now = findViewById(R.id.expense_date_now);
        backspace = findViewById(R.id.expense_backspace);
        txtamount = findViewById(R.id.txt_expense_amount);
        txtDesc = findViewById(R.id.txt_expense_note);
        btnsave = findViewById(R.id.btn_expense_save);
        username = getPreference("username");


        calendar= ChoosenDateST.getInstance().getDate();

        bundle = getIntent().getExtras();
        incomeTotal = IncomeSingleton.getInstance().getAllIncome();
        lbl_budget_type = findViewById(R.id.lbl_budget_type);
        lbl_budget_amount_alocated = findViewById(R.id.lbl_budget_alloc);
        lbl_budget_rem = findViewById(R.id.lbl_budget_rem);

    }

    private void setBudgetAllocation(Double amt) {
        try
        {
            String type = customMethod.getPreference((Activity) ctx, username, "budgetType");
            if (!TextUtils.isEmpty( type.toString()))
                lbl_budget_type.setText(""+type);
            else
                lbl_budget_type.setText("Not Set");


            String amountAloc = customMethod.getPreference((Activity) ctx, username, "budgetAmount");
            if (!TextUtils.isEmpty( amountAloc.toString()))
                lbl_budget_amount_alocated.setText("Php "+methods.formatter.format(Double.parseDouble(amountAloc)));//+methods.formatter.format(amountAloc)
            else
                lbl_budget_amount_alocated.setText("Php 0.00");

            try
            {
                Double bal = Double.parseDouble(amountAloc) - amt;
                if (bal < 0 )
                    lbl_budget_rem.setText("Php 0.00");
                else
                    lbl_budget_rem.setText("Php "+methods.formatter.format(bal));

            }
            catch (Exception ex)
            {
                //Toast.makeText(ctx, "Error in Rem "+ex, Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception ex)
        {
            lbl_budget_type.setText("");
            lbl_budget_type.setText("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.expense_backspace:
                eraseTxt();
                break;
            case R.id.btn_expense_save:
                methods.vibrate(ctx);
                if (!(txtamount.getText().toString().equals("")))
                {
                    if (selectedID != -1)
                        {
                            if (btnsave.getText().toString().equals("UPDATE"))
                                onUpdate();
                            else
                            {
                                if (checkExpense(selectedID))
                                {
                                    if (!(budgetAmount <= 0.0)) {
                                        if ((inExpense + Double.parseDouble(txtamount.getText().toString().replace(",", "")) > budgetAmount)) {
                                            message.warning("You have only set your " + budgetType + " budget to Php" + methods.formatter.format(budgetAmount) + "!\n\nWould you still continue?",ctx);
                                            AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                                    .setIcon(android.R.drawable.ic_dialog_info)
                                                    .setTitle("Warning")
                                                    .setMessage("You have only set your " + budgetType + " budget to Php" + methods.formatter.format(budgetAmount) + "!\n\nWould you still continue?")
                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            onsave();
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
                                        } else {
                                            onsave();
                                        }
                                    }
                                    else {
                                        onsave();
                                    }
                                }
                                else
                                {
                                    message.warning(error_message+"You're out of budget for "+categoryNameSelected+"! \n\nWould you like to borrow?",ctx);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                                    alertDialogBuilder.setTitle("Out of Budget!");
                                    alertDialogBuilder.setMessage(error_message+"You're out of budget for "+categoryNameSelected+"! \n\nWould you like to borrow?");
                                    alertDialogBuilder.setCancelable(false);
                                    alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
                                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            double txtAmtInputDouble = Double.parseDouble(txtamount.getText().toString().replace(",",""));
                                            amountLack = (txtAmtInputDouble - amtRemaining);
                                            message.warning("You still need Php "+methods.formatter.format(amountLack)+" to continue this transaction.",ctx);
                                            showBorrowDialogue();
                                        }


                                    });
                                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    alertDialogBuilder.show();
                                }

                            }
                        }
                        else
                        {
                            message.error("Please select category!",ctx);
                        }

                }
                else
                {
                    txtamount.setError("Not valid amount!");
                }

                break;
        }
    }

    private void showBorrowDialogue() {
            final Dialog dialog = new Dialog(ctx);
            dialog.setContentView(R.layout.dialogue_user_borrow_menu);
            dialog.setCancelable(false);
            Button btnFrSavings,btnOtherCat;
            btnFrSavings = dialog.findViewById(R.id.btnFromSavings);
            btnOtherCat = dialog.findViewById(R.id.btnFromOtherCategory);
            ImageView btnClose = dialog.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnFrSavings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Double savings = SavingSingleton.getInstance().getAllSavings();
                    Double amountInputed = Double.parseDouble(txtamount.getText().toString().replace(",",""));


                    String str = amtRemaining+" + "+savings+" >= "+amountInputed;
                    Log.d("Testing",str);
                    if ((amtRemaining + savings) > amountInputed )
                        showBorrowSavings();
                    else
                    {
                        message.success("Not Enough Savings!\nYou have only a total of Php"+methods.formatter.format(savings)+" in your savings!.",ctx);
                    }
                }
            });
            btnOtherCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOtherCatAvailable()) {
                        showDialogBorrowCat();
                    }
                    else
                    {
                        message.warning("Not Enough Remaining Balance!",ctx);
                    }
                }
            });
            dialog.create();
            dialog.show();

    }
    private Boolean isOtherCatAvailable()
    {
        Double total = 0.0;
        for (CategoryAmount cmt: mylist)
        {
            total += cmt.getAmount();
        }
        if (Double.parseDouble(txtamount.getText().toString().replace(",","")) <= total)
            return true;

        return false;
    }
    private void showBorrowSavings() {
            Dialog dia = new Dialog(ctx);
            dia.setContentView(R.layout.dialogue_borrow_saving);
            dia.setCancelable(true);

            final EditText txtSavingsAmount = dia.findViewById(R.id.txtamount);
            TextView lblmessage = dia.findViewById(R.id.lblmsg);
            Button btnSave = dia.findViewById(R.id.btnsave);
            lblmessage.setText("You have only a total of Php "+ methods.formatter.format(SavingSingleton.getInstance().getAllSavings())+" from your Savings!.");
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Message")
                            .setMessage("Are you sure you want to continue?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    final String amt = txtSavingsAmount.getText().toString();
                                    if (!(TextUtils.isEmpty(amt)))
                                    {
                                        if (!(Double.parseDouble(amt) <= 10))
                                        {
                                            if (SavingSingleton.getInstance().getAllSavings() >= Double.parseDouble(amt))
                                            {

                                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
                                                alertDialogBuilder.setTitle("Confirmation");
                                                alertDialogBuilder.setMessage("Are you sure you want to continue?");
                                                alertDialogBuilder.setCancelable(false);
                                                alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
                                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Toast.makeText(Activity_expense_bills.this, "Canceled!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        borrowSavings(amt);
                                                        updateCategoryUponBorrowSavings(Double.parseDouble(amt));
                                                    }
                                                });

                                                alertDialogBuilder.create();
                                                alertDialogBuilder.show();
                                            }
                                            else
                                                Toast.makeText(Activity_expense_bills.this, "Not enough savings! ", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                            Toast.makeText(Activity_expense_bills.this, "Minimum amount to be borrow is Php 10.00!", Toast.LENGTH_SHORT).show();
                                    }else
                                        txtSavingsAmount.setError("!");
                                }

                                private void borrowSavings(String amt) {
                                    //Insert into History
                                    String details = "Borrow Php "+methods.formatter.format(Double.parseDouble(amt))+" from savings";

                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                    date = calendar.getTime();
                                    String fDate = df.format(date);

                                    Map<String,String> params = new HashMap<>();
                                    params.put("histname","Borrow "+methods.formatter.format(Double.parseDouble(amt))+ " Saving");
                                    params.put("histDetails",details);
                                    params.put("dateCreated",fDate);
                                    params.put("icon","borrow.png");
                                    params.put("userId",""+userId);
                                    params.put("type","Borrow Savings");

                                    notify.addtoHistory(ctx,params);
                                    String amountBorrowed = methods.formatter.format(Double.parseDouble(txtSavingsAmount.getText().toString()));
                                    updateSavings(username,txtSavingsAmount.getText().toString());
                                    String desc = "Borrow amount Php "+amountBorrowed+" from savings for "+categoryNameSelected+".";
                                    saveSavingsToIncome(""+methods.formatter.format(Double.parseDouble(amt)),desc);

                                    String description = "Expense in "+categoryNameSelected;

                                    Map<String,String> param = new HashMap<>();
                                    param.put("histname",description+"");
                                    param.put("histDetails","Borrow amount of Php "+methods.formatter.format(Double.parseDouble(amountBorrowed))+" from savings for the "+categoryNameSelected+".");
                                    param.put("dateCreated",fDate);
                                    param.put("icon","othercat.png");
                                    param.put("userId",""+userId);
                                    param.put("type","Borrow Other Category");
                                    notify.addtoHistory(ctx,param);

                                    Double totalAmount = Double.parseDouble(txtamount.getText().toString().replace(",",""));
                                    saveExpense(details+" "+txtDesc.getText().toString(),totalAmount);

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
            dia.create();
            dia.show();

    }

    private void updateSavings(String username,String amount) {
        AndroidNetworking.get(methods.USER_API_SERVER+"borrowSavings.php?todo=BORROW&username="+username+"&amount="+amount+"")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        //Utils.message.error(""+error.getErrorBody(),ctx);
                    }
                });
    }

    private void showDialogBorrowCat() {
        Dialog dialog = new Dialog(ctx);
        dialog.setContentView(R.layout.dialogue_borrow_other_cat);
        ListView listView = dialog.findViewById(R.id.listView);
        TextView lblTo = dialog.findViewById(R.id.lblinto);

        final ArrayList<CategoryAmount> list = getCategoryAmountList(mylist);
        BorrowCatAdaptor adaptor = new BorrowCatAdaptor(this,list,selectedID);
        listView.setAdapter(adaptor);
        final TextView lbltotalBorrow = dialog.findViewById(R.id.lblTotalBorrow);

        Button btnOk = dialog.findViewById(R.id.btnOk);
        lblTo.setText(""+categoryNameSelected);


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                alert.setTitle("Message");
                alert.setMessage("Are you sure you want to continue?");
                alert.setCancelable(false);
                alert.setIcon(R.drawable.ic_info_outline_white_48dp);
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ctx, "Canceled!", Toast.LENGTH_SHORT).show();
                    }
                });
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (!(BorrowTotalSingleton.getInstance().getHasError()))
                                    save();
                                else
                                {
                                    message.warning("Amount Borrow not valid!",ctx);
                                }

                            }
                            private void save() {
                                methods.vibrate(ctx);
                                Double total = 0.0;
                                String description = "Borrow from ";
                                Category selected = getObject(selectedID);
                                ArrayList<Category> finalCategory = new ArrayList<>();
                                Log.d("BORROW", "After Borrow");
                                for (CategoryAmount cmt: list)
                                {
                                    if (cmt.getCategoryId() != selectedID)
                                    {
                                        total += cmt.getAmountBorrow();
                                        if (!(cmt.getAmountBorrow() <= 0))
                                        {
                                            Log.d("BORROW","***** "+cmt.getCategoryName()+" ***** "+cmt.getAmountBorrow());
                                            Category category = new Category();
                                            description += " "+cmt.getCategoryName()+"="+cmt.getAmountBorrow()+",";

                                            Double amtFinal = Double.parseDouble(cmt.getRemPercentage().replace(",", "")) - cmt.getAmountBorrow();
                                            Double percentage = Double.parseDouble(methods.percentage(amtFinal));
                                            cmt.setPercentage(percentage);

                                            Double AmtaddToSelected = methods.amount(selected.getPercentage()) + cmt.getAmountBorrow();
                                            Double selectedPerc = Double.parseDouble(methods.percentage(AmtaddToSelected));
                                            selected.setPercentage(selectedPerc);

                                            category.setId(cmt.getCategoryId());
                                            category.setCategoryName(cmt.getCategoryName());
                                            category.setIcon(""+getIcon(cmt.getCategoryId()));
                                            category.setPercentage(percentage);
                                            finalCategory.add(category);
                                        }
                                    }
                                }
                                description += "";

                                finalCategory.add(selected);
                                for (Category cmt: finalCategory)
                                {
                                    addToMyCategoy(""+cmt.getId(),user.getUser_ID(),cmt.getPercentage(),0);
                                }

                                String strNote = txtDesc.getText().toString()+" ,"+description;
                                Double totalAmount = Double.parseDouble(txtamount.getText().toString().replace(",","")) + amountLack;

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                                date = calendar.getTime();
                                String fDate = df.format(date);

                                Map<String,String> params = new HashMap<>();
                                params.put("histname","Borrow from Savings");
                                params.put("histDetails","Borrow amount of Php "+methods.formatter.format(total)+" for the "+categoryNameSelected+".");
                                params.put("dateCreated",fDate);
                                params.put("icon","othercat.png");
                                params.put("userId",""+UserLogin.getInstance().getUser_ID());
                                params.put("type","Borrow Other Category");
                                notify.addtoHistory(ctx,params);

                                saveExpense(strNote,totalAmount);

                                //Expense History
                                Map<String,String> param = new HashMap<>();
                                param.put("histname","Expense to "+categoryNameSelected);
                                param.put("histDetails","Expense to "+categoryNameSelected+" a total of Php"+methods.formatter.format(totalAmount));
                                param.put("dateCreated",fDate);
                                param.put("icon",""+getIcon(selectedID));
                                param.put("userId",""+UserLogin.getInstance().getUser_ID());
                                param.put("type","Expense");
                                notify.addtoHistory(ctx,param);


                            }
                        }
                );
                alert.create();
                alert.show();
                }


        });
        dialog.create();
        dialog.show();

        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        String amount = BorrowTotalSingleton.getInstance().getTotal().toString();
                        if (!(TextUtils.isEmpty(amount))) {
                            Double value = 0.0;
                            for (CategoryAmount cmt: list)
                            {
                                if (cmt.getAmountBorrow() != 0 )
                                    value += cmt.getAmountBorrow();
                            }
                            lbltotalBorrow.setText("" + methods.formatter.format(value));
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 1000, 2000);
    }



    private ArrayList<CategoryAmount> getCategoryAmountList(ArrayList<CategoryAmount> mylist)
    {
        ArrayList<CategoryAmount> list = mylist;
        for (CategoryAmount cmt: mylist)
        {
            cmt.setIcon(""+getIcon(cmt.getCategoryId()));
        }
        return list;
    }
    private String getIcon(int id)
    {
        for (Category cmt: MyCategorySingleton.getInstance().getList())
        {
            if (id == cmt.getId())
                return cmt.getIcon();
        }
        return null;
    }

    private void saveSavingsToIncome(final String amount,final String note) {
        final String amountDetails = "amount-"+amount+"|type-Savings";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"save_income.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = Calendar.getInstance().getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("type","insert");
                params.put("userID",""+userId);
                params.put("categoryID","27");
                params.put("payment","Additional");
                params.put("dateCreated",fDate);
                params.put("amount",amount);
                params.put("noteDesc",note);
                params.put("amountDetails",amountDetails);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            Intent intent = new Intent(this,Activity_dashboard.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.balance) {
            showRemainingDialogue();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showRemainingDialogue()
    {
        RemainingExpenseST.resetInstance();

        RemAdaptor Readaptor = new RemAdaptor(this,mylist);

        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_my_bplan_remaining);
        ListView listView =dialog.findViewById(R.id.dialogue_listview_remaining_bplan);

        listView.setAdapter(Readaptor);
        dialog.setCancelable(true);
        dialog.create();
        dialog.show();
    }

    private void loadcheckCategory()
    {
        if (MyCategorySingleton.getInstance().getList().size() == 0)
            promptAddCategory();

    }

    private void promptAddCategory() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("Oops you have no categories!\nContinue to add category");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplication(), Activity_add_category.class);
                startActivity(intent);
                finish();
            }
        });
        alertDialogBuilder.show();
    }

    private void onsave() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to save this expense?");
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (selectedID != -1)
                    loadingDialogue();
                else
                    message.error("Please select category!",ctx);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.WHITE);
        ba.setTextColor(Color.WHITE);
    }

    private void onUpdate() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Confirm");
        alertDialogBuilder.setMessage("Are you sure you want to save changes?");
        alertDialogBuilder.setIcon(R.drawable.ic_info_outline_white_48dp);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (selectedID != -1)
                    loadingDialogue();
                else
                    message.error("Please select category!",ctx);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button ba = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        bq.setTextColor(Color.WHITE);
        ba.setTextColor(Color.WHITE);
    }
    private void eraseTxt()
    {
        try
        {
            String temp = txtamount.getText().toString();
            txtamount.setText(temp.substring(0,temp.length()-1));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    private void gridViewListener() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            View viewPrev;
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                try {
                    if (nPrevSelGridItem != -1) {
                        viewPrev = gridView.getChildAt(nPrevSelGridItem);
                        viewPrev.setBackgroundColor(Color.TRANSPARENT);
                    }
                    nPrevSelGridItem = position;
                    if (nPrevSelGridItem == position) {
                        //View viewPrev = (View) gridview.getChildAt(nPrevSelGridItem);
                        categoryNameSelected = categories.get(position).getCategoryName();
                        categoryiconSelected = categories.get(position).getIcon().toString();
                        selectedID = categories.get(position).getId();
                        view.setBackgroundColor(getResources().getColor(R.color.light_grey));

                        adapterView.showContextMenuForChild(view);
                        if (categories.get(position).getId() != 0) {
                            String strAmount = txtamount.getText().toString().replace(",","");
                            if (strAmount.equals(""))
                                txtamount.setError(null);
                            else if (strAmount != "")
                            {
                                amount = strAmount;
                                if (Double.parseDouble(amount) > 10)
                                {
                                    txtamount.setText(""+methods.formatter.format(Double.parseDouble(strAmount)));
                                }
                                else
                                {
                                    txtamount.setError("Amount must be greater than Php 10.00!");
                                }
                            }
                            Alerter.create(Activity_expense_bills.this).setTitle("Category").setBackgroundColorRes(R.color.gray).setIcon(R.drawable.ic_done).setText(categoryNameSelected + "").show();
                        }
                        else
                        {
                            selectedID = -1;
                            //message.info("Oops!\nYou must be a premium user!",getBaseContext());
                            for(CategoryAmount cmt:mylist)
                            {
                                Log.d("TESTING",cmt.getRemPercentage()+"-"+cmt.getAmount());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void loadingDialogue()
    {
        progressDialog = new ProgressDialog(Activity_expense_bills.this);
        progressDialog.setMessage("Saving...\nPlease wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if(btnsave.getText().toString().equals("UPDATE"))
                    UpdateExpense();
                else
                {
                    if (checkExpense(selectedID))
                    {
                        if (!isPriority())
                        {
                            progressDialog.hide();
                            Alerter.create((Activity)ctx)
                                    .setTitle("Mr.FinMan")
                                    .setText("Warning\n"+categoryNameSelected+" is not your priority!")
                                    .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                                    .setDuration(5000)
                                    .setIcon(R.drawable.ic_warning_outline_white)
                                    .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_warning))
                                    .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            saveExpense();
                                            Alerter.hide();
                                            updateGoal(goalId);
                                        }
                                    }).show();
                        }
                        else
                        {
                            saveExpense();
                        }
                    }
                    else
                    {

                        progressDialog.hide();
                        Alerter.create((Activity)ctx)
                                .setTitle("Mr.FinMan")
                                .setText(error_message)
                                .setIcon(R.drawable.ic_info_outline_black_24dp)
                                .setBackgroundColorInt(getResources().getColor(R.color.bootstrap_brand_danger))
                                .show();
                    }
                }
            }
        }.start();
    }

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


    private void getBudgetExpense()
    {
        budgetType = customMethod.getPreference(Activity_expense_bills.this, username, "budgetType");
        String dt = "";
        SimpleDateFormat month = new SimpleDateFormat("MM/yyyy");
        if (!TextUtils.isEmpty(budgetType))
        {
            budgetAmount = Double.parseDouble(customMethod.getPreference(Activity_expense_bills.this, username, "budgetAmount"));

            if (budgetType.equals("Daily"))
                dt = methods.date.format(Calendar.getInstance().getTime());
            if (budgetType.equals("Weekly"))
            {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                cal.set(Calendar.DAY_OF_WEEK, cal.MONDAY);
                String firstWkDay = String.valueOf(cal.getTime());
                dt +=  methods.date.format(cal.getTime());

                //cal.set(Calendar.DAY_OF_WEEK, cal.SUNDAY);
                cal.add(Calendar.DAY_OF_WEEK, 6);
                String lastWkDay =  String.valueOf(cal.getTime());
                dt += " "+methods.date.format(cal.getTime());

            }
            if (budgetType.equals("Monthly"))
                dt = month.format(Calendar.getInstance().getTime());

            AndroidNetworking.get(methods.USER_API_SERVER+"budgetCheck.php?username="+username+"&type="+budgetType.toUpperCase()+"&date="+dt+"")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {

                            try {
                                JSONObject jObj = response.getJSONObject(0);
                                inExpense = jObj.getDouble("amount");
                                setBudgetAllocation(inExpense);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        public void onError(ANError error) {
                            Log.d("TESTING"," Expense  "+error);
                        }
                    });


            //Budget Display

        }
        else
        {
            budgetType = "null";
        }


    }
    public Boolean check(int id)
    {
        for (CategoryAmount c:RemainingExpenseST.getInstance().getList()) {
            if (id == c.getCategoryId() )
                return true;
        }
        return false;
    }




    private Category getObject(int id) {
        for (Category cat:MyCategorySingleton.getInstance().getList() ) {
            if (cat.getId() == id)
                return cat;
        }
        return null;
    }



    private Boolean checkExpense(int categoryId) {
        try
        {
            String amnt = txtamount.getText().toString().replace(",","");
            for (CategoryAmount cmt : mylist)
            {
                Log.d("TESTING",categoryId+"  category Id: "+cmt.getCategoryId());
                if (cmt.getCategoryId() == categoryId)
                {
                    Double amount =Double.parseDouble(amnt);
                    Double alloAmt = cmt.getAmount();
                    String amtOver = cmt.getRemPercentage();
                    //Log.d("TESTING",amount+" <= "+alloAmt);
                    if( amount <= alloAmt)
                    {
                        return true;
                    }
                    else
                    {
                        error_message = "You only have Php "+methods.formatter.format(alloAmt)+" out of Php "+amtOver+" budget for this category!\n\n";
                        amtRemaining = alloAmt;
                        return false;
                    }
                }
            }
        }
        catch(Exception ex)
        {
            Toast.makeText(ctx, "Error in Parse "+ex, Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    public void waitload()
    {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(),Activity_dashboard.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    private void saveExpense()
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
                        message.error(jsonObject.getString("message"),ctx);
                        progressDialog.hide();

                    }
                    else if (jsonObject.getInt("code") == 1)
                    {
                        message.success(jsonObject.getString("message"),ctx);

                        waitload();
                        progressDialog.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.error(e.toString(),ctx);
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               /* Alerter.create(Activity_expense.this).setText("NO INTERNET CONNECTION!\n"+error)
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();*/
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = calendar.getTime();
                String fDate = df.format(date);
                String amnt = txtamount.getText().toString().replace(",","");

                Map<String,String> params = new HashMap<>();
                params.put("userID",String.valueOf(userId));
                params.put("categoryID",Integer.toString(selectedID));
                params.put("amount",amnt);
                params.put("note",txtDesc.getText().toString());
                params.put("dateCreated",fDate);
                if (btmapImageResult == null)
                    params.put("imgReceipt","NONE");
                else
                    params.put("imgReceipt",""+imageToString(btmapImageResult));

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }

    private void saveExpense(final String note, final Double totalAmt) {
        progressDialog = new ProgressDialog(Activity_expense_bills.this);
        progressDialog.setMessage("Saving...\nPlease wait....");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "save_expense.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                        try {
                            // Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                            JSONArray jsonA = new JSONArray(response);
                            JSONObject jsonObject = jsonA.getJSONObject(0);

                            if (jsonObject.getInt("code") == 0) {
                                message.error(jsonObject.getString("message"), ctx);
                                progressDialog.hide();

                            } else if (jsonObject.getInt("code") == 1) {
                                message.success(jsonObject.getString("message"), ctx);
                                waitload();
                                progressDialog.hide();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("TESTING"," Expense  "+e);
                            progressDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR","Save Expense"+error);
                        progressDialog.hide();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                        date = calendar.getTime();
                        String fDate = df.format(date);
                        String amnt = txtamount.getText().toString().replace(",", "");

                        Map<String, String> params = new HashMap<>();
                        params.put("userID", Integer.toString(userId));
                        params.put("categoryID", Integer.toString(selectedID));
                        params.put("amount", String.valueOf(totalAmt));
                        params.put("note", note);
                        params.put("dateCreated", fDate);
                        if (btmapImageResult == null)
                            params.put("imgReceipt", "NONE");
                        else
                            params.put("imgReceipt", "" + imageToString(btmapImageResult));

                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(ctx);
                requestQueue.add(stringRequest);

                progressDialog.hide();

            }

        }.start();

    }

    private void UpdateExpense()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"update_Expense.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                     //Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    JSONArray jsonA = new JSONArray(response);
                    JSONObject jsonObject = jsonA.getJSONObject(0);

                    if (jsonObject.getInt("code") == 0)
                    {
                        message.error(jsonObject.getString("message"),ctx);
                        progressDialog.hide();

                    }
                    else if (jsonObject.getInt("code") == 1)
                    {
                        //Add to history
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                        Calendar calendar = Calendar.getInstance();
                        Date date = calendar.getTime();
                        String fDate = df.format(date);

                        Map<String,String> params = new HashMap<>();
                        params.put("histname","Update expense "+categoryNameSelected);
                        params.put("histDetails","");
                        params.put("dateCreated",fDate);
                        params.put("icon","food.png");
                        params.put("userId",""+ userId);
                        params.put("type","Goal Notify");
                        notify.addtoHistory(ctx,params);


                        message.success(jsonObject.getString("message"),ctx);
                        Intent intent = new Intent(ctx,Activity_transactions.class);
                        intent.putExtra("type","Day");
                        intent.putExtra("category",""+categoryNameSelected);
                        intent.putExtra("date",""+methods.date.format(calendar.getTime()));
                        startActivity(intent);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("TESTING"," Expense  "+e);
                    progressDialog.hide();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Alerter.create(Activity_expense_bills.this).setText("NO INTERNET CONNECTION!\n")
                        .setIcon(R.drawable.ic_info_outline_black_24dp)
                        .show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");
                date = calendar.getTime();
                String fDate = df.format(date);

                Map<String,String> params = new HashMap<>();
                params.put("Id",update_id);
                params.put("userID",Integer.toString(userId));
                params.put("categoryID",Integer.toString(selectedID));
                params.put("amount",amount);
                params.put("note",txtDesc.getText().toString());
                params.put("dateCreated",fDate);
                if (btmapImageResult == null)
                    params.put("imgReceipt","NONE");
                else
                    params.put("imgReceipt",""+imageToString(btmapImageResult));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);



    }
    private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream ouputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,ouputStream);
        byte[]  imageBytes = ouputStream.toByteArray();

        String encodedImage = Base64.encodeToString(imageBytes,Base64.DEFAULT);
        return  encodedImage;
    }
    private void remaining()
    {
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                mylist.clear();
                //RemainingExpenseST.resetInstance();
                for (int i =0; i<MyCategorySingleton.getInstance().getList().size();i++)
                {
                    Category category = MyCategorySingleton.getInstance().getList().get(i);
                    if (!check(category.getId()))
                    {
                        CategoryAmount camount = new CategoryAmount();
                        camount.setCategoryId( category.getId());
                        camount.setCategoryName(""+category.getCategoryName());
                        // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                        camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                        camount.setAmount(methods.amount(category.getPercentage()));

                        mylist.add(camount);
                        //RemainingExpenseST.getInstance().getList().add(camount);
                    }
                    for (CategoryAmount c:categoryAmounts) {
                        if (category.getId() == c.getCategoryId() )
                        {
                            Double amt = c.getAmount();
                            String totalExpensePercentage = methods.percentage(amt);
                            String remainingPercentage = ""+(category.getPercentage() - Double.parseDouble(totalExpensePercentage));
                            String remPerc = remainingPercentage+"/"+c.getPercentage();

                            String amountPercSet = ""+methods.amount(category.getPercentage());
                            Double remainingAmount = Double.parseDouble(amountPercSet) - c.getAmount();



                            CategoryAmount camount = new CategoryAmount();
                            camount.setCategoryName(""+category.getCategoryName());

                            camount.setRemPercentage(methods.formatter.format(amt + remainingAmount));
                            camount.setAmount(remainingAmount);

                            camount.setCategoryId( c.getCategoryId());

                            mylist.add(camount);
                        }
                    }

                }
            }
        }.start();
    }

    private void addToMyCategoy( final String catID, final int userid, final double percentage,final int isPriority)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "NewExpenseUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.toString().equals("1")) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING"," update Category expense  "+error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("catID", catID);
                params.put("user_ID", Integer.toString(userid));
                params.put("percentage", Double.toString(percentage));
                params.put("isPriority",String.valueOf(isPriority));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateCategoryUponBorrowSavings(Double amount)
    {
        Category catSelected = getCategory(selectedID);
        Log.d("LOGS","catSelected : "+catSelected.getCategoryName());
        Log.d("LOGS","Income Total : "+incomeTotal);

        //get Percentage of the selected Category
        Double perc = catSelected.getPercentage();
        Log.d("LOGS","Percentage Selected : "+perc);
        //Value
        Double amountValue = amount(perc);
        incomeTotal += amount;
        Double addAmount = amountValue + amount;
        String percNewSelected =  percentage(addAmount);

       // String percentage = methods.percentage(addAmount);
        catSelected.setPercentage(Double.parseDouble(methods.formatter00.format(Double.parseDouble(percNewSelected))));
        //Log.d("LOGS","Income "+IncomeSingleton.getInstance().getIncomeMonthly());
        String str = "";
        for (Category cmt:MyCategorySingleton.getInstance().getList())
        {
            Log.d("LOGS",cmt.getId()+" - "+cmt.getCategoryName()+" - "+cmt.getPercentage());
            //Log.d("LOGS",cmt.getId()+" - "+cmt.getCategoryName()+" - "+cmt.getPercentage()+" - "+cmt.getIcon()+" - "+methods.amount(cmt.getPercentage()));
            //str += cmt.getId()+" - "+cmt.getCategoryName()+" - "+cmt.getPercentage()+" - "+cmt.getIcon()+" - "+methods.amount(cmt.getPercentage())+" \n";
        }
        ArrayList<Category> updatedCategory = new ArrayList<>();

        updatedCategory.add(catSelected);

        for (Category cmt:MyCategorySingleton.getInstance().getList())
        {
            if (cmt.getId() != catSelected.getId())
            {
                Double amtValue = amount(cmt.getPercentage());
                String prctge = percentage(amtValue);
                cmt.setPercentage(Double.parseDouble(methods.formatter00.format(Double.parseDouble(prctge))));
                updatedCategory.add(cmt);
            }
        }
        for (Category cmt:updatedCategory)
        {
            //Log.d("LOGS",cmt.getId()+" - "+cmt.getCategoryName()+" - "+cmt.getPercentage());
            //Log.d("LOGS",cmt.getId()+" - "+cmt.getCategoryName()+" - "+cmt.getPercentage()+" - "+cmt.getIcon()+" - "+methods.amount(cmt.getPercentage()));
            addToMyCategoy(""+cmt.getId(), user.getUser_ID(),cmt.getPercentage(),0);
        }

    }

    private Category getCategory(int id)
    {
        for (Category cmt:MyCategorySingleton.getInstance().getList())
        {
            if (cmt.getId() == id)
                return cmt;
        }
        return null;
    }

    public String percentage(double item)
    {
        double total = incomeTotal;
        Double perc = 0.0;
        perc = ( item / total ) * 100;
        String value = ""+methods.formatter00.format(perc);
        return  value;
    }

    public double amount(double item)
    {
        double total = incomeTotal;
        Double val = 0.0;
        val = (item / 100) * total;
        return val;
    }

    public Boolean isPriority()
    {
        for(Category cmt: MyCategorySingleton.getInstance().getList())
        {
            if (cmt.getId() == selectedID)
            {
                if (cmt.getPriority())
                    return true;
            }
        }
        return false;
    }

}
