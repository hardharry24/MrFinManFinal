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
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

import Models.MyBill;
import Models.Transaction;
import Utils.methods;

public class MyBillsAdapter extends BaseAdapter {
    ArrayList<MyBill> myBills;
    Context ctx;

    public MyBillsAdapter(ArrayList<MyBill> myBills, Context ctx) {
        this.myBills = myBills;
        this.ctx = ctx;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        MyBill bill = myBills.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_my_bills_list,null,true);

            holder.lblamount = view.findViewById(R.id.lbl_bills_amount);
            holder.lblname = view.findViewById(R.id.lbl_bills_name);
            holder.lbldueDate = view.findViewById(R.id.lbl_bills_date);
            holder.lblId = view.findViewById(R.id.lbl_bills_id);

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }


        if (bill != null) {
            if (!(bill.getDueDate().toString().contains("/")))
            {
                holder.lbldueDate.setText(""+bill.getPaymentType());//methods.dateComplete.format(d1)
                holder.lblname.setText(" "+bill.getBillname());
                holder.lblamount.setText("Php "+methods.formatter.format(bill.getAmount()));
                holder.lblId.setText("ID : "+bill.getId());
            }
            else
            {
                String[] arrdate = bill.getDueDate().split("/");
                Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);

                holder.lbldueDate.setText(""+methods.dateComplete.format(d1)+" "+bill.getPaymentType());//methods.dateComplete.format(d1)
                holder.lblname.setText(" "+bill.getBillname());
                holder.lblamount.setText("Php "+methods.formatter.format(bill.getAmount()));
                holder.lblId.setText("ID : "+bill.getId());
            }

        }

        return view;
    }
    @Override
    public int getCount() {
        return myBills.size();
    }

    @Override
    public Object getItem(int i) {
        return myBills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return myBills.get(i).getId();
    }

    static class ViewHolder
    {
        TextView lblname;
        TextView lbldueDate;
        TextView lblamount;
        TextView lblId;
    }


}
