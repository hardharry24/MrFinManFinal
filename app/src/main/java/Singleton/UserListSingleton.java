package Singleton;

import java.util.ArrayList;

import Models.user;

public class UserListSingleton {
    private static UserListSingleton mInstance;
    public static ArrayList<user> userlist;

    public static UserListSingleton getInstance()
    {
        if (mInstance == null)
            mInstance = new UserListSingleton();
        return mInstance;
    }

    private UserListSingleton()
    {
        userlist = new ArrayList<>();
    }

    public void addtoList(user u)
    {
        userlist.add(u);
    }

    public user getUser(int userId)
    {
        for (user u:userlist) {
            if (u.getUserId() == userId)
                return u;
        }
        return null;
    }
    public ArrayList<user> getList()
    {
        return userlist;
    }

    public static UserListSingleton resetInstance()
    {
        return mInstance = null;
    }



}
