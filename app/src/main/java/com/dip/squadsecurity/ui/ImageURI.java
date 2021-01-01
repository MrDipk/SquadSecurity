package com.dip.squadsecurity.ui;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

public class ImageURI {
public  String img1,img2,img3;
    public void URL(Uri uri, String str, Context context){
        if(str.equals("Profile")) {
            img1 = uri.toString();
        }
        else if(str.equals("AdharFront")) {
            img2 = uri.toString();
        }
        else if(str.equals("AdharBack")) {
            img3 = uri.toString();
        }
    }
}
