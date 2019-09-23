package Singleton;

import java.util.Calendar;

public class BorrowTotalSingleton {
    private static BorrowTotalSingleton mInstance;

    Double total = 0.0;
    boolean hasError = false;

    public boolean getHasError() {
        return hasError;
    }


    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public static BorrowTotalSingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new BorrowTotalSingleton();
        return mInstance;
    }


    public static BorrowTotalSingleton resetInstance()
    {
        return mInstance = null;
    }



}
