package com.eisen.administrator.test.mem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.eisen.administrator.test.JSONParser;
import com.eisen.administrator.test.R;
import com.eisen.administrator.test.chat.ChatActivity;

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
public class MemberFragment extends Fragment {
    SharedPreferences prefs;
    private ListView lv_CurrentUser;
    ArrayList<MemberInfo> al = new ArrayList<MemberInfo>();
    ListView lv_Friend;
    ArrayList<HashMap<String, String>> users = new ArrayList<HashMap<String, String>>();
    List<NameValuePair> params;
    ListAdapter la_FriendAdapter;
    ImageView lv_RefreshFriend;
    ProgressDialog friend_progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        prefs = getActivity().getSharedPreferences("Chat", 0);
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        SharedPreferences.Editor edit = prefs.edit();
        //edit.putString("REG_FROM", mobno.getText().toString());
        //edit.putString("FROM_NAME", name.getText().toString());

        al.add(new MemberInfo(prefs.getString("FROM_NAME",""), R.drawable.prf_default,prefs.getString("REG_FROM","") ));

        MemberAdapter adapter = new MemberAdapter(getActivity().getApplicationContext(),R.layout.fragment_member_item, al);         // 데이터
        lv_CurrentUser = (ListView)view.findViewById(R.id.mystate);
        lv_CurrentUser.setAdapter(adapter);
        lv_CurrentUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*Intent intent = new Intent(getActivity().getApplicationContext(), MyInfoFragment.class);
                intent.putExtra("name", al.get(position).GetName());
                intent.putExtra("profile", al.get(position).GetProf());
                intent.putExtra("state", al.get(position).GetState());
                startActivity(intent);*/
                Toast.makeText(getActivity(),"HI !",Toast.LENGTH_SHORT).show();
            }
        });
        lv_Friend = (ListView)view.findViewById(R.id.Friend);
        friend_progress = new ProgressDialog(getActivity());
        friend_progress.setMessage(getString(R.string.in_reload));
        friend_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        friend_progress.setIndeterminate(true);

        la_FriendAdapter = new FriendListAdapter(getActivity().getApplicationContext(), users ,R.layout.fragment_friend_item);
        lv_RefreshFriend = (ImageView) view.findViewById(R.id.refresh_friend);
        lv_RefreshFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.clear();
                friend_progress.setMessage(getString(R.string.in_reload));
                friend_progress.show();
                new LoadFriend().execute();
            }
        });
        friend_progress.show();
        new LoadFriend().execute();

        return view;
    }

    private class LoadFriend extends AsyncTask<String, String, JSONArray> {

        @Override
        protected JSONArray doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("mobno", prefs.getString("REG_FROM","")));
            return json.getJSONArray("http://swmae7ubt.cloudapp.net:80/getfriend",params);


        }
        @Override
        protected void onPostExecute(JSONArray json) {
            lv_Friend.setSelection(0);
            Log.e("JSON",json.length()+"");

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
            la_FriendAdapter = new FriendListAdapter(getActivity().getApplicationContext(), users ,R.layout.fragment_member_item);         // 데이터
            //dapter = new SimpleAdapter(getActivity(), users, R.layout.fragment_live_list_single, new String[] { "name","mobno" }, new int[] {R.id.name, R.id.mobno});
            lv_Friend.setAdapter(la_FriendAdapter);
            lv_Friend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Bundle args = new Bundle();
                    args.putString("mobno", users.get(position).get("mobno"));
                    Intent chat = new Intent(getActivity(), ChatActivity.class);
                    chat.putExtra("INFO", args);
                    chat.putExtra("ME",0);
                    startActivity(chat);
                }
            });
            friend_progress.dismiss();
        }
    }

}
