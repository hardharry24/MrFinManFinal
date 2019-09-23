package Singleton;

import java.util.ArrayList;

import Models.Category;
import Models.User.history;

public class HistorySingleton {
    private static HistorySingleton mInstance;
    private ArrayList<history> list = null;

    public static HistorySingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new HistorySingleton();
        return mInstance;
    }

    private HistorySingleton()
    {
        list = new ArrayList<history>();
    }

    public ArrayList<history> getList() {

        return list;
    }
    public void add(history category)
    {
        list.add(category);
    }
    public void remove(int index)
    {
        list.remove(index);
    }


    public static HistorySingleton resetInstance()
    {
        return mInstance = null;
    }



}
