package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.CategoryAmount;
import Models.User.Saving;
import Utils.methods;

public class SavingAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<Saving> savings = new ArrayList<>();

    public SavingAdaptor(Context ctx,  ArrayList<Saving> savings)
    {
        this.ctx = ctx;
        this.savings = savings;
    }
    @Override
    public int getCount() {
        return savings.size();
    }

    @Override
    public Object getItem(int i) {
        return savings.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Saving s = savings.get(i);
        if (view == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_saving_item,null,true);

            holder.lbldate = view.findViewById(R.id.lblDate);
            holder.lblamount = view.findViewById(R.id.lblAmount);
            holder.lblCat = view.findViewById(R.id.lblCategory);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        if (s != null)
        {
            holder.lblCat.setText(""+s.getCategory());
            holder.lbldate.setText(""+s.getDateCreated());
            holder.lblamount.setText("Php "+methods.formatter.format(s.getAmount()));
        }
        return view;
    }

    static class ViewHolder
    {
        TextView lbldate,lblamount,lblCat;

    }
}
