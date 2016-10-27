package com.eisen.administrator.test.blind;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eisen.administrator.test.JSONParser;
import com.eisen.administrator.test.R;
import com.eisen.administrator.test.chat.ChatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JongHyun Lee on 2016-07-23.
 */
public class BlindMemberDetailActivity extends Activity {

    Button btn_Chat, btn_AddFriend;
    String tel;
    public String TelNumber;
    List<NameValuePair> params;
    ProgressDialog addFriend_progress;
    SharedPreferences prefs;
    // called when DetailsFragmentListener's view needs to be created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_detail);
        // inflate DetailsFragment's layout

        prefs = this.getSharedPreferences("Chat", 0);
        SharedPreferences.Editor edit = prefs.edit();
        addFriend_progress = new ProgressDialog(this);
        addFriend_progress.setMessage(getString(R.string.in_create));
        addFriend_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        addFriend_progress.setIndeterminate(true);
        btn_Chat = (Button) findViewById(R.id.btn_on_chat);
        btn_AddFriend = (Button) findViewById(R.id.btn_add_friend);

        // get the EditTexts
        ImageView profileImageView = (ImageView) findViewById(R.id.friend_profile);
        TextView nameTextView = (TextView) findViewById(R.id.friend_nameTextView);
        TextView phoneTextView = (TextView) findViewById(R.id.friend_phoneTextView);

        Intent intent = getIntent(); // 보내온 Intent를 얻는다
        nameTextView.setText(prefs.getString("name",""));
        phoneTextView.setText(prefs.getString("mobno",""));
        Log.e("TextView",prefs.getString("name","")+" : "+prefs.getString("mobno",""));
        // profileImageView.setImageResource(intent.getIntExtra("profile", 0));

        btn_Chat.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               SharedPreferences.Editor edit = prefs.edit();
               edit.putString("mobno",  prefs.getString("mobno",""));
               //edit.putString("name", users.get(position).get("name"));
               edit.commit();

               Bundle args = new Bundle();
               args.putString("mobno", prefs.getString("mobno",""));
               Intent chat = new Intent(getApplication(), ChatActivity.class);
               chat.putExtra("INFO", args);
               chat.putExtra("ME",0);
               startActivity(chat);
           }
        });

        btn_AddFriend.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                new AddFriend().execute();
                //callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivity(callIntent);
            }
        });
    }

    private class AddFriend extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("rq_mobno", prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("rcv_mobno", prefs.getString("mobno","")));
            Log.e("MOB_NO",prefs.getString("REG_FROM","")+" : "+prefs.getString("mobno",""));
            JSONObject jObj = json.getJSONFromUrl("http://swmae7ubt.cloudapp.net:80/addfriend",params);
            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            addFriend_progress.dismiss();
            try {
                String res = json.getString("response");
                if(res.equals("Sucessfully Add Friend")) {
                    Toast.makeText(getApplication(), res, Toast.LENGTH_SHORT).show();
                }else{
                   Toast.makeText(getApplication(), res, Toast.LENGTH_SHORT).show();
                }
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
