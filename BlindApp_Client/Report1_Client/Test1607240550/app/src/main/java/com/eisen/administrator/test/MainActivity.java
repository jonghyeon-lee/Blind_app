package com.eisen.administrator.test;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-07-17.
 */
public class MainActivity extends AppCompatActivity{
    SharedPreferences prefs;
    Context context;//ActivityCompat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        prefs = getSharedPreferences("Chat", 0);
        context = getApplicationContext();
        final TextView titleTv = (TextView) findViewById(R.id.tab_title_text);
        final ArrayList<String> tab_title = new ArrayList<String>();
        tab_title.add("블라인더");
        tab_title.add("멤버");
        tab_title.add("설정");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_name_live));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_name_mem));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_name_set));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                titleTv.setText(tab_title.get(tab.getPosition()));
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

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
}
