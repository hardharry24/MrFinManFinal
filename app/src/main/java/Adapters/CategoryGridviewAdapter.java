package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.teamcipher.mrfinman.mrfinmanfinal.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

import Models.Category;
import Utils.methods;

public class CategoryGridviewAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Category> categories;
    private
    View grid;

    public CategoryGridviewAdapter(Context context, ArrayList<Category> categories) {
        this.mContext = context;
        this.categories = categories;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        Category category = categories.get(i);
        if (view == null)
        {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gridview_category_layout,null,true);

            holder.textView = view.findViewById(R.id.category_gridview_title);
            holder.imageView = view.findViewById(R.id.category_gridview_icon);
            holder.txtviewId = view.findViewById(R.id.category_gridview_id);
            holder.txtviewId.setVisibility(View.INVISIBLE);
            view.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)view.getTag();
        }
        if (category != null)
        {
            String icon_url = methods.icon_server()+categories.get(i).getIcon();
            holder.textView.setText(categories.get(i).getCategoryName());
            holder.txtviewId.setText(""+categories.get(i).getId());
            Picasso.get().load(icon_url).into(holder.imageView);
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
        return categories.get(i).getId();
    }

    private class ViewHolder
    {
        protected TextView textView;
        protected ImageView imageView;
        protected TextView txtviewId;
    }

}

