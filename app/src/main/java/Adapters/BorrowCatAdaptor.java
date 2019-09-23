package Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import Models.Category;
import Models.CategoryAmount;
import Singleton.BorrowTotalSingleton;
import Singleton.CategoryTotalSingleton;
import Singleton.MyCategorySingleton;
import Utils.customMethod;
import Utils.message;
import Utils.methods;


public class BorrowCatAdaptor extends BaseAdapter {
    public static ArrayList<CategoryAmount> categoryAmounts;
    Activity ctx;
    int tempItemValue = 0;
    int selectedId;
    boolean hasError = false;


    public BorrowCatAdaptor(Activity context, ArrayList<CategoryAmount> categoryAmounts,int selectedId) {
        this.categoryAmounts = categoryAmounts;
        this.ctx = context;
        this.selectedId = selectedId;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View view, @NonNull ViewGroup parent) {
        final CategoryAmount ctAmt = categoryAmounts.get(position);
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_borrow_cat, null, true);

            holder.txtCategory = view.findViewById(R.id.borrowCatName);
            holder.txtRemaining = view.findViewById(R.id.borrowRemaining);
            holder.txtbalance = view.findViewById(R.id.borrowBal);
            holder.imageView = view.findViewById(R.id.borrowImg);
            holder.txtBorrowAmount = view.findViewById(R.id.borrowtxtAmt);

            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (ctAmt != null)
        {

            Picasso.get()
                    .load(methods.icon_server()+""+categoryAmounts.get(position).getIcon())
                    .into(holder.imageView );
            holder.txtCategory.setText(""+ctAmt.getCategoryName());
            holder.txtbalance.setText(""+methods.formatter.format(ctAmt.getAmount()));

            Double remaining = Double.parseDouble(ctAmt.getAmount().toString().replace(",", "")) - Double.parseDouble(holder.txtBorrowAmount.getText().toString());
            holder.txtRemaining.setText(""+methods.formatter.format(remaining));

            if (selectedId == ctAmt.getCategoryId()) {
                holder.txtBorrowAmount.setEnabled(false);
                holder.txtBorrowAmount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ctx, "Not available!", Toast.LENGTH_SHORT).show();
                    }
                });
                //holder.txtBorrowAmount.setFocusable(false);
            }
          
            holder.txtBorrowAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    double amtPr = 0.0;
                    if (!b)
                    {
                        try {
                            final EditText value = (EditText) view;
                            if (!(TextUtils.isEmpty(value.getText().toString()))) {
                                Double val = Double.parseDouble(value.getText().toString());
                                Double remaining = Double.parseDouble(ctAmt.getAmount().toString().replace(",", "")) - val;
                                ctAmt.setAmountBorrow(val);
                                if (remaining < 0) {
                                    value.setError("Amount to borrow must be lesser than the balance!");
                                    BorrowTotalSingleton.getInstance().setHasError(true);
                                }
                                else {
                                    BorrowTotalSingleton.getInstance().setHasError(false);
                                }
                                holder.txtRemaining.setText("" + methods.formatter.format(remaining));

                                if (val < 10 && val != 0)
                                    value.setError("Minimum amount to borrow is 10.00");
                            }
                            else
                            {
                                ctAmt.setAmountBorrow(0.0);
                            }
                        }catch (Exception ex) {
                            Log.e("TESTING",ex.toString());
                        }
                    }

                }
            });
        }
        return view;
    }


    @Override
    public int getCount() {
        return categoryAmounts.size();
    }

    @Nullable
    @Override
    public CategoryAmount getItem(int position) {
        return categoryAmounts.get(position);
    }

    @Override
    public long getItemId(int i) {
        return categoryAmounts.get(i).getCategoryId();
    }

    private class ViewHolder
    {
        protected TextView txtRemaining;
        protected TextView txtCategory;
        protected ImageView imageView;
        protected TextView txtbalance;
        protected EditText txtBorrowAmount;
    }
}
