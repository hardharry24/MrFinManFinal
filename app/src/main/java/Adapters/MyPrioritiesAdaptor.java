package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.teamcipher.mrfinman.mrfinmanfinal.Admin.Activity_dashboard_admin;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;

import Models.Category;
import Models.user;
import Singleton.MyCategorySingleton;
import Utils.methods;


public class MyPrioritiesAdaptor extends BaseAdapter {
    Context ctx;
    ArrayList<Category> categories;
    View view;
    String todo;
    String finalPriorities = "";


    public MyPrioritiesAdaptor(Context ctx, ArrayList<Category> categories,String todo)
    {
        this.ctx = ctx;
        this.categories = categories;
        this.todo = todo;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        Category u = categories.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_prio_items,null,true);

            holder.lblname = view.findViewById(R.id.lblname);
            holder.imgIcon = view.findViewById(R.id.imgIcon);

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (u != null)
        {
           holder.lblname.setText(""+u.getCategoryName());
            if(todo.equals("EDIT")) {
                holder.lblname.setCheckMarkDrawable(R.drawable.ic_lens_black);
                if (u.getPriority())
                    holder.lblname.setCheckMarkDrawable(R.drawable.ic_check_circle);
            }
            Picasso.get().load(methods.icon_server()+u.getIcon()).transform(methods.transformation).into(holder.imgIcon);
        }

        if(todo.equals("EDIT")) {
            holder.lblname.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckedTextView lblName = (CheckedTextView) view;
                    if (holder.lblname.isChecked()) {
                        holder.lblname.setCheckMarkDrawable(R.drawable.ic_none);
                        holder.lblname.setChecked(false);

                        updateFalse(lblName.getText().toString());
                    } else {
                        holder.lblname.setCheckMarkDrawable(R.drawable.ic_check_circle);
                        holder.lblname.setChecked(true);

                        updateTrue(lblName.getText().toString());
                    }
                }
            });
        }
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
        return 0;
    }

    static class ViewHolder
    {
        private CheckedTextView lblname;
        private ImageView imgIcon;
    }

    public static void updateTrue(String cat)
    {
        for (Category cmt:MyCategorySingleton.getInstance().getList()) {
            if (cat.equals(cmt.getCategoryName()))
                cmt.setPriority(true);
        }
    }
    public static void updateFalse(String cat)
    {
        for (Category cmt:MyCategorySingleton.getInstance().getList()) {
            if (cat.equals(cmt.getCategoryName()))
                cmt.setPriority(false);
        }
    }
}
