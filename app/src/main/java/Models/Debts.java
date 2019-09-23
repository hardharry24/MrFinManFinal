package Models;

import android.widget.ListView;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Debts {
    @SerializedName("debtId")
    int id;
    @SerializedName("debtName")
    String name;
    @SerializedName("amount")
    Double amount;
    @SerializedName("categoryId")
    int categoryId;
    @SerializedName("categoryDesc")
    String categoryDesc;
    @SerializedName("dueDate")
    String dueDate;
    @SerializedName("description")
    String description;
    @SerializedName("period")
    String period;
    @SerializedName("dateCreated")
    String date;
    @SerializedName("noDays")
    int noDays;
    @SerializedName("equivalent")
    double equivalent;
    @SerializedName("balance")
    double balance;
    @SerializedName("icon")
    String icon;
    @SerializedName("isNotify")
    int isNotify;
    @SerializedName("isNotifyBefore")
    int isNotifyBefore;

    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }

    @SerializedName("output")
    List<Debts> deptlist;

    public List<Debts> getDeptlist() {
        return deptlist;
    }

    public void setDeptlist(List<Debts> deptlist) {
        this.deptlist = deptlist;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getNoDays() {
        return noDays;
    }

    public void setNoDays(int noDays) {
        this.noDays = noDays;
    }

    public double getEquivalent() {
        return equivalent;
    }

    public void setEquivalent(double equivalent) {
        this.equivalent = equivalent;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Debts(String date, int id, String name, Double amount) {
        this.date = date;
        this.id = id;
        this.name = name;
        this.amount = amount;
    }
    public Debts()
    {

    }


    public int getIsNotifyBefore() {
        return isNotifyBefore;
    }

    public void setIsNotifyBefore(int isNotifyBefore) {
        this.isNotifyBefore = isNotifyBefore;
    }

    public int getIsNotify() {
        return isNotify;
    }

    public void setIsNotify(int isNotify) {
        this.isNotify = isNotify;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmout() {
        return amount;
    }

    public void setAmout(Double amout) {
        this.amount = amout;
    }



    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
