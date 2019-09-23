package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.MyBill;
import Models.user;

public class UserAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<user> userlist;

    public UserAdaptor( Context ctx,ArrayList<user> userlist)
    {
        this.ctx = ctx;
        this.userlist = userlist;
    }
    @Override
    public int getCount() {
        return userlist.size();
    }

    @Override
    public Object getItem(int i) {
        return userlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return userlist.get(i).getUserId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        user s = userlist.get(i);
        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_user_list,null,true);

            holder.fullname = view.findViewById(R.id.lblName);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        if (s != null)
        {
            holder.fullname.setText(s.getLname()+", "+s.getFname()+" "+s.getMi()+".");
        }
        return view;
    }

    static class ViewHolder
    {
        TextView fullname;

    }
}
