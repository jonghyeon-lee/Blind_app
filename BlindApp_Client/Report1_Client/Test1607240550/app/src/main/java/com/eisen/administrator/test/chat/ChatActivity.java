package com.eisen.administrator.test.chat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.eisen.administrator.test.JSONParser;
import com.eisen.administrator.test.Manifest;
import com.eisen.administrator.test.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Administrator on 2016-07-17.
 */
public class ChatActivity  extends FragmentActivity implements EmoticonGridAdapter.KeyClickListener {
    SharedPreferences m_ChatPrefs;
    private View mv_PopUp;
    private LinearLayout ml_EmoticonCover;
    private PopupWindow m_PopupWindow;
    private int m_KeyboardHeight;
    private LinearLayout ml_Parent;
    private boolean mb_IsKeyBoardVisible;
    private Bitmap[] m_Emoticon;
    int previousHeightDiffrence = 0;
    private static final int NO_OF_EMOTICONS = 55;

    SharedPreferences m_Prefs;
    List<NameValuePair> params;
    Bundle m_Bundle;

    // RemakeChatBubble
    private MessageAdapter m_MessageAdapter;
    private ListView mlv_ChatList;
    private EditText met_Content;
    private Button mbt_Send;

    private static final boolean TO_ME=false;
    private static final boolean FROM_OPPOSITE=true;

    ProgressDialog send_progress;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //tab = (TableLayout)findViewById(R.id.tab);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
        m_Prefs = getSharedPreferences("Chat", 0);
        m_Bundle = getIntent().getBundleExtra("INFO");
        SharedPreferences.Editor edit = m_Prefs.edit();
        edit.putString("CURRENT_ACTIVE", m_Bundle.getString("mobno"));
        edit.commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice, new IntentFilter("Msg"));
        m_ChatPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor edit2 = m_ChatPrefs.edit();
        edit2.clear();
        edit2.commit();

        ml_Parent = (LinearLayout) findViewById(R.id.activity_chat);
        ml_EmoticonCover = (LinearLayout) findViewById(R.id.footer_for_emoticon);
        mv_PopUp = getLayoutInflater().inflate(R.layout.activity_chat_emoticon_pop_up, null);
        //ChatBubble
        mbt_Send = (Button) findViewById(R.id.post_button);
        mlv_ChatList = (ListView) findViewById(R.id.chat_list);
        met_Content = (EditText) findViewById(R.id.chat_content);
        m_MessageAdapter = new MessageAdapter (getApplicationContext(), R.layout.activity_chat_message);

        mbt_Send.setOnClickListener(new View.OnClickListener() {
            /*chat_msg = (EditText)findViewById(R.id.chat_msg);
            send_btn = (Button)findViewById(R.id.sendbtn);

            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TableRow tr2 = new TableRow(getApplicationContext());
                    tr2.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    TextView textview = new TextView(getApplicationContext());
                    textview.setTextSize(20);
                    textview.setTextColor(Color.parseColor("#A901DB"));
                    textview.setText(Html.fromHtml("<b>You : </b>" + chat_msg.getText().toString()));
                    tr2.addView(textview);
                    tab.addView(tr2);
                    new Send().execute();
                }
            });*/

            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
        mlv_ChatList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mlv_ChatList.setAdapter(m_MessageAdapter);

        m_MessageAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mlv_ChatList.setSelection(m_MessageAdapter.getCount() - 1);
            }
        });
        mlv_ChatList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (m_PopupWindow.isShowing())
                    m_PopupWindow.dismiss();
                return false;
            }
        });
        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);
        ImageView emoticonsButton = (ImageView) findViewById(R.id.emoticon_button);
        emoticonsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!m_PopupWindow.isShowing()) {

                    m_PopupWindow.setHeight((int) (m_KeyboardHeight));

                    if (mb_IsKeyBoardVisible) {
                        ml_EmoticonCover.setVisibility(LinearLayout.GONE);
                    } else {
                        ml_EmoticonCover.setVisibility(LinearLayout.VISIBLE);
                    }
                    m_PopupWindow.showAtLocation(ml_Parent, Gravity.BOTTOM, 0, 0);

                } else {
                    m_PopupWindow.dismiss();
                }
            }
        });

        //initTextMatch();
        readEmoticons();
        enablePopUpView();
        checkKeyboardHeight(ml_Parent);

        if(m_Bundle.getString("name") != null){
            /*TableRow tr1 = new TableRow(getApplicationContext());
            tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml("<b>"+bundle.getString("name")+" : </b>"+bundle.getString("msg")));
            tr1.addView(textview);
            tab.addView(tr1);*/
            String receiveMsg = m_Bundle.getString("msg");
            //String receiveMsg = textConverter(value);
            //Spanned tmp = emoticonConverter(receiveMsg);
            m_MessageAdapter.add(new MessageItem(FROM_OPPOSITE, receiveMsg));
        }
        send_progress = new ProgressDialog(this);
        send_progress.setMessage(getString(R.string.in_send));
        send_progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        send_progress.setIndeterminate(true);
    }

    private void sendChatMessage() {
        if(met_Content.getText().toString().length() > 0) {
            send_progress.show();
            SharedPreferences.Editor editor = m_ChatPrefs.edit();
            editor.putString("Msg",met_Content.getText().toString());
            editor.commit();

            String sendMsg = m_ChatPrefs.getString("Msg", "");
            //Spanned sp = emoticonConverter(sendMsg);
            m_MessageAdapter.add(new MessageItem(TO_ME, sendMsg));

            new Send().execute();
        }
    }


    private BroadcastReceiver onNotice= new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String rcv = intent.getStringExtra("msg");
            String str1 = intent.getStringExtra("fromname");
            String str2 = intent.getStringExtra("fromu");
            if(str2.equals(m_Bundle.getString("mobno"))){
                /*
                TableRow tr1 = new TableRow(getApplicationContext());
                tr1.setLayoutParams(new TableRow.LayoutParams( TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(getApplicationContext());
                textview.setTextSize(20);
                textview.setTextColor(Color.parseColor("#0B0719"));
                textview.setText(Html.fromHtml("<b>"+str1+" : </b>"+str));
                tr1.addView(textview);
                tab.addView(tr1);*/
                //Spanned tmp = emoticonConverter(rcv);
                m_MessageAdapter.add(new MessageItem(FROM_OPPOSITE, rcv));
            }



        }
    };

    private void enablePopUpView() {

        ViewPager pager = (ViewPager) mv_PopUp.findViewById(R.id.emoticon_pager);
        pager.setOffscreenPageLimit(3);

        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
            paths.add(i + ".png");
        }

        EmoticonPagerAdapter adapter = new EmoticonPagerAdapter(ChatActivity.this, paths, this);
        pager.setAdapter(adapter);

        // Creating a pop window for emoticons keyboard
        m_PopupWindow = new PopupWindow(mv_PopUp, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) m_KeyboardHeight, false);

        ImageView backSpace = (ImageView) mv_PopUp.findViewById(R.id.back);
        backSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                met_Content.dispatchKeyEvent(event);
            }
        });

        m_PopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                ml_EmoticonCover.setVisibility(LinearLayout.GONE);
            }
        });
    }
    private Spanned emoticonConverter(String input)
    {
        String[] result=input.split(".png");
        //String i_src="<img src ='";
        Spanned symbol=null;
        for(int i=0 ; i<result.length ; i++) {
            Spanned cs = getImageToString(result[i],i);
            //symbol = (Spanned) Text
            if(i==0)
                symbol = cs;
            else
                symbol = (Spanned) TextUtils.concat(symbol,cs);
            //symbol =symbol + cs;
        }
        //Spanned cs = Html.fromHtml("<img src ='" + index + "'/>", imageGetter, null);
        return symbol;
    }
    private Spanned getImageToString(final String input, int cnt){
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                Drawable d = new BitmapDrawable(getResources(), m_Emoticon[Integer.parseInt(input) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };
        Spanned result;
        if((cnt+1)%3!=0)
            result= Html.fromHtml("<img src ='" + input + "'.png/>", imageGetter, null);
        else
            result= Html.fromHtml("<img src ='" + input + "'.png/><br/>", imageGetter, null);
        Log.e ("TTAG","cs:"+result+", item:"+input);
        return result;
    }
    private void readEmoticons(){
        m_Emoticon = new Bitmap[NO_OF_EMOTICONS];
        for(short i = 0; i< NO_OF_EMOTICONS; i++){
            m_Emoticon[i] = getImage((i+1)+".png");
        }
    }
    private Bitmap getImage(String path) {
        AssetManager mngr = getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }
    private void changeKeyboardHeight(int height) {
        if (height > 100) {
            m_KeyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, m_KeyboardHeight);
            ml_EmoticonCover.setLayoutParams(params);
        }

    }
    private void checkKeyboardHeight(final View parentLayout) {

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previousHeightDiffrence - heightDifference > 50) {
                            m_PopupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;
                        if (heightDifference > 100) {

                            mb_IsKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference);
                        } else {
                            mb_IsKeyBoardVisible = false;

                        }

                    }
                });

    }
    private class Send extends AsyncTask<String, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser json = new JSONParser();
            params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("from", m_Prefs.getString("REG_FROM","")));
            params.add(new BasicNameValuePair("fromn", m_Prefs.getString("FROM_NAME","")));
            params.add(new BasicNameValuePair("to", m_Bundle.getString("mobno")));
            params.add((new BasicNameValuePair("msg", m_ChatPrefs.getString("Msg", ""))));
            Log.e("TAG_SEND", "SEND_MESSAGE : " + m_ChatPrefs.getString("Msg", ""));

            JSONObject jObj = json.getJSONFromUrl("http://swmae7ubt.cloudapp.net:80/send",params);
            return jObj;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            met_Content.setText("");
            SharedPreferences.Editor editor = m_ChatPrefs.edit();
            editor.clear();
            editor.commit();
            String res = null;

            try {
                res = json.getString("response");
                if(res.equals("Failure")){
                    Toast.makeText(getApplicationContext(),"The user has logged out. You cant send message anymore !",Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            send_progress.dismiss();
        }
    }
    @Override
    public void keyClickedIndex(final String index) {
        Log.e("TAG", "keyClickedIndex parameter index :"+index);
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                //Log.e("KEYCLICK INDEX : ",""+index+", StringTokenizer : "+st.toString());
                Drawable d = new BitmapDrawable(getResources(), m_Emoticon[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
        };

        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);

        int cursorPosition = met_Content.getSelectionStart();
        met_Content.getText().insert(cursorPosition, cs);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}