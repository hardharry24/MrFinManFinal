package Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;
import java.util.Date;

import Models.MyBill;
import Models.billerlist;
import Utils.message;
import Utils.methods;

public class BillerListAdaptor extends BaseAdapter {
    ArrayList<billerlist> billerlists;
    Context ctx;
    int index = -1;

    public BillerListAdaptor(ArrayList<billerlist> billerlists, Context ctx) {
        this.billerlists = billerlists;
        this.ctx = ctx;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        index = i;
        final ViewHolder holder;
        billerlist bill = billerlists.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_biller_user_list,null,true);

            holder.lblemail = view.findViewById(R.id.lbl_biller_user_email);
            holder.lblname = view.findViewById(R.id.lbl_biller_user_name);
            holder.lbldueDate = view.findViewById(R.id.lbl_biller_user_duedate);
            holder.lbltype = view.findViewById(R.id.lbl_biller_user_type);
            holder.btnMessage = view.findViewById(R.id.lbl_biller_user_message);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }


        if (bill != null) {

            if (!(bill.getDueDate().toString().contains("/")))
            {
                holder.lbldueDate.setText( ""+bill.getPaymentType());//methods.dateComplete.format(d1)
                holder.lblname.setText(""+bill.getBillname());//""+bill.getLname()+", "+bill.getFname()
                holder.lbltype.setText(""+bill.getBillId());
                holder.lbltype.setVisibility(View.GONE);
                holder.lblemail.setText(""+bill.getLname()+", "+bill.getFname());
            }
            else
            {
                String[] arrdate = bill.getDueDate().split("/");
                Date d1 = new Date(arrdate[1]+"/"+arrdate[0]+"/"+arrdate[2]);

                holder.lbldueDate.setText(""+methods.dateComplete.format(d1)+" "+bill.getPaymentType());//methods.dateComplete.format(d1)
                holder.lblname.setText(""+bill.getBillname());//""+bill.getLname()+", "+bill.getFname()
                holder.lbltype.setText(""+bill.getBillId());
                holder.lbltype.setVisibility(View.GONE);
                holder.lblemail.setText(""+bill.getLname()+", "+bill.getFname());//"Bill Name: "+bill.getBillname()
            }

        }
        holder.lblname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = Integer.parseInt(holder.lbltype.getText().toString());
                billerlist b = (billerlist)getItem(id);
                showDetails(b);
            }
        });
        holder.btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // billerlist b = billerlists.get(i);
                int id = Integer.parseInt(holder.lbltype.getText().toString());
                billerlist b = (billerlist)getItem(id);
                showDetails(b);


            }
        });
        return view;
    }



    @Override
    public int getCount() {
        return billerlists.size();
    }

    @Override
    public Object getItem(int i) {

        for (billerlist bill:billerlists) {
            if (bill.getBillId() == i)
                return  bill;
        }
        return billerlists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return billerlists.get(i).getBillId();
    }



    static class ViewHolder
    {
        TextView lblname;
        TextView lbldueDate;
        TextView lblemail;
        TextView lbltype;
        ImageButton btnMessage;
        ImageButton btnInfo;
    }

    private void showDetails(billerlist b) {
        try {
            //Context context = ctx.getApplicationContext();

            final Dialog dialog = new Dialog(ctx);
            dialog.create();
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            dialog.setContentView(R.layout.dialogue_biller_details);
            dialog.setCancelable(true);
            EditText lbllname, lblfname, lblemail, lblcontact, lblbill, lbldateCreated, lblduedate, lbldesc, lblpaymentType, lblamount;
            Button btnClose;

            lbllname = dialog.findViewById(R.id.lbllname);
            lblfname = dialog.findViewById(R.id.lblfname);
            lblemail = dialog.findViewById(R.id.lblemail);
            lblcontact = dialog.findViewById(R.id.lblcontactNo);
            lblbill = dialog.findViewById(R.id.lblbillname);
            lbldateCreated = dialog.findViewById(R.id.lbldateCreated);
            lblduedate = dialog.findViewById(R.id.lbldateDue);
            lbldesc = dialog.findViewById(R.id.lbldescription);
            lblpaymentType = dialog.findViewById(R.id.lblpaymentType);
            lblamount = dialog.findViewById(R.id.lblbillamount);
            btnClose = dialog.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            lbllname.setText("" + b.getLname());
            lblfname.setText("" + b.getFname());

            String val = b.getContactno();

            if (b.getEmail().toString() != "")
                lblemail.setText("" + b.getEmail());
            else
                lblemail.setText("No Email");
            if (b.getContactno().toString() != "")
                lblcontact.setText("" + b.getContactno());
            else
                lblcontact.setText("No Contact Number");

            lblbill.setText("" + b.getBillname());
            lbldateCreated.setText("" + b.getDateCreated());
            lblduedate.setText("" + b.getDueDate());
            lbldesc.setText("" + b.getDescription());
            lblpaymentType.setText("" + b.getPaymentType());
            lblamount.setText("Php" + methods.formatter.format(b.getAmount()));
            dialog.show();
        }catch (Exception ex)
        {
            Log.e("ERROR",ex.toString());
        }

    }



}
