package com.dip.squadsecurity;

public class SearchGuardList {
    private String  name,phno,uid;
    public SearchGuardList(String name,String phno,String uid){
        this.name=name;
        this.phno=phno;
        this.uid=uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
