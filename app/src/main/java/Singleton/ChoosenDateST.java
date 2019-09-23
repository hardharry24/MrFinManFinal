package Singleton;

import java.util.ArrayList;
import java.util.Calendar;

import Models.CategoryAmount;

public class ChoosenDateST {
    private static ChoosenDateST mInstance;

    Calendar date;

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public static ChoosenDateST getInstance()
    {
        if (mInstance == null)
            mInstance = new ChoosenDateST();
        return mInstance;
    }


    public static ChoosenDateST resetInstance()
    {
        return mInstance = null;
    }



}
