package Singleton;

import java.util.ArrayList;

import Models.Priorities;

public class PrioritiesSingleton {
    private static PrioritiesSingleton mInstance;
    private ArrayList<Priorities> list = null;

    public static PrioritiesSingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new PrioritiesSingleton();
        return mInstance;
    }

    private PrioritiesSingleton()
    {
        list = new ArrayList<Priorities>();
    }

    public ArrayList<Priorities> getList() {

        return list;
    }
    public void add(Priorities priority)
    {
        list.add(priority);
    }
    public void remove(int index)
    {
        list.remove(index);
    }



    public static PrioritiesSingleton resetInstance()
    {
        return mInstance = null;
    }



}
