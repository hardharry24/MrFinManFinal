package Utils;

import es.dmoral.toasty.Toasty;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.tapadoo.alerter.Alerter;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

public class message {
    public static void error(String msg,Context ctx)
    {
        Toasty.error(ctx,msg, Toast.LENGTH_SHORT,true).show();
    }
    public static void info(String msg,Context ctx)
    {
        Toasty.info(ctx,msg, Toast.LENGTH_SHORT,true).show();;
    }
    public static void success(String msg,Context ctx)
    {
        Toasty.success(ctx,msg, Toast.LENGTH_SHORT,true).show();
    }
    public static void warning(String msg,Context ctx)
    {
        Toasty.warning(ctx,msg, Toast.LENGTH_SHORT,true).show();;
    }


    public static void error(String msg,Context ctx,int length)
    {
        Toasty.error(ctx,msg, length,true).show();
    }
    public static void info(String msg,Context ctx,int length)
    {
        Toasty.info(ctx,msg, length,true).show();;
    }
    public static void success(String msg,Context ctx,int length)
    {
        Toasty.success(ctx,msg, length,true).show();
    }
    public static void warning(String msg,Context ctx,int length)
    {
        Toasty.warning(ctx,msg, length,true).show();;
    }

    public static void alertSuccess(Context ctx,String msg)
    {
        Alerter.create((Activity)ctx)
                .setTitle("Mr.FinMan")
                .setText(msg)
                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                .setDuration(5000)
                .setIcon(R.drawable.ic_info_outline_white_48dp)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_success))
                .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        //Toast.makeText(Activity_my_goals_add.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public static void alertWarning(Context ctx,String msg)
    {
        Alerter.create((Activity)ctx)
                .setTitle("Mr.FinMan")
                .setText(msg)
                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                .setDuration(5000)
                .setIcon(R.drawable.ic_warning_outline_white)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_warning))
                .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        //Toast.makeText(Activity_my_goals_add.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public static void alertError(Context ctx,String msg)
    {
        Alerter.create((Activity)ctx)
                .setTitle("Mr.FinMan")
                .setText(msg)
                .setIcon(R.drawable.ic_info_outline_white_48dp)
                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                .setDuration(5000)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_danger))
                .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        //Toast.makeText(Activity_my_goals_add.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public static void alertInfo(Context ctx,String msg)
    {
        Alerter.create((Activity)ctx)
                .setTitle("Mr.FinMan")
                .setText(msg)
                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                .setDuration(5000)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_info))
                .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        //Toast.makeText(Activity_my_goals_add.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

    public static void alertPrim(Context ctx,String msg)
    {
        Alerter.create((Activity)ctx)
                .setTitle("Mr.FinMan")
                .setText(msg)
                .setEnterAnimation(R.anim.alerter_slide_in_from_left)
                .setDuration(5000)
                .setBackgroundColorInt(ctx.getResources().getColor(R.color.bootstrap_brand_primary))
                .addButton("OK", R.style.AlertButton, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        //Toast.makeText(Activity_my_goals_add.this, "", Toast.LENGTH_SHORT).show();
                    }
                }).show();
    }

}
