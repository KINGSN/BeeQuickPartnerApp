package com.pocketporter.partner.utility;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

public class HelperClass {

    public static void getNotification(Context context){
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName());

        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            CharSequence name = "MyChannel";
            String descr = "demo";
        }
    }
}
