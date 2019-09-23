package Singleton;

public class IncomeSingleton {
    private static IncomeSingleton instance;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private double total = 0;
    private double allIncome = 0;
    private double incomeMonthly;

    public double getIncomeMonthly() {
        return incomeMonthly;
    }

    public void setIncomeMonthly(double incomeMonthly) {
        this.incomeMonthly = incomeMonthly;
    }

    public double getAllIncome() {
        return allIncome;
    }

    public void setAllIncome(double allIncome) {
        this.allIncome = allIncome;
    }

    public static IncomeSingleton getInstance() {
        if (instance == null)
            instance = new IncomeSingleton();
        return instance;
    }

    public static IncomeSingleton resetInstance()
    {
        return instance = null;
    }
}
