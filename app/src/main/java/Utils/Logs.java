package Utils;

import android.util.Log;

public class Logs {
    final static String TAG = "Mr.FinMan";

    public static void LOGS(String msg)
    {
        Log.d(TAG,msg);
    }
    public static void LOGS_ADMIN(String msg)
    {
        Log.d(TAG,"ADMIN "+ msg);
    }

    public static void LOGS_BILLER(String msg)
    {
        Log.d(TAG,"BILLER "+ msg);
    }
}
