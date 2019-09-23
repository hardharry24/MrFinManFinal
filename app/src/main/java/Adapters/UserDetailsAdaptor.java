package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.user;


public class UserDetailsAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<user> users;
    View view;

    public UserDetailsAdaptor(Context ctx, ArrayList<user> users)
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
            view = inflater.inflate(R.layout.listview_admin_user_list,null,true);

            holder.lblcount = view.findViewById(R.id.lblCount);
            holder.lblname = view.findViewById(R.id.lblName);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (u != null)
        {
            holder.lblcount.setText(""+u.getId());


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
