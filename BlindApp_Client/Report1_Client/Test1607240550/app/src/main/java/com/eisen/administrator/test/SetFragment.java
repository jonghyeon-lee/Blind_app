package com.eisen.administrator.test;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.List;

/**
 * Created by Administrator on 2016-07-17.
 */
public class SetFragment  extends Fragment {
    private Button btn_Logout;
    private Button btn_SendMail;
    private Button btn_SendSMS;
    private final int MY_PERMISSIONS_SEND_SMS=5;
    String mail_title="[초대] Veiled Talk의 세계로 초대합니다.";
    String mailText="플레이스토어에서 'Veiled Talk'을 검색해보세요 !";
    List<NameValuePair> params;
    SharedPreferences prefs;
    ListAdapter adapter;
    ProgressDialog logout_progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS);
        Log.e("SMS","CHK CNT : "+permissionCheck);
        View view = inflater.inflate(R.layout.fragment_set, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.SEND_SMS},1);
        logout_progress = new ProgressDialog(getActivity());
        logout_progress.setMessage(getString(R.string.in_logout));
        logout_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        logout_progress.setIndeterminate(true);
        btn_Logout = (Button) view.findViewById(R.id.logout);
        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.popup_logout_title)
                        .setMessage(R.string.popup_logout_content)
                        .setPositiveButton(R.string.popup_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences.Editor edit = prefs.edit();
                                edit.putString("REG_FROM", "");
                                edit.commit();
                                Intent login = new Intent(getActivity(), LoginActivity.class);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(R.string.popup_no, null).show();
            }
        });
        btn_SendMail = (Button) view.findViewById(R.id.btn_send_mail);
        btn_SendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                emailIntent.setType("plain/text");
                emailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mail_title);
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailText);
                startActivity(emailIntent);
            }
        });
        btn_SendSMS = (Button) view.findViewById(R.id.btn_send_sms);
        btn_SendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.SEND_SMS)) {
                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.SEND_SMS},
                                MY_PERMISSIONS_SEND_SMS);

                    }
                }
            }
        });
        return view;
    }

    public void sendSMS(String smsNumber, String smsText){
       PendingIntent sentIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_SENT_ACTION"),  PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent deliveredIntent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("SMS_DELIVERED_ACTION"), 0);

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 전송 성공
                        Toast.makeText(context, "전송 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        // 전송 실패
                        Toast.makeText(context, "전송실패", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        // 서비스 지역 아님
                        Toast.makeText(context, "서비스 지역이 아닙니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        // 무선 꺼짐
                        Toast.makeText(context, "무선(Radio)가 꺼져있습니다", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        // PDU 실패
                        Toast.makeText(context, "PDU Null", Toast.LENGTH_SHORT).show();
                        break;
                }
//                Toast.makeText(getApplicationContext(),String.valueOf(getResultCode()), Toast.LENGTH_SHORT).show();
            }

        }, new IntentFilter("SMS_SENT_ACTION"));

        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        // 도착 완료
                        Toast.makeText(context, "SMS 도착 완료", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        // 도착 안됨
                        Toast.makeText(context, "SMS 도착 실패", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter("SMS_DELIVERED_ACTION"));

        SmsManager mSmsManager = SmsManager.getDefault();

        mSmsManager.sendTextMessage(smsNumber, null, smsText, sentIntent, deliveredIntent);
        Toast.makeText(getActivity(),"2"+smsText,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS("01073134107", mailText);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.e("SMS","IN if onRequestPermissionsResult");
                    Toast.makeText(getActivity(),"Permission granted",Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("SMS","IN else onRequestPermissionsResult");
                    Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    /*
    private class Logout extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            JSONObject jObj = json.getJSONFromUrl("http://swmae7ubt.cloudapp.net:80/logout",params);

            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {

            String res = null;
            try {
                res = json.getString("response");
                Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
                if(res.equals("Removed Sucessfully")) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("REG_FROM", "");
                    edit.commit();
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    logout_progress.dismiss();
                    //startActivity(login);
                    getActivity().finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    */
}
