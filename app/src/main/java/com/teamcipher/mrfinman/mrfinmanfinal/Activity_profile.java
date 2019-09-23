package com.teamcipher.mrfinman.mrfinmanfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import Models.Result;
import Models.user;
import Singleton.UserLogin;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.customMethod;
import Utils.message;
import Utils.methods;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_profile extends AppCompatActivity {

    EditText txtLname,txtFname,txtMi,txtEmail,txtCont,txtUser,txtpass;
    TextView lblUserLogin;
    @BindView(R.id.btnEdit)
    ImageView btnEditProfile;
    Button btnSave;
    Context ctx;
    String username = "";
    LinearLayout layoutBtn;
    String todo ="EDIT";
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initialization();

    }
    @OnClick(R.id.btnEdit)
    public void editProfile(View view)
    {
        if (todo.equals("EDIT"))
        {
            todo = "EDITED";
            edit();
        }
        else
        {
            edited();
            todo = "EDIT";
        }

    }

    @OnClick(R.id.btnProfSave)
    public void saveChanges(View view)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Confirmation")
                .setMessage("Are you sure you to save changes?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Call<Result> updateUserInfo = apIservice.updateUserInfo(username,txtLname.getText().toString().toUpperCase(),
                                txtFname.getText().toString().toUpperCase(),txtMi.getText().toString().toUpperCase(),txtEmail.getText().toString(),txtCont.getText().toString(),
                                txtUser.getText().toString(),txtpass.getText().toString());

                        updateUserInfo.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                switch (response.body().getCode())
                                {
                                    case 0:
                                        txtEmail.setError(""+response.body().getMessage());
                                        message.warning(response.body().getMessage(),ctx);
                                        break;
                                    case 1:
                                        txtCont.setError(""+response.body().getMessage());
                                        message.warning(response.body().getMessage(),ctx);
                                        break;
                                    case 2:
                                        txtUser.setError(""+response.body().getMessage());
                                        message.warning(response.body().getMessage(),ctx);
                                        break;
                                    case 3:
                                        message.success(response.body().getMessage(),ctx);
                                        edited();
                                        toUpperTxt();
                                        break;
                                    case 4:
                                        message.error(response.body().getMessage(),ctx);
                                        break;
                                }
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Logs.LOGS("User profile error "+t.toString());
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

    private void toUpperTxt() {
        String lname = txtLname.getText().toString();
        String fname = txtFname.getText().toString();
        String mi = txtMi.getText().toString();
        txtLname.setText(lname.toUpperCase());
        txtFname.setText(fname.toUpperCase());
        txtMi.setText(mi.toUpperCase());
    }

    private void loadUserInfo() {
        Call<UserLogin> userDetails = apIservice.getUserDetails(username);
        userDetails.enqueue(new Callback<UserLogin>() {
            @Override
            public void onResponse(Call<UserLogin> call, retrofit2.Response<UserLogin> response) {
                if (response.isSuccessful())
                {
                    lblUserLogin.setText(""+response.body().getFullname());
                    txtLname.setText(""+response.body().getLname());
                    txtFname.setText(""+response.body().getFname());
                    txtMi.setText(""+response.body().getMi());
                    txtCont.setText(""+response.body().getContactNo());
                    txtEmail.setText(""+response.body().getEmail());
                    txtUser.setText(""+response.body().getUsername());
                    txtpass.setText(""+response.body().getPassword());

                    UserLogin.getInstance().setUserId(response.body().getUserId());
                }
            }

            @Override
            public void onFailure(Call<UserLogin> call, Throwable t) {
                Logs.LOGS("Background Error "+t);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this,Activity_dashboard.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void initialization() {
        try
        {
            username = getPreference("username");
            ctx = this;

            onheader("Profile");
            txtLname = findViewById(R.id.txtProfLname);
            txtFname = findViewById(R.id.txtProfFname);
            txtMi = findViewById(R.id.txtProfMI);
            txtEmail = findViewById(R.id.txtProfEmail);
            txtCont = findViewById(R.id.txtProfcontactNo);
            txtUser = findViewById(R.id.txtProfUsername);
            txtpass = findViewById(R.id.txtProfPassword);
            layoutBtn = findViewById(R.id.linearBtn);
            lblUserLogin = findViewById(R.id.lblUser);
            loadUserInfo();
            edited();
        }
        catch (Exception ex)
        {

        }
    }
    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }



    private void edited()
    {
        layoutBtn.setVisibility(View.GONE);
        EditText[] editTexts = {txtLname,txtFname,txtMi,txtEmail,txtCont,txtUser,txtpass};
        customMethod.setDisAbleEditText(editTexts);
    }
    private void edit()
    {
        layoutBtn.setVisibility(View.VISIBLE);
        EditText[] editTexts = {txtLname,txtFname,txtMi,txtEmail,txtCont,txtUser,txtpass};
        customMethod.setEnAbleEditText(editTexts);
    }

    private void onheader(String profile) {
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
            tv.setText(profile); // ActionBar title text
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
    public void onBackPressed() {
        startActivity(new Intent(this,Activity_dashboard.class));
        finish();
    }
}
