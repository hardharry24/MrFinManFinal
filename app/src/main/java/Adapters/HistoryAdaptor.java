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
import Models.User.history;
import Utils.methods;

import static Utils.methods.getDateDiff;

public class HistoryAdaptor extends BaseAdapter {

    Context ctx;
    ArrayList<history> histories;

    public HistoryAdaptor(Context ctx, ArrayList<history> histories) {
        this.ctx = ctx;
        this.histories = histories;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       ViewHolder holder;
        history history = histories.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_my_history,null,true);

            holder.lbldetails = view.findViewById(R.id.lbldetails);
            holder.lblname = view.findViewById(R.id.lblname);
            holder.lbldate = view.findViewById(R.id.lbldate);
            holder.lblid = view.findViewById(R.id.lblid);
            holder.img = view.findViewById(R.id.icon);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (history != null)
        {
            holder.lblname.setText(""+history.getActionName());
            if (history.getDetails().length() > 17)
                holder.lbldetails.setText("Details : "+history.getDetails().substring(0,16)+"(.....)");
            else
                holder.lbldetails.setText("Details : "+history.getDetails()+"(.....)");

            holder.lblid.setText(""+history.getId());
            holder.lblid.setVisibility(View.INVISIBLE);
            String[] arrdate = history.getDate().split(" ");
            String d = arrdate[0].replace('-','/');
            Date date = new Date(d);
            Date time = new Date(d+" "+arrdate[1]);
            holder.lbldate.setText(methods.dateComplete.format(date)+" "+methods.time.format(time));

            Picasso.get().load(methods.icon_server()+history.getIcon()).into(holder.img);
        }

        return view;
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int i) {
        return histories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return histories.get(i).getId();
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lbldate;
        private TextView lbldetails;
        private TextView lblid;
        private ImageView img;
    }



}
