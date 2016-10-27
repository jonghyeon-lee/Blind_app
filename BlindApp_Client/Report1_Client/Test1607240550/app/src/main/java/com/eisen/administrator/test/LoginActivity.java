package com.eisen.administrator.test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends FragmentActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "PROJECT KEY";
    static final String TAG = "SWM7th";
    GoogleCloudMessaging gcm;
    String regid;
    Context context;

    SharedPreferences prefs;
    EditText name, mobno;
    Button btn_Login;
    Button btn_Register;
    List<NameValuePair> params;
    ProgressDialog login_progress;
    String pName;
    String pMobno;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //View view = inflater.inflate(R.layout.login_fragment, container, false);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        setContentView(R.layout.activity_login);
        name = (EditText) findViewById(R.id.name);
        mobno = (EditText) findViewById(R.id.mobno);
        btn_Login = (Button) findViewById(R.id.log_btn);
        btn_Register = (Button) findViewById(R.id.new_register_btn);
        login_progress = new ProgressDialog(this);
        login_progress.setMessage(getString(R.string.in_login));
        login_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        login_progress.setIndeterminate(true);
        prefs = getSharedPreferences("Chat", 0);
        context = getApplicationContext();
        if(!prefs.getString("REG_FROM","").isEmpty()){  // 로그인 된 상태이므로, 바로 다음 화면
            Bundle args = new Bundle();
            // args.putString("email", users.get();;
            Intent info = new Intent(getApplication(),MainActivity.class);
            info.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            info.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            info.putExtra("INFO", args);
            startActivity(info);
        }else  if(!prefs.getString("REG_ID", "").isEmpty()){ //  GCM 등록은 했으나 비로그인 상태



        }else if(checkPlayServices()){
            new Register().execute();
        }else{
            Toast.makeText(getApplicationContext(),"This device is not supported",Toast.LENGTH_SHORT).show();
        }

        btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pName =  name.getText().toString();
                pMobno =  mobno.getText().toString();
                if(!pName.isEmpty() && !pMobno.isEmpty()) {
                    login_progress.show();
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", mobno.getText().toString());
                    edit.putString("FROM_NAME", name.getText().toString());
                    edit.commit();
                    new Login().execute();
                }
                else
                {
                    Toast.makeText(getApplication(), R.string.toast_require_empty, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Intent RegisterIntent = new Intent(getApplication(),RegisterActivity.class);
                RegisterIntent.putExtra("INFO", args);
                startActivity(RegisterIntent);
            }
        });
    }
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.popup_backkey_title)
                        .setMessage(R.string.popup_backkey_content)
                        .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton(R.string.popup_no, null).show();
                return false;
            default:
                return false;
        }
    }
    private class Register extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(SENDER_ID);
                    Log.e("RegId",regid);

                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_ID", regid);
                    edit.commit();

                }
                return  regid;

            } catch (IOException ex) {
                Log.e("Error", ex.getMessage());
                return "Fails";

            }
        }
        @Override
        protected void onPostExecute(String json) {
            //Fragment reg = new LoginFragment();
           //
        }
    }

    private class Login extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("name", pName));
            params.add(new BasicNameValuePair("mobno", pMobno));
            params.add((new BasicNameValuePair("reg_id",prefs.getString("REG_ID",""))));
            //Log.e("TAG","name:"+ name.getText().toString() +", mobno:" + mobno.getText().toString()+ ", reg_id:"+prefs.getString("REG_ID",""));
            JSONObject jObj = json.getJSONFromUrl("http://swmae7ubt.cloudapp.net:80/login",params);
            return jObj;

        }
        @Override
        protected void onPostExecute(JSONObject json) {
            login_progress.dismiss();
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
                }else{
                    //TEST/Toast.makeText(getApplication(), res, Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}