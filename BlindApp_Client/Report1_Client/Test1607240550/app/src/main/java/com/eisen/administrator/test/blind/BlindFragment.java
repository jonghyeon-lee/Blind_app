package com.eisen.administrator.test.blind;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.eisen.administrator.test.JSONParser;
import com.eisen.administrator.test.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016-07-17.
 */
public class BlindFragment extends Fragment {
    ListView mLv_member;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    List<NameValuePair> params;
    SharedPreferences prefs;
    ListAdapter la_AllMemberAdapter;
    ImageView refresh;
    ProgressDialog sync_progress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_live, container, false);
        prefs = getActivity().getSharedPreferences("Chat", 0);
        sync_progress = new ProgressDialog(getActivity());
        sync_progress.setMessage(getString(R.string.in_reload));
        sync_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        sync_progress.setIndeterminate(true);
        mLv_member = (ListView)view.findViewById(R.id.live_listView);
        refresh = (ImageView)view.findViewById(R.id.refresh_blinder);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.clear();
                sync_progress.show();
                new Load().execute();
            }
        });

        la_AllMemberAdapter = new BlindListAdapter(getActivity().getApplicationContext(), users ,R.layout.fragment_member_item);

        new Load().execute();
        return view;
    }

    private class Load extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            return json.getJSONArray("http://swmae7ubt.cloudapp.net:80/getuser",params);
        }
        @Override
        protected void onPostExecute(JSONArray json) {
            mLv_member.setSelection(0);
            //json.getString(0);
            for(int i = 0; i < json.length(); i++){
                JSONObject c = null;
                try {
                    c = json.getJSONObject(i);
                    String name = c.getString("name");
                    String mobno = c.getString("mobno");
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put("name", name);
                    map.put("mobno", mobno);
                    users.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            la_AllMemberAdapter = new BlindListAdapter(getActivity().getApplicationContext(), users ,R.layout.fragment_member_item);         // 데이터
            //dapter = new SimpleAdapter(getActivity(), users, R.layout.fragment_live_list_single, new String[] { "name","mobno" }, new int[] {R.id.name, R.id.mobno});
            mLv_member.setAdapter(la_AllMemberAdapter);
            mLv_member.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("mobno", users.get(position).get("mobno"));
                    edit.putString("name", users.get(position).get("name"));
                    edit.commit();
                    Bundle args = new Bundle();
                    args.putString("mobno", users.get(position).get("mobno"));
                    args.putString("name", users.get(position).get("name"));
                    Intent chat = new Intent(getActivity(), BlindMemberDetailActivity.class);
                    chat.putExtra("INFO", args);
                    chat.putExtra("ME",0);
                    startActivity(chat);
                }
            });
            sync_progress.dismiss();
        }
    }
}
