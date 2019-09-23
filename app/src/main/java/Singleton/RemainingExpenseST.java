package Singleton;

import java.util.ArrayList;

import Models.Category;
import Models.CategoryAmount;

public class RemainingExpenseST {
    private static RemainingExpenseST mInstance;
    private ArrayList<CategoryAmount> list = null;

    public static RemainingExpenseST getInstance()
    {
        if (mInstance == null)
            mInstance = new RemainingExpenseST();
        return mInstance;
    }

    private RemainingExpenseST()
    {
        list = new ArrayList<CategoryAmount>();
    }

    public ArrayList<CategoryAmount> getList() {

        return list;
    }
    public void add(CategoryAmount category)
    {
        list.add(category);
    }
    public void remove(int index)
    {
        list.remove(index);
    }



    public static RemainingExpenseST resetInstance()
    {
        return mInstance = null;
    }



}
