package Singleton;

import java.util.Calendar;

import Models.Biller.Biller;

public class BillerSingleton {
    private static BillerSingleton mInstance;

    Biller billerInfo;

    public static BillerSingleton getmInstance() {
        return mInstance;
    }

    public static void setmInstance(BillerSingleton mInstance) {
        BillerSingleton.mInstance = mInstance;
    }

    public Biller getBillerInfo() {
        return billerInfo;
    }

    public void setBillerInfo(Biller billerInfo) {
        this.billerInfo = billerInfo;
    }

    public static BillerSingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new BillerSingleton();
        return mInstance;
    }


    public static BillerSingleton resetInstance()
    {
        return mInstance = null;
    }



}
