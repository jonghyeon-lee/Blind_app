package com.eisen.administrator.test.chat;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;

import com.eisen.administrator.test.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-07-17.
 */
public class EmoticonPagerAdapter extends PagerAdapter {

    ArrayList<String> ma_Symbols;
    private static final int NO_OF_EMOTICONS_PER_PAGE = 100;
    FragmentActivity mActivity;
    EmoticonGridAdapter.KeyClickListener mListener;

    public EmoticonPagerAdapter(FragmentActivity activity,
                               ArrayList<String> symbols, EmoticonGridAdapter.KeyClickListener listener) {
        this.ma_Symbols = symbols;
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double) ma_Symbols.size()
                / (double) NO_OF_EMOTICONS_PER_PAGE);
    }

    @Override
    public Object instantiateItem(View collection, int position) {

        View layout = mActivity.getLayoutInflater().inflate(
                R.layout.activity_chat_emoticon_grid, null);

        int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
        ArrayList<String> emoticonsInAPage = new ArrayList<String>();

        for (int i = initialPosition; i < initialPosition
                + NO_OF_EMOTICONS_PER_PAGE
                && i < ma_Symbols.size(); i++) {
            emoticonsInAPage.add(ma_Symbols.get(i));
        }

        GridView grid = (GridView) layout.findViewById(R.id.emoticon_grid);
        EmoticonGridAdapter adapter = new EmoticonGridAdapter(
                mActivity.getApplicationContext(), emoticonsInAPage, position,
                mListener);
        grid.setAdapter(adapter);

        ((ViewPager) collection).addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

