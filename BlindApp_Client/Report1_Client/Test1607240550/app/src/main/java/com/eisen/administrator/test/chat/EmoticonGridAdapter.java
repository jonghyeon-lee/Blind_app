package com.eisen.administrator.test.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.eisen.administrator.test.R;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-07-17.
 */
public class EmoticonGridAdapter extends BaseAdapter {

    private ArrayList<String> paths;

    private int pageNumber;
    SharedPreferences prefs;
    Context mContext;

    KeyClickListener mListener;

    public EmoticonGridAdapter(Context context, ArrayList<String> paths, int pageNumber, KeyClickListener listener) {
        this.mContext = context;
        this.paths = paths;
        this.pageNumber = pageNumber;
        this.mListener = listener;
        prefs= PreferenceManager.getDefaultSharedPreferences(context);

    }
    public View getView(int position, View convertView, ViewGroup parent){

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.activity_chat_emoticon_item, null);
        }

        final String path = paths.get(position);

        ImageView image = (ImageView) v.findViewById(R.id.item_by_img);
        image.setImageBitmap(getImage(path));

        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.keyClickedIndex(path);
                String value = prefs.getString("Msg","Default");
                SharedPreferences.Editor editor = prefs.edit();
                if(value.equals("Default"))
                {
                    editor.putString("Msg",path);
                    editor.commit();
                }
                else
                {
                    value = value + path;
                    editor.clear();
                    editor.putString("Msg",value);
                    editor.commit();
                }
            }
        });

        return v;
    }

    @Override
    public int getCount() {
        return paths.size();
    }

    @Override
    public String getItem(int position) {
        return paths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPageNumber () {
        return pageNumber;
    }

    private Bitmap getImage (String path) {
        AssetManager mngr = mContext.getAssets();
        InputStream in = null;

        try {
            in = mngr.open("symbols/" + path);
        } catch (Exception e){
            e.printStackTrace();
        }

        //BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inSampleSize = chunks;

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }



    public interface KeyClickListener {

        public void keyClickedIndex(String index);
    }
}
