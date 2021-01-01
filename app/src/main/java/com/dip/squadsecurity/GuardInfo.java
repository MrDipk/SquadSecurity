package com.dip.squadsecurity;

public class GuardInfo {

    public String Gid,CompanyName,GuardName,GuardPhno,GuardDOB,
            GuardFName,GuardFPhno,GuardPAddress,MarriedStatus,GuardWName,GuardQualification,GuardPCDetails,
            GuardExprience;
    public GuardInfo(){

    }

    public GuardInfo(String Gid,String CompanyName,String GuardName,
              String GuardPhno,String GuardDOB,String GuardFName,String GuardFPhno,String GuardPAddress,
              String MarriedStatus,String GuardWName,String GuardQualification,String GuardPCDetails,String GuardExprience){
        this.Gid=Gid;
        this.CompanyName=CompanyName;
        this.GuardName=GuardName;
        this.GuardPhno=GuardPhno;
        this.GuardDOB=GuardDOB;
        this.GuardFName=GuardFName;
        this.GuardFPhno=GuardFPhno;
        this.GuardPAddress=GuardPAddress;
        this.MarriedStatus=MarriedStatus;
        this.GuardWName=GuardWName;
        this.GuardQualification=GuardQualification;
        this.GuardPCDetails=GuardPCDetails;
        this.GuardExprience=GuardExprience;
    }
}
