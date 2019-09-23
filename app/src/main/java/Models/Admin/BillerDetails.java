package Models.Admin;

public class BillerDetails {
    int id;
    int billerId;
    String billerAddress;
    String billerName;
    String billerContactno;
    String billerEmail;
    int userId;
    String Repemail;
    String Repusername;
    String RecontactNo;
    String Repfullname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBillerId() {
        return billerId;
    }

    public void setBillerId(int billerId) {
        this.billerId = billerId;
    }

    public String getBillerAddress() {
        return billerAddress;
    }

    public void setBillerAddress(String billerAddress) {
        this.billerAddress = billerAddress;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRepemail() {
        return Repemail;
    }

    public void setRepemail(String repemail) {
        Repemail = repemail;
    }

    public String getRepusername() {
        return Repusername;
    }

    public void setRepusername(String repusername) {
        Repusername = repusername;
    }

    public String getRecontactNo() {
        return RecontactNo;
    }

    public void setRecontactNo(String recontactNo) {
        RecontactNo = recontactNo;
    }

    public String getRepfullname() {
        return Repfullname;
    }

    public void setRepfullname(String repfullname) {
        Repfullname = repfullname;
    }
}
