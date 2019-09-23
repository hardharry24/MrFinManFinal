package Singleton;


public class UserToken {
    private static UserToken mInstance;

    String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static UserToken getInstance()
    {
        if (mInstance == null)
            mInstance = new UserToken();
        return mInstance;
    }


    public static UserToken resetInstance()
    {
        return mInstance = null;
    }



}
