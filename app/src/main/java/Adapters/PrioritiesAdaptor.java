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
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.Priority;
import Models.user;
import Utils.methods;


public class PrioritiesAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<Priority> priorities;
    View view;

    public PrioritiesAdaptor(Context ctx, ArrayList<Priority> priorities)
    {
        this.ctx = ctx;
        this.priorities = priorities;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try
        {
            ViewHolder holder;
            Priority p = priorities.get(i);
            if (view == null)
            {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.listview_admin_priorities,null,true);

                holder.lblpercentage = view.findViewById(R.id.lblPercentage);
                holder.lblname = view.findViewById(R.id.lblCategory);
                holder.lblPid = view.findViewById(R.id.lbl_pId);
                holder.icon = view.findViewById(R.id.imgIcon);
                view.setTag(holder);
            }
            else
            {
                holder = (ViewHolder)view.getTag();
            }
            if (p != null)
            {
                try {
                    holder.lblPid.setText(""+p.getpId());
                    holder.lblname.setText(""+p.getCategoryDesc());
                    holder.lblpercentage.setText(""+ methods.formatter00.format(p.getPercentage())+"%");
                }
                catch (Exception ex)
                {

                }
                Picasso.get()
                        .load(methods.icon_server()+""+p.getIcon())
                        .into(holder.icon );

            }
        }
        catch (Exception ex)
        {

        }
        return view;
    }

    @Override
    public int getCount() {
        return priorities.size();
    }

    @Override
    public Object getItem(int i) {
        return priorities.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lblPid;
        private TextView lblpercentage;
        private ImageView icon;
    }


}
