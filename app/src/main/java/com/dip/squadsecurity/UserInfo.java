package com.dip.squadsecurity;


public class UserInfo {
    public String name,phno;

   public UserInfo(){}
    public UserInfo(String phno){
       this.phno=phno;
    }
   public UserInfo(String name,String phno){
        this.name=name;
        this.phno=phno;
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
}
