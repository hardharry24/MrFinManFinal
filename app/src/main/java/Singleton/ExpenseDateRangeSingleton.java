package Singleton;

import java.util.Date;

public class ExpenseDateRangeSingleton {

    private static ExpenseDateRangeSingleton instance;

    Date startDate;
    Date endDate;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static ExpenseDateRangeSingleton getInstance()
    {
        if (instance == null)
            instance = new ExpenseDateRangeSingleton();
        return instance;
    }



    public static ExpenseDateRangeSingleton resetInstance()
    {
        return instance = null;
    }








}
