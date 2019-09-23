package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Adapters.MyBudgetPlanAdapter;
import Adapters.RemAdaptor;
import background.background;
import Class.DecimalRemover;
import Models.Category;
import Models.CategoryAmount;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Singleton.RemainingExpenseST;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;

public class Activity_my_budget_plan extends AppCompatActivity implements View.OnClickListener {
    PieChart mychart;
    AwesomeTextView btnEdit;
    ListView listView;
    MyBudgetPlanAdapter adapter;
    UserLogin user;
    String username = "";
    Context ctx;
    TextView lblTotalExpense;
    Double totalExpense = 0.0;
    ArrayList<CategoryAmount> mylist = new ArrayList<>();
    ArrayList<CategoryAmount> categoryAmounts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_budget_plan);

        initializations();
        //onclicks();

        loadLoadingView();

    }

    private void loadLoadingView() {
        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setTitle("Message");
        progressDialog.setMessage("Please wait.....");
        progressDialog.create();
        progressDialog.show();

        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                setGraph();
                loadcheckCategory();
                remaining();
                progressDialog.hide();
            }
        }.start();
    }

    private void setListview() {
        ArrayList<Category> categories = MyCategorySingleton.getInstance().getList();
        for (int i=0;i < categories.size();i++)
        {
            //Toast.makeText(ctx, categories.get(i).getPercentage()+"  --  "+mylist.get(i).getAmount(), Toast.LENGTH_SHORT).show();
            categories.get(i).setRemaining(mylist.get(i).getAmount());
        }

        adapter = new MyBudgetPlanAdapter(Activity_my_budget_plan.this,categories);
        listView.setAdapter(adapter);
    }

    private void setGraph() {

        List<PieEntry> pieEntryList = new ArrayList<>();

        for (int i = 0; i < MyCategorySingleton.getInstance().getList().size(); i++)
        {
            Float perc = Float.parseFloat(Double.toString(MyCategorySingleton.getInstance().getList().get(i).getPercentage()));
            String lbl = MyCategorySingleton.getInstance().getList().get(i).getCategoryName();
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
        mychart.setCenterText("Total Income\nPhp "+ methods.formatter.format(IncomeSingleton.getInstance().getAllIncome()));
        mychart.setCenterTextColor(getResources().getColor(R.color.green));
        mychart.setCenterTextSize(15f);



    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }


    private void initializations() {
        //getCategoryAmount.php?username
        username = getPreference("username");
        ctx = Activity_my_budget_plan.this;
        stopService(new Intent(this, background.class));
        user = UserLogin.getInstance();
        mychart = findViewById(R.id.myChart_budget_plan);
        listView = findViewById(R.id.my_budget_plan_listview);
        lblTotalExpense = findViewById(R.id.lblTotalExpense);

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
        tv.setText("My Budget Plan"); // ActionBar title text
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setTypeface(typeface, typeface.ITALIC);
        actionBar.setCustomView(tv);

        categoryAmountList();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mybudget_menu, menu);
        return true;
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
        if (id == R.id.my_edit_bplan)
        {
            Intent intent = new Intent(this, Activity_editbudgetplan.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.my_showrem_bplan)
        {
            showRemainingDialogue();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
    }
    public void categoryAmountList()
    {
        totalExpense = 0.0;
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
                            totalExpense += c.getAmount();

                            RemainingExpenseST.getInstance().add(c);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    lblTotalExpense.setText("Php "+methods.formatter.format(totalExpense));
                    }
                    public void onError(ANError error) {

                    }
                });
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
    public Boolean check(int id)
    {
        for (CategoryAmount c:categoryAmounts) {
            if (id == c.getCategoryId() )
                return true;
        }
        return false;
    }
    public void showRemainingDialogue()
    {
        RemainingExpenseST.resetInstance();
        ArrayList<CategoryAmount> mylist = new ArrayList<>();

        for (int i =0; i<MyCategorySingleton.getInstance().getList().size();i++)
        {
            Category category = MyCategorySingleton.getInstance().getList().get(i);
            if (!check(category.getId()))
            {
                CategoryAmount camount = new CategoryAmount();
                camount.setCategoryName(""+category.getCategoryName());
               // camount.setRemPercentage(category.getPercentage()+"/"+category.getPercentage());
                camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                camount.setAmount(methods.amount(category.getPercentage()));

                mylist.add(camount);
                RemainingExpenseST.getInstance().getList().add(camount);
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

                    mylist.add(camount);
                    RemainingExpenseST.getInstance().getList().add(camount);
                }
            }
        }
        RemAdaptor Readaptor = new RemAdaptor(this,mylist);

        final Dialog dialog = new Dialog(this,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_my_bplan_remaining);
        ListView listView =dialog.findViewById(R.id.dialogue_listview_remaining_bplan);

        listView.setAdapter(Readaptor);
        dialog.setCancelable(true);
        dialog.create();
        dialog.show();
    }

    private void remaining()
    {

        RemainingExpenseST.resetInstance();
        for (int i =0; i<MyCategorySingleton.getInstance().getList().size();i++)
        {
            Category category = MyCategorySingleton.getInstance().getList().get(i);
            if (!check(category.getId()))
            {
                CategoryAmount camount = new CategoryAmount();
                camount.setCategoryName(""+category.getCategoryName());
                camount.setRemPercentage(methods.formatter.format(methods.amount(category.getPercentage())));
                camount.setAmount(methods.amount(category.getPercentage()));

                mylist.add(camount);
                RemainingExpenseST.getInstance().getList().add(camount);
            }
            for (CategoryAmount c:categoryAmounts) {
                if (category.getId() == c.getCategoryId() )
                {
                    try
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

                        mylist.add(camount);
                        RemainingExpenseST.getInstance().getList().add(camount);
                    }
                    catch (NumberFormatException ex)
                    {
                        Toast.makeText(ctx, ""+ex, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
        setListview();
    }
}
