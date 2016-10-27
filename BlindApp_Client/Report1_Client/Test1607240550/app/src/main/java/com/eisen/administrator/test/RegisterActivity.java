package com.eisen.administrator.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-18.
 */
public class RegisterActivity extends FragmentActivity {
    List<NameValuePair> params;
    Context context;
    SharedPreferences prefs;
    EditText name, mobno;
    Button btn_CreateID;
    String pName,pMobno;
    ProgressDialog create_progress;
    public void onCreate(Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.login_fragment, container, false);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_register);
        prefs = getSharedPreferences("Chat", 0);
        context = getApplicationContext();

        create_progress = new ProgressDialog(this);
        create_progress.setMessage(getString(R.string.in_create));
        create_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        create_progress.setIndeterminate(true);
        name = (EditText) findViewById(R.id.reg_name);
        mobno = (EditText) findViewById(R.id.reg_mobno);
        btn_CreateID = (Button) findViewById(R.id.register_btn);
        btn_CreateID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pName =  name.getText().toString();
                pMobno =  mobno.getText().toString();
                if(!pName.isEmpty() && !pMobno.isEmpty()) {
                    create_progress.show();
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", mobno.getText().toString());
                    edit.putString("FROM_NAME", name.getText().toString());
                    edit.commit();
                    new Create().execute();
                }
                else
                {
                    Toast.makeText(getApplication(), R.string.toast_require_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class Create extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", pName));
            params.add(new BasicNameValuePair("mobno", pMobno));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));
            //Log.e("TAG","name:"+ name.getText().toString() +", mobno:" + mobno.getText().toString()+ ", reg_id:"+prefs.getString("REG_ID",""));
            JSONObject jObj = json.getJSONFromUrl("http://swmae7ubt.cloudapp.net:80/create",params);
            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            create_progress.dismiss();
            try {
                //json.getString
                String res = json.getString("response");
                if(res.equals("Sucessfully Registered")) {
                    name.setText("");
                    mobno.setText("");
                    Bundle args = new Bundle();
                    // args.putString("email", users.get();;
                    Intent info = new Intent(getApplication(),MainActivity.class);
                    info.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    info.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    info.putExtra("INFO", args);
                    startActivity(info);
                    finish();
                }else{
                    //TEST/Toast.makeText(getApplication(), res, Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
