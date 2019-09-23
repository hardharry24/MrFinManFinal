package com.teamcipher.mrfinman.mrfinmanfinal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Date;

import Utils.methods;

public class DialogTransactionDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_transaction_details);

        this.setFinishOnTouchOutside(false);


        setTitle("Transaction Details");
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            TextView lblname = findViewById(R.id.transaction_lblname);
            TextView lblamount = findViewById(R.id.transaction_lblamount);
            TextView lbldes = findViewById(R.id.transaction_lbldesc);
            TextView lbldate = findViewById(R.id.transaction_lbldate);
            TextView lbltime = findViewById(R.id.transaction_lbltime);
            TextView lbltype = findViewById(R.id.transaction_lbltype);
            ImageView img = findViewById(R.id.transaction_img);


            String[] arrdate = bundle.getString("dateCreated").toString().split("/");

            Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);
           // Date time = new Date(""+bundle.getString("timeCreated"));

            lblname.setText(""+bundle.getString("Name"));
            lblamount.setText("Php "+bundle.getString("amount"));//methods.formatter.format(
            lbldes.setText(""+bundle.getString("note"));
            lbldate.setText(""+methods.dateComplete.format(d1));
            lbltime.setText(""+bundle.getString("timeCreated"));
            lbltype.setText(""+bundle.getString("type").toUpperCase());
            Picasso.get().load(methods.icon_server()+bundle.getString("icon")).into(img);





        }



    }
}
