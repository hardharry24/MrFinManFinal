package Adapters;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Models.Category;
import Models.Category_bp_list;
import Singleton.CategoryTotalSingleton;
import Singleton.IncomeSingleton;
import Singleton.MyCategorySingleton;
import Utils.Logs;
import Utils.message;
import Utils.methods;


public class Category_edit_bp_adapter extends BaseAdapter {
    public static ArrayList<Category> categories;
    Activity ctx;
    double sum = 0.0;
    int tempItemValue = 0;


    public Category_edit_bp_adapter( Activity context, ArrayList<Category> categories) {
        this.categories = categories;
        this.ctx = context;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        final Category ctbp = categories.get(position);

        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_editbudgetplan, null, true);
            holder.textView = view.findViewById(R.id.lbl_bp_category);
            holder.imageView = view.findViewById(R.id.bpicon);
            holder.editTextPerc = view.findViewById(R.id.txt_budget_percentage);
            holder.editTextAmount = view.findViewById(R.id.txt_budget_amount);

            //holder.editTextAmount.edit
            holder.editTextAmount.setEnabled(false);
            holder.editTextAmount.setClickable(false);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (ctbp != null)
        {
            Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(3)
                    .cornerRadiusDp(30)
                    .oval(false)
                    .build();

            Picasso.get()
                    .load(methods.icon_server()+""+categories.get(position).getIcon())
                    .transform(transformation)
                    .into(holder.imageView );

            holder.textView.setText(getItem(position).getCategoryName());


            holder.editTextPerc.setText(""+methods.formatter00.format(ctbp.getPercentage()));
            holder.editTextAmount.setText(""+methods.formatter.format(methods.amount(ctbp.getPercentage())));

            holder.editTextPerc.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (!checkPercentage(MyCategorySingleton.getInstance().totalVal()))
                    {
                        final int pos = view.getId();
                        final EditText value = (EditText)view;
                        value.setError(null);
                        value.setTextColor(Color.parseColor("#FFFFFF"));
                        CategoryTotalSingleton.getInstance().setTotal(MyCategorySingleton.getInstance().totalVal());
                    }
                    return false;
                }
            });

            holder.editTextPerc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    if (!b)
                    {
                        try {
                            final int pos = view.getId();
                            final EditText value = (EditText)view;
                            if (isNumberDo(value.getText().toString()))
                            {
                                Double val = Double.parseDouble(value.getText().toString());
                                ctbp.setPercentage(Double.parseDouble(value.getText().toString()));
                                //Toast.makeText(ctx, ""+ctbp.get, Toast.LENGTH_SHORT).show();
                                holder.editTextAmount.setText(""+ methods.formatter.format(methods.amount(val)));
                                if (checkPercentage(CategoryTotalSingleton.getInstance().totalVal()))
                                {
                                    //value.setError("");
                                    value.setTextColor(Color.parseColor("#FF0000"));

                                    double rem = MyCategorySingleton.getInstance().totalVal() - 100;
                                    methods.showMessage(ctx,"Warning","",R.drawable.ic_close,R.color.orange);

                                    message.warning("You exceed " +methods.formatter00.format(rem) +"% higher from the limit.",ctx);
                                }
                                else
                                    Log.d("TESTING","ERROR checking percentage");
                            }
                            else
                                Log.d("TESTING","ERROR");
                        }
                        catch (Exception ex)
                        {
                            Logs.LOGS(""+ex);
                        }


                    }//percentage = (value / total) * 100
                }
            });

        /*    holder.editTextAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    try
                    {
                        if (!b)
                        {
                            final EditText value = (EditText)view;
                            //Toast.makeText(ctx, ""+value.getText().toString(), Toast.LENGTH_SHORT).show();
                            String itemValue = value.getText().toString().replace(",","");
                            if (isNumber(itemValue))
                            {
                                holder.editTextPerc.setText(""+methods.percentage(Double.parseDouble(itemValue) )  );
                                //Toast.makeText(ctx, ""+methods.percentage(Double.parseDouble(itemValue)), Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(ctx, "Not number", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex)
                    {
                        message.error(""+ex.toString(),ctx);
                    }
                }
            });*/
        }
        return view;
    }
    public Boolean isNumber(String str)
    {
        try {
            Double num = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return  false;
        }
    }

    public Boolean isNumberDo(String str)
    {
        try {
            Double num = Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return  false;
        }
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Nullable
    @Override
    public Category getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int i) {
        return categories.get(i).getId();
    }

    private class ViewHolder
    {
        protected TextView textView;
        protected ImageView imageView;
        protected EditText editTextPerc;
        protected EditText editTextAmount;
    }

    private Boolean checkPercentage(Double value)
    {
        if (value>100)
        {
            return true;
        }
        return false;
    }






}
