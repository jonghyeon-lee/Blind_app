package com.eisen.administrator.test.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eisen.administrator.test.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-07-17.
 */
public class MessageAdapter extends ArrayAdapter<MessageItem> {
    private TextView mtv_AACc;
    private List<MessageItem> ml_Message = new ArrayList<MessageItem>();
    private LinearLayout m_SingleMessageContainer;
    private Context ct_Parent;

    public MessageAdapter(Context context, int textViewResourceId)
    {
        super(context,textViewResourceId);
        ct_Parent = context;

        /*int available = m_TTSobj.isLanguageAvailable();    //해당 언어가 지원하는 언어인지 검사

        if (available < 0) {                    //지원하지 않으면 메시지 출력
            Toast.makeText(ct_Parent, R.string.msg_not_support_lang, Toast.LENGTH_SHORT).show();
            m_isTTSSupport = false;
        } else m_isTTSSupport = true;                //지원하는 언어이면 플래그 변경*/
    }

    @Override
    public void add(MessageItem item) {
        ml_Message.add(item);
        super.add(item);
    }
    public int getCount()                       {return this. ml_Message.size();}
    public MessageItem getItem (int index)      {return this. ml_Message.get(index);}
    public Bitmap decodeToBitmap(byte[] decodedByte) {
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        View cv = convertView;
        if(cv == null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cv = inflater.inflate(R.layout.activity_chat_message, parent, false);
        }
        m_SingleMessageContainer = (LinearLayout) cv.findViewById(R.id.singleMessageContainer);
        mtv_AACc = (TextView) cv.findViewById(R.id.item_text_aac);

        MessageItem sMessageItem = getItem(position);

        mtv_AACc.setText(sMessageItem.getText());
        /*String input = sMessageItem.getText();

        Spanned input_sp =(Spanned) TextUtils.concat(sMessageItem.getEmoticon(),sMessageItem.getText());
        mtv_AACc.setText(input_sp);*/

        mtv_AACc.setBackgroundResource(sMessageItem.getWho() ? R.drawable.bubble_a : R.drawable.bubble_b);
        m_SingleMessageContainer.setGravity(sMessageItem.getWho() ? Gravity.LEFT : Gravity.RIGHT);
        return cv;
    }

}
