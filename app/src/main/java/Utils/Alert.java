package Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.widget.Button;

import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_dashboard;
import com.teamcipher.mrfinman.mrfinmanfinal.Activity_editbudgetplan;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import Singleton.MyCategorySingleton;

public class Alert {
    public static void messageAnime(Context ctx,String title,String body)
    {
        Alerter.create((Activity) ctx)
                .setTitle(title)
                .setText(body)
                .setIcon(R.drawable.ic_launcher)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_success)).show();

    }
    public static void alert(Context ctx, String msg)
    {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage(""+msg);
        alertDialogBuilder.setIcon(R.drawable.ic_info_white);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        Button bq = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bq.setTextColor(Color.WHITE);



    }
}
