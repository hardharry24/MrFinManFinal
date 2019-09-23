package Singleton;

public class CategoryTotalSingleton {

    private static CategoryTotalSingleton instance;

    private double total = 0.0;
    public static CategoryTotalSingleton getInstance()
    {
        if (instance == null)
            instance = new CategoryTotalSingleton();
        return instance;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double totalVal()
    {
        double sum =0;
        for ( int i=0; i<MyCategorySingleton.getInstance().getList().size();i++)
            sum = sum + MyCategorySingleton.getInstance().getList().get(i).getPercentage();
        return sum;
    }

    public static CategoryTotalSingleton resetInstance()
    {
        return instance = null;
    }








}
