package Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

import java.util.Calendar;
import java.util.Map;

public class customMethod {
    Context ctx;
    public static int getRole(String name)
    {
        if (name == "User")
            return 1;
        else if (name == "Biller")
            return 2;
        else if (name == "Admin")
            return 3;
        else if (name == "All")
            return 4;
        return 0;
    }

    public static int getMoth(int month)//0
    {
        switch (month-1)
        {
            case 0:
                return Calendar.JANUARY;
            case 1:
                return Calendar.FEBRUARY;
            case 2:
                return Calendar.MARCH;
            case 3:
                return Calendar.APRIL;
            case 4:
                return Calendar.MAY;
            case 5:
                return Calendar.JUNE;
            case 6:
                return Calendar.JULY;
            case 7:
                return Calendar.AUGUST;
            case 8:
                return Calendar.SEPTEMBER;
            case 9:
                return Calendar.OCTOBER;
            case 10:
                return Calendar.NOVEMBER;
            case 11:
                return Calendar.DECEMBER;
            default:
                return 0;
        }
    }
    public static void savePreference(Activity act,String title, String key, String value)
    {
        SharedPreferences preferences = act.getSharedPreferences(title,0);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static String getPreference(Activity act,String title,String key)
    {
        SharedPreferences preferences = act.getSharedPreferences(title,0);
        return preferences.getString(key,null);
    }

    public static void setDisAbleEditText(EditText[] editTexts)
    {
        for (EditText editText: editTexts)
            editText.setEnabled(false);
    }
    public static void setEnAbleEditText(EditText[] editTexts)
    {
        for (EditText editText: editTexts)
            editText.setEnabled(true);
    }

    /* AndroidNetworking.get(methods.SMS_API_SERVER+"send.php?pnumber="+pn+"&message="+msg)
            .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
        @Override
        public void onResponse(JSONArray response) {
            //Utils.message.success("\n\n"+pn+" "+response,ctx);
            //Toast.makeText(ctx, ""+response, Toast.LENGTH_LONG).show();
        }
        @Override
        public void onError(ANError error) {
            Utils.message.error(""+error,ctx);
        }
    });*/


}
