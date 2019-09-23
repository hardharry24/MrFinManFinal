package com.teamcipher.mrfinman.mrfinmanfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Adapters.MyBillsAdapter;
import Adapters.MyGoalAdapter;
import Models.MyBill;
import Models.MyGoals;
import Singleton.MyCategorySingleton;
import Singleton.UserLogin;
import Utils.methods;

public class Activity_my_bills extends AppCompatActivity {
    SwipeMenuListView listView;
    ArrayList<MyBill> myBills = new ArrayList<>();
    MyBillsAdapter adapter;
    int userId = UserLogin.getInstance().getUser_ID();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bills);

        initialization();
        onheaderActionBar("My Bills");
        popListview();
        swipeMenu();

    }

    private void initialization() {
        listView = findViewById(R.id.listview_my_bills_list);
    }
    private void swipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem editItem = new SwipeMenuItem(Activity_my_bills.this);
                editItem.setWidth(90);
                editItem.setIcon(R.drawable.ic_pen);
                menu.addMenuItem(editItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(Activity_my_bills.this);
                deleteItem.setWidth(90);
                deleteItem.setIcon(R.drawable.ic_delete_forever_black_24dp);
                menu.addMenuItem(deleteItem);

            }
        };
        listView.setMenuCreator(creator);

        listView.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        // listView.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        try {

                        }
                        catch (Exception ex)
                        {
                            Toast.makeText(Activity_my_bills.this, ex.toString(), Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });

    }

    private void popListview() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"bill_list.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Toast.makeText(Activity_my_goals.this, ""+response, Toast.LENGTH_SHORT).show();
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i =0 ; i<jsonArray.length();i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        MyBill myBill = new MyBill();
                        myBill.setAmount(Double.parseDouble(""+j.getString("amount")));
                        myBill.setBalance(Double.parseDouble(""+j.getString("balance")));
                        myBill.setDueDate(""+j.getString("dueDate"));
                        myBill.setDesc(""+j.getString("description"));
                        myBill.setBillname(""+j.getString("billName"));
                        myBill.setId(j.getInt("billId"));
                        myBill.setIcon(j.getString("Icon"));
                        myBill.setCategoryName(""+j.getString("Desc"));
                        myBills.add(myBill);

                    }

                    adapter = new MyBillsAdapter(myBills,Activity_my_bills.this);
                    listView.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(Activity_my_bills.this, ""+e.toString(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Activity_my_bills.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("userID",""+userId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    private void onheaderActionBar(String title) {
        setTitle(title);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_goal_menu, menu);
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
        return super.onOptionsItemSelected(item);
    }


}
