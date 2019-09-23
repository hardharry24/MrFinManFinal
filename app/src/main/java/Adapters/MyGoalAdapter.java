package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Models.MyGoals;
import Models.Transaction;
import Utils.methods;

import static Utils.methods.getDateDiff;

public class MyGoalAdapter extends BaseAdapter {

    Context ctx;
    ArrayList<MyGoals> myGoals;

    public MyGoalAdapter(Context ctx, ArrayList<MyGoals> myGoals) {
        this.ctx = ctx;
        this.myGoals = myGoals;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       ViewHolder holder;
        MyGoals goals = myGoals.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_my_goals_list,null,true);

            holder.lblamount = view.findViewById(R.id.lbl_mygoal_amount);
            holder.lblname = view.findViewById(R.id.lbl_mygoal_name);
            holder.lbldate = view.findViewById(R.id.lbl_mygoal_date);
            holder.img = view.findViewById(R.id.img_listview_my_goal);
            holder.IconStatus = view.findViewById(R.id.lbliconStatus);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (goals != null)
        {

            String[] arrdate = goals.getTargetDate().split("/");
            Date d1 = new Date(arrdate[0]+"/"+arrdate[1]+"/"+arrdate[2]);
            //Date

            if (Calendar.getInstance().getTime().before(d1))
                holder.IconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_statuc_active));
            else
                holder.IconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_status_elapse));

            holder.lblamount.setText("Php "+methods.formatter.format(Double.parseDouble( goals.getAmount())));
            Date dt = Calendar.getInstance().getTime();
            String dtNow = methods.date.format(dt);
            String dtEnd = methods.date.format(d1);

            int remDays = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), dtNow, dtEnd);
            holder.lbldate.setText("Target Date : "+methods.date_dMMM_yyyy.format(d1)+"  ("+remDays+" day/s left)");//methods.dateComplete.format(
            holder.lblname.setText(goals.getName());

            Picasso.get().load(methods.icon_server()+goals.getIcon()).into(holder.img);
        }

        return view;
    }

    @Override
    public int getCount() {
        return myGoals.size();
    }

    @Override
    public Object getItem(int i) {
        return myGoals.get(i);
    }

    @Override
    public long getItemId(int i) {
        return myGoals.get(i).getId();
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lbldate;
        private TextView lblamount;
        private ImageView img,IconStatus;
    }



}
