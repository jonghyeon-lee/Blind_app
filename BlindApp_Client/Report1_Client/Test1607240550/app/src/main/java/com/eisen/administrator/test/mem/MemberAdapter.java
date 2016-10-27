package com.eisen.administrator.test.mem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eisen.administrator.test.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-07-18.
 */
class MemberAdapter  extends BaseAdapter { // 리스트 뷰의 아답타
    Context context;
    int layout;
    ArrayList<MemberInfo> al_MemberInfo;
    LayoutInflater inf;

    public MemberAdapter(Context context, int layout, ArrayList<MemberInfo> al) {
        this.context = context;
        this.layout = layout;
        this.al_MemberInfo = al;
        inf = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return al_MemberInfo.size();
    }
    @Override
    public Object getItem(int position) {
        return al_MemberInfo.get(position);
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
        ImageView profile = (ImageView)convertView.findViewById(R.id.my_image);
        TextView name = (TextView)convertView.findViewById(R.id.my_name);
        TextView state = (TextView)convertView.findViewById(R.id.my_state);

        profile.setImageResource(al_MemberInfo.get(position).GetProf());

        name.setText(al_MemberInfo.get(position).GetName());
        state.setText(al_MemberInfo.get(position).GetState());


        return convertView;
    }
}
