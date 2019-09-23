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
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;
import com.teamcipher.mrfinman.mrfinmanfinal.Biller.Activity_dashboard_biller;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;
import java.util.Date;

import Models.Transaction;
import Models.user;
import Utils.methods;


public class UserListAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<user> users;
    View view;

    public UserListAdaptor(Context ctx, ArrayList<user> users)
    {
        this.ctx = ctx;
        this.users = users;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        user u = users.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_admin_manage,null,true);

            holder.lblcount = view.findViewById(R.id.lblcount);
            holder.lblname = view.findViewById(R.id.lblName);
            holder.lbluname = view.findViewById(R.id.lbluname);
            holder.lbltype = view.findViewById(R.id.lblType);
            holder.icon = view.findViewById(R.id.imgStatus);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (u != null)
        {
            holder.lblcount.setText(""+u.getId());
            holder.lbluname.setText("username: "+u.getUsername());

            if(u.getLock() == false)
                holder.icon.setImageDrawable(((Activity_dashboard_admin)ctx).getDrawable(R.drawable.ic_lock_open));
            else
                holder.icon.setImageDrawable(((Activity_dashboard_admin)ctx).getDrawable(R.drawable.ic_lock));

            if (u.getRoleId() == 1)
                holder.lbltype.setText("User");
            else if (u.getRoleId() == 2)
                holder.lbltype.setText("Biller");
            else if (u.getRoleId() == 3)
                holder.lbltype.setText("Admin");
            if (u.getMi().equals(""))
                holder.lblname.setText(u.getLname()+", "+u.getFname());
            else
                holder.lblname.setText(u.getLname()+", "+u.getFname()+" "+u.getMi()+".");


        }
        return view;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lbltype;
        private TextView lblcount;
        private TextView lbluname;
        private ImageView icon;
    }


}
