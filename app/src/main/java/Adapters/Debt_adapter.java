package Adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import Models.Category_bp_list;
import Models.Debts;
import Utils.methods;

public class Debt_adapter extends ArrayAdapter<Debts> {
    Double total = 0.0;
    ArrayList<Debts> debts;
    Activity ctx;

    public Debt_adapter(Activity context, ArrayList<Debts> debts) {
        super(context, R.layout.listview_my_debts, debts);
        this.debts = debts;
        this.ctx = context;
        savePreference("TOTAL_DEBT","0");
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        Debts debt = debts.get(position);
        if (convertView == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(ctx);
            convertView = inflater.inflate(R.layout.listview_my_debts, null, true);

            holder.lblname = convertView.findViewById(R.id.debt_lbl_name);
            holder.lbldate = convertView.findViewById(R.id.debt_lbl_date);
            holder.lblamount = convertView.findViewById(R.id.debt_lbl_amount);
            holder.lblicon = convertView.findViewById(R.id.debt_lblIcon);
            holder.lbliconStatus = convertView.findViewById(R.id.lbliconStatus);
            holder.lblbalance = convertView.findViewById(R.id.debt_lbl_balance);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }

        if (debt != null)
        {
            //Re arrange formation
            /*String[] arrdate = debts.get(position).getDueDate().split("/");
            Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);
*/
         /*   if (Calendar.getInstance().getTime().before(d1))
                holder.lbliconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_statuc_active));
            else
                holder.lbliconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_status_elapse));*/

            if (debt.getBalance() != 0)
                holder.lbliconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_statuc_active));
            else if (debt.getBalance() > 0)
                holder.lbliconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_status_elapse));
            else
                holder.lbliconStatus.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_check_white_48dp));

            holder.lblbalance.setText("Balance : Php "+methods.formatter.format(debt.getBalance()));
            if (debt.getName().length() > 12)
                holder.lblname.setText(""+debt.getName().substring(0,12));
            else
                holder.lblname.setText(""+debt.getName());
            //+methods.dateComplete.format(d1)+"     "+
            holder.lbldate.setText(""+debt.getNoDays()+" "+debt.getPeriod());
            holder.lblamount.setText("Php "+methods.formatter.format(debt.getAmount()));

            total += debt.getBalance();

            savePreference("TOTAL_DEBT",""+total);

            Picasso.get().load(methods.icon_server()+debt.getIcon()).into(holder.lblicon);
        }

        return convertView;
    }

    @Override
    public Debts getItem(int i) {
        return debts.get(i);
    }

    @Override
    public int getCount() {
        return debts.size();
    }



    @Override
    public long getItemId(int i) {
        return debts.get(i).getId();
    }

    private String getPreference(String key)
    {
        SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
        return preferences.getString(key,null);
    }

    private void savePreference(String key,String value)
    {
        try
        {
            SharedPreferences preferences = ctx.getSharedPreferences("credentials",0);
            SharedPreferences.Editor  editor = preferences.edit();
            editor.putString(key,value);
            editor.commit();
        }
        catch (Exception ex)
        {

        }
    }



    static class ViewHolder
    {
        TextView lblname;
        TextView lbldate;
        TextView lblamount;
        ImageView lblicon,lbliconStatus;
        TextView lblbalance;
    }


}
