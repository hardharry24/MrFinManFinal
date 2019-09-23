package Models.Biller;

import com.google.gson.annotations.SerializedName;

public class Biller {
    @SerializedName("billerId")
    int billerId;
    @SerializedName("billerName")
    String billerName;
    @SerializedName("billerContactno")
    String billerContactno;
    @SerializedName("billerEmail")
    String billerEmail;

    public int getBillerId() {
        return billerId;
    }

    public void setBillerId(int billerId) {
        this.billerId = billerId;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getBillerContactno() {
        return billerContactno;
    }

    public void setBillerContactno(String billerContactno) {
        this.billerContactno = billerContactno;
    }

    public String getBillerEmail() {
        return billerEmail;
    }

    public void setBillerEmail(String billerEmail) {
        this.billerEmail = billerEmail;
    }
}
