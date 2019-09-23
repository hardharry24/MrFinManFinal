package com.teamcipher.mrfinman.mrfinmanfinal.Admin;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.text.TextUtils;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_add_category;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_login;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Adapters.CategoryGridviewAdapter;
import Adapters.PrioritiesAdaptor;
import Adapters.UserDetailsAdaptor;
import Models.Category;
import Models.MyBill;
import Models.Priorities;
import Models.user;
import Singleton.IncomeSingleton;
import Utils.message;
import Utils.methods;

/**
 * A simple {@link Fragment} subclass.
 */
public class frag_manage_priorities extends Fragment {
    GridView gridView;
    Button btnSave;
    int nPrevSelGridItem = -1,selectedID = -1;
    String  categoryNameSelected ="",categoryiconSelected="";
    ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoryGridviewAdapter adapter;
    ArrayList<Models.Priority> priorities = new ArrayList<>();
    ListView listView;
    Context ctx;
    View view;
    Dialog dialog;
    TextView lblTotal;
    PrioritiesAdaptor adaptor;
    FloatingActionMenu btnFabAdd;
    SwipeRefreshLayout swipe_refresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Activity_dashboard_admin) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color=\"gray\">" + "Manage Priorities" + "</font>"));
        view = inflater.inflate(R.layout.admin_fragment_manage_priorities, container, false);

        initialization();
        populate();
        onclicks();
        loadPercentage();

        return view;
    }

    private void loadTotal() {
        try
        {
            AndroidNetworking.get(methods.ADMIN_API_SERVER+"priorities.php?todo=CHECK")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("TOTAL",""+response);
                            try {
                                JSONObject jsonObject = response.getJSONObject(0);
                                int code = jsonObject.getInt("code");
                                String msg = jsonObject.getString("message");
                                double total = jsonObject.getDouble("total");
                                switch (code)
                                {
                                    case 0:
                                        message.error(msg,ctx);

                                        lblTotal.setText("");
                                        lblTotal.setText(""+methods.formatter00.format(total));
                                        savePreference("total_percentage",""+methods.formatter00.format(total));
                                        lblTotal.setTextColor(getResources().getColor(R.color.red));

                                        break;
                                    case 1:
                                        //message.success(msg,ctx);
                                        lblTotal.setTextColor(getResources().getColor(R.color.green));
                                        lblTotal.setText(""+methods.formatter00.format(total));
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
        catch (Exception ex)
        {

        }
    }

    private void onclicks() {
        try
        {
            btnFabAdd.setOnMenuButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAddDialogue();
                }

                private void showAddDialogue() {
                    final Dialog dialog = new Dialog(ctx);
                    dialog.setContentView(R.layout.dialogue_admin_add_priorities);
                    gridView = dialog.findViewById(R.id.grid_view_add_category);
                    Button btnOk = dialog.findViewById(R.id.btnOK);
                    final EditText txtPercentage = dialog.findViewById(R.id.txtPercentage);
                    adapter = new CategoryGridviewAdapter(ctx,categoryArrayList);
                    gridView.setAdapter(adapter);
                    setGridViewClick(gridView);

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectedID != -1)
                            {
                                if (!(TextUtils.isEmpty(txtPercentage.getText())))
                                    showConfirmation();
                                else
                                    txtPercentage.setError("Not Valid!");
                            }
                            else
                                message.error("Not Valid Category!",ctx);
                        }

                        private void showConfirmation() {
                            AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setTitle("Confirmation")
                                    .setMessage("Are you sure you want to add "+categoryNameSelected+" category to your priorities?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            OnAddPrio();
                                            loadTotal();
                                        }

                                        private void OnAddPrio() {
                                            AndroidNetworking.get(methods.ADMIN_API_SERVER+"priorities.php?todo=INSERT&categoryId="+selectedID+"&percentage="+txtPercentage.getText().toString()+"")
                                                    .setTag("test")
                                                    .setPriority(Priority.LOW)
                                                    .build()
                                                    .getAsJSONArray(new JSONArrayRequestListener() {
                                                        @Override
                                                        public void onResponse(JSONArray response) {
                                                            try {
                                                                JSONObject jsonObject = response.getJSONObject(0);
                                                                int code = jsonObject.getInt("code");
                                                                String msg = jsonObject.getString("message");
                                                                switch (code)
                                                                {
                                                                    case 0:
                                                                        message.error(msg,ctx);
                                                                        break;
                                                                    case 1:
                                                                        message.success(msg,ctx);
                                                                        fragmentRedirection(new frag_manage_priorities());
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



                    dialog.setCancelable(true);
                    dialog.create();
                    dialog.show();
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

        if (v.getId()==R.id.listViewCategory) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.long_press_menu_priorities, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Models.Priority p = (Models.Priority)adaptor.getItem(info.position);

        switch(item.getItemId()) {
            case R.id.edit:
                showEditDialogue(p);

                return true;
            case R.id.delete:
                showDeleteDialogue(p);

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void showDeleteDialogue(final Models.Priority p) {

        try
        {
            AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Confirmation")
                    .setMessage("Confirm to delete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OnDeletePrio(p);

                        }

                        private void OnDeletePrio(Models.Priority p) {
                            try
                            {
                                AndroidNetworking.get(methods.ADMIN_API_SERVER+"priorities.php?todo=DELETE&pId="+p.getpId()+"")
                                        .setTag("test")
                                        .setPriority(Priority.LOW)
                                        .build()
                                        .getAsJSONArray(new JSONArrayRequestListener() {
                                            @Override
                                            public void onResponse(JSONArray response) {
                                                try {
                                                    JSONObject jsonObject = response.getJSONObject(0);
                                                    int code = jsonObject.getInt("code");
                                                    String msg = jsonObject.getString("message");
                                                    switch (code)
                                                    {
                                                        case 0:
                                                            message.error(msg,ctx);
                                                            break;
                                                        case 1:
                                                            message.success(msg,ctx);
                                                            fragmentRedirection(new frag_manage_priorities());
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
                            catch (Exception ex)
                            {

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
        catch (Exception ex)
        {

        }
    }

    private void showEditDialogue(Models.Priority p) {
        try
        {
            dialog = new Dialog(ctx);
            dialog.setContentView(R.layout.dialogue_admin_update_priorities);
            dialog.setCancelable(true);
            final EditText txtPerc = dialog.findViewById(R.id.txtPercentage);

            ImageView imgIcon = dialog.findViewById(R.id.imgIcon);
            TextView lblCategory = dialog.findViewById(R.id.lblcategory);
            Button btnUpdate = dialog.findViewById(R.id.btnOK);

            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();

            Picasso.get()
                    .load(methods.icon_server()+""+p.getIcon())
                    .transform(transformation)
                    .into(imgIcon);

            lblCategory.setText(""+p.getCategoryDesc());
            txtPerc.setText(""+methods.formatter00.format(p.getPercentage()));


            final Models.Priority pNew = p;


            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!(TextUtils.isEmpty(txtPerc.getText().toString())))
                    {
                        pNew.setPercentage(Double.parseDouble(txtPerc.getText().toString()));
                        onConfirmEdit(pNew);

                    }
                    else
                    {
                        txtPerc.setError("!");
                    }
                }

                private void onConfirmEdit(final Models.Priority pNew) {
                    AlertDialog alertDialog = new AlertDialog.Builder(ctx)
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("Confirmation")
                            .setMessage("Confirm to update?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    OnUpdatePrio(pNew);

                                }

                                private void OnUpdatePrio(Models.Priority p) {
                                    AndroidNetworking.get(methods.ADMIN_API_SERVER+"priorities.php?todo=UPDATE&pId="+p.getpId()+"&categoryId="+p.getCategoryId()+"&percentage="+p.getPercentage()+"")
                                            .setTag("test")
                                            .setPriority(Priority.LOW)
                                            .build()
                                            .getAsJSONArray(new JSONArrayRequestListener() {
                                                @Override
                                                public void onResponse(JSONArray response) {
                                                    try {
                                                        JSONObject jsonObject = response.getJSONObject(0);
                                                        int code = jsonObject.getInt("code");
                                                        String msg = jsonObject.getString("message");
                                                        switch (code)
                                                        {
                                                            case 0:
                                                                message.error(msg,ctx);
                                                                break;
                                                            case 1:
                                                                message.success(msg,ctx);
                                                                fragmentRedirection(new frag_manage_priorities());
                                                                break;
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }
                                                public void onError(ANError error) {
                                                    //message.error(""+error,ctx);
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

    public void fragmentRedirection(Fragment ctx)
    {
        (getActivity()).getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_admin, ctx)
                .addToBackStack(null)
                .commit();
    }

    private void populate() {
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
                                    Models.Priority p = new  Models.Priority();
                                    p.setpId(jObject.getInt("pId"));
                                    p.setCategoryDesc(""+jObject.getString("categorDesc"));
                                    p.setCategoryId(jObject.getInt("categoryId"));
                                    p.setPercentage(jObject.getDouble("percentage"));
                                    p.setIcon(""+jObject.getString("icon"));
                                    priorities.add(p);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    //Toast.makeText(ctx, "eeeee"+e, Toast.LENGTH_SHORT).show();
                                }

                            }
                            adaptor = new PrioritiesAdaptor(ctx,priorities);
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

    private void initialization() {
        try
        {
            ctx = getContext();
            listView = view.findViewById(R.id.listViewCategory);
            setHasOptionsMenu(true);
            registerForContextMenu(listView);
            btnFabAdd = view.findViewById(R.id.fab_add);
            populateGrid();
            lblTotal = view.findViewById(R.id.lbltotal);
            swipe_refresh = view.findViewById(R.id.swipe_refresh);
        }
        catch (Exception ex)
        {

        }
    }

    private void populateGrid() {
        try
        {
            categoryArrayList.clear();
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

                    } catch (JSONException e) {
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Alerter.create((Activity) ctx).setText("NO INTERNET CONNECTION!")
                            .setIcon(R.drawable.ic_info_outline_black_24dp)
                            .show();
                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(ctx);
            requestQueue.add(stringRequest);
        }
        catch (Exception ex)
        {

        }
    }

    private void setGridViewClick(final GridView gridViewItem)
    {
        try
        {
            gridViewItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                View viewPrev;
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    try {
                        if (nPrevSelGridItem != -1) {
                            viewPrev = gridViewItem.getChildAt(nPrevSelGridItem);
                            //viewPrev.setBackgroundColor(getResources().getColor(R.color.background));
                        }
                        nPrevSelGridItem = position;
                        if (nPrevSelGridItem == position) {
                            categoryNameSelected = categoryArrayList.get(position).getCategoryName();
                            categoryiconSelected = categoryArrayList.get(position).getIcon().toString();
                            selectedID = categoryArrayList.get(position).getId();
                            //viewPrev.setBackgroundColor(Color.GRAY);

                            message.success(categoryNameSelected+" Selected",ctx);

                            adapterView.showContextMenuForChild(view);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        catch (Exception ex)
        {

        }
    }

    public void loadPercentage()
    {
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
                            loadTotal();
                            populate();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, 5000);
        }
        catch (Exception ex)
        {

        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getActivity().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }
    private void savePreference(String key,String value)
    {
        try
        {
            SharedPreferences preferences = getActivity().getSharedPreferences("credentials",0);
            SharedPreferences.Editor  editor = preferences.edit();
            editor.putString(key,value);
            editor.commit();
        }
        catch (Exception ex)
        {

        }
    }
}
