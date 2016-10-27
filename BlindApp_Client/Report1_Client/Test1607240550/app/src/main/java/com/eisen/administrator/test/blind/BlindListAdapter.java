package com.eisen.administrator.test.blind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eisen.administrator.test.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016-07-18.
 */
public class BlindListAdapter extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<HashMap<String, String>> alhm_Member;
    LayoutInflater inf;
    //adapter = new SimpleAdapter(getActivity(), users, R.layout.fragment_live_list_single, new String[] { "name","mobno" }, new int[] {R.id.name, R.id.mobno});
    public BlindListAdapter(Context context, ArrayList<HashMap<String, String>> al, int layout) {
        this.context = context;
        this.layout = layout;
        this.alhm_Member = al;
        inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return alhm_Member.size();
    }
    @Override
    public Object getItem(int position) {
        return alhm_Member.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView = inf.inflate(layout, null);
        }
        Map dataSet = alhm_Member.get(position);
        ImageView profile = (ImageView)convertView.findViewById(R.id.my_image);
        TextView name = (TextView)convertView.findViewById(R.id.my_name);
        TextView state = (TextView)convertView.findViewById(R.id.my_state);

        profile.setImageResource(R.drawable.prf_default);

        name.setText("숨겨진 누군가");
        state.setText("대화해보세요 !");

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        this.notifyDataSetChanged();
    }
}
