package Singleton;

import com.google.gson.annotations.SerializedName;

public class UserLogin {

    @SerializedName("username")
    String username;
    @SerializedName("lastname")
    String lname;
    @SerializedName("firstname")
    String fname;
    @SerializedName("MI")
    String mi;
    @SerializedName("email")
    String email;
    @SerializedName("contactNo")
    String contactNo;
    int billerId;

    int role;
    @SerializedName("userID")
    int userId;
    @SerializedName("password")
    String password;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getBillerId() {
        return billerId;
    }

    public void setBillerId(int billerId) {
        this.billerId = billerId;
    }

    public String getMi() {
        return mi;
    }

    public void setMi(String mi) {
        this.mi = mi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getFullname()
    {
        if (mi == "")
            return fname+" "+mi+"."+" "+lname;
        else
            return fname+""+" "+lname;
    }

    int user_ID;

    public static UserLogin instance;

    public static UserLogin getInstance() {
        if (instance == null)
            instance = new UserLogin();
        return instance;
    }

    public UserLogin()
    {

    }

    public UserLogin(String username, String lname, String fname, int user_ID) {
        this.username = username;
        this.lname = lname;
        this.fname = fname;
        this.user_ID = user_ID;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public int getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(int user_ID) {
        this.user_ID = user_ID;
    }

    public String fullname()
    {
        return getFname()+" "+getLname();
    }

    public static UserLogin resetInstance()
    {
       return instance = null;
    }




}
