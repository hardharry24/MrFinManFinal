package Models;

public class CategoryAmount {
    int categoryId;
    Double amount;
    Double percentage;
    String categoryName;
    String remPercentage;
    double amountBorrow;
    String icon;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getAmountBorrow() {
        return amountBorrow;
    }

    public void setAmountBorrow(double amountBorrow) {
        this.amountBorrow = amountBorrow;
    }

    public String getRemPercentage() {
        return remPercentage;
    }

    public void setRemPercentage(String remPercentage) {
        this.remPercentage = remPercentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
