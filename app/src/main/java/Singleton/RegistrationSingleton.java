package Singleton;

import java.util.ArrayList;

import Models.Category;

public class RegistrationSingleton {
    private static RegistrationSingleton mInstance;
    String lname;
    String fname;
    String mi;
    String email;
    String contactNo;
    String username;
    String password;
    String roledId;
    Boolean isBiller;



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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoledId() {
        return roledId;
    }

    public void setRoledId(String roledId) {
        this.roledId = roledId;
    }

    public Boolean getBiller() {
        return isBiller;
    }

    public void setBiller(Boolean biller) {
        isBiller = biller;
    }

    public static RegistrationSingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new RegistrationSingleton();
        return mInstance;
    }

    public static RegistrationSingleton resetInstance()
    {
        return mInstance = null;
    }



}
