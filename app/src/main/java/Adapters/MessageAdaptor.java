package Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;
import java.util.Date;

import Models.Message;
import Models.User.history;
import Utils.methods;

public class MessageAdaptor extends BaseAdapter {

    Context ctx;
    ArrayList<Message> messages;

    public MessageAdaptor(Context ctx, ArrayList<Message> messages) {
        this.ctx = ctx;
        this.messages = messages;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       ViewHolder holder;
        Message message = messages.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_message,null,true);

            holder.cardViewBiller = view.findViewById(R.id.cardBiller);
            holder.cardViewUser = view.findViewById(R.id.cardUser);

            holder.lblmessage_biller = view.findViewById(R.id.lblbiller_message);
            holder.lblmessage_user = view.findViewById(R.id.lbluser_message);

            holder.lbldate_biller = view.findViewById(R.id.lbldate_biller);
            holder.lbldate_user = view.findViewById(R.id.lbldate_user);

            holder.imgBiller = view.findViewById(R.id.biller);
            holder.imgUser = view.findViewById(R.id.user);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (message != null)
        {
            if (message.getType().equals("USER"))
            {
                holder.cardViewBiller.setVisibility(View.GONE);
                holder.imgBiller.setVisibility(View.GONE);
                holder.lbldate_user.setVisibility(View.GONE);
                holder.lbldate_user.setVisibility(View.GONE);

                holder.cardViewUser.setVisibility(View.VISIBLE);

                holder.lblmessage_user.setVisibility(View.VISIBLE);
                holder.lblmessage_user.setText(""+message.getMsg());

            }
            else
            {
                holder.cardViewUser.setVisibility(View.GONE);
                holder.imgUser.setVisibility(View.GONE);
                holder.lbldate_biller.setVisibility(View.GONE);

                holder.cardViewBiller.setVisibility(View.VISIBLE);
                holder.lblmessage_biller.setVisibility(View.VISIBLE);

                holder.lblmessage_biller.setText(""+message.getMsg());
            }
        }

        return view;
    }



    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return messages.get(i).getId();
    }

    static class ViewHolder
    {
        private TextView lblmessage_user;
        private TextView lblmessage_biller;
        private TextView lbldate_user;
        private TextView lbldate_biller;
        private TextView lbldetails;
        private CardView cardViewUser;
        private CardView cardViewBiller;

        private ImageView imgBiller;
        private ImageView imgUser;
    }



}
