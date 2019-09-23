package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Adapters.CategoryGridviewAdapter;
import Models.Category;
import Models.biller;
import Singleton.UserLogin;
import Utils.message;
import Utils.methods;
import es.dmoral.toasty.Toasty;

public class Activity_my_bills_add extends AppCompatActivity {

    ImageView btnBack;
    Button btnSave;
    EditText txtamount,txtDate,txtnote,txtname;
    GridView gridView;
    UserLogin user;
    int nPrevSelGridItem = -1;
    SimpleDateFormat df = new SimpleDateFormat("dd MMMM, yyyy");
    ArrayList<Category> categories = new ArrayList<>();
    String datepick= "",amount = "";
    TextView lblbiller;
    Button btnShowbiller;
    int selectedID = -1;
    Context ctx;
    ListView listView;
    Dialog dialog;
    TextView lblMessage;
    String username = "";
    Calendar calendar = Calendar.getInstance();
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bills);

        setTitle("New Bill");
        initialization();
    }


    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void initialization() {
        try
        {
            username = getPreference("username");
            ctx  = this;
            user = UserLogin.getInstance();
            btnSave = findViewById(R.id.btn_new_bills_save);
            txtamount = findViewById(R.id.txt_my_bill_amount);
            txtDate = findViewById(R.id.txt_new_bill_due_date);
            txtname = findViewById(R.id.txt_new_bill_name);
            lblbiller = findViewById(R.id.txt_new_bill_biller);
            btnShowbiller = findViewById(R.id.dialogue_btnshowbiller);
            txtnote = findViewById(R.id.txt_new_bill_desc);
            spinner = findViewById(R.id.txt_spinner_new_bill_type);
            lblMessage = findViewById(R.id.txt_new_bill_message);

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_today_blue);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            txtamount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (txtamount.getText().toString().equals(""))
                        txtamount.setError(null);
                    else if (txtamount.getText().toString() != "")
                    {
                        amount = txtamount.getText().toString();
                        if (Double.parseDouble(txtamount.getText().toString()) > 10)
                        {
                            txtamount.setText(""+methods.formatter.format(Double.parseDouble(txtamount.getText().toString())));
                        }
                        else
                        {
                            txtamount.setError("Amount must be greater than Php 10.00!");
                        }
                    }
                }
            });
            txtamount.setError(null);
            btnShowbiller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AndroidNetworking.get(methods.server() + "getbillerlist.php")
                            .setPriority(Priority.LOW)
                            .build()
                            .getAsJSONArray(new JSONArrayRequestListener() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    ArrayList<String>  billers = new ArrayList<>();
                                    for (int i=0;i<response.length();i++)
                                    {
                                        try {
                                            JSONObject jsonObject = response.getJSONObject(i);
                                            billers.add(""+jsonObject.getString("billerName"));

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    final ArrayAdapter<String> adapter = new ArrayAdapter<String> (ctx,android.R.layout.simple_list_item_1,billers){
                                        @Override
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            TextView textView = (TextView) super.getView(position, convertView, parent);
                                            textView.setTextColor(getResources().getColor(R.color.gray));
                                            return textView;
                                        }
                                    };
                                    dialog = new Dialog(ctx);
                                    dialog.setContentView(R.layout.dialogue_show_biller_list);
                                    listView = dialog.findViewById(R.id.dialogue_listview_biller_list);

                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                            lblbiller.setText(""+adapterView.getItemAtPosition(i));
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                                @Override
                                public void onError(ANError error) {
                                    //Toast.makeText(ctx, ""+error, Toast.LENGTH_SHORT).show();
                                }
                            });


                }
            });
            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCalendar();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    savebill();
                }
            });
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    //Toast.makeText(ctx, ""+spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
              /*  if (txtamount.getText().toString() == "" || txtname.getText().toString() == "")
                {
                    Toast.makeText(ctx, "Oops amount, bill name or date must not empty!", Toast.LENGTH_SHORT).show();
                }
                else
                {*/


                    if (i != 0)
                    {
                        if (i ==1)
                        {
                            String msg = "You have chosen one time payment for "+txtname.getText().toString()+
                                    " for Php"+txtamount.getText().toString()+" this will due on "+txtDate.getText().toString()+".";
                            lblMessage.setText(msg);
                        }
                        else if (i ==2)
                        {
                            Double weekly = Double.parseDouble(txtamount.getText().toString().replace(",","")) * 4;
                            String msg = "You have chosen weekly payment for "+txtname.getText().toString()+
                                    " for Php"+txtamount.getText().toString()+" weekly you need to allocate Php"+methods.formatter.format(weekly)+" for the payment every month.";
                            lblMessage.setText(msg);
                        }
                        else if (i ==3)
                        {
                            Double semi = Double.parseDouble(txtamount.getText().toString().replace(",","")) * 2;
                            String msg = "You have chosen semi-monthly payment for "+txtname.getText().toString()+
                                    " for Php"+txtamount.getText().toString()+" weekly you need to allocate Php"+methods.formatter.format(semi)+" for the payment every 15days.";
                            lblMessage.setText(msg);
                        }
                        else if (i ==4)
                        {
                            Double annualy = Double.parseDouble(txtamount.getText().toString().replace(",","")) * 12;
                            String msg = "You have chosen Monthly payment for "+txtname.getText().toString()+
                                    " for Php"+txtamount.getText().toString()+" weekly you need to allocate Php"+methods.formatter.format(annualy) +" for the payment every year.";
                            lblMessage.setText(msg);
                        }
                        else if (i ==5)
                        {
                            String msg = "You have chosen Monthly payment for "+txtname.getText().toString()+
                                    "for Php"+txtamount.getText().toString()+"annually.";
                            lblMessage.setText(msg);
                        }
                    }
                    else
                        lblMessage.setText("");
                    //   }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

        }
        catch (Exception ex)
        {

        }
    }

    private void savebill() {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, methods.server() + "bills.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response.trim().equals("1"))
                    {
                        Toasty.success(ctx,"Successfuly added to your bills!",Toast.LENGTH_SHORT).show();
                        onBackPressed();
                        //Toast.makeText(ctx, "Successfuly added to your bills!", Toast.LENGTH_SHORT).show();
                    }
                    else if (response.trim().equals("3"))
                    {
                       // Toast.makeText(ctx, "Oops Bill name Already Exist!", Toast.LENGTH_SHORT).show();
                        Toasty.error(ctx,"Oops Bill name Already Exist!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ctx, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    /*Alerter.create(Activity_my_bills_add.this).setText("NO INTERNET CONNECTION!\n" + error)
                            .setIcon(R.drawable.ic_info_outline_black_24dp)
                            .show();*/
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d h:mm:s");

                    final Date date = Calendar.getInstance().getTime();
                    String fDate = df.format(date);

                   Map<String, String> params = new HashMap<>();
                   params.put("type","insert");
                    params.put("userID", "" + user.getUser_ID());
                    params.put("billname", ""+txtname.getText().toString());
                    params.put("amount", ""+amount);

                    String biller = lblbiller.getText().toString();
                    if (biller == "")
                        params.put("billername", "0");
                    else
                        params.put("billername", ""+biller);

                    params.put("note",""+ txtnote.getText().toString());
                    params.put("targetDate", datepick);
                    params.put("paymenttype",""+spinner.getSelectedItem().toString());
                    params.put("dateCreated", fDate);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
        catch (Exception ex)
        {
            methods.showMessage(Activity_my_bills_add.this, "Mr.FinMan", "Please try again" , R.drawable.ic_close, R.color.red);
        }

        /*Intent intent = new Intent(this,Activity_my_goals.class);
        startActivity(intent);
        finish();*/

    }

    private void showCalendar() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
         DatePickerDialog.OnDateSetListener  mdatelistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //month = month +1;
                calendar.set(year, month, day);
                txtDate.setText(df.format(calendar.getTime()));
                datepick = dt.format(calendar.getTime());
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,mdatelistener,year,month,day);
        dialog.show();
    }

    public void notification(String body)
    {
        //Intent intent = new Intent(this,Activity_my_goals.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

       /* NotificationCompat.Builder mbuilder = new NotificationCompat.Builder(this)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setSmallIcon(R.drawable.alerter_ic_notifications)
        .setContentTitle("Mr.FinMan")
        .setContentText(body);
       // .addAction(R.drawable.ic_send,"View",pendingIntent);
      //  mbuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(121,mbuilder.build());*/
        //methods.showMessage(this,"Message","OK",R.drawable.ic_view_24dp,R.color.green);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(Activity_my_bills_add.this);
        notification.setSmallIcon(R.drawable.ic_view_24dp);
        notification.setContentTitle("HELLO");
        notification.setContentText("Wewewe");
        notification.setTicker("This is a ticker");
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(12312,notification.build());

    }
}
