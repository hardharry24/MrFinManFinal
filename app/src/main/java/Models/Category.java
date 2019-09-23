package Models;

public class Category {
    int id;
    String categoryName;
    String icon;
    String type;
    Double percentage;
    boolean isPriority = false;
    Double remaining;

    public boolean isPriority() {
        return isPriority;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }

    public boolean getPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }


    public Category()
    {

    }
    public Category(String categoryName, String icon, String type, int id) {
        this.categoryName = categoryName;
        this.icon = icon;
        this.type = type;
        this.id = id;
    }


    public Category(String categoryName, String icon, int id) {
        this.categoryName = categoryName;
        this.icon = icon;
        this.id = id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }



}
