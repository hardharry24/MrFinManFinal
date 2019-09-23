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

import Models.Admin.BillerDetails;
import Models.user;


public class AdminBillerAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<BillerDetails> billerDetails;
    View view;

    public AdminBillerAdaptor(Context ctx, ArrayList<BillerDetails> billerDetails)
    {
        this.ctx = ctx;
        this.billerDetails = billerDetails;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        BillerDetails b = billerDetails.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_admin_manage_biller,null,true);

            holder.lblcount = view.findViewById(R.id.lblcount);
            holder.lblname = view.findViewById(R.id.lblName);
            holder.lbluname = view.findViewById(R.id.lbluname);
            holder.lblRep = view.findViewById(R.id.lblRep);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (b!= null)
        {
            holder.lblcount.setText(""+b.getId());
            holder.lbluname.setText("username: "+b.getBillerEmail());
            holder.lblname.setText(""+b.getBillerName());
            holder.lblRep.setText("Rep: "+b.getRepfullname());


        }
        return view;
    }

    @Override
    public int getCount() {
        return billerDetails.size();
    }

    @Override
    public Object getItem(int i) {
        return billerDetails.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lblcount;
        private TextView lbluname;
        private TextView lblRep;
    }


}
