package Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.Category;
import Singleton.IncomeSingleton;
import Utils.methods;


public class MyBudgetPlanAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Category> categories;

    public MyBudgetPlanAdapter(Context ctx, ArrayList<Category> categories) {
        this.ctx = ctx;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Category ctbp = categories.get(position);

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_my_budget_plan, null, true);


            holder.lblname = view.findViewById(R.id.my_budget_plan_name);
            holder.lblperentage = view.findViewById(R.id.my_budget_plan_perc);
            holder.imageView = view.findViewById(R.id.my_budget_plan_icon);
            holder.lblprcRemaining = view.findViewById(R.id.my_budget_plan_rem_perc);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
            double total = IncomeSingleton.getInstance().getAllIncome();
            double perc = ctbp.getPercentage();
            double eq = (perc / 100 ) * total;
            holder.lblname.setText(ctbp.getCategoryName());
            holder.lblperentage.setText(" ( Php "+methods.formatter.format(ctbp.getRemaining())+" / Php "+methods.formatter.format( methods.amount(perc))+" )");//methods.formatter.format( methods.amount(perc))

            holder.lblprcRemaining.setText(methods.percentage(ctbp.getRemaining())+"/"+methods.formatter00.format(perc)+"%");


            Picasso.get().load(methods.icon_server()+ctbp.getIcon()).into(holder.imageView);

        return view;
    }
    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return categories.get(i);
    }

    @Override
    public long getItemId(int i) {
        return categories.get(i).getId();
    }

    static class ViewHolder
    {
        protected TextView lblname;
        protected TextView lblperentage;
        protected ImageView imageView;
        protected TextView lblprcRemaining;
    }

}

