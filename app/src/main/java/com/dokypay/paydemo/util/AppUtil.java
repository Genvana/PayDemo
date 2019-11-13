package com.dokypay.paydemo.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class AppUtil {

    /**
     * 部分国产手机禁用三方应用读取应用列表，故使用此方法，获取特定packageinfo，若没有安装对应包，则抛出异常，以此判断是否安装。
     * @return true->已安装;false->未安装
     */
    public static boolean checkAppInstalled(Application application, String packageName){
        boolean result = true;
        PackageManager packageManager = application.getPackageManager();
        try{
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName,PackageManager.GET_GIDS);
            result = packageInfo !=null;
        }catch(PackageManager.NameNotFoundException e) {
            result =false;
        }
        return result;

    }

    public static void toast(Context context, String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
