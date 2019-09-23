package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.CategoryAmount;
import Models.user;
import Utils.methods;

public class RemAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<CategoryAmount> amounts = new ArrayList<>();

    public RemAdaptor(Context ctx, ArrayList<CategoryAmount> amounts)
    {
        this.ctx = ctx;
        this.amounts = amounts;
    }
    @Override
    public int getCount() {
        return amounts.size();
    }

    @Override
    public Object getItem(int i) {
        return amounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        CategoryAmount s = amounts.get(i);
        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_rem_list,null,true);

            holder.lblcateogry = view.findViewById(R.id.lblcategory);
            holder.lblremPercentage = view.findViewById(R.id.lblpercentage);
            holder.lblamount = view.findViewById(R.id.lblamount);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        if (s != null)
        {
            holder.lblcateogry.setText(""+s.getCategoryName());
            holder.lblremPercentage.setText("Budget: Php "+s.getRemPercentage());
            holder.lblamount.setText("Php "+ methods.formatter.format(s.getAmount()));
            if (s.getAmount() < 0 ) {
                holder.lblamount.setTextColor(ctx.getResources().getColor(R.color.red));
            }
            else if  (s.getAmount() <= 100 && s.getAmount() < 0) {
                holder.lblamount.setTextColor(ctx.getResources().getColor(R.color.orange));
            }


        }
        return view;
    }

    static class ViewHolder
    {
        TextView lblcateogry,lblamount,lblremPercentage;

    }
}
