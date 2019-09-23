package com.teamcipher.mrfinman.mrfinmanfinal.Biller;

import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.teamcipher.mrfinman.mrfinmanfinal.R;

import Models.biller;
import Utils.APIclient;
import Utils.APIservice;
import Utils.Logs;
import Utils.customMethod;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Activity_biller_profile extends AppCompatActivity {
    EditText txtLname,txtFname,txtMi,txtEmail,txtContact,txtUname,txtPword,txtComNam,txtComEmail,txtComCont,txtComAddress;
    TextView lblUser;
    String username;
    LinearLayout layoutBtn;
    biller billerAllInfo = new biller();
    APIservice apIservice = APIclient.getClient().create(APIservice.class);
    String todo ="EDIT";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biller_profile);
        username = getPreference("username");

        populateBillerInfo();
        initialize();
        setValue();
    }

    private void setValue() {


    }

    private void initialize() {
        try
        {

        }
        catch (Exception ex)
        {

        }
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
    private void edited()
    {
        layoutBtn.setVisibility(View.GONE);
        EditText[] editTexts = {txtLname,txtFname,txtMi,txtEmail,txtContact,txtUname,txtPword ,txtComNam,txtComEmail,txtComCont,txtComAddress};
        customMethod.setDisAbleEditText(editTexts);
    }
    private void edit()
    {
        layoutBtn.setVisibility(View.VISIBLE);
        EditText[] editTexts = {txtLname,txtFname,txtMi,txtEmail,txtContact,txtUname,txtPword,txtComNam,txtComEmail,txtComCont,txtComAddress};
        customMethod.setEnAbleEditText(editTexts);
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void onheader(String profile) {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#808080\">" +profile+ "</font>")));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateBillerInfo()
    {
        try
        {
            final Call<biller> billerInfo = apIservice.BillerInfo(username);
            billerInfo.enqueue(new Callback<biller>() {
                @Override
                public void onResponse(Call<biller> call, Response<biller> response) {
                    billerAllInfo.setId(response.body().getId());
                    billerAllInfo.setLname(response.body().getLname());
                    billerAllInfo.setFname(response.body().getFname());
                    billerAllInfo.setMi(response.body().getMi());
                    billerAllInfo.setName(response.body().getName());
                    billerAllInfo.setUserId(response.body().getUserId());
                    billerAllInfo.setEmail(response.body().getEmail());
                    billerAllInfo.setContact(response.body().getContact());
                    billerAllInfo.setRepContact(response.body().getRepContact());
                    billerAllInfo.setRepEmail(response.body().getRepEmail());
                    billerAllInfo.setRepUsername(response.body().getRepUsername());
                    billerAllInfo.setRepPassword(response.body().getRepPassword());
                    billerAllInfo.setAddress(response.body().getAddress());

                   // lblUser.setText(billerAllInfo.getFullname()+"\n"+billerAllInfo.getName());

                   // lblUser.setText(billerAllInfo.getFullname()+"\n"+billerAllInfo.getName());

                    txtLname.setText(billerAllInfo.getLname());
                    txtFname.setText(billerAllInfo.getFname());
                    txtMi.setText(billerAllInfo.getMi());
                    txtEmail.setText(billerAllInfo.getRepEmail());
                    txtContact.setText(billerAllInfo.getRepContact());
                    txtUname.setText(username);
                    txtPword.setText(billerAllInfo.getRepPassword());
                    txtComNam.setText(billerAllInfo.getName());
                    txtComAddress.setText(billerAllInfo.getAddress());
                    txtComCont.setText(billerAllInfo.getContact());
                    txtComEmail.setText(billerAllInfo.getEmail());
                }

                @Override
                public void onFailure(Call<biller> call, Throwable t) {
                    Logs.LOGS_BILLER("Error in populateBillerInfo "+t);
                }
            });
        }
        catch (Exception ex)
        {

        }
    }
}
