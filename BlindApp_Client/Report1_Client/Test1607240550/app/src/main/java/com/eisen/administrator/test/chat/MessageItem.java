package com.eisen.administrator.test.chat;

import android.content.Context;
import android.text.Spanned;

/**
 * Created by Administrator on 2016-07-17.
 */
public class MessageItem  {

    private boolean who;
    private Spanned emoticon;
    private String text;
    //private View.OnClickListener ttsRead;

    public MessageItem(boolean who, String text) {//Spanned symbol, String text) {
        this.who=who;
        //this.emoticon =symbol;
        this.text=text;


    }

    public boolean getWho() {    return who;}
    //public Spanned getEmoticon()  {   return emoticon;}
    public String getText()    {   return text;}
    //public View.OnClickListener getTtsRead()  {   return ttsRead;}
}
