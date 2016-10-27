package com.eisen.administrator.test.mem;

/**
 * Created by Administrator on 2016-07-18.
 */
public class MemberInfo {
    private String name = "";
    private int profile;
    private String state = "";

    public MemberInfo(String name, int profile, String state) {
        super();
        this.name = name;
        this.profile = profile;
        this.state = state;
    }
    public MemberInfo() {}
    String GetName(){return name;}
    String GetState(){return state;}
    int GetProf(){return profile;}
    void SetName(String str){name=str;}
    void SetState(String str){state=str;}
    void SetProf(int i){profile=i;}

}
