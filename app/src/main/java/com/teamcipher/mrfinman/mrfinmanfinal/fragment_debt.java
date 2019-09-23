package com.teamcipher.mrfinman.mrfinmanfinal;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Adapters.Debt_adapter;
import Models.Category;
import Models.Debts;
import Models.MyBill;
import Models.MyGoals;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.message;
import Utils.methods;
import retrofit2.Call;
import retrofit2.Callback;

import static Utils.methods.ctx;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_debt extends Fragment {

    View view;
    ListView listView;
    Context ctx;
    Debt_adapter adapter;
    ArrayList<Debts> debts = new ArrayList<>();
    FloatingActionButton fabNew;
    FloatingActionMenu fabMenu;
    FrameLayout frameLayout;
    TextView lblTotalDebt;
    SwipeRefreshLayout swipeRefreshLayout;
    int userId = 0;
    APIservice apIservice = APIclient.getClient().create(APIservice.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment_debt, container, false);
        initialization();
        onheader("My Debts");
        populate();
        btnOnclick();
        return view;
    }

    private void btnOnclick() {
        //Icon Create Debt
        fabNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabMenu.close(true);
                Intent intent = new Intent(ctx,Activity_my_debt_add.class);
                startActivity(intent);
            }
        });

        //Icon Button Menu
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fabMenu.isOpened()) {
                    fabMenu.close(true);
                }
            }
        });
        fabMenu.setClosedOnTouchOutside(true);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId()==R.id.deb_listview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Debts dbt = adapter.getItem(info.position);

        switch(item.getItemId()) {

            case R.id.details:
                dialogueDetails(dbt);
                return true;
            case R.id.edit:
                Intent intent = new Intent(ctx, Activity_my_debt_add.class);
                intent.putExtra("CategoryId",dbt.getCategoryId());
                intent.putExtra("Category",dbt.getCategoryDesc());
                intent.putExtra("Desc",dbt.getDescription());
                intent.putExtra("dueDate",dbt.getDueDate());
                intent.putExtra("Name",dbt.getName());
                intent.putExtra("date",dbt.getDate());
                intent.putExtra("period",dbt.getPeriod());
                intent.putExtra("no",dbt.getNoDays());
                intent.putExtra("Id",dbt.getId());
                intent.putExtra("amount",""+dbt.getAmount());

                startActivity(intent);
                return true;
            case R.id.delete:
                delete(dbt);
                refreshTotal();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void dialogueDetails(Debts dbt) {
        final Dialog dialog = new Dialog(ctx,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialogue_debt_details);
        dialog.setCancelable(true);
        ImageView img;
        EditText txtName,txtAmount,txtDueDate,txtPaymentType,txtDate,txtDesc,txtbalance;
        txtName = dialog.findViewById(R.id.lbldebtname);
        txtAmount = dialog.findViewById(R.id.lbldebtamount);
        txtDueDate = dialog.findViewById(R.id.lbldateDue);
        txtDate = dialog.findViewById(R.id.lbldateCreated);
        txtDesc = dialog.findViewById(R.id.lbldescription);
        txtPaymentType = dialog.findViewById(R.id.lblpaymentType);
        img =dialog.findViewById(R.id.lblImageIcon);
        txtbalance = dialog.findViewById(R.id.lbldebtbalanceamount);

        Button btnClose  = dialog.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        Picasso.get().load(methods.icon_server()+dbt.getIcon()).transform(methods.transformation).into(img);

        String[] arrdate = dbt.getDueDate().split("/");

        Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);

        txtbalance.setText("Php "+methods.formatter.format(dbt.getBalance()));
        txtPaymentType.setText(""+dbt.getPeriod());
        txtAmount.setText("Php "+methods.formatter.format(dbt.getAmount()));
        txtDate.setText(""+dbt.getDate());
        txtDueDate.setText(""+methods.dateComplete.format(d1));
        txtName.setText(dbt.getName());
        txtDesc.setText(dbt.getDescription());
        dialog.create();
        dialog.show();



    }

    private void populate() {
        refreshTotal();
        debts.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server()+"debt.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);

                    for (int i =0 ; i<jsonArray.length();i++)
                    {
                        JSONObject j = jsonArray.getJSONObject(i);
                        Debts dbt = new Debts();
                        dbt.setAmount( Double.parseDouble(j.getString("amount")));
                        dbt.setId(Integer.parseInt(j.getString("debtId")));
                        dbt.setCategoryDesc(j.getString("categoryDesc"));
                        dbt.setCategoryId(j.getInt("categoryId"));
                        dbt.setDate(j.getString("dateCreated"));
                        dbt.setPeriod(j.getString("period"));
                        dbt.setDueDate(j.getString("dueDate"));
                        dbt.setNoDays(j.getInt("noDays"));
                        dbt.setEquivalent(j.getDouble("equivalent"));
                        dbt.setBalance(j.getDouble("balance"));
                        dbt.setName(j.getString("debtName"));
                        dbt.setDescription(j.getString("description"));
                        dbt.setIcon(j.getString("icon"));
                        dbt.setIsNotify(j.getInt("isNotify"));
                        dbt.setIsNotifyBefore(j.getInt("isNotifyBefore"));
                        debts.add(dbt);
                    }

                    adapter = new Debt_adapter((Activity) ctx,debts);
                    listView.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                    Logs.LOGS("Error in Debt Populate "+e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logs.LOGS("Error in Debt Populate "+error);
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("type","LIST");
                params.put("userId",""+ userId);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);
        requestQueue.add(stringRequest);
    }

    private void delete(final Debts g, final String type)
    {
        AndroidNetworking.get(methods.server()+"debtDeleteUndo.php?type="+type+"&Id="+g.getId()+"")
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
                    }
                });
    }
    //Delete Debt and confirmation
    public void delete(final Debts g)
    {

        final AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to delete "+g.getName()+"?")
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
    //Restore or Undo deleted Debt
    private void undo(final Debts dbt)
    {
        //Snackbar murag toast
        final Snackbar snackbar = Snackbar.make(frameLayout,"Successfuly Deleted!",Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete(dbt,"UNDO");
                snackbar.dismiss();
            }
        });

    }

    private void initialization() {
        try
        {
            userId  = Integer.parseInt(getPreference("userID"));
            listView = view.findViewById(R.id.deb_listview);
            frameLayout = view.findViewById(R.id.frameLayout);
            fabNew = view.findViewById(R.id.fab_goal_add);
            fabMenu = view.findViewById(R.id.fab_goal_menu);
            lblTotalDebt = view.findViewById(R.id.lblTotal_debt_balance);

            ctx = getContext();
            registerForContextMenu(listView);
            swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
            onRefresh();


        }
        catch (Exception ex)
        {

        }
    }

    private void refreshTotal()
    {
        try
        {
            String amtBal = getPreference("TOTAL_DEBT");
            lblTotalDebt.setText("Php "+methods.formatter.format(Double.parseDouble(amtBal)));
        }catch (Exception ex)
        {

        }

    }
    private String getPreference(String key) {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials", 0);
        return preferences.getString(key, null);
    }

    private void onRefresh() {
        try
        {
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    swipeRefreshLayout.setRefreshing(true);

                    populate();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    },300);

                }
            });
        }
        catch (Exception ex)
        {

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
}
