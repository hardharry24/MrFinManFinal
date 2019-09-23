package Singleton;

import java.util.ArrayList;

import Models.Category;

public class MyCategorySingleton {
    private static MyCategorySingleton mInstance;
    private ArrayList<Category> list = null;

    public static MyCategorySingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new MyCategorySingleton();
        return mInstance;
    }

    private MyCategorySingleton()
    {
        list = new ArrayList<Category>();
    }

    public ArrayList<Category> getList() {

        return list;
    }
    public void add(Category category)
    {
        list.add(category);
    }
    public void remove(int index)
    {
        list.remove(index);
    }

    public double totalVal()
    {
        double sum =0;
        for ( int i=0; i<getList().size();i++)
            sum = sum + getList().get(i).getPercentage();
        return sum;
    }

    public static MyCategorySingleton resetInstance()
    {
        return mInstance = null;
    }



}
