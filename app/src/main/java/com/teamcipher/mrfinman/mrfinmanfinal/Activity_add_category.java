package com.teamcipher.mrfinman.mrfinmanfinal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Models.Category;
import Singleton.MyCategorySingleton;
import Singleton.UserLogin;
import Utils.methods;

public class Activity_add_category extends AppCompatActivity implements View.OnClickListener {

    GridView gridView;
    Button btnSave;
    int nPrevSelGridItem = -1,selectedID = -1, userId;
    String  categoryNameSelected ="",categoryiconSelected="";
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoryGridviewAdapter adapter;
    RelativeLayout relativeLayout;

    public UserLogin user = UserLogin.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        initialization();
        refreshCategorySingleTon();
        Onclicks();
        populateGrid();
        onContextMenu();


    }
    private void refreshCategorySingleTon() {
        MyCategorySingleton.resetInstance();
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
                        ct.setPriority(false);
                        MyCategorySingleton.getInstance().getList().add(ct);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Backgound Error","Category ");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(ctx, "" + error, Toast.LENGTH_SHORT).show();
                Log.d("Backgound Error","Category ");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderIcon(R.drawable.ic_pen_24dp);
        menu.setHeaderTitle("Action");
        menu.add(0, v.getId(), 0, "Add to my category");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, Activity_editbudgetplan.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getGroupId() == 0)
        {
            //addToMyCategoy();
            Category category = new Category();
            category.setCategoryName(categoryNameSelected);
            category.setIcon(categoryiconSelected);
            category.setId(selectedID);
            category.setPercentage(0.0);

            if (!contain(categoryNameSelected))
            {
                addToMyCategoy(""+selectedID, userId, 0.0,0);
                Intent intent = new Intent(getApplicationContext(),Activity_editbudgetplan.class);
                startActivity(intent);
                finish();
                //Toast.makeText(getBaseContext(), "Original Size "+MyCategorySingleton.getInstance().getList().size(), Toast.LENGTH_SHORT).show();
                Alerter.create(Activity_add_category.this).setTitle("Mr.FinMan").setBackgroundColorRes(R.color.chartreuse).setIcon(R.drawable.ic_done).setText("Success").show();
            }
            else
                showMessage("Category already exist!");
            //showMessage("Categories "+MyCategorySingleton.getInstance().getList().size());
        }

        return true;
    }
    private void addToMyCategoy( final String catID, final int userid, final double percentage,final int isPriority)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "NewExpenseUserCategory.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING"," Check Edit bplan "+error);
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
    public boolean contain(String name)
    {
        for (Category category: MyCategorySingleton.getInstance().getList() )
        {
            if(category.getCategoryName().equals(name))
                    return true;
        }
        return false;
    }
    private void showMessage(String s) {
        Snackbar snackbar = Snackbar.make(relativeLayout,""+s,Snackbar.LENGTH_SHORT);
        snackbar.show();

    }



    private void onContextMenu() {
        registerForContextMenu(gridView);
    }

    private void populateGrid() {
        String url = methods.server()+"list_Category.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray  = new JSONArray(response);
                    for (int i=0;i<jsonArray.length(); i++)
                    {
                        JSONObject jobj  = jsonArray.getJSONObject(i);

                       Category category1 = new Category();
                       category1.setIcon(""+jobj.getString("Icon"));
                       category1.setId(Integer.parseInt(""+jobj.getString("ID")));
                       category1.setCategoryName(""+jobj.getString("Desc"));
                       categoryArrayList.add(category1);
                    }
                    adapter = new CategoryGridviewAdapter(Activity_add_category.this,categoryArrayList);
                    gridView.setAdapter(adapter);
                } catch (JSONException e) {
                    //e.printStackTrace();
                    Log.d("TESTING","POP Categories Activity_add_category");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TESTING","POP Categories Activity_add_category");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);


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
                        categoryNameSelected = categoryArrayList.get(position).getCategoryName();
                        categoryiconSelected = categoryArrayList.get(position).getIcon().toString();
                        selectedID = categoryArrayList.get(position).getId();
                        view.setBackgroundColor(getResources().getColor(R.color.light_grey));

                        adapterView.showContextMenuForChild(view);

                        //Toast.makeText(getApplication(), "You selected "+categoryNameSelected, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void Onclicks() {

        //btnSave.setOnClickListener(this);
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {
        userId = Integer.parseInt(getPreference("userID"));
        gridView = findViewById(R.id.grid_view_add_category);
        relativeLayout = findViewById(R.id.relative);
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
        tv.setText("Add My Category"); // ActionBar title text
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(25);
        tv.setTypeface(typeface, typeface.ITALIC);
        actionBar.setCustomView(tv);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId())
        {


        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Activity_editbudgetplan.class);
        startActivity(intent);
        finish();
    }
}
