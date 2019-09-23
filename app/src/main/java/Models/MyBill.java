package Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MyBill {
    @SerializedName("billId")
    int id;
    //@SerializedName("billId")
    int categoryID;
    @SerializedName("billName")
    String billname;
    @SerializedName("dateCreated")
    String dateCreated;
    @SerializedName("amount")
    Double amount;
    @SerializedName("description")
    String desc;
    @SerializedName("dueDate")
    String dueDate;
    @SerializedName("balance")
    double balance;
    //@SerializedName("billId")
    String categoryName;
    //@SerializedName("billId")
    String icon;
    @SerializedName("paymentType")
    String paymentType;
    @SerializedName("isActive")
    Boolean isActive;
    @SerializedName("billerId")
    int billerId;
    @SerializedName("billerName")
    String comName;
    @SerializedName("billerAddress")
    String comAdress;
    @SerializedName("billerContactno")
    String comContactNo;
    @SerializedName("billerfname")
    String billerfname;
    @SerializedName("billerlname")
    String billerlname;
    @SerializedName("billerMIname")
    String billerMIname;
    @SerializedName("isNotify")
    int isNotify;
    @SerializedName("isNotifyBefore")
    int isNotifyBefore;

    @SerializedName("output")
    List<MyBill> getlistBills;

    @SerializedName("code")
    int code;

    public int getCode() {
        return code;
    }

    public List<MyBill> getGetlistBills() {
        return getlistBills;
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

    public String getComName() {
        return comName;
    }

    public void setComName(String comName) {
        this.comName = comName;
    }

    public String getComAdress() {
        return comAdress;
    }

    public void setComAdress(String comAdress) {
        this.comAdress = comAdress;
    }

    public String getComContactNo() {
        return comContactNo;
    }

    public void setComContactNo(String comContactNo) {
        this.comContactNo = comContactNo;
    }

    public String getBillerfname() {
        return billerfname;
    }

    public void setBillerfname(String billerfname) {
        this.billerfname = billerfname;
    }

    public String getBillerlname() {
        return billerlname;
    }

    public void setBillerlname(String billerlname) {
        this.billerlname = billerlname;
    }

    public String getBillerMIname() {
        return billerMIname;
    }

    public void setBillerMIname(String billerMIname) {
        this.billerMIname = billerMIname;
    }
    public String getFullname()
    {
        return getBillerlname()+", "+getBillerfname();
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public int getBillerId() {
        return billerId;
    }

    public void setBillerId(int billerId) {
        this.billerId = billerId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getBillname() {
        return billname;
    }

    public void setBillname(String billname) {
        this.billname = billname;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String billerfullname()
    {
        if (getBillerMIname() == "")
            return getBillerfname()+" "+getBillerMIname()+"."+" "+getBillerlname();
        else
            return getBillerfname()+" "+getBillerlname();
    }



}
