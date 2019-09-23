package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.beardedhen.androidbootstrap.BootstrapThumbnail;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import Models.Category;
import Models.Transaction;
import Utils.methods;




public class Transactions extends BaseAdapter {
    Context ctx;
    ArrayList<Transaction> transactions;
    View view;

    public Transactions(Context ctx,ArrayList<Transaction> transactions)
    {
        this.ctx = ctx;
        this.transactions = transactions;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        Transaction tr = transactions.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_transaction_list,null,true);

            holder.lblamount = view.findViewById(R.id.lbl_transaction_amount);
            holder.lblname = view.findViewById(R.id.lbl_transaction_name);
            holder.lbldate = view.findViewById(R.id.lbl_transaction_date);
            holder.icon = view.findViewById(R.id.lblIcon_transaction);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (tr != null)
        {
            if (tr.getType().toString().equals("Expense"))
                holder.lblamount.setTextColor(Color.parseColor("#FF0000"));
            else
                holder.lblamount.setTextColor(Color.parseColor("#228B22"));

            if (tr.getDate().equals(""))
                holder.lbldate.setText("");
            else {
                String[] arrdate = tr.getDate().split(" ");
                String d = arrdate[0].replace('-', '/');
                Date date = new Date(d);
                Date time = new Date(d + " " + arrdate[1]);
                holder.lbldate.setText("" + methods.dateComplete.format(date) + "  " + methods.time.format(time));
            }

            //methods.dateComplete.format(d1)

            holder.lblamount.setText(methods.formatter.format(tr.getAmount()));

           // holder.lbldate.setText(""+tr.getDate());
            holder.lblname.setText(tr.getName());

            Picasso.get().load(methods.icon_server()+tr.getIcon()).into(holder.icon);
        }
        return view;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public Object getItem(int i) {
        return transactions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder
    {
        private TextView lblname;
        private TextView lbldate;
        private TextView lblamount;
        private ImageView icon;
    }


}
