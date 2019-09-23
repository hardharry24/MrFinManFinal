package com.teamcipher.mrfinman.mrfinmanfinal;

import android.icu.text.DisplayContext;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Adapters.MessageAdaptor;
import Adapters.UserListAdaptor;
import Models.Message;
import Models.user;
import Utils.ExternalStorageUtil;
import Utils.message;
import Utils.methods;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class Testing extends AppCompatActivity {
    ArrayList<Message> messages = new ArrayList<>();
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
        listView = findViewById(R.id.listviewMessages);
        populate();
        //messages = getMessages();


    }

    private ArrayList<Message> getMessages() {
        Message m = new Message();
        m.setDate("1232");
        m.setId(1);
        m.setMsg("Hellosdadasdweyyyyfsdfsdfsdfsdfsdfsdfsdfsdfyoooweyyyyfsdfsdfsdfsdfsdfsdfsdfsdfyoooweyyyyfsdfsdfsdfsdfsdfsdfsdfsdfyooo");
        m.setType("USER");
        messages.add(m);

        Message m1 = new Message();
        m1.setDate("56565");
        m1.setId(2);
        m1.setMsg("wesfsyfdsdfsyyyfsdfsdfsdfsdfsdfsdfsdfsdfyooo");
        m1.setType("BILLER");
        messages.add(m1);

        Message m3 = new Message();
        m3.setDate("56565");
        m3.setId(3);
        m3.setMsg("weyyyyfsdfsdfsdfsdfsdfsdfsdfsdfyooo");
        m3.setType("USER");
        messages.add(m3);
        return messages;
    }


    private void populate() {
            //userArrayList.clear();
            AndroidNetworking.get(methods.BILLER_API_SERVER+"getMessage.php?userId_1=42&userId_2=41")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            for (int i =0 ;i<response.length(); i++)
                            {
                                try {
                                    JSONObject jObject = response.getJSONObject(i);
                                    Message msg = new Message();
                                    msg.setId(jObject.getInt("id"));
                                    msg.setType(jObject.getString("type"));
                                    msg.setMsg(jObject.getString("message"));
                                    msg.setDate(jObject.getString("created"));

                                    messages.add(msg);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Testing.this, ""+e, Toast.LENGTH_SHORT).show();
                                }

                            }

                            MessageAdaptor adaptor = new MessageAdaptor(getApplicationContext(),messages);
                            listView.setAdapter(adaptor);

                        }
                        public void onError(ANError error) {
                            message.error(""+error,Testing.this);
                        }
                    });

    }

    public void generate(View view)
    {
        //Toast.makeText(this, ""+userArrayList.size(), Toast.LENGTH_SHORT).show();
        String publicDocDirPath = ExternalStorageUtil.getPublicExternalStorageBaseDir(Environment.DIRECTORY_DOWNLOADS);
        File newFile = new File(publicDocDirPath,"Report"+".xls");
        //File file = new File("C:\\Users\\abbe\\Desktop\\android\\file.xls");

        try {
            WritableWorkbook  workbook = Workbook.createWorkbook(newFile);
            WritableSheet sheet = workbook.createSheet("Users",0);

            String[] header = {"USER ID","LASTNAME","FIRSTNAME","MI","EMAIL","CONTACT NUMBER","USERNAME","ROLE ID"};


            for(int i=0; i<header.length;i++)
            {
                Label label = new Label(i,0,header[i]);
                sheet.addCell(label);

            }

           /* for(int j=0;j<userArrayList.size();j++)
            {
                user u = userArrayList.get(j);
                String[] info = {""+u.getUserId(),""+u.getLname(),""+u.getFname(),""+u.getMi(),""+u.getEmail(),""+u.getContactNo(),""+u.getUsername(),""+u.getRoleId()};

                for (int i=0; i<header.length; i++)
                {
                    Label label = new Label(i,j+1,info[i]);
                    sheet.addCell(label);
                }

            }
*/
            workbook.write();
            workbook.close();

            Log.e("FILE","SUCCESS "+publicDocDirPath);

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FILE","FAIL "+e);

        } catch (WriteException e) {
            e.printStackTrace();
            Log.e("FILE","FAIL "+e);
        }

    }
   /* public boolean checkDate()
    {
        Date today = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        Date lastDayOfMonth = calendar.getTime();

        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // System.out.println("Today            : " + sdf.format(today));
        // System.out.println("Last Day of Month: " + sdf.format(lastDayOfMonth));
        //if ()
    }*/

}
