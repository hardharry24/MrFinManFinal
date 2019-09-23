package Models;

import com.google.gson.annotations.SerializedName;

public class biller {
    @SerializedName("billerId")
    int id;
    @SerializedName("billerName")
    String name;
    @SerializedName("billerAddress")
    String Address;
    @SerializedName("billerContactno")
    String Contact;
    @SerializedName("billerEmail")
    String Email;
    boolean isActive;
    @SerializedName("userId")
    int userId;
    @SerializedName("fname")
    String fname;
    @SerializedName("lname")
    String lname;
    @SerializedName("mi")
    String mi;
    @SerializedName("email")
    String repEmail;
    @SerializedName("contactNo")
    String repContact;
    @SerializedName("username")
    String repUsername;
    @SerializedName("password")
    String repPassword;

    public String getFullname()
    {
        if (getMi() == "")
            return getFname()+" "+getMi()+"."+" "+getLname();
        else
            return getFname()+""+" "+getLname();
    }
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getRepEmail() {
        return repEmail;
    }

    public void setRepEmail(String repEmail) {
        this.repEmail = repEmail;
    }

    public String getRepContact() {
        return repContact;
    }

    public void setRepContact(String repContact) {
        this.repContact = repContact;
    }

    public String getRepUsername() {
        return repUsername;
    }

    public void setRepUsername(String repUsername) {
        this.repUsername = repUsername;
    }

    public String getRepPassword() {
        return repPassword;
    }

    public void setRepPassword(String repPassword) {
        this.repPassword = repPassword;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getContact() {
        return Contact;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
