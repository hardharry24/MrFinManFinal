package Singleton;

public class SavingSingleton {
    private static SavingSingleton instance;

    double allSavings;

    public double getAllSavings() {
        return allSavings;
    }

    public void setAllSavings(double allSavings) {
        this.allSavings = allSavings;
    }

    public static SavingSingleton getInstance() {
        if (instance == null)
            instance = new SavingSingleton();
        return instance;
    }

    public static SavingSingleton resetInstance()
    {
        return instance = null;
    }
}
